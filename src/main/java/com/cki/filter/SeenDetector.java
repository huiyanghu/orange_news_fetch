package com.cki.filter;

import java.io.Serializable;

public interface SeenDetector extends Serializable {

	public boolean seen(String url);

	public boolean accept(String url);

	public void clear();

	public int size();
}
