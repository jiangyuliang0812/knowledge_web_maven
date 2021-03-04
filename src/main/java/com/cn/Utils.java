package com.cn;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

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

public class Utils {

	// use class Utils to get user, password ... from database
	private static Properties properties = new Properties();
	static {
		ClassLoader classLoader = Utils.class.getClassLoader();
		InputStream ips = classLoader.getResourceAsStream("com/cn/mysql.properties");
		try {
			properties.load(ips);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static String getValue(String key) {
		return properties.getProperty(key);
	}

	// 查看每一个entity是否有三元组
	public static ArrayList<String> saveTriples(ArrayList<ArrayList<String>> data, List predicatesList, String database_name, String column_name) throws UnsupportedEncodingException {
		
		ArrayList<String> triples = new ArrayList<String>();
		String entity = "";
		String entity_all_capital = "";
		String entity_first_capital = "";
		
		for (int i = 0; i < data.size(); i++) {
			ArrayList<String> unit = data.get(i);
			String id = unit.get(0);
			String words = unit.get(1);
			
			if (words != null && words.length() != 0){
				
				for (String j : words.split(",")) {
					
					entity = j.replace(" ", "_").replace(".", "_");
					entity_first_capital = firstCapital(entity);
					entity_all_capital = allCapital(entity);
					
					// 不断地往triples里添加找到三元组
					triples = getTriples(entity_first_capital, triples, id, database_name, column_name, predicatesList);
					// 如果找到不到首字母大写的，再尝试全部大写
					if(triples.size() == 0){
						triples = getTriples(entity_all_capital, triples, id, database_name, column_name, predicatesList);
					}
					
					// System.out.println(triples);
				}	
			}
		}
		return triples;
	}
	
	// 通过sparql语句来查找三元组，检查是否是英文，然后存入数据库
    public static ArrayList<String> getTriples(String entity, ArrayList<String> triples, String id_business, String database_name, String column_name, List predicatesList)
            throws UnsupportedEncodingException {
    	// SPARQL的语法
        String queryString = "prefix dbr: <http://dbpedia.org/resource/>\n"
                + "PREFIX dbo: <http://dbpedia.org/ontology/>\n"
                + "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n"
                + "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> \n" 
                + "select ?y ?z where {dbr:" + entity + "  ?y ?z}";

        // 执行SPARQL语句
        QueryExecution qexec = getResult(queryString);

        // 新建一个动态数组
        ArrayList<String> triple = new ArrayList<String>();

        try {
        	// 新建一个结果集 储存查询返回的结果
            ResultSet results = qexec.execSelect();
            
            Map<String, Integer> counter = new HashMap<>();
            List predicates = new ArrayList(predicatesList);
            for(int i = 0; i < predicates.size(); i ++){
                String predicate = (String) predicates.get(i);
                counter.put(predicate, 0);             
            }
	        
            // hasNext是判断是否还有下个元素 。
            for (; results.hasNext();) {
            	
                QuerySolution soln = results.nextSolution();

                // System.out.println(entity);
                String s = entity;
                String p = getElement(soln.get("?y").toString());
                String o = getElement(soln.get("?z").toString());
                
                if (predicates.contains(p) && (o != null && o.length() != 0)) {
                	
					int i = counter.get(p);
					i = i + 1;
					counter.put(p, i);
					// System.out.println(counter.get(p));
					if(counter.get(p) == 3){
					    for (Object item : predicates) {
					        if (item.equals(p)) {	      
					        	predicates.remove(item);
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
                    
                    // 筛选一下，是否是英文
                    boolean b = tripleFilter(string_triple);
                    if (b == true) {
                    	// 去重处理，如果不包含再添加
                        if (!triples.contains(string_triple)) {
                            insert_database(database_name, column_name, id_business, p, string_triple);
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
    
    // 将三元组存入数据库
    public static void insert_database(String database_name, String column_name, String id_business, String p, String triple) {

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
			String sql_insert = "insert into %s(id_business,predicate,%s) values (%s,%s,%s)";

			// Pre compilation
			ps = conn.prepareStatement(sql_insert);

			// Fill placeholder
			String sql = String.format(sql_insert, database_name, column_name, id_business, '"' + p + '"', '"' + triple + '"');

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
 			// System.out.println(entity_upper);
 			return entity_upper;
 		} else {
 			return entity;
 		}
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
		// 以:为分割，取最后一个字符串
		String[] tem3 = res2.split(":");
		String res3 = tem3[tem3.length - 1];
		
		return res3;
	}
	
	// 英文过滤函数 只是判断
	public static boolean tripleFilter(String string_triple) {

		// 判断如果有@,那么只能@en
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
				if (!((c[i] >= 32 && c[i] <= 48)||(c[i] >= 64 && c[i] <= 126))) {
					count += 1;
				}
			}
			
			// 判断百分之90都是英文字母或标点符号就属于英文
			float pro = (c.length - count) / c.length;
			if (pro > 0.9) {
				return true;
			} else {
				return false;
			}
		}

	}
}
