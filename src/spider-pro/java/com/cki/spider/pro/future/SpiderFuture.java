package com.cki.spider.pro.future;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class SpiderFuture<V> implements Future<V> {

	public static final Throwable INTERRUPTED = new Throwable("Interrupted");

	public static final Throwable CANCELLED = new Throwable("Cancelled");

	public static final Throwable CANNOT_CONNECT = new Throwable("Cannot connect");

	public static final Throwable CONNECTION_LOST = new Throwable("Connection lost");

	public static final Throwable SHUTTING_DOWN = new Throwable("Shutting down");

	public static final Throwable EXECUTION_REJECTED = new Throwable("Execution rejected by connection");

	public static final Throwable TIMED_OUT = new Throwable("Request execution timed out");

	public static final Throwable INVALID_REDIRECT = new Throwable("Redirect without Location header");

	protected V result;

	private int waiters;

	private boolean done;

	private Throwable cause;

	@Override
	public boolean cancel(boolean mayInterruptIfRunning) {

		synchronized (this) {
			if (this.done) {
				return false;
			}

			this.cause = CANCELLED;
			this.done = true;

			if (this.waiters > 0) {
				this.notifyAll();
			}
		}

		return true;
	}

	@Override
	public V get() throws InterruptedException, ExecutionException {

		if (Thread.interrupted()) {
			throw new InterruptedException();
		}

		synchronized (this) {
			while (!this.done) {
				waiters++;

				try {
					this.wait();
				} finally {
					waiters--;
				}
			}
		}

		return this.result;
	}

	@Override
	public V get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {

		if (Thread.interrupted()) {
			throw new InterruptedException();
		}

		long timeoutNanos = unit.toNanos(timeout);

		long startTime = timeoutNanos <= 0 ? 0 : System.nanoTime();
		long waitTime = timeoutNanos;

		try {
			synchronized (this) {
				if (this.done) {
					return this.result;
				} else if (waitTime <= 0) {
					return this.result;
				}

				this.waiters++;

				try {
					for (;;) {
						try {
							this.wait(waitTime / 1000000, (int) (waitTime % 1000000));
						} catch (InterruptedException e) {
							throw e;
						}

						if (this.done) {
							return this.result;
						} else {
							waitTime = timeoutNanos - (System.nanoTime() - startTime);

							if (waitTime <= 0) {
								return this.result;
							}
						}
					}
				} finally {
					this.waiters--;
				}
			}
		} finally {
			Thread.currentThread().interrupt();
		}
	}

	@Override
	public boolean isCancelled() {

		return cause == CANCELLED;
	}

	@Override
	public boolean isDone() {
		return this.done;
	}

	public boolean setSuccess(V result) {

		synchronized (this) {
			if (this.done) {
				return false;
			}

			this.done = true;
			this.result = result;

			if (this.waiters > 0) {
				this.notifyAll();
			}
		}

		return true;
	}

	public boolean setFailure(Throwable e) {

		synchronized (this) {

			if (this.done) {
				return false;
			}

			this.cause = e;
			this.done = true;

			if (this.waiters > 0) {
				this.notifyAll();
			}
		}

		return true;
	}

	public Throwable getCause() {
		return this.cause;
	}
}
