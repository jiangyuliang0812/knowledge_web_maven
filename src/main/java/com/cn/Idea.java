package com.cn;

import java.sql.*;

/**
 * 实体类的作用 在JAVAWeb中，实体类里面的某一个类，相当于是数据库里的一张表，一个类里面的某个字段相当于表的列名
 * 在实体里有getter和setter方法，getter是只读，setter是写入
 */

public class Idea {
	
	private int idea_id;
	private String purpose;
	private String mechanism;
	private Date createTime;
	private String keyword_purpose;
	private String keyword_mechanism;
	
	
	public int getIdea_id() {
		return idea_id;
	}

	public void setIdea_id(int idea_id) {
		this.idea_id = idea_id;
	}


	public String getPurpose() {
		return purpose;
	}

	public void setPurpose(String purpose) {
		this.purpose = purpose;
	}

	public String getMechanism() {
		return mechanism;
	}

	public void setMechanism(String mechanism) {
		this.mechanism = mechanism;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	
	public String getKeyword_purpose() {
		return keyword_purpose;
	}

	public void setKeyword_purpose(String keyword_purpose) {
		this.keyword_purpose = keyword_purpose;
	}
	
	public String getKeyword_mechanism() {
		return keyword_mechanism;
	}

	public void setKeyword_mechanism(String keyword_mechanism) {
		this.keyword_mechanism = keyword_mechanism;
	}
	
	
	
	public Idea(int idea_id, String purpose, String mechanism, Date createTime, String keyword_purpose, String keyword_mechanism) {
		super();
		this.idea_id = idea_id;
		this.purpose = purpose;
		this.mechanism = mechanism;
		this.createTime = createTime;
		this.keyword_purpose = keyword_purpose;
		this.keyword_mechanism = keyword_mechanism;
	}

}
