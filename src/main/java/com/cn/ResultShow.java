package com.cn;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;


import javax.servlet.http.HttpServlet;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.sql.*;

public class ResultShow extends HttpServlet {
	
	public static List<BusinessModel> showBusinessModel(List<Float> similarity) {
		
		List<Float> list_sim = similarity;
		
		List<BusinessModel> list_bm = new ArrayList<BusinessModel>();
		
		//从大到小排序字符串，得到三个 和他的index
		for(int i = 0; i < 3; i++){
			
			float sim = Collections.max(list_sim);
			int index = list_sim.indexOf(sim);
			int id = index + 1;
			System.out.println("Max : " + sim + "\n" + "Id: " + id);
			
			//根据index依次得到相应的商业模式
			BusinessModel bm = getBusinessModel(id,sim);
			
			list_bm.add(bm);
			list_sim.set(index, 0.0f); //改成0后找下一个最大值

		}
		return list_bm;
	}
	
	// 根据id得到相应的商业模式信息
	public static BusinessModel getBusinessModel(int id, float sim) {

		Utils util = new Utils();
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		BusinessModel business_unit = new BusinessModel(id, null, null, sim);

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
			String sql = "SELECT name, description FROM business WHERE id = '" + id + "'";

			// Pre compilation
			ps = conn.prepareStatement(sql);

			// Execute
			rs = ps.executeQuery(sql);
			
			while (rs.next()) {
				int business_id = id;
				String name = rs.getString("name");			
				String description = rs.getString("description");
				business_unit = new BusinessModel(business_id,name,description,sim);
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
		return business_unit;
		
	}
	
}
