package com.cn;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import org.apache.jasper.tagplugins.jstl.core.If;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.CoreAnnotations.LemmaAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.PartOfSpeechAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;

public class similarity {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		List<String> names = Arrays.asList("Add-on","Affiliation","Aikido","Auction","Barter","Cash Machine","Cross Selling","Crowdfunding","Crowdsourcing","Customer Loyalty");

		List<String> descriptions = Arrays.asList("The core offering is priced competitively, but there are numerous extras that drive the final price up. In the end, the costumer pays more than he or she initially assumed. Customers benefit from a variable offer, which they can adapt to their specific needs.","The focus lies in supporting others to successfully sell products and directly benefit from successful transactions. Affiliates usually profit from some kind of pay-per-sale or pay-per-display compensation. The company, on the other hand, is able to gain access to a more diverse potential customer base without additional active sales or marketing efforts.","Aikido is a Japanese martial art in which the strength of an attacker is used against him or her. As a business model, Aikido allows a company to offer something diametrically opposed to the image and mindset of the competition. This new value proposition attracts customers who prefer ideas or concepts opposed to the mainstream","Auctioning means selling a product or service to the highest bidder. The final price is achieved when a particular end time of the auction is reached or when no higher offers are received. This allows the company to sell at the highest price acceptable to the customer. The customer benefits from the opportunity to influence the price of a product.",
				"Barter is a method of exchange in which goods are given away to customers without the transaction of actual money. In return, they provide something of value to the sponsoring organisation. The exchange does not have to show any direct connection and is valued differently by each party.","In the Cash Machine concept, the customer pays upfront for the products sold to the customer before the company is able to cover the associated expenses. This results in increased liquidity which can be used to amortise debt or to fund investments in other areas.","In this model, services or products from a formerly excluded industry are added to the offerings, thus leveraging existing key skills and resources. In retail especially, companies can easily provide additional products and offerings that are not linked to the main industry on which they were previously focused. Thus, additional revenue can be generated with relatively few changes to the existing infrastructure and assets, since more potential customer needs are met.","A product, project or entire start-up is financed by a crowd of investors who wish to support the underlying idea, typically via the Internet. If the critical mass is achieved, the idea will be realized and investors receive special benefits, usually proportionate to the amount of money they provided.","The solution of a task or problem is adopted by an anonymous crowd, typically via the Internet. Contributors receive a small reward or have the chance to win a prize if their solution is chosen for production or sale. Customer interaction and inclusion can foster a positive relationship with a company, and subsequently increase sales and revenue.","Customers are retained and loyalty assured by providing value beyond the actual product or service itself, i.e., through incentive-based programs. The goal is to increase loyalty by creating an emotional connection or simply rewarding it with special offers. Customers are voluntarily bound to the company, which protects future revenue.");
		
		String triples = "Auction has final price,Auction has highest bidder,factory produce product,Customers pay high prices,Investors invest money,Investor support ideas";

		// �ȸı���̬ ����תԭ�Σ�����ת������Сд
		List<String> triples_reform = wordReform(triples);

		//�������飬Ϊ���Ժ���ʾÿһ��idea�����ƶ�
		List<Float> description_sim = new ArrayList();

		//�������𿪣�һһ�Ա�
		for (String description : descriptions) {

			List<String> description_reform = wordReform(description);
			
			//���㵥������������Ԫ������ƶ�
			description_sim.add(SimilarityCalculation(triples_reform, description_reform));
		}

		
		System.out.println(description_sim);
		float Max = Collections.max(description_sim);
		int Index = description_sim.indexOf(Max) + 1;
		System.out.println("Max : " + Max + "\n" + "Index : " + Index);
	}
	
	//���ƶ�����Ԫ�������������Ĵ���������Ԫ��ʵ�����
	public static float SimilarityCalculation(List<String> triples, List<String> describles) {
		// triple�ĵ�����
		float sum_of_triples = 0;

		// ���������triple��ĵ�����
		float sum_of_description = 0;

		for (String i : triples) {
			sum_of_triples += 1;
			if (describles.contains(i)) {
				sum_of_description += 1;
			}
		}

		float similarity = sum_of_description / sum_of_triples;
		return similarity;
	}

	public static List wordReform(String text) {
		/**
		 * ����һ��StanfordCoreNLP object tokenize(�ִ�)��ssplit(�Ͼ�)��
		 * pos(���Ա�ע)��lemma(���λ�ԭ)�� ner(����ʵ��ʶ��)��parse(�﷨����)��ָ�����⣿ͬ��ʷֱ棿
		 */
		List<String> poss = Arrays.asList("VB", "VBD", "VBG", "VBN", "VBP", "VBZ");
		List result = new ArrayList<String>();

		Properties props = new Properties();
		props.put("annotators", "tokenize, ssplit, pos, lemma"); // ����Annotators
		StanfordCoreNLP pipeline = new StanfordCoreNLP(props); // ���δ���

		// ��textִ�����е�Annotators�����֣�
		Annotation document = new Annotation(text);
		pipeline.annotate(document);

		// �����sentences �а��������з���������������ɻ�֪�����
		List<CoreMap> sentences = document.get(SentencesAnnotation.class);

		for (CoreMap sentence : sentences) {
			for (CoreLabel token : sentence.get(TokensAnnotation.class)) {

				String word = token.get(TextAnnotation.class); // ��ȡ�ִ�
				String pos = token.get(PartOfSpeechAnnotation.class); // ��ȡ���Ա�ע
				// String ne = token.get(NamedEntityTagAnnotation.class); 
				
				// ��ȡ����ʵ��ʶ����
				String lemma = token.get(LemmaAnnotation.class); // ��ȡ���λ�ԭ���

				if (poss.contains(pos)) {

					// v
					result.add(lemma.toLowerCase());
					// System.out.println(word + "\t" + pos + "\t" + lemma);
				}

				else {
					
					// n
					result.add(Inflector.getInstance().singularize(word).toLowerCase());
				}
			}

		}
		return (List<String>) result;
	}
	
	
	public ArrayList<ArrayList<String>> selectMySQl_BM() {

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
			String sql = "SELECT id,name,description FROM business_models;";

			// Pre compilation
			ps = conn.prepareStatement(sql);

			// ִ�в�ѯ��� �Ѳ�ѯ�������ݴ��� ����� �� ResultSet
			java.sql.ResultSet rs = ps.executeQuery(sql);

			ArrayList<ArrayList<String>> result = new ArrayList();
			ArrayList ids = new ArrayList();
			ArrayList names = new ArrayList();
			ArrayList descriptions = new ArrayList();

			// �������
			while (rs.next()) {
				ids.add(String.valueOf(rs.getInt("id")));
				names.add(rs.getString("name"));
				descriptions.add(rs.getString("description"));
			}
			rs.close();

			result.add(ids);
			result.add(names);
			result.add(descriptions);

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
	
	public ArrayList<ArrayList<String>> selectMySQl_Triples() {

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
			String sql = "SELECT idea_id,s,p,o FROM triples ORDER BY idea_id DESC LIMIT 1;";

			// Pre compilation
			ps = conn.prepareStatement(sql);

			// ִ�в�ѯ��� �Ѳ�ѯ�������ݴ��� ����� �� ResultSet
			java.sql.ResultSet rs = ps.executeQuery(sql);

			ArrayList<ArrayList<String>> result = new ArrayList();
			ArrayList idea_ids = new ArrayList();
			ArrayList data_purpose = new ArrayList();
			ArrayList data_mechanism = new ArrayList();

			// �������
			while (rs.next()) {
				idea_ids.add(String.valueOf(rs.getInt("idea_id")));
				data_purpose.add(rs.getString("Keyword_purpose"));
				data_mechanism.add(rs.getString("Keyword_mechanism"));
			}
			rs.close();

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
