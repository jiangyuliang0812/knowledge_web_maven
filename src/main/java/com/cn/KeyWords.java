package com.cn;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.Header;

import com.monkeylearn.MonkeyLearn;
import com.monkeylearn.MonkeyLearnException;
import com.monkeylearn.MonkeyLearnResponse;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.CoreAnnotations.LemmaAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.PartOfSpeechAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;

public class KeyWords {

	public static void main(String[] args) throws MonkeyLearnException {
		String text = "Elon Musk has shared a photo of the spacesuit designed by SpaceX. This is the second image shared of the new design and the first to feature the spacesuit’s full-body look.";
		String result = getKeywords(text);
		System.out.print(result);
	}
	
	// 之后可以在这个函数改善关键词提取结果
	public static String getKeywords(String text) throws MonkeyLearnException{
		
		// Use Monkey Learn to get keywords from idea
		ArrayList<ArrayList<Map<String,String>>> res_monkey = Monkey(text);
		
		// 处理monkey的结果
		
		// keyWordsList_text(List) will be put in keyWords_text(String)
		List<String> keyWordsList_text = new ArrayList<String>();
		
		// Monkey will separate phrase, so we use regular expressions to get phrases
		Pattern p_text = Pattern.compile("\\S+"); 
		Matcher m_text = p_text.matcher(text.replace(",", " ").replace(".", " "));

		// Now we have all the words in the text
		List<String> all_words_phrases_text = new ArrayList<String>();
		
		// Words and phrases in purpose will be added one by one, One word is one group
		while(m_text.find()) { 
			all_words_phrases_text.add(m_text.group());
		}
		
		for (Map i : res_monkey.get(0)){
			
			String key_word_text = (String) i.get("keyword");
			
			// If there are keywords in the phrase, just use the phrase, not the keyword
			for (String j : all_words_phrases_text){ 
				
				//如果被正则匹配分开的词组包含关键字，那我们就储存这个分开的词组，如果不包含就存原来的
				key_word_text = (j.indexOf(key_word_text) >= 0)?j:key_word_text;
				//名词转单数
				key_word_text = Inflector.getInstance().singularize(key_word_text);
				
			}
			
			// Avoid adding the same words 
			if (!keyWordsList_text.contains(key_word_text))
				
				keyWordsList_text.add(key_word_text);
		}
		
		
		// Add verb 
		for (String i : (List<String>) getVerbKeyWord(text)){
			keyWordsList_text.add(i);
		}

		//remove same words
		keyWordsList_text = KeyWords.removeDuplicate(keyWordsList_text);
		
		// All keywords are extracted, we will sort them by index
		
		// Convert to lowercase letters, Easy to compare
		String text_lower = text.toLowerCase();
		
		//Overwrite Interface Comparator
		Collections.sort(keyWordsList_text, new Comparator<String>()
	    {
	        public int compare(String a1, String a2)
	        {	
	        	// Compare the index of word1 and word2
	        	int word1 = text_lower.indexOf(a1.toLowerCase());
	        	
	        	//Because Monkey Learn will turn the singular into plural, or some capitalization issues, so I need to convert singular, plural and capitalization
	        	word1 = (word1 >= 0)?word1:text_lower.indexOf(a1.split("#")[0].toLowerCase());
	        	word1 = (word1 >= 0)?word1:text_lower.indexOf(Inflector.getInstance().pluralize(a1.split("#")[0].toLowerCase()));
	           	
	        	int word2 = text_lower.indexOf(a2.toLowerCase());
	        	word2 = (word2 >= 0)?word2:text_lower.indexOf(a2.split("#")[0].toLowerCase());
	        	word2 = (word2 >= 0)?word2:text_lower.indexOf(Inflector.getInstance().pluralize(a2.split("#")[0].toLowerCase()));
	        	
	        	// Ascending
	        	return word1 - word2;
	        }
	    });
		
		// Put Keywords into String format for easy to put them into database
		String keyWords_text = String.join(",", keyWordsList_text);
		
		// 处理一下结果
		keyWords_text = keyWords_text.replace(".", "").replace("’", "");
		
		
		return keyWords_text;
	}
	
	public static ArrayList Monkey(String text) throws MonkeyLearnException {
		String API_KEY = "5340cb0f3513a405c29a29b1a81ec7c3bd6c73c9";
		String MODEL_ID = "ex_YCya9nrn";
        MonkeyLearn ml = new MonkeyLearn(API_KEY);
        String[] textList = {text};
        MonkeyLearnResponse res = ml.extractors.extract(MODEL_ID, textList);
        ArrayList result = res.arrayResult;
        
        // Query remaining times
        Header[][] headers = res.headers;
        for (Header[] header : headers) {
         for (Header h : header){
          System.out.println("Key : " + h.getName()  + " ,Value : " + h.getValue());
         }
      
     }

        System.out.println(headers.toString());
      
        // X-Query-Limit-Limit  您当前的查询限制。
        // X-Query-Limit-Remaining 您的帐户可以使用的查询数量。
        // X-Query-Limit-Request-Queries 此请求消耗的查询数。
        
        return (ArrayList<ArrayList<Map<String,String>>>) result;
        
    }
	
	
	public static List getVerbKeyWord(String text) {
		
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
	
	public static List<String> removeDuplicate(List<String> stringList) {
	    Set<String> set = new LinkedHashSet<>();
	    set.addAll(stringList);

	    stringList.clear();

	    stringList.addAll(set);
	    return stringList;
	}

}
