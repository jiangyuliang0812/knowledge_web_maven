package com.cn;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public class TriplesIdea extends HttpServlet {
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		getAndSetTriplesIdea();
	}

	public static void getAndSetTriplesIdea() {

		Utils util = new Utils();
		// 拿到Idea的关键字
		ArrayList<ArrayList<String>> data_keyword_idea = getKeywordIdea();
		// System.out.println(data_keyword_idea);

		// 用sparql语句并且存入数据库
		String entity = "";
		String entity_all_capital = "";
		String entity_first_capital = "";

		try {
			
			if (data_keyword_idea != null) {

				for (int i = 0; i < data_keyword_idea.size(); i++) {
					ArrayList<String> idea_unit = data_keyword_idea.get(i);
					String id_idea = idea_unit.get(0);
					String words = idea_unit.get(1);

					for (String j : words.split(",")) {
						ArrayList<String> triples = new ArrayList<String>();
						entity = j.replace(" ", "_").replace(".", "_");
						// 看看是不是首字母大写或者全部大写
						entity_first_capital = Utils.firstCapital(entity);
						entity_all_capital = Utils.allCapital(entity);
						
						triples = getTriples_idea(entity_first_capital, triples, id_idea);
						// 如果找到不到首字母大写的，再尝试全部大写
						if(triples.size() == 0){
							triples = getTriples_idea(entity_all_capital, triples, id_idea);
						}
						// System.out.println(triples);
					}
				}
			}
			
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	public static ArrayList<ArrayList<String>> getKeywordIdea() {

		Utils util = new Utils();
		Connection conn = null;
		PreparedStatement ps = null;
		ArrayList<ArrayList<String>> idea_list = new ArrayList<ArrayList<String>>();
		

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
			String sql = "SELECT idea_id,keyword_idea FROM ideas ORDER BY idea_id DESC LIMIT 1;";

			// Pre compilation
			ps = conn.prepareStatement(sql);

			// 执行查询语句 把查询到的数据存在 结果集 中 ResultSet
			java.sql.ResultSet rs = ps.executeQuery(sql);
			
			// 添加数据
			while (rs.next()) {
				// HashMap的读取
				String idea_id = String.valueOf(rs.getInt("idea_id"));
				String keyword_idea = rs.getString("keyword_idea");

				ArrayList<String> idea_unit = new ArrayList<String>();
				idea_unit.add(idea_id);
				idea_unit.add(keyword_idea);
				idea_list.add(idea_unit);

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

		return idea_list;
	}


	public static ArrayList<String> getTriples_idea(String entity, ArrayList<String> triples, String id_idea)
			throws UnsupportedEncodingException {
	
		// 头实体
		String queryString1 = "prefix dbr: <http://dbpedia.org/resource/>\n"
				+ "PREFIX dbo: <http://dbpedia.org/ontology/>\n"
				+ "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n"
				+ "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> \n" + "select ?y ?z where {dbr:" + entity + " ?y" + " ?z}";
		
		
		// 尾实体
		String queryString2 = "prefix dbr: <http://dbpedia.org/resource/>\n"
				+ "PREFIX dbo: <http://dbpedia.org/ontology/>\n"
				+ "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n"
				+ "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> \n" + "select ?x ?y where {?x " + " ?y  dbr:" + entity + "}";
		

		// 执行SPARQL语句
		QueryExecution qexec1 = Utils.getResult(queryString1);
		QueryExecution qexec2 = Utils.getResult(queryString2);

		// 新建一个动态数组
		ArrayList<String> triple = new ArrayList<String>();
		
		try {
			
			// 新建一个结果集 储存查询返回的结果
			ResultSet results1 = qexec1.execSelect();
	        
	        List predicatesList1 = Arrays.asList("type","subject","primaryTopic","seeAlso","specialist","industry","product","manufacturer","owners","founder","foundedBy","brands","service","areaServed","owner");
           
	        Map<String, Integer> counter1 = new HashMap<>();
	        // 变成arrayList才能做删除操作
	        List predicates1 = new ArrayList(predicatesList1);
            for(int i = 0; i < predicates1.size(); i ++){
                String predicate = (String) predicates1.get(i);
                counter1.put(predicate, 0);             
            }
	        
	        // 变成arrayList才能做删除操作
	       
	        
			// hasNext是判断是否还有下个元素 。
			for (; results1.hasNext();) {
				QuerySolution soln = results1.nextSolution();

				// System.out.println(entity);
				String s = entity;
				String p = Utils.getElement(soln.get("?y").toString());
				String o = Utils.getElement(soln.get("?z").toString());
				
				
				if (predicates1.contains(p) && (o != null && o.length() != 0)) {
					
					int i = counter1.get(p);
					i = i + 1;
					counter1.put(p, i);
					// System.out.println(counter.get(p));
					if(counter1.get(p) == 3){
					    for (Object item : predicates1) {
					        if (item.equals(p)) {	      
					        	predicates1.remove(item);
					            break;
					        }
					    }			   
					}	
					triple.add(s);
					triple.add(p);
					triple.add(o);
					// 因为要将三元组整体存到表中，所以从arrayList变成String
					String string_triple = String.valueOf(triple);
					string_triple = string_triple.replace(",", " ");

					// 检查是否是英文
					boolean b = Utils.tripleFilter(string_triple);
					if (b == true) {
						// 去重处理，如果不包含再添加
						if (!triples.contains(string_triple)) {
							triples.add(string_triple);
							insert_idea(string_triple, p, id_idea);
							// System.out.println(string_triple);
						}
					}
					triple = new ArrayList<String>();
				}
			}
			
			// 新建一个结果集 储存查询返回的结果
			ResultSet results2 = qexec2.execSelect();
			
	        List predicatesList2 = Arrays.asList("type","subject","primaryTopic","seeAlso","specialist","industry","product","manufacturer","owners","founder","foundedBy","brands","service","areaServed","owner");
	           
	        Map<String, Integer> counter2 = new HashMap<>();
	        // 变成arrayList才能做删除操作
	        List predicates2 = new ArrayList(predicatesList2);
            for(int i = 0; i < predicates2.size(); i ++){
                String predicate = (String) predicates2.get(i);
                counter2.put(predicate, 0);             
            }
            
			// hasNext是判断是否还有下个元素 。
			for (; results2.hasNext();) {
				QuerySolution soln = results2.nextSolution();

				// System.out.println(entity);
				String s = Utils.getElement(soln.get("?x").toString());
				String p = Utils.getElement(soln.get("?y").toString());
				String o = entity;
				
				
				if (predicates2.contains(p) && (s != null && s.length() != 0)) {
					
					int i = counter2.get(p);
					i = i + 1;
					counter2.put(p, i);
					// System.out.println(counter.get(p));
					if(counter2.get(p) == 3){
					    for (Object item : predicates2) {
					        if (item.equals(p)) {	      
					        	predicates2.remove(item);
					            break;
					        }
					    }			   
					}	
					triple.add(s);
					triple.add(p);
					triple.add(o);
					// 因为要将三元组整体存到表中，所以从arrayList变成String
					String string_triple = String.valueOf(triple);
					string_triple = string_triple.replace(",", " ");
						
					// 检查是否是英文
					boolean b = Utils.tripleFilter(string_triple);
					if (b == true) {
						// 去重处理，如果不包含再添加
						if (!triples.contains(string_triple)) {
							triples.add(string_triple);
							insert_idea(string_triple, p, id_idea);
							// System.out.println(string_triple);
						}
					}
					triple = new ArrayList<String>();
				}
			}

		} finally {
			qexec1.close();
			qexec2.close();
		}
		return triples;
	}


	public static void insert_idea(String triple, String p, String id_idea) {

		Utils util = new Utils();
		Connection conn = null;
		PreparedStatement ps = null;

		// 去掉大括号
		triple = triple.replace("[", "").replace("]", ",").replace("_", " ");

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
			String sql_insert = "insert into triplesidea(id_idea,predicate,triples_idea) values (%s,%s,%s)";

			// Pre compilation
			ps = conn.prepareStatement(sql_insert);

			// Fill placeholder
			String sql = String.format(sql_insert, id_idea, '"' + p + '"', '"' + triple + '"');

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
