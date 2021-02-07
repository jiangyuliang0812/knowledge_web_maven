package com.cn;

import java.sql.*;

/**
 * ʵ��������� ��JAVAWeb�У�ʵ���������ĳһ���࣬�൱�������ݿ����һ�ű�һ���������ĳ���ֶ��൱�ڱ������
 * ��ʵ������getter��setter������getter��ֻ����setter��д��
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
