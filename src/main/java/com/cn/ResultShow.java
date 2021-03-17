package com.cn;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.sql.PreparedStatement;
import java.sql.ResultSet;


import javax.servlet.http.HttpServlet;


import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.io.IOException;
import java.sql.*;

public class ResultShow extends HttpServlet {
	
	public static void main(String[] args) throws IOException {
		
		Map<String, Float> similarity = new HashMap<>();
		similarity.put("11",(float) 0.36835);
		similarity.put("1",(float) 0.36984998);
		similarity.put("2",(float) 0.38615);
		similarity.put("3",(float) 0.30935);
		similarity.put("4",(float) 0.37685);
		similarity.put("5",(float) 0.3979);
		similarity.put("6",(float) 0.59625);
		similarity.put("7",(float) 0.18505);
		similarity.put("8",(float) 0.53435004);
		similarity.put("9",(float) 0.39415);
		similarity.put("10",(float) 0.43225);
		showBusinessModel(similarity);

	}
	
	
	public static List<BusinessModel> showBusinessModel(Map<String, Float> similarity) {
		
		List<String> ids = new ArrayList<>();
		List<Float> similarities = new ArrayList<>();
		
		for(Map.Entry<String, Float> sim : similarity.entrySet()){
			ids.add(sim.getKey());
			similarities.add(sim.getValue());		
		}
		
		List<BusinessModel> list_bm = new ArrayList<BusinessModel>();
		
		// 对相似度处理，以便显示在UI上
		for(int i = 0; i < similarities.size(); i++){
			float tem_sim = similarities.get(i);
			tem_sim = tem_sim * 100;
			DecimalFormat df = new DecimalFormat("#.##");
			tem_sim =  Float.valueOf(df.format(tem_sim));
			similarities.set(i,tem_sim);
		}
		
		// 从大到小排序字符串，得到三个 和他的index
		for(int i = 0; i < 3; i++){
			
			float sim = Collections.max(similarities);
			int index = similarities.indexOf(sim);
			int id = Integer.valueOf(ids.get(index)).intValue();
			System.out.println("Max : " + sim + "\n" + "Id: " + id);
			
			//根据index依次得到相应的商业模式
			BusinessModel bm = getBusinessModel(id,sim);
			
			list_bm.add(bm);
			similarities.set(index, 0.0f); //改成0后找下一个最大值

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
			String sql = "SELECT name, description FROM businessmodel WHERE id = '" + id + "'";

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
