package com.it7890.orange.dto;

public class AsNameDTO {

	private int width;
	private String asName;
	
	public int getWidth() {
		return width;
	}
	public void setWidth(int width) {
		this.width = width;
	}
	public String getAsName() {
		return asName;
	}
	public void setAsName(String asName) {
		this.asName = asName;
	}
	
	public AsNameDTO(int width, String asName) {
		super();
		this.width = width;
		this.asName = asName;
	}
	public AsNameDTO() {
		super();
	}
}
