package com.cn;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
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
	
	Utils util = new Utils();

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		req.setCharacterEncoding("utf-8");
		resp.setCharacterEncoding("utf-8");

		getTriples_idea();

		resp.sendRedirect("/knowledge_web_maven/FirstPage.jsp");

	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doGet(req, resp);
	}
	
	public static void getTriples_idea(){
		
		//�õ�Idea�Ĺؼ���
		ArrayList<ArrayList<String>> data_keyword_idea = getKeywordIdea();
		System.out.println(data_keyword_idea);
		
		//��sparql��䲢�Ҵ������ݿ�
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
						// �����ǲ�������ĸ��д����ȫ����д
						entity_all_capital = allCapital(entity);
						entity_first_capital = firstCapital(entity);
						triples = getTriples_idea(entity_all_capital, triples, id_idea);
						triples = getTriples_idea(entity_first_capital, triples, id_idea);
						//System.out.println(triples);
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
		//ArrayList<ArrayList<ArrayList<String>>> result = new ArrayList<ArrayList<ArrayList<String>>>();

		try {
			Class.forName("com.mysql.cj.jdbc.Driver");

			String host = util.getValue("host");
			String user = util.getValue("user");
			String password = util.getValue("password");
			String database = util.getValue("database");
			String url = String.format("jdbc:mysql://%s:3306/%s?connectTimeout=3000", host, database);
			
			conn = DriverManager.getConnection(url, user, password);
			
			Statement statement = conn.createStatement();
			
			// ��д��ѯ���
			String sql = "SELECT idea_id,keyword_idea FROM ideas;";
			
			// Pre compilation
			ps = conn.prepareStatement(sql);
			
			// ִ�в�ѯ��� �Ѳ�ѯ�������ݴ��� ����� �� ResultSet
			java.sql.ResultSet rs = ps.executeQuery(sql);
			
			// �������
			while (rs.next()) {
				// HashMap�Ķ�ȡ
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
	
	// ȫ����д
	public static String allCapital(String entity) {
		return entity.toUpperCase();
	}
	// ����ĸ��д
	public static String firstCapital(String entity) {

		char[] cs = entity.toCharArray();
		int a = cs[0];
		if (a >= 97 && a <= 122){
			cs[0] -= 32;
			String entity_upper = String.valueOf(cs);
			System.out.println(entity_upper);
			return entity_upper;
		}
		else{
			return entity;
		}
	}
	
	public static ArrayList<String> getTriples_idea(String entity, ArrayList<String> triples, String id_idea)
			throws UnsupportedEncodingException {
		// SPARQL���﷨
		String queryString = "prefix dbr: <http://dbpedia.org/resource/>\n"
				+ "PREFIX dbo: <http://dbpedia.org/ontology/>\n"
				+ "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n"
				+ "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> \n" 
				+ "select ?y ?z where {dbr:" + entity + " ?y" + " ?z}";
		//97 - 122
		
		// ִ��SPARQL���
		QueryExecution qexec = getResult(queryString);
		
		// �½�һ����̬����
		ArrayList<String> triple = new ArrayList<String>();

		try {
			// �½�һ������� �����ѯ���صĽ��
			ResultSet results = qexec.execSelect();
			// hasNext���ж��Ƿ����¸�Ԫ�� ��
			for (; results.hasNext();) {
				QuerySolution soln = results.nextSolution();
				
				// System.out.println(entity);
				String s = entity;
				String p = getElement(soln.get("?y").toString());
				String o = getElement(soln.get("?z").toString());
				List predicate_des = Arrays.asList("label","comment","product","industry");

				if (predicate_des.contains(p) && (o != null && o.length() != 0)){
					triple.add(s);
					triple.add(p);
					triple.add(o);
					
					// ��ΪҪ����Ԫ������浽���У����Դ�arrayList���Stringz
					String string_triple = String.valueOf(triple);
				
					// ����Ƿ���Ӣ��
					boolean b = isEnglish(string_triple);
					if (b == true) {
						// ȥ�ش�����������������
						if (!triples.contains(string_triple)) {
							triples.add(string_triple);
							//insert_idea(string_triple,p, id_idea);
							System.out.println(string_triple);
						}
					}
					triple = new ArrayList<String>();
					
				}
			}

		} finally {
			qexec.close();
		}
		return triples;
	}
	
	// ִ�з������
	public static QueryExecution getResult(String queryString) {
		Query query = QueryFactory.create(queryString);
		QueryExecution qexec = QueryExecutionFactory.sparqlService("https://dbpedia.org/sparql", query);
		return qexec;
	}
	
	// ���˽��
	public static String getElement(String res) {
		// �ѽ�β��enȥ��
		res = res.replace("@en", "");
		// ��/Ϊ�ָȡ���һ���ַ���
		String[] tem1 = res.split("/");
		String res1 = tem1[tem1.length - 1];
		// ��#Ϊ�ָȡ���һ���ַ���
		String[] tem2 = res1.split("#");
		String res2 = tem2[tem2.length - 1];
		return res2;
	}
	
	
	
	// Ӣ�Ĺ��˺��� ֻ���ж�
		public static boolean isEnglish(String string_triple) {

			// �ж�ֻ����@en
			if (string_triple.contains("@")) {

				if (string_triple.contains("@en")) {
					return true;
				} else {
					return false;
				}

			} else {

				int count = 0;
				char c[] = string_triple.toCharArray();
				for (int i = 0; i < c.length; i++) {
					if (!(c[i] >= 32 && c[i] <= 126)) {
						count += 1;
					}
				}
				// �жϰٷ�֮80����Ӣ����ĸ������Ӣ��
				float pro = (c.length - count) / c.length;
				if (pro > 0.8) {
					return true;
				} else {
					return false;
				}
			}

		}
		
		public static void insert_idea(String triple, String p, String id_idea) {

			Utils util = new Utils();
			Connection conn = null;
			PreparedStatement ps = null;

			// ȥ��������
			triple = triple.replace(",", "").replace("[", "").replace("]", ",").replace("_", " ");

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
