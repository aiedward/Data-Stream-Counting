package assignment4;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.*;

public class StreamData {
	
	ArrayList<HashFun> hashes;
	
	public StreamData(String hashFile,int numHashFunctions, int numBuckets, int p){
		createHashFunctions(hashFile,numHashFunctions,numBuckets,p);
	}
	
	//Creates the hash functions
	private void createHashFunctions(String hashFile, int numHashFunctions,int numBuckets, int p){
		hashes = new ArrayList<HashFun>(numHashFunctions);
		File fi = new File(hashFile);
		BufferedReader br;
		try{
			br = new BufferedReader(new FileReader(fi));
			String line = null;
			String[] str;
			while ((line = br.readLine()) != null && !line.equals("")) {
				//System.out.println(line);
				str = line.split("\\s+");
				int a = Integer.parseInt(str[0]);
				int b = Integer.parseInt(str[1]);
				hashes.add(new HashFun(a,b,p,numBuckets));
			}
			br.close();
			return;
		}catch(IOException es){
			System.out.println("Error reading in hash parameters");
		}
	}

	//Increments the buckets in the hashes
	public void startCounting(String streamFile){
		File fi = new File(streamFile);
		BufferedReader br;
		try{
			br = new BufferedReader(new FileReader(fi));
			String line = null;
			while ((line = br.readLine()) != null && !line.equals("")) {
				//System.out.println(line);
				int id = Integer.parseInt(line);
				HashFun.incrementAll(hashes,id);
			}
			br.close();
			return;
		}catch(IOException es){
			System.out.println("Error reading in the stream");
		}
	}

	//This is actually necessary but the algorithm is fast enough to make an unnecessary loop
	public int findNumWords(String countFile){
		int t = 0;
		File fi = new File(countFile);
		BufferedReader br;
		try{
			br = new BufferedReader(new FileReader(fi));
			String line = null;
			String[] str;
			while ((line = br.readLine()) != null && !line.equals("")) {
				//System.out.println(line);
				str = line.split("\\s+");
				int count = Integer.parseInt(str[1]);
				t = t + count;
			}
			br.close();
			return t;
		}catch(IOException es){
			System.out.println("Error reading in the count File");
		}
		return t;
	}
	
	//Computes the errors for us
	public void computeErrors(String countFile, String relativeErrorFile, String exactWordFrequencyFile){
		int t = findNumWords(countFile);
		File fi = new File(countFile);
		BufferedReader br;
		try{
			//Create the file if it doesnt exist
			File relErrFile = new File(relativeErrorFile);
			relErrFile.delete();
			if(!relErrFile.exists()) {
				relErrFile.createNewFile();
			}
			//Create the file if it doesnt exist
			File exWordFreqFile = new File(exactWordFrequencyFile);
			exWordFreqFile.delete();
			if(!exWordFreqFile.exists()) {
				exWordFreqFile.createNewFile();
			}
			//Open the files
			FileOutputStream oFile1 = new FileOutputStream(relErrFile, false); 
			BufferedWriter bw1 = new BufferedWriter(new OutputStreamWriter(oFile1));
			FileOutputStream oFile2 = new FileOutputStream(exWordFreqFile, false); 
			BufferedWriter bw2 = new BufferedWriter(new OutputStreamWriter(oFile2));
			//Start with the algorithm
			br = new BufferedReader(new FileReader(fi));
			String line = null;
			String[] str;
			while ((line = br.readLine()) != null && !line.equals("")) {
				//System.out.println(line);
				str = line.split("\\s+");
				int id = Integer.parseInt(str[0]);
				double actualCount = (double)Integer.parseInt(str[1]);
				double f = (double)HashFun.getMinCount(hashes, id);
				double er = (f - actualCount)/actualCount;
				double exactFreq = actualCount/(double)t;
				bw1.write("" + er);
				bw1.newLine();
				bw2.write("" + exactFreq);
				bw2.newLine();
			}
			br.close();
			bw1.close();
			bw2.close();
			return;
		}catch(IOException es){
			System.out.println("Error reading in the count File");
		}
		return;
	}
	
	public static void main(String[] args){
		String hashFile = "hash_params.txt";
		String streamFile = "words_stream_tiny.txt";
		String countFile = "counts_tiny.txt";
		String relativeErrorFile = "y_relativeError_tiny.txt";
		String exactWordFrequencyFile = "x_exactWordFrequency_tiny.txt";
		//delta = e^-5
		int numHashFns = 5;
		//epsilon = e*10-4
		int numBuckets = 10000;
		int p = 123457;
		StreamData std = new StreamData(hashFile,numHashFns,numBuckets,p);
		//Small file run
		run(std,hashFile,streamFile,countFile,relativeErrorFile,exactWordFrequencyFile);
		streamFile = "words_stream.txt";
		countFile = "counts.txt";
		relativeErrorFile = "y_relativeError.txt";
		exactWordFrequencyFile = "x_exactWordFrequency.txt";
		//Very big file run
		run(std,hashFile,streamFile,countFile,relativeErrorFile,exactWordFrequencyFile);
		
	}
	
	public static void run(StreamData std, String hashFile, String streamFile, String countFile,
			String relativeErrorFile, String exactWordFrequencyFile){
		//Counting
		System.out.println("Starting counting");
		double begin = System.currentTimeMillis();
		std.startCounting(streamFile);
		double end = System.currentTimeMillis();
		double time = (end - begin)/1000;
		System.out.println("Finished counting");
		System.out.println("Counting took " + time + "seconds");
		//Error Computation
		System.out.println("Starting to compute errors");
		begin = System.currentTimeMillis();
		std.computeErrors(countFile,relativeErrorFile,exactWordFrequencyFile);
		end = System.currentTimeMillis();
		time = (end - begin)/1000;
		System.out.println("Finished computing errors");
		System.out.println("Error computation took " + time + "seconds");
	}
}



