package com.it7890.orange.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

//@Entity
//@Table(name="topic_fetch")
public class TopicFetch implements Serializable {

	private static final long serialVersionUID = 3117424858872874857L;

	private Long id;
	
	private Long createTime;
    
    private Long updateTime;
    
    private Long hotTime;

    private String content;

    private String title;

    private String fl;

    private Long createId;
    
    private Long updaterId;

    private Integer status;

    private Integer type;

    private String sourceId;
    
    private String sourceUsername;
    
    private Long siteUrlConfigId;
    
    private Long siteUrlItemId;
    
    private String urlSalt;
    
    private int isSecret;	//0共有 1私有
    
    private Long fetchTime;
    
    @Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content == null ? null : content.trim();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title == null ? null : title.trim();
    }

    public Long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Long updateTime) {
        this.updateTime = updateTime;
    }

    public String getFl() {
        return fl;
    }

    public void setFl(String fl) {
        this.fl = fl == null ? null : fl.trim();
    }

    public Long getUpdaterId() {
        return updaterId;
    }

    public void setUpdaterId(Long updaterId) {
        this.updaterId = updaterId;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getSourceId() {
        return sourceId;
    }

    public void setSourceId(String sourceId) {
        this.sourceId = sourceId;
    }

    public Long getCreateId() {
        return createId;
    }

    public void setCreateId(Long createId) {
        this.createId = createId;
    }

    public Long getHotTime() {
        return hotTime;
    }

    public void setHotTime(Long hotTime) {
        this.hotTime = hotTime;
    }

    public String getSourceUsername() {
        return sourceUsername;
    }

    public void setSourceUsername(String sourceUsername) {
        this.sourceUsername = sourceUsername == null ? null : sourceUsername.trim();
    }

	public Long getSiteUrlConfigId() {
		return siteUrlConfigId;
	}

	public void setSiteUrlConfigId(Long siteUrlConfigId) {
		this.siteUrlConfigId = siteUrlConfigId;
	}

	public String getUrlSalt() {
		return urlSalt;
	}

	public void setUrlSalt(String urlSalt) {
		this.urlSalt = urlSalt;
	}
	public int getIsSecret() {
		return isSecret;
	}
	public void setIsSecret(int isSecret) {
		this.isSecret = isSecret;
	}
	public Long getSiteUrlItemId() {
		return siteUrlItemId;
	}
	public void setSiteUrlItemId(Long siteUrlItemId) {
		this.siteUrlItemId = siteUrlItemId;
	}
	public Long getFetchTime() {
		return fetchTime;
	}
	public void setFetchTime(Long fetchTime) {
		this.fetchTime = fetchTime;
	}
}