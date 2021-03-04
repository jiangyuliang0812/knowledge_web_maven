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

public class DataInsert {
	
	public static void main(String[] args) {
		
		String text = "Many burglars break into retail stores, offices, or houses by crashing a window. TCO could be prevented by putting electricity on the glass wall, as soon as the owner is no longer at home, or the shop is closed, or best when the alarm is set up. This way, if a burgler tries to break the glass-window, he/she gets shocked by electricity.";
		insertMySQl(text);
		
	}

	public static void insertMySQl(String idea) {
		// Use class Utils to get user, password ... from database
		Utils util = new Utils();
		Connection conn = null;
		PreparedStatement ps = null;

		try {
			// get Keywords
			String keywords = KeyWords.getKeywords(idea);
			
			// Connect to the database
			Class.forName("com.mysql.cj.jdbc.Driver");
			
			String host = util.getValue("host");
			String user = util.getValue("user");
			String password = util.getValue("password");
			String database = util.getValue("database");
			String url = String.format("jdbc:mysql://%s:3306/%s?connectTimeout=3000", host, database);
			
			conn = DriverManager.getConnection(url, user, password);	

			// Write a SQL statement,store it in String, then pre-compile, and then execute
			String sql_insert = "insert into ideas(idea,createTime,keyword_idea) values (%s,now(),%s)";

			// Pre compilation
			ps = conn.prepareStatement(sql_insert);

			// Fill placeholder
			String sql = String.format(sql_insert, '"' + idea + '"','"'+ keywords + '"');
			
			//execute
			ps.executeUpdate(sql);

			// System.out.println("Successfully Inserted!");

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
