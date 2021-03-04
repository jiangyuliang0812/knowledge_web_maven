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

		String text1 = "Retail  comment   it does not always result in a purchase., Office  comment   ranging from a small off, House  comment  A house is a single-unit residential building  which may range in complexity from a rudimentary hut to a complex  structure of wood  masonry  concrete or other material  outfitted with plumbing  electrical  and heating  ventilation  and air conditioning systems. Houses use a range of different roofing systems to keep precipitation such as rain from getting into the dwelling space. Houses may have doors or locks to secure the dwelling space and protect its inhabitants and contents from burglars or other trespassers. Most conventional modern houses in Western cultures will contain one or more bedrooms and bathrooms  a kitchen or cooking area  and a living room. A house may have a separate dining room  or the eating area may be integrated into another room. Some large houses in North America , Window  comment  A window is an opening in a wall  door  roof or vehicle that allows the passage of light and may also allow the passage of sound and sometimes air. Modern windows are usually glazed or covered in some other transparent or translucent material  a sash set in a frame in the opening; the sash and frame are also referred to as a window. Many glazed windows may be opened  to allow ventilation  or closed  to exclude inclement weather. Windows often have a latch or similar mechanism to lock the window shut or to hold it open by various amounts., Electricity  comment  Electricity is the set of physical phenomena associated with the presence and motion of matter that has a property of electric charge. Electricity is related to magnetism  both being part of the phenomenon of electromagnetism  as described by Maxwell's equations. Various common phenomena are related to electricity  including lightning  static electricity  electric heating  electric discharges and many others. Electricity is at the heart of many modern technologies  being used for,";
		String text2 = "Google  comment  Google LLC is an American multinational technology company that specializes in Internet-related services and products  which include online advertising technologies  a search engine  cloud computing  software  and hardware. It is considered one of the Big Four technology companies alongside Amazon  Apple and Microsoft., Rolex  comment  (For other uses  see Rolex (disambiguation).) Rolex SA () is a Swiss luxury watch manufacturer based in Geneva  Switzerland. Originally founded as Wilsdorf and Davis by Hans Wilsdorf and Alfred Davis in London  England in 1905  the company registered Rolex as the brand name of its watches in 1908 and became Rolex Watch Co. Ltd. in 1915. After World War I  the company moved its base of operations to Geneva  Switzerland to avoid heavy taxation in post-war Britain  and in 1920 Hans Wilsdorf registered Montres Rolex SA in Geneva as the new company name which eventually became Rolex SA in later years. Since 1960  the company has been owned by the Hans Wilsdorf Foundation  a private family trust.,";

		Dandelion(text1, text2);

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
			//	System.out.println(l);

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
