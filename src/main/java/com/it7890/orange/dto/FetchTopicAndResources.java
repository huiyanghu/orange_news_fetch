package com.it7890.orange.dto;

import java.io.Serializable;
import java.util.ArrayList;

import com.it7890.orange.entity.TopicFetch;
import com.it7890.orange.entity.TopicFetchResources;
import com.it7890.orange.entity.UserInfo;

public class FetchTopicAndResources implements Serializable {

	private static final long serialVersionUID = -5319226008331032659L;
	
	private TopicFetch topicFetch;
	private TopicFetchResources topicFetchResources;
	private UserInfo userInfo;
	
	private ArrayList<String> waitDownload;
	private ArrayList<String> alreadyDownload;
	private int executeCount;
	
	public TopicFetch getTopicFetch() {
		return topicFetch;
	}
	public void setTopicFetch(TopicFetch topicFetch) {
		this.topicFetch = topicFetch;
	}
	public TopicFetchResources getTopicFetchResources() {
		return topicFetchResources;
	}
	public void setTopicFetchResources(TopicFetchResources topicFetchResources) {
		this.topicFetchResources = topicFetchResources;
	}
	public ArrayList<String> getWaitDownload() {
		return waitDownload;
	}
	public void setWaitDownload(ArrayList<String> waitDownload) {
		this.waitDownload = waitDownload;
	}
	public ArrayList<String> getAlreadyDownload() {
		return alreadyDownload;
	}
	public void setAlreadyDownload(ArrayList<String> alreadyDownload) {
		this.alreadyDownload = alreadyDownload;
	}
	public int getExecuteCount() {
		return executeCount;
	}
	public void setExecuteCount(int executeCount) {
		this.executeCount = executeCount;
	}
	public UserInfo getUserInfo() {
		return userInfo;
	}
	public void setUserInfo(UserInfo userInfo) {
		this.userInfo = userInfo;
	}
}
