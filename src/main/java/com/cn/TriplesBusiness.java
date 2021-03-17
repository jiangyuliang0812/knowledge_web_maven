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

	public static void main(String[] args) throws UnsupportedEncodingException {
		
		// 拿到商业模式数据
		ArrayList<ArrayList<ArrayList<String>>> dataBusinessModel = getBusinessModel();
		// System.out.println(dataBusinessModel);
		ArrayList<ArrayList<String>> data_description = dataBusinessModel.get(0);
		ArrayList<ArrayList<String>> data_example = dataBusinessModel.get(1);
		ArrayList<ArrayList<String>> data_selling = dataBusinessModel.get(2);
		ArrayList<ArrayList<String>> data_advantage = dataBusinessModel.get(3);
		ArrayList<ArrayList<String>> data_focus = dataBusinessModel.get(4);
		ArrayList<ArrayList<String>> data_who = dataBusinessModel.get(5);
		ArrayList<ArrayList<String>> data_money = dataBusinessModel.get(6);
		// System.out.println(data_description);
		
		// 用sparql来得到三元组 并存入
		
		// Description
		if (data_description != null) {
			ArrayList<String> triples = new ArrayList<String>();
			List predicatesList = Arrays.asList("type","subject","primaryTopic","seeAlso","specialist");
			String database_name = "triplesbedes";
			String column_name = "triples_description";
			Utils.saveTriples(data_description,predicatesList,database_name,column_name);
			
		}
		
		// Example
		if (data_example != null) {
			ArrayList<String> triples = new ArrayList<String>();
			List predicatesList = Arrays.asList("owners","founder","industry","product","manufacturer","brands","foundedBy","service","areaServed");
			String database_name = "triplesbeexample";
			String column_name = "triples_example";
			Utils.saveTriples(data_example,predicatesList,database_name,column_name);
			
		}
		
		// Selling
		if (data_selling != null) {
			ArrayList<String> triples = new ArrayList<String>();
			List predicatesList = Arrays.asList("type","subject","primaryTopic","seeAlso","specialist");
			String database_name = "triplesbesell";
			String column_name = "triples_sell";
			Utils.saveTriples(data_selling,predicatesList,database_name,column_name);
			
		}
		
		// Advantage
		if (data_advantage != null) {
			ArrayList<String> triples = new ArrayList<String>();
			List predicatesList = Arrays.asList("type","subject","primaryTopic","seeAlso","specialist");
			String database_name = "triplesbeadvan";
			String column_name = "triples_advantage";
			Utils.saveTriples(data_advantage,predicatesList,database_name,column_name);
			
		}
		
		// Focus
		if (data_focus != null) {
			ArrayList<String> triples = new ArrayList<String>();
			List predicatesList = Arrays.asList("type","subject","primaryTopic","seeAlso","specialist");
			String database_name = "triplesbefocus";
			String column_name = "triples_focus";
			Utils.saveTriples(data_focus,predicatesList,database_name,column_name);
			
		}

		// Who
		if (data_who != null) {
			ArrayList<String> triples = new ArrayList<String>();
			List predicatesList = Arrays.asList("type","subject","primaryTopic","seeAlso","specialist");
			String database_name = "triplesbewho";
			String column_name = "triples_who";
			Utils.saveTriples(data_who,predicatesList,database_name,column_name);
			
		}
		
		// Money
		if (data_money != null) {
			ArrayList<String> triples = new ArrayList<String>();
			List predicatesList = Arrays.asList("type","subject","primaryTopic","seeAlso","specialist");
			String database_name = "triplesbemoney";
			String column_name = "triples_money";
			Utils.saveTriples(data_money,predicatesList,database_name,column_name);
			
		}
		
	}
	
	public static ArrayList<ArrayList<ArrayList<String>>> getBusinessModel() {

		Utils util = new Utils();
		Connection conn = null;
		PreparedStatement ps = null;
		ArrayList<ArrayList<String>> description_list = new ArrayList<ArrayList<String>>();
		ArrayList<ArrayList<String>> example_list = new ArrayList<ArrayList<String>>();
		ArrayList<ArrayList<String>> selling_list = new ArrayList<ArrayList<String>>();
		ArrayList<ArrayList<String>> advantage_list = new ArrayList<ArrayList<String>>();
		ArrayList<ArrayList<String>> focus_list = new ArrayList<ArrayList<String>>();
		ArrayList<ArrayList<String>> who_list = new ArrayList<ArrayList<String>>();
		ArrayList<ArrayList<String>> money_list = new ArrayList<ArrayList<String>>();
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
			String sql = "SELECT id,name,keyword_description,example,what_you_selling,keyword_advantage,keyword_focus,who_you_sell_to,how_you_make_money FROM businessmodel;";

			// Pre compilation
			ps = conn.prepareStatement(sql);

			// 执行查询语句 把查询到的数据存在 结果集 中 ResultSet
			java.sql.ResultSet rs = ps.executeQuery(sql);

			// 添加数据
			while (rs.next()) {
				// HashMap的读取
				String id = String.valueOf(rs.getInt("id"));
				String description = rs.getString("keyword_description");
				String example = rs.getString("example");
				String selling = rs.getString("what_you_selling");
				String advantage = rs.getString("keyword_advantage");
				String focus = rs.getString("keyword_focus");
				String who = rs.getString("who_you_sell_to");
				String money = rs.getString("how_you_make_money");
				
				ArrayList<String> description_unit = new ArrayList<String>();
				description_unit.add(id);
				description_unit.add(description);
				description_list.add(description_unit);
				
				ArrayList<String> example_unit = new ArrayList<String>();
				example_unit.add(id);
				example_unit.add(example);
				example_list.add(example_unit);
					
				ArrayList<String> selling_unit = new ArrayList<String>();
				selling_unit.add(id);
				selling_unit.add(selling);
				selling_list.add(selling_unit);
				
				ArrayList<String> advantage_unit = new ArrayList<String>();
				advantage_unit.add(id);
				advantage_unit.add(advantage);
				advantage_list.add(advantage_unit);
				
				ArrayList<String> focus_unit = new ArrayList<String>();
				focus_unit.add(id);
				focus_unit.add(focus);
				focus_list.add(focus_unit);
				
				ArrayList<String> who_unit = new ArrayList<String>();
				who_unit.add(id);
				who_unit.add(who);
				who_list.add(who_unit);
				
				ArrayList<String> money_unit = new ArrayList<String>();
				money_unit.add(id);
				money_unit.add(money);
				money_list.add(money_unit);
				
			}
			
			rs.close();
			
			result.add(description_list);
			result.add(example_list);
			result.add(selling_list);
			result.add(advantage_list);
			result.add(focus_list);
			result.add(who_list);
			result.add(money_list);

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
