package com.cn;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.monkeylearn.MonkeyLearn;
import com.monkeylearn.MonkeyLearnResponse;
import com.monkeylearn.MonkeyLearnException;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.Properties;
import java.util.Set;

import edu.stanford.nlp.dcoref.CorefChain;
import edu.stanford.nlp.dcoref.CorefCoreAnnotations.CorefChainAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.LemmaAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.NamedEntityTagAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.PartOfSpeechAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphCoreAnnotations;
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreeCoreAnnotations.TreeAnnotation;
import edu.stanford.nlp.util.CoreMap;

import org.apache.http.Header;

public class DataInsert extends HttpServlet {

	// Use class Utils to get user, password ... from database
	Utils util = new Utils();

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		req.setCharacterEncoding("utf-8");
		resp.setCharacterEncoding("utf-8");

		// get purpose and mechanism from FirstPage
		String purpose = req.getParameter("purpose");
		String mechanism = req.getParameter("mechanism");

		insertMySQl(purpose, mechanism);

		// go back to Firstpage
		resp.sendRedirect("/knowledge_web_maven/FirstPage.jsp");
		

	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doGet(req, resp);
	}

	public void insertMySQl(String purpose, String mechanism) {

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
			
			// Use Monkey Learn to get keywords from purpose 
			ArrayList<ArrayList<Map<String,String>>> res_purpose = getKeyWord(purpose);
			
			// keyWordsList_purpose(List) will be put in keyWords_purpose(String)
			List<String> keyWordsList_purpose = new ArrayList<String>();
			
			// Monkey will separate phrase, so we use regular expressions to get phrases
			Pattern p_purpose = Pattern.compile("\\S+"); 
			Matcher m_purpose = p_purpose.matcher(purpose.replace(",", " ").replace(".", " "));

			// Now we have all the words in the purpose, including phrases
	
			List<String> all_words_phrases_purpose = new ArrayList<String>();
			
			// Words and phrases in purpose will be added one by one, One word is one group
			while(m_purpose.find()) { 
				all_words_phrases_purpose.add(m_purpose.group());
			}
			
			for (Map i : res_purpose.get(0)){
				
				String key_word_purpose = (String) i.get("keyword");
				// If there are keywords in the phrase, just use the phrase, not the keyword
				for (String j : all_words_phrases_purpose){ 
					
					//如果被正则匹配分开的词组包含关键字，那我们就储存这个分开的词组，如果不包含就存原来的
					key_word_purpose = (j.indexOf(key_word_purpose) >= 0)?j:key_word_purpose;
					//名词转单数
					key_word_purpose = Inflector.getInstance().singularize(key_word_purpose);
					
				}
				
				
				// Avoid adding the same words 
				if (!keyWordsList_purpose.contains(key_word_purpose))
					
					keyWordsList_purpose.add(key_word_purpose);
			}
			
			// Add verb 
			for (String i : (List<String>) getVerbKeyWord(purpose)){
				keyWordsList_purpose.add(i);
			}
			
			//remove same waords
			keyWordsList_purpose = removeDuplicate(keyWordsList_purpose);
			
			// All keywords are extracted, we will sort them by index
			
			// Convert to lowercase letters, Easy to compare
			String purpose_lower = purpose.toLowerCase();
			
			//Overwrite Interface Comparator
			Collections.sort(keyWordsList_purpose, new Comparator<String>()
		    {
		        public int compare(String a1, String a2)
		        {	
		        	// Compare the index of word1 and word2
		        	int word1 = purpose_lower.indexOf(a1.toLowerCase());
		        	
		        	//Because Monkey Learn will turn the singular into plural, or some capitalization issues, so I need to convert singular, plural and capitalization
		        	word1 = (word1 >= 0)?word1:purpose_lower.indexOf(a1.split("#")[0].toLowerCase());
		        	word1 = (word1 >= 0)?word1:purpose_lower.indexOf(Inflector.getInstance().pluralize(a1.split("#")[0].toLowerCase()));
		           	
		        	int word2 = purpose_lower.indexOf(a2.toLowerCase());
		        	word2 = (word2 >= 0)?word2:purpose_lower.indexOf(a2.split("#")[0].toLowerCase());
		        	word2 = (word2 >= 0)?word2:purpose_lower.indexOf(Inflector.getInstance().pluralize(a2.split("#")[0].toLowerCase()));
		        	
		        	// Ascending
		        	return word1 - word2;
		        }
		    });
			
			// Put Keywords into String format for easy to put them into database
			String keyWords_purpose = String.join(",", keyWordsList_purpose);
			
			
			
			//Use Monkey Learn to get keywords from mechanism
			ArrayList<ArrayList<Map<String,String>>> res_mechanism = getKeyWord(mechanism);
			
			List keyWordsList_mechanism = new ArrayList<String>();
			for (Map i : res_mechanism.get(0)){
			
				keyWordsList_mechanism.add(i.get("keyword"));
			}
	
			String keyWords_mechanism = String.join(",", keyWordsList_mechanism);

			// Write a SQL statement,store it in String, then pre-compile, and then execute
			String sql_insert = "insert into ideas(purpose,mechanism,createTime,keyword_purpose,keyword_mechanism) values (%s,%s,now(),%s,%s)";

			// Pre compilation
			ps = conn.prepareStatement(sql_insert);

			// Fill placeholder
			String sql = String.format(sql_insert, '"' + purpose + '"', '"' + mechanism + '"','"'+keyWords_purpose+'"','"'+keyWords_mechanism+'"');
			
			//execute
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
	
	
	public ArrayList getKeyWord(String text) throws MonkeyLearnException {
		String API_KEY = "5340cb0f3513a405c29a29b1a81ec7c3bd6c73c9";
		String MODEL_ID = "ex_YCya9nrn";
        MonkeyLearn ml = new MonkeyLearn(API_KEY);
        String[] textList = {text};
        MonkeyLearnResponse res = ml.extractors.extract(MODEL_ID, textList);
        ArrayList result = res.arrayResult;
        
//      Query remaining times
        Header[][] headers = res.headers;
        for (Header[] header : headers) {
         for (Header h : header){
          System.out.println("Key : " + h.getName() 
          + " ,Value : " + h.getValue());
         }
      
     }

        System.out.println(headers.toString());
      
//		X-Query-Limit-Limit  您当前的查询限制。
//      X-Query-Limit-Remaining 您的帐户可以使用的查询数量。
//      X-Query-Limit-Request-Queries 此请求消耗的查询数。
        
        return (ArrayList<ArrayList<Map<String,String>>>) result;
        
    }
	
	
	public List getVerbKeyWord(String text) {
		
		/*
		 * 
		CC	Coordinating conjunction
		CD	Cardinal number
		DT	Determiner
		EX	Existential there
		FW	Foreign word
		IN	Preposition or subordinating conjunction
		JJ	Adjective
		JJR	Adjective, comparative
		JJS	Adjective, superlative
		LS	List item marker
		MD	Modal
		NN	Noun, singular or mass
		NNS	Noun, plural
		NNP	Proper noun, singular
		NNPS	Proper noun, plural
		PDT	Predeterminer
		POS	Possessive ending
		PRP	Personal pronoun
		PRP$	Possessive pronoun
		RB	Adverb
		RBR	Adverb, comparative
		RBS	Adverb, superlative
		RP	Particle
		SYM	Symbol
		TO	to
		UH	Interjection
		VB	Verb, base form
		VBD	Verb, past tense
		VBG	Verb, gerund or present participle
		VBN	Verb, past participle
		VBP	Verb, non-3rd person singular present
		VBZ	Verb, 3rd person singular present
		WDT	Wh-determiner
		WP	Wh-pronoun
		WP$	Possessive wh-pronoun
		WRB	Wh-adverb
		 */
		
		/**
		 * 创建一个StanfordCoreNLP object tokenize(分词)、ssplit(断句)、
		 * pos(词性标注)、lemma(词形还原)、 ner(命名实体识别)、parse(语法解析)、指代消解？同义词分辨？
		 */
		//
		List<String> poss = Arrays.asList("VB","VBD","VBG","VBN","VBP","VBZ");
		List result = new ArrayList<String>();

		Properties props = new Properties();
		props.put("annotators", "tokenize, ssplit, pos, lemma"); // 七种Annotators
		StanfordCoreNLP pipeline = new StanfordCoreNLP(props); // 依次处理

		Annotation document = new Annotation(text); 
		pipeline.annotate(document); // 对text执行所有的Annotators（七种）

		// 下面的sentences 中包含了所有分析结果，遍历即可获知结果。
		List<CoreMap> sentences = document.get(SentencesAnnotation.class);
		for (CoreMap sentence : sentences) {
			for (CoreLabel token : sentence.get(TokensAnnotation.class)) {

				String word = token.get(TextAnnotation.class); // 获取分词
				String pos = token.get(PartOfSpeechAnnotation.class); // 获取词性标注
			 // String ner = token.get(NamedEntityTagAnnotation.class); // 获取命名实体识别结果
				String lemma = token.get(LemmaAnnotation.class); // 获取词形还原结果
				if (poss.contains(pos))
					//result.add(word + "#" + lemma);
					//只要获得动词原形
					result.add(lemma);
			}
			
		}
		return (List<String>)result;
	}
	
	public List<String> removeDuplicate(List<String> stringList) {
	    Set<String> set = new LinkedHashSet<>();
	    set.addAll(stringList);

	    stringList.clear();

	    stringList.addAll(set);
	    return stringList;
	}
	
}
