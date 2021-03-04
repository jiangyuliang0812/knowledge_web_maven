package com.cn;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Compare {
	
	public static void main(String[] args) throws IOException {
		
		getSimilarity();
		
	}

	public static Map<String, Float> getSimilarity() throws IOException {
		
		// 得到所有BusinessModel
		Map<String, Map<String, List>> result_bm = getBusinessModel();
		
		
		// 得到最新一条Idea
		Map<String, List> result_idea = getIdea();
		// System.out.println(result_idea);
		
		// 对比一条Idea和所有的BusinessModel
		Map<String, List> result_sim = new HashMap<>();
		
		// 这个for循环得到idea的结果集
		for(Map.Entry<String, List> idea : result_idea.entrySet()){
			
		    String predicate_idea = idea.getKey();
		    List Triples_idea = idea.getValue();
		    
		    String text_idea = String.join(" ",Triples_idea);
		    
		 	// 这个for循环得到了所有商业模式的结果集
		    for (Map.Entry<String, Map<String, List>> bm : result_bm.entrySet()){
		    	String bm_Id = bm.getKey();
		    	boolean flag = true;
		    	
		    	// 这个for得到了一个单位的商业模式
		    	for(Map.Entry<String, List> bm_unit : bm.getValue().entrySet()){
		    		String predicate_bm = bm_unit.getKey();
				    List Triples_bm = bm_unit.getValue();
				    
				    if (predicate_idea.equals(predicate_bm)){
				    	
				    	String text_bm = String.join(" ",Triples_bm).replace(",,", ",");
				    	System.out.println(text_idea);
				    	System.out.println(text_bm);
				    	float sim = SimCacu.Dandelion(text_idea,text_bm);
				    	flag = false;
				    	
				    	System.out.println(predicate_idea);
				    	System.out.println(bm_Id);
				    	System.out.println(sim);
				    	
				    	if (result_sim.containsKey(bm_Id)) {
							List tem_list = result_sim.get(bm_Id);	
							tem_list.add(sim);
							result_sim.put(bm_Id, tem_list);	
						} else {
							List tem_list = new ArrayList();
							tem_list.add(sim);
							result_sim.put(bm_Id, tem_list);
						}
				    	break;
				    }
		    	}
		    	if(flag){
		    		
		    		System.out.println(predicate_idea);
			    	System.out.println(bm_Id);
			    	System.out.println(0);
			    	
			    }
		    }
		    
		}
		
		// 处理相似度结果
		System.out.println(result_sim);
		
		// 这个for循环为了找到result里最长的list 先遍历map 然后得到最长的list
		int size = 0;
		for(Map.Entry<String, List> result_similarity : result_sim.entrySet()){
			List simList = result_similarity.getValue();
			
			if(size <= simList.size()){
				size = simList.size();
			}		
		}
		
		float sum = 0;
		
		Map<String, Float> sim_final = new HashMap<>();
		for(Map.Entry<String, List> result_similarity : result_sim.entrySet()){
			
			String business_id = result_similarity.getKey();
			List similarity = result_similarity.getValue();
			
			for(int i = 0; i < similarity.size(); i++){
				sum += (float)similarity.get(i); 
			}
			
			sum = sum/size;
			sim_final.put(business_id, sum);
			sum = 0;	 

		}
		
		System.out.println(sim_final);
		return sim_final;
		
	}
	
	public static Map<String, Map<String, List>> getBusinessModel() {

		Utils util = new Utils();
		Connection conn = null;
		PreparedStatement ps1 = null;
		PreparedStatement ps2 = null;
		PreparedStatement ps3 = null;
		PreparedStatement ps4 = null;
		PreparedStatement ps5 = null;
		
		Map<String, Map<String, List>> result = new HashMap<>();

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
			String sql_com = "SELECT triples_company,predicate,id_business FROM triplesbecom;";
			String sql_des = "SELECT triples_description,predicate,id_business FROM triplesbedes;";
			String sql_sell = "SELECT triples_sell,predicate,id_business FROM triplesbesell;";
			String sql_advan = "SELECT triples_advantage,predicate,id_business FROM triplesbeadvan;";
			String sql_money = "SELECT triples_money,predicate,id_business FROM triplesbemoney;";

			// Pre compilation
			ps1 = conn.prepareStatement(sql_com);
			ps2 = conn.prepareStatement(sql_des);
			ps3 = conn.prepareStatement(sql_sell);
			ps4 = conn.prepareStatement(sql_advan);
			ps5 = conn.prepareStatement(sql_money);
			

			// 执行查询语句 把查询到的数据存在 结果集 中 ResultSet
			java.sql.ResultSet rs1 = ps1.executeQuery(sql_com);
			java.sql.ResultSet rs2 = ps2.executeQuery(sql_des);
			java.sql.ResultSet rs3 = ps3.executeQuery(sql_sell);
			java.sql.ResultSet rs4 = ps4.executeQuery(sql_advan);
			java.sql.ResultSet rs5 = ps5.executeQuery(sql_money);
			
			// 添加数据
			while (rs1.next()) {
				// HashMap的读取 通过get拿到rs结果集里的数据
				String id_business = String.valueOf(rs1.getInt("id_business"));
				String predicate = rs1.getString("predicate");
				String triples_company = rs1.getString("triples_company");

				// 利用containsKey()方法来判断是否存在某个键
				if (result.containsKey(id_business)) {
					Map<String, List> tem_map = result.get(id_business);
					if (tem_map.containsKey(predicate)) {
						List tem_list = tem_map.get(predicate);
						// System.out.println(tem_list);
						// Map数据类型就能直接添加到找到的list里，不需要再put
						tem_list.add(triples_company);
						
					} else {
						List tem_list = new ArrayList();
						tem_list.add(triples_company);
						tem_map.put(predicate, tem_list);
						
					}
				} else {
					Map<String, List> tem_map = new HashMap<>();
					List tem_list = new ArrayList();
					tem_list.add(triples_company);
					tem_map.put(predicate, tem_list);
					result.put(id_business, tem_map);
				}

			}
			
			// 添加数据
			while (rs2.next()) {
				// HashMap的读取 通过get拿到rs结果集里的数据
				String id_business = String.valueOf(rs2.getInt("id_business"));
				String predicate = rs2.getString("predicate");
				String triples_description = rs2.getString("triples_description");

				// 利用containsKey()方法来判断是否存在某个键
				if (result.containsKey(id_business)) {
					Map<String, List> tem_map = result.get(id_business);
					if (tem_map.containsKey(predicate)) {
						List tem_list = tem_map.get(predicate);
						// System.out.println(tem_list);
						tem_list.add(triples_description);
						
					} else {
						List tem_list = new ArrayList();
						tem_list.add(triples_description);
						tem_map.put(predicate, tem_list);
					
					}
				} else {
					Map<String, List> tem_map = new HashMap<>();
					List tem_list = new ArrayList();
					tem_list.add(triples_description);
					tem_map.put(predicate, tem_list);
					result.put(id_business, tem_map);
				}

			}
			
			// 添加数据
			while (rs3.next()) {
				// HashMap的读取 通过get拿到rs结果集里的数据
				String id_business = String.valueOf(rs3.getInt("id_business"));
				String predicate = rs3.getString("predicate");
				String triples_sell = rs3.getString("triples_sell");

				// 利用containsKey()方法来判断是否存在某个键
				if (result.containsKey(id_business)) {
					Map<String, List> tem_map = result.get(id_business);
					if (tem_map.containsKey(predicate)) {
						List tem_list = tem_map.get(predicate);
						// System.out.println(tem_list);
						tem_list.add(triples_sell);
						
					} else {
						List tem_list = new ArrayList();
						tem_list.add(triples_sell);
						tem_map.put(predicate, tem_list);
						
					}
				} else {
					Map<String, List> tem_map = new HashMap<>();
					List tem_list = new ArrayList();
					tem_list.add(triples_sell);
					tem_map.put(predicate, tem_list);
					result.put(id_business, tem_map);
				}

			}
			
			// 添加数据
			while (rs4.next()) {
				// HashMap的读取 通过get拿到rs结果集里的数据
				String id_business = String.valueOf(rs4.getInt("id_business"));
				String predicate = rs4.getString("predicate");
				String triples_advantage = rs4.getString("triples_advantage");

				// 利用containsKey()方法来判断是否存在某个键
				if (result.containsKey(id_business)) {
					Map<String, List> tem_map = result.get(id_business);
					if (tem_map.containsKey(predicate)) {
						List tem_list = tem_map.get(predicate);
						// System.out.println(tem_list);
						tem_list.add(triples_advantage);
						
						
					} else {
						List tem_list = new ArrayList();
						tem_list.add(triples_advantage);
						tem_map.put(predicate, tem_list);
					}
				} else {
					Map<String, List> tem_map = new HashMap<>();
					List tem_list = new ArrayList();
					tem_list.add(triples_advantage);
					tem_map.put(predicate, tem_list);
					result.put(id_business, tem_map);
				}

			}
			
			// 添加数据
			while (rs5.next()) {
				// HashMap的读取 通过get拿到rs结果集里的数据
				String id_business = String.valueOf(rs5.getInt("id_business"));
				String predicate = rs5.getString("predicate");
				String triples_money = rs5.getString("triples_money");

				// 利用containsKey()方法来判断是否存在某个键
				if (result.containsKey(id_business)) {
					Map<String, List> tem_map = result.get(id_business);
					if (tem_map.containsKey(predicate)) {
						List tem_list = tem_map.get(predicate);
						// System.out.println(tem_list);
						tem_list.add(triples_money);
						
					} else {
						List tem_list = new ArrayList();
						tem_list.add(triples_money);
						tem_map.put(predicate, tem_list);
						
					}
				} else {
					Map<String, List> tem_map = new HashMap<>();
					List tem_list = new ArrayList();
					tem_list.add(triples_money);
					tem_map.put(predicate, tem_list);
					result.put(id_business, tem_map);
				}

			}
			
			rs1.close();
			rs2.close();
			rs3.close();
			rs4.close();
			rs5.close();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {

			// Close Resources
			try {

				if (ps1 != null) {
					ps1.close();
				}
				if (ps2 != null) {
					ps2.close();
				}
				if (ps3 != null) {
					ps3.close();
				}
				if (ps4 != null) {
					ps4.close();
				}
				if (ps5 != null) {
					ps5.close();
				}
				if (conn != null) {
					conn.close();
				}

			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return result;

	}
	
	
	public static Map<String, List> getIdea() {

		Utils util = new Utils();
		Connection conn = null;
		PreparedStatement ps = null;
		Map<String, List> result = new HashMap<>();

		try {
			Class.forName("com.mysql.cj.jdbc.Driver");

			String host = util.getValue("host");
			String user = util.getValue("user");
			String password = util.getValue("password");
			String database = util.getValue("database");
			String url = String.format("jdbc:mysql://%s:3306/%s?connectTimeout=3000", host, database);

			conn = DriverManager.getConnection(url, user, password);

			Statement statement = conn.createStatement();
			

			// 编写查询语句 得到最大index的idea
			String sql = "SELECT triples_idea,predicate,id_idea FROM triplesidea where id_idea = (SELECT max(id_idea) FROM triplesidea);";

			// Pre compilation
			ps = conn.prepareStatement(sql);

			// 执行查询语句 把查询到的数据存在 结果集 中 ResultSet
			java.sql.ResultSet rs = ps.executeQuery(sql);

			// 添加数据
			while (rs.next()) {
				// HashMap的读取 通过get拿到rs结果集里的数据
				String id_idea = String.valueOf(rs.getInt("id_idea"));
				String predicate = rs.getString("predicate");
				String triples_idea = rs.getString("triples_idea");

				// 利用containsKey()方法来判断是否存在某个键
				if (result.containsKey(predicate)) {
					List tem_list = result.get(predicate);
					// System.out.println(tem_list);
					tem_list.add(triples_idea);
					
				} else {
					List tem_list = new ArrayList();
					tem_list.add(triples_idea);
					result.put(predicate, tem_list);
				}
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
		return result;

	}
	
}
