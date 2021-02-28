package com.cn;

import java.sql.*;

public class BusinessModel {
	
	private int id;
	private String name;
	private String description;
	private float similarity;
	
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	public float getSimilarity() {
		return similarity;
	}
	public void setSimilarity(float similarity) {
		this.similarity = similarity;
	}
	
	
	
	public BusinessModel(int id, String name, String description, float similarity) {
		super();
		this.id = id;
		this.name = name;
		this.description = description;
		this.similarity = similarity;
	}


}
