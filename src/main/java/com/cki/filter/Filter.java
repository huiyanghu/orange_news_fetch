package com.cki.filter;

import java.io.UnsupportedEncodingException;

public abstract class Filter {

	int hashCount;

	private static MurmurHash hasher = new MurmurHash();

	int getHashCount() {
		return hashCount;
	}

	public int[] getHashBuckets(String key) {
		return Filter.getHashBuckets(key, hashCount, buckets());
	}

	public int[] getHashBuckets(byte[] key) {
		return Filter.getHashBuckets(key, hashCount, buckets());
	}

	abstract int buckets();

	public abstract void clear();

	public abstract void add(String key);

	public abstract boolean isPresent(String key);

	abstract int emptyBuckets();

	static int[] getHashBuckets(String key, int hashCount, int max) {

		byte[] b;

		try {
			b = key.getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}

		return getHashBuckets(b, hashCount, max);
	}

	static int[] getHashBuckets(byte[] b, int hashCount, int max) {

		int[] result = new int[hashCount];
		int hash1 = hasher.hash(b, b.length, 0);
		int hash2 = hasher.hash(b, b.length, hash1);

		for (int i = 0; i < hashCount; i++) {
			result[i] = Math.abs((hash1 + i * hash2) % max);
		}

		return result;
	}

}
