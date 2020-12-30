package com.cn;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
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
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public class sparqlQuery extends HttpServlet {
	Utils util = new Utils();

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		req.setCharacterEncoding("utf-8");
		resp.setCharacterEncoding("utf-8");

		getTriples();

		resp.sendRedirect("/knowledge_web_maven/FirstPage.jsp");

	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doGet(req, resp);
	}

	public void getTriples() {

		ArrayList<ArrayList<ArrayList<String>>> all_triples = new ArrayList<ArrayList<ArrayList<String>>>();

		// �ó����ݿ������Ϣ�浽data
		ArrayList<ArrayList<String>> data = selectMySQl();
		ArrayList<String> data_purpose = data.get(0);
		// ArrayList<String> data_mechanism = data.get(1);
		ArrayList<String> idea_ids = data.get(2);

		String entity = "";
		String entity1 = "";
		String entity2 = "";

		try {
			if (data_purpose != null) {
				
				for (int index = 0; index < data_purpose.size(); index++) {
					String i = data_purpose.get(index);
					String idea_id = idea_ids.get(index);
					
					// �Զ���Ϊ�ָ����ţ����ָ��ĵ��ʴ���j
					for (String j : i.split(",")) {

						ArrayList<ArrayList<String>> triples = new ArrayList<ArrayList<String>>();
						
						entity = j.replace(" ", "_").replace(".", "_");
						//�����ǲ�������ĸ��д����ȫ����д
						entity1 = wordTrans1(entity);
						entity2 = wordTrans2(entity);
						triples = getTriples(entity1, triples, idea_id);
						triples = getTriples(entity2, triples, idea_id);
						
						all_triples.add(triples);
						System.out.println(triples);

					}
				}
			}
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// ��������ǰѹؼ�����Ϊͷʵ��
	public ArrayList<ArrayList<String>> getTriples(String entity, ArrayList<ArrayList<String>> triples, String idea_id)
			throws UnsupportedEncodingException {

		// SPARQL���﷨
		String queryString = 

				"prefix dbr: <http://dbpedia.org/resource/>\n" +
				//"select ?y ?z where {dbr:" + entity + " ?y ?z FILTER (langMatches(lang(?z),'en'))}";
				"select ?y ?z where {dbr:" + entity + " ?y ?z }";
		

		// ִ��SPARQL���
		QueryExecution qexec = getResult(queryString);

		// �½�һ����̬����
		ArrayList<String> triple = new ArrayList<String>();

		try {
			// �½�һ������� �����ѯ���صĽ��
			ResultSet results = qexec.execSelect();
			// �ο���sparql��ѯrdf���� �� hasNext���ж��Ƿ����¸�Ԫ�� ��
			for (; results.hasNext();) {
				QuerySolution soln = results.nextSolution();
				System.out.println(entity);
				triple.add(entity);
				triple.add(getElement(soln.get("?y").toString()));
				triple.add(getElement(soln.get("?z").toString()));
				insertMySQl(triple, idea_id);
				triples.add(triple);
				triple = new ArrayList<String>();
			}

		} finally {
			qexec.close();
		}
		return triples;
	}


	// ִ�з������
	public QueryExecution getResult(String queryString) {
		Query query = QueryFactory.create(queryString);
		QueryExecution qexec = QueryExecutionFactory.sparqlService("http://dbpedia.org/sparql", query);
		return qexec;
	}

	// ��ѯ���Ķ�����������ַ��/��Ķ���������Ҫ�ģ���/Ϊ�ָȡ���һ���ַ���
	public String getElement(String res) {
		String[] tem = res.split("/");
		return tem[tem.length - 1];
		
	}
	

	// ȫ����д
	public String wordTrans1(String entity) {
		return entity.toUpperCase();
	}

	// ����ĸ��д
	public String wordTrans2(String entity) {
		char[] cs = entity.toCharArray();
		cs[0] -= 32;
		String entity1 = String.valueOf(cs);
		return entity1;
	}

	public void insertMySQl(List<String> triple, String idea_id) {

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
			String sql_insert = "insert into triples(idea_id,s,p,o) values (%s,%s,%s,%s)";

			// Pre compilation
			ps = conn.prepareStatement(sql_insert);

			// Fill placeholder
			String sql = String.format(sql_insert, idea_id, '"' + triple.get(0) + '"', '"' + triple.get(1) + '"',
					'"' + triple.get(2) + '"');
			
			System.out.println(sql);
			// execute
			ps.executeUpdate(sql);

			System.out.println("Successfully Inserted!");

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

	// ��ȡ���ݿ�������� ���������µ�һ������
	public ArrayList<ArrayList<String>> selectMySQl() {

		Connection conn = null;
		PreparedStatement ps = null;

		try {
			Class.forName("com.mysql.cj.jdbc.Driver");

			String host = "localhost";
			String user = "root";
			String password = "root";
			String database = "webdatabase";

			// String host = util.getValue("host");
			// String user = util.getValue("user");
			// String password = util.getValue("password");
			// String database = util.getValue("database");
			String url = String.format("jdbc:mysql://%s:3306/%s?connectTimeout=3000", host, database);

			conn = DriverManager.getConnection(url, user, password);

			Statement statement = conn.createStatement();

			// ��д��ѯ���
			String sql = "SELECT idea_id,Keyword_purpose,Keyword_mechanism FROM ideas ORDER BY idea_id DESC LIMIT 1;";

			// Pre compilation
			ps = conn.prepareStatement(sql);

			// ִ�в�ѯ��� �Ѳ�ѯ�������ݴ��� ����� �� ResultSet
			java.sql.ResultSet rs = ps.executeQuery(sql);

			ArrayList idea_ids = new ArrayList();
			ArrayList data_purpose = new ArrayList();
			ArrayList data_mechanism = new ArrayList();

			// �������
			while (rs.next()) {
				//HashMap�Ķ�ȡ
				idea_ids.add(String.valueOf(rs.getInt("idea_id")));
				data_purpose.add(rs.getString("Keyword_purpose"));
				data_mechanism.add(rs.getString("Keyword_mechanism"));
			}
			rs.close();
			
			ArrayList<ArrayList<String>> result = new ArrayList();
			result.add(data_purpose);
			result.add(data_mechanism);
			result.add(idea_ids);

			return result;

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

		return null;

	}

}
