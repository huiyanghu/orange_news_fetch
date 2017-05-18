package com.cki.filter;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.cki.filter.config.FilterConfig;

@Component
public class DocKeyCollector {

	Logger logger = LoggerFactory.getLogger(this.getClass());
	@Autowired
	private FilterConfig filterConfig;

	List<String> buffer = new ArrayList<String>();

	int counter = 0;

	ReentrantLock lock = new ReentrantLock();

	@PreDestroy
	public void beforeDestroy() {

		dump(buffer);
		logger.info("finish flush buffer data.");
	}

	public void dump(String key) {

		try {
			lock.lock();

			buffer.add(key);

			if ((counter++) > 50) {

				dump(buffer);

				buffer.clear();

				counter = 0;

			}
		} catch (Throwable e) {
			logger.error("dump key error.{}", key, e);
		} finally {
			lock.unlock();
		}

	}

	public void dump(Collection<String> keys) {

		BufferedWriter writer = null;

		try {
			File f = buildDump(new Date());

			writer = new BufferedWriter(new FileWriter(f, true));

			for (String key : keys) {
				writer.write(key);
				writer.newLine();
			}

			writer.flush();

		} catch (Exception e) {
			logger.error("dump doc keys error.{}", keys, e);
		} finally {
			if (writer != null) {
				try {
					writer.close();
				} catch (IOException e) {
					logger.error("close writer error.", e);
				}
			}
		}

	}

	private File buildDump(Date date) {

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String filePath = new StringBuilder(filterConfig.getDumpPath()).append(File.separator).append("dump.").append(sdf.format(date)).append(".txt").toString();

		File f = new File(filePath);

		return f;

	}

	public File findDump(Date date) {
		return buildDump(date);
	}

	public File[] findAllDump() {

		File dir = new File(filterConfig.getDumpPath());

		if (!dir.exists() || !dir.isDirectory()) {
			logger.error("not find dump dir.{}", filterConfig.getDumpPath());
			return new File[0];
		}

		return dir.listFiles(new FilenameFilter() {

			public boolean accept(File dir, String name) {

				if (name.startsWith("dump.") && name.endsWith(".txt")) {
					return true;
				}

				return false;
			}
		});

	}
}
