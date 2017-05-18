package com.cki.filter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.cki.filter.config.FilterConfig;

@Component
public class UniqueService {
	static final int MIN_SIZE = 1000000;
	static final double PRECISE = 1.0E-5;

	Logger log = LoggerFactory.getLogger(this.getClass());
	@Autowired
	private DocKeyCollector keyCollector;
	@Autowired
	private FilterConfig config;

	SeenDetector pool = null;

	int mask = 0;

	AtomicInteger count = new AtomicInteger(0);

	ReentrantLock lock = new ReentrantLock();

	@PostConstruct
	public void init() {

		log.info("init unique pool...");

		if (config.isDebug()) {
			log.info("debug=true,abort init unique pool.");
			return;
		}

		File[] dumps = keyCollector.findAllDump();

		int size = predictFilterSize(dumps);

		initBloomFilter(size);

		for (File dump : dumps) {

			int counter = readAndfeed(dump);

			log.info("feed {} from file:{}", counter, dump.getName());

		}

		log.info("init unique pool end,size~={}", size / 2);

	}

	private synchronized void initBloomFilter(int size) {

		if (size < MIN_SIZE) {
			size = MIN_SIZE;
		}

		log.info("init bloomfilter, size {}", size);

		mask = size;

		pool = new BloomFilterSeenDetector(size, PRECISE);
	}

	private int readAndfeed(File dump) {

		int counter = 0;

		BufferedReader reader = null;

		try {

			reader = new BufferedReader(new InputStreamReader(new FileInputStream(dump), "utf-8"));

			while (true) {

				String line = reader.readLine();

				if (line == null) {
					break;
				}

				if (count.incrementAndGet() > mask) {
					log.info("resize bloomfilter, count {}", count.get());
					count.set(0);
					init();
				}

				pool.accept(line);

				counter++;

			}

		} catch (Exception e) {
			log.error("read keys from dump file error.file:{}", dump.getPath(), e);
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					log.error("close reader error.", e);
				}
			}
		}

		return counter;
	}

	private int predictFilterSize(File[] dumps) {

		long total = 0L;

		for (File dump : dumps) {
			total += dump.length();
		}

		int size = (int) (total / 32) * 2;

		return size;
	}

	public boolean seen(String k) {
		return pool.seen(k);
	}

	public boolean feed(String k) {

		boolean result = false;

		lock.lock();

		try {

			if (count.incrementAndGet() > mask) {
				log.info("resize bloomfilter, count {}", count.get());
				count.set(0);
				init();
			}

			result = pool.accept(k);
		} finally {
			lock.unlock();
		}

		if (result) {
			this.keyCollector.dump(k);
		}

		return result;
	}

	public void rebuild(Date day) {

		init();

	}

	public void rebuildAll() {

		init();

	}

}
