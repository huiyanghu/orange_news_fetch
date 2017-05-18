package com.it7890.orange.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

//@Entity
//@Table(name="topic")
public class Topic implements Serializable {
	
	private static final long serialVersionUID = -1917194473483997255L;

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
    
    private Long topicFetchId;
    
    private int isSecret;	//0共有 1私有

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
        this.fl = fl;
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

	public Long getTopicFetchId() {
		return topicFetchId;
	}

	public void setTopicFetchId(Long topicFetchId) {
		this.topicFetchId = topicFetchId;
	}
	public int getIsSecret() {
		return isSecret;
	}
	public void setIsSecret(int isSecret) {
		this.isSecret = isSecret;
	}
}