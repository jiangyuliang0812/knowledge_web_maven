package com.cn;

import java.sql.*;

/**
 * 实体类的作用 在JAVAWeb中，实体类里面的某一个类，相当于是数据库里的一张表，一个类里面的某个字段相当于表的列名
 * 在实体里有getter和setter方法，getter是只读，setter是写入
 */

public class Idea {
	
	private int idea_id;
	private String idea;
	private Date createTime;
	private String keywod_idea;
	
	
	public int getIdea_id() {
		return idea_id;
	}

	public void setIdea_id(int idea_id) {
		this.idea_id = idea_id;
	}


	public String getIdea() {
		return idea;
	}

	public void setIdea(String idea) {
		this.idea = idea;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	
	public String getKeyword_idea() {
		return keywod_idea;
	}

	public void setKeyword_idea(String keywod_idea) {
		this.keywod_idea = keywod_idea;
	}

	
	public Idea(int idea_id, String idea, Date createTime, String keyword_idea) {
		super();
		this.idea_id = idea_id;
		this.idea = idea;
		this.createTime = createTime;
		this.keywod_idea = keyword_idea;
	}

}
