package assignment4;

import java.util.*;

public class HashFun {
	private int a;
	private int b;
	private int p;
	private int nBuckets;
	private int[] buckets;
	
	public HashFun(int a, int b, int p, int nBuckets){
		this.a = a;
		this.b = b;
		this.p = p;
		this.nBuckets = nBuckets;
		buckets = new int[nBuckets];
	}
	
	public void increment(int id){
		int bucket = hash(id);
		buckets[bucket] = buckets[bucket] + 1;
	}
	
	public int getCount(int id){
		int bucket = hash(id);
		return buckets[bucket];
	}
	
	public int hash(int id){
		int y = id%p;
		int hash_val = (a*y + b)%p;
		return hash_val%nBuckets;
	}
	
	public static void incrementAll(ArrayList<HashFun> hashes, int id){
		for(int i = 0; i < hashes.size(); i++){
			HashFun hash = hashes.get(i);
			hash.increment(id);
		}
	}
	
	public static int getMinCount(ArrayList<HashFun> hashes, int id){
		int minCount = Integer.MAX_VALUE;
		for(int i = 0; i < hashes.size(); i++){
			int count = hashes.get(i).getCount(id);
			if(count < minCount) minCount = count;
		}
		return minCount;
	}
}
