package com.cn;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class Evaluation extends HttpServlet {

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		req.setCharacterEncoding("utf-8");
		resp.setCharacterEncoding("utf-8");
		
		List list = new ArrayList<>();
		String name = "";
		String yes = "Yes";
		String no = "No";
		
		// 获得所有前端传来的参数
		Enumeration paramNames = req.getParameterNames();
		// 遍历所有参数
		while (paramNames.hasMoreElements()) {
			String paramName = (String) paramNames.nextElement();
			String paramValue = req.getParameter(paramName);
			if((paramValue.equals(yes))||(paramValue.equals(no))){
				list.add(paramValue);	
			}else{
				name = paramValue;
			}
		}
		
		String evaluation = String.join(",",list);
		System.out.println(name);
		System.out.println(evaluation);
		
		// 获得idea的id
		String idea_id = getIdeaId();
		
		// 存入数据库 name,evaluation,idea_id
		insertResult(name, evaluation, idea_id);
		
		resp.sendRedirect("/knowledge_web_maven/ThirdPage.jsp");
		
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doGet(req, resp);
	}
	
	public static String getIdeaId() {

		Utils util = new Utils();
		Connection conn = null;
		PreparedStatement ps = null;
		String idea_id = "";
		

		try {
			Class.forName("com.mysql.cj.jdbc.Driver");

			String host = util.getValue("host");
			String user = util.getValue("user");
			String password = util.getValue("password");
			String database = util.getValue("database");
			String url = String.format("jdbc:mysql://%s:3306/%s?connectTimeout=3000", host, database);

			conn = DriverManager.getConnection(url, user, password);

			Statement statement = conn.createStatement();
			
			// 编写查询语句
			String sql = "SELECT idea_id FROM ideas ORDER BY idea_id DESC LIMIT 1;";

			// Pre compilation
			ps = conn.prepareStatement(sql);

			// 执行查询语句 把查询到的数据存在 结果集 中 ResultSet
			java.sql.ResultSet rs = ps.executeQuery(sql);
			
			// 添加数据
			while (rs.next()) {
				idea_id = String.valueOf(rs.getInt("idea_id"));
			}

			rs.close();
			

		} catch (Exception e) {
			e.printStackTrace();
		} finally {

			// Close Resources
			try {

				if (ps != null) {
					ps.close();
				}

				if (conn != null) {
					conn.close();
				}

			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		return idea_id;
	}
	
	
	public static void insertResult(String name, String evaluation, String idea_id) {

		Utils util = new Utils();
		Connection conn = null;
		PreparedStatement ps = null;

		try {

			// Connect to the database
			Class.forName("com.mysql.cj.jdbc.Driver");

			String host = util.getValue("host");
			String user = util.getValue("user");
			String password = util.getValue("password");
			String database = util.getValue("database");
			String url = String.format("jdbc:mysql://%s:3306/%s?connectTimeout=3000", host, database);

			conn = DriverManager.getConnection(url, user, password);

			// Write a SQL statement,store it in String, then pre-compile, and
			// then execute
			String sql_insert = "insert into result(name,evaluation,idea_id) values (%s,%s,%s)";

			// Pre compilation
			ps = conn.prepareStatement(sql_insert);

			// Fill placeholder
			String sql = String.format(sql_insert, '"' + name + '"', '"' + evaluation + '"', '"' + idea_id + '"');

			// execute
			ps.executeUpdate(sql);

		} catch (Exception e) {
			e.printStackTrace();

		} finally {

			// Close Resources
			try {

				if (ps != null) {
					ps.close();
				}

				if (conn != null) {
					conn.close();
				}

			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

}
