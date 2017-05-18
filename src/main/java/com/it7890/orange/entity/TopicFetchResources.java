package com.it7890.orange.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

//@Entity
//@Table(name="topic_fetch_resources")
public class TopicFetchResources implements Serializable {
    
	private static final long serialVersionUID = -2972175090116746767L;

	private Long id;
	
    private Long topicId;

    private String imgThumbnailUrl;

    private String imgMidUrl;

    private String imgUrl;

    private String videoUrl;

    private String videoThumbnailUrl;

    private String audioUrl;

    private Integer width;

    private Integer height;

    private Long createTime;
    
    private int playDuration;
    
    private int playTimes;
    
    private String topicSourceId;
    
    private int domainId;

    @Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
    public Long getTopicId() {
        return topicId;
    }

    public void setTopicId(Long topicId) {
        this.topicId = topicId;
    }

    public String getImgThumbnailUrl() {
        return imgThumbnailUrl;
    }

    public void setImgThumbnailUrl(String imgThumbnailUrl) {
        this.imgThumbnailUrl = imgThumbnailUrl == null ? null : imgThumbnailUrl.trim();
    }

    public String getImgMidUrl() {
        return imgMidUrl;
    }

    public void setImgMidUrl(String imgMidUrl) {
        this.imgMidUrl = imgMidUrl == null ? null : imgMidUrl.trim();
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl == null ? null : imgUrl.trim();
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl == null ? null : videoUrl.trim();
    }

    public String getVideoThumbnailUrl() {
        return videoThumbnailUrl;
    }

    public void setVideoThumbnailUrl(String videoThumbnailUrl) {
        this.videoThumbnailUrl = videoThumbnailUrl == null ? null : videoThumbnailUrl.trim();
    }

    public String getAudioUrl() {
        return audioUrl;
    }

    public void setAudioUrl(String audioUrl) {
        this.audioUrl = audioUrl == null ? null : audioUrl.trim();
    }

    public Integer getWidth() {
        return width;
    }

    public void setWidth(Integer width) {
        this.width = width;
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

	public int getPlayDuration() {
		return playDuration;
	}

	public void setPlayDuration(int playDuration) {
		this.playDuration = playDuration;
	}

	public int getPlayTimes() {
		return playTimes;
	}

	public void setPlayTimes(int playTimes) {
		this.playTimes = playTimes;
	}

	public String getTopicSourceId() {
		return topicSourceId;
	}

	public void setTopicSourceId(String topicSourceId) {
		this.topicSourceId = topicSourceId;
	}
	public int getDomainId() {
		return domainId;
	}
	public void setDomainId(int domainId) {
		this.domainId = domainId;
	}
}