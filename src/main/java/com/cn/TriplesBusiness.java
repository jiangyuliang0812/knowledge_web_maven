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

public class TriplesBusiness {

	public static void main(String[] args) {

		// 拿到商业模式数据
		ArrayList<ArrayList<ArrayList<String>>> dataBusinessModel = getBusinessModel();
		// System.out.println(dataBusinessModel);
		ArrayList<ArrayList<String>> data_description = dataBusinessModel.get(0);
		ArrayList<ArrayList<String>> data_company = dataBusinessModel.get(1);
		// System.out.println(data_description);
		// System.out.println(data_company);

		// 用sparql来得到三元组 并存入

		// Description的谓词 dbo:abstract rdfs:comment dbo:type rdfs:label
		String entity = "";
		String entity_all_capital = "";
		String entity_first_capital = "";

		try {

			if (data_description != null) {
				for (int i = 0; i < data_description.size(); i++) {
					ArrayList<String> description_unit = data_description.get(i);
					String id_business = description_unit.get(0);
					String words = description_unit.get(1);

					for (String j : words.split(",")) {
						ArrayList<String> triples = new ArrayList<String>();
						entity = j.replace(" ", "_").replace(".", "_");
						// 看看是不是首字母大写或者全部大写
						entity_all_capital = allCapital(entity);
						entity_first_capital = firstCapital(entity);
						triples = getTriples_des(entity_all_capital, triples, id_business);
						triples = getTriples_des(entity_first_capital, triples, id_business);
						// System.out.println(triples);
					}
				}
			}

			// Company的谓词 dbo:product dbo:industry dbp:services dbo:service
			if (data_company != null) {
				for (int i = 0; i < data_company.size(); i++) {
					ArrayList<String> company_unit = data_company.get(i);
					String id_business = company_unit.get(0);
					String words = company_unit.get(1);

					for (String k : words.split(",")) {

						ArrayList<String> triples = new ArrayList<String>();
						entity = k.replace(" ", "_").replace(".", "_");
						// 看看是不是首字母大写或者全部大写
						entity_all_capital = allCapital(entity);
						entity_first_capital = firstCapital(entity);
						triples = getTriples_com(entity_all_capital, triples, id_business);
						triples = getTriples_com(entity_first_capital, triples, id_business);
						// System.out.println(triples);
					}
				}
			}

		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static ArrayList<ArrayList<ArrayList<String>>> getBusinessModel() {

		Utils util = new Utils();
		Connection conn = null;
		PreparedStatement ps = null;
		ArrayList<ArrayList<String>> description_list = new ArrayList<ArrayList<String>>();
		ArrayList<ArrayList<String>> company_list = new ArrayList<ArrayList<String>>();
		ArrayList<ArrayList<ArrayList<String>>> result = new ArrayList<ArrayList<ArrayList<String>>>();

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
			String sql = "SELECT id,name,keyword_description,company FROM business;";

			// Pre compilation
			ps = conn.prepareStatement(sql);

			// 执行查询语句 把查询到的数据存在 结果集 中 ResultSet
			java.sql.ResultSet rs = ps.executeQuery(sql);

			// 添加数据
			while (rs.next()) {
				// HashMap的读取
				String id = rs.getString("id");
				// String id = String.valueOf(rs.getInt("id"));
				String name = rs.getString("name");
				String keyword_description = rs.getString("keyword_description");
				String company = rs.getString("company");

				ArrayList<String> description_unit = new ArrayList<String>();
				description_unit.add(id);
				description_unit.add(keyword_description);
				description_list.add(description_unit);

				ArrayList<String> company_unit = new ArrayList<String>();
				company_unit.add(id);
				company_unit.add(company);
				company_list.add(company_unit);

			}
			rs.close();

			result.add(description_list);
			result.add(company_list);

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

	// 全部大写
	public static String allCapital(String entity) {
		return entity.toUpperCase();
	}

	// 首字母大写
	public static String firstCapital(String entity) {

		char[] cs = entity.toCharArray();
		int a = cs[0];
		if (a >= 97 && a <= 122) {
			cs[0] -= 32;
			String entity_upper = String.valueOf(cs);
			System.out.println(entity_upper);
			return entity_upper;
		} else {
			return entity;
		}
	}

	public static ArrayList<String> getTriples_des(String entity, ArrayList<String> triples, String id_business)
			throws UnsupportedEncodingException {
		// SPARQL的语法
		String queryString = "prefix dbr: <http://dbpedia.org/resource/>\n"
				+ "PREFIX dbo: <http://dbpedia.org/ontology/>\n"
				+ "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n"
				+ "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> \n" + "select ?y ?z where {dbr:" + entity + "  ?y ?z FILTER (lang(?z) = 'en') }";

		// 执行SPARQL语句
		QueryExecution qexec = getResult(queryString);

		// 新建一个动态数组
		ArrayList<String> triple = new ArrayList<String>();

		try {
			// 新建一个结果集 储存查询返回的结果
			ResultSet results = qexec.execSelect();
			// hasNext是判断是否还有下个元素 。
			for (; results.hasNext();) {
				QuerySolution soln = results.nextSolution();

				// System.out.println(entity);
				String s = entity;
				String p = getElement(soln.get("?y").toString());
				String o = getElement(soln.get("?z").toString());
				List predicate_des = Arrays.asList("label", "comment");
				if (predicate_des.contains(p) && (o != null && o.length() != 0)) {
					triple.add(s);
					triple.add(p);
					triple.add(o);

					// 因为要将三元组整体存到表中，所以从arrayList变成Stringz
					String string_triple = String.valueOf(triple);

					// 检查是否是英文
					boolean b = isEnglish(string_triple);
					if (b == true) {
						// 去重处理，如果不包含再添加
						if (!triples.contains(string_triple)) {
							// insert_des(string_triple,p, id_business);
							triples.add(string_triple);
							//System.out.println(string_triple);
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

	public static ArrayList<String> getTriples_com(String entity, ArrayList<String> triples, String id_business)
			throws UnsupportedEncodingException {
		// SPARQL的语法
		String queryString = "prefix dbr: <http://dbpedia.org/resource/>\n"
				+ "PREFIX dbo: <http://dbpedia.org/ontology/>\n"
				+ "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n"
				+ "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> \n" + "select ?y ?z where {dbr:" + entity + " ?y" + " ?z}";

		// 执行SPARQL语句
		QueryExecution qexec = getResult(queryString);

		// 新建一个动态数组
		ArrayList<String> triple = new ArrayList<String>();

		try {
			// 新建一个结果集 储存查询返回的结果
			ResultSet results = qexec.execSelect();
			// hasNext是判断是否还有下个元素 。
			for (; results.hasNext();) {
				QuerySolution soln = results.nextSolution();

				// System.out.println(entity);
				String s = entity;
				String p = getElement(soln.get("?y").toString());
				String o = getElement(soln.get("?z").toString());
				List predicate_com = Arrays.asList("product", "industry");
				if (predicate_com.contains(p) && (o != null && o.length() != 0)) {
					triple.add(s);
					triple.add(p);
					triple.add(o);

					// 因为要将三元组整体存到表中，所以从arrayList变成Stringz
					String string_triple = String.valueOf(triple);

					// 检查是否是英文
					boolean b = isEnglish(string_triple);
					if (b == true) {
						// 去重处理，如果不包含再添加
						if (!triples.contains(string_triple)) {
							// insert_des(string_triple,p, id_business);
							triples.add(string_triple);
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

	// 执行访问语句
	public static QueryExecution getResult(String queryString) {
		Query query = QueryFactory.create(queryString);
		QueryExecution qexec = QueryExecutionFactory.sparqlService("https://dbpedia.org/sparql", query);
		return qexec;
	}

	// 过滤结果
	public static String getElement(String res) {
		// 把结尾的en去掉
		res = res.replace("@en", "");
		// 以/为分割，取最后一个字符串
		String[] tem1 = res.split("/");
		String res1 = tem1[tem1.length - 1];
		// 以#为分割，取最后一个字符串
		String[] tem2 = res1.split("#");
		String res2 = tem2[tem2.length - 1];

		return res2;
	}

	public static void insert_des(String triple, String p, String id_business) {

		Utils util = new Utils();
		Connection conn = null;
		PreparedStatement ps = null;

		// 去掉大括号
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
			String sql_insert = "insert into triplesbedes(id_business,predicate,triples_description) values (%s,%s,%s)";

			// Pre compilation
			ps = conn.prepareStatement(sql_insert);

			// Fill placeholder
			String sql = String.format(sql_insert, id_business, '"' + p + '"', '"' + triple + '"');

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

	public static void insert_com(String triple, String p, String id_business) {

		Utils util = new Utils();
		Connection conn = null;
		PreparedStatement ps = null;

		// 去掉大括号
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
			String sql_insert = "insert into triplesbecom(id_business,predicate,triples_company) values (%s,%s,%s)";

			// Pre compilation
			ps = conn.prepareStatement(sql_insert);

			// Fill placeholder
			String sql = String.format(sql_insert, id_business, '"' + p + '"', '"' + triple + '"');

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

	// 英文过滤函数 只是判断
	public static boolean isEnglish(String string_triple) {

		// 判断只能是@en
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
			// 判断百分之80都是英文字母就属于英文
			float pro = (c.length - count) / c.length;
			if (pro > 0.8) {
				return true;
			} else {
				return false;
			}
		}

	}

}
