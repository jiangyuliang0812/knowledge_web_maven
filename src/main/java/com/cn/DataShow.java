package com.cn;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;


import javax.servlet.http.HttpServlet;


import java.util.ArrayList;
import java.util.List;
import java.sql.*;

import com.cn.Idea;

public class DataShow extends HttpServlet {

	public List showMySQl() {

		Utils util = new Utils();
		List<Idea> list = new ArrayList<Idea>();

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {

			// Connection
			Class.forName("com.mysql.cj.jdbc.Driver");

			String host = util.getValue("host");
			String user = util.getValue("user");
			String password = util.getValue("password");
			String database = util.getValue("database");
			String url = String.format("jdbc:mysql://%s:3306/%s?connectTimeout=3000", host, database);

			conn = DriverManager.getConnection(url, user, password);

			// Select
			String sql = "SELECT * FROM ideas ORDER BY idea_id DESC;";

			// Pre compilation
			ps = conn.prepareStatement(sql);

			// Execute
			rs = ps.executeQuery(sql);

			while (rs.next()) {
				int idea_id = rs.getInt("idea_id");
				String purpose = rs.getString("purpose");
				String mechanism = rs.getString("mechanism");
				Date createTime = rs.getDate("createTime");
				String keyword_purpose = rs.getString("keyword_purpose");
				String keyword_mechanism = rs.getString("keyword_mechanism");
				
				// show data in Console
				//System.out.printf("%s %s", purpose, mechanism + "\n");

				Idea idea = new Idea(idea_id, purpose, mechanism, createTime, keyword_purpose, keyword_mechanism);
				list.add(idea);
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {

			// Close Resources
			try {

				if (rs != null) {
					rs.close();
				}

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
		
		return list;

	}
}
