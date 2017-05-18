package com.cki.filter;

import java.util.BitSet;

public class BloomFilter extends Filter {

	private BitSet filter_;

	public BloomFilter(int numElements, int bucketsPerElement) {
		this(BloomCalculations.computeBestK(bucketsPerElement), new BitSet(
				numElements * bucketsPerElement + 20));
	}

	public BloomFilter(int numElements, double maxFalsePosProbability) {

		BloomCalculations.BloomSpecification spec = BloomCalculations
				.computeBucketsAndK(maxFalsePosProbability);

		filter_ = new BitSet(numElements * spec.bucketsPerElement + 20);
		hashCount = spec.K;
	}

	BloomFilter(int hashes, BitSet filter) {

		hashCount = hashes;
		filter_ = filter;
	}

	public void clear() {
		filter_.clear();
	}

	int buckets() {
		return filter_.size();
	}

	BitSet filter() {
		return filter_;
	}

	public boolean isPresent(String key) {

		for (int bucketIndex : getHashBuckets(key)) {
			if (!filter_.get(bucketIndex)) {
				return false;
			}
		}

		return true;
	}

	public boolean isPresent(byte[] key) {

		for (int bucketIndex : getHashBuckets(key)) {
			if (!filter_.get(bucketIndex)) {
				return false;
			}
		}

		return true;
	}

	public void add(String key) {

		for (int bucketIndex : getHashBuckets(key)) {
			filter_.set(bucketIndex);
		}
	}

	public void add(byte[] key) {

		for (int bucketIndex : getHashBuckets(key)) {
			filter_.set(bucketIndex);
		}
	}

	public String toString() {
		return filter_.toString();
	}

	int emptyBuckets() {

		int n = 0;

		for (int i = 0; i < buckets(); i++) {
			if (!filter_.get(i)) {
				n++;
			}
		}

		return n;
	}
}
