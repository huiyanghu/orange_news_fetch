package com.cki.filter;

import java.util.concurrent.atomic.AtomicReference;

public class BloomFilterSeenDetector implements SeenDetector {

	protected Filter filter = null;

	protected AtomicReference<Filter> flag = null;

	public BloomFilterSeenDetector(int n, double maxFalsePosProbability) {

		if (n > Integer.MAX_VALUE) {
			throw new IllegalArgumentException(
					"Illegal bloom filter n item size:" + n);
		}

		filter = new BloomFilter(n, maxFalsePosProbability);
		flag = new AtomicReference<Filter>(filter);
	}

	public boolean seen(String url) {
		return filter.isPresent(url);
	}

	public boolean accept(String url) {

		boolean ret = false;

		lock();

		try {
			if (!filter.isPresent(url)) {
				filter.add(url);

				ret = true;
			}
		} finally {
			unlock();
		}

		return ret;
	}

	private void lock() {

		while (flag.compareAndSet(filter, null)) {
		}
	}

	private void unlock() {
		flag.set(filter);
	}

	public void clear() {

		lock();

		try {
			filter.clear();
		} finally {
			unlock();
		}
	}

	public int size() {
		throw new UnsupportedOperationException();
	}
}
