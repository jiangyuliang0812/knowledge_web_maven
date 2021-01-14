package com.cn;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.CoreAnnotations.LemmaAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.PartOfSpeechAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;

public class SimCacu {

	public static void main(String[] args) throws IOException {

		ArrayList<String> triples = selectMySQl_triple();
		String triple = String.valueOf(triples);
		triple = triple.replace("[", "").replace("]", "").replace("#", "");

		System.out.println(triple);

		String description = "The core offering is priced competitively, but there are numerous extras that drive the final price up. In the end, the costumer pays more than he or she initially assumed. Customers benefit from a variable offer, which they can adapt to their specific needs.";

		Dandelion(triple, description);

	}

	public static float Dandelion(String text1, String text2) throws IOException {
		// TODO Auto-generated method stub

		String token = "31d664bec47a44ba9857e0fbfe3c38b7";

		URL url = new URL("https://api.dandelion.eu/datatxt/sim/v1/?text1=" + text1.replace(" ", "%20") + "&text2="
				+ text2.replace(" ", "%20") + "&token=" + token + "&lang=en");

		HttpURLConnection urlcon = (HttpURLConnection) url.openConnection();
		urlcon.connect(); // 获取连接
		InputStream is = urlcon.getInputStream();
		BufferedReader buffer = new BufferedReader(new InputStreamReader(is));

		String l = null;
		String result = null;
		float similarity = 0;
		while ((l = buffer.readLine()) != null) {
			System.out.println(l);

			// 处理String l 得到similarity
			l = l.replace("{", "").replace("}", "").replace(":", ",");
			ArrayList<String> list = new ArrayList();
			for (String a : l.split(",")) {
				list.add(a);
			}
			result = list.get(3);
			similarity = Float.parseFloat(result);
		}

		System.out.println(similarity);
		return similarity;
	}

	// 提取数据库里的数据 这里是最新的一条数据
	public static ArrayList<String> selectMySQl_triple() {

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

			// 编写查询语句
			String sql = "SELECT s,p,o FROM triples ORDER BY triple_id LIMIT 1;";

			// Pre compilation
			ps = conn.prepareStatement(sql);

			// 执行查询语句 把查询到的数据存在 结果集 中 ResultSet
			java.sql.ResultSet rs = ps.executeQuery(sql);

			ArrayList<String> result = new ArrayList();

			// 添加数据
			while (rs.next()) {
				// HashMap的读取
				result.add(rs.getString("s"));
				result.add(rs.getString("p"));
				result.add(rs.getString("o"));
			}
			rs.close();

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

	// 第一步 拿资格triple 和description
	// 第二步 先改变形态 动词转原形，名词转单数和小写
	// 第三步 对比
}
