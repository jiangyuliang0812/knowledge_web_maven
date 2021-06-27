package com.cn;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.apache.http.Header;

import com.monkeylearn.MonkeyLearn;
import com.monkeylearn.MonkeyLearnException;
import com.monkeylearn.MonkeyLearnResponse;

public class test {

	public static void main(String[] args) throws MonkeyLearnException {
		String text = "Tour guide is a very good job, but in the future, if there is a tour guide mobile app, when people walk to a tourist attraction, the mobile app can introduce the corresponding tourist attraction based on GPS positioning, and the mobile app will make money through advertising, such as recommending hotels and restaurants.";
		System.out.printf("\n");
		System.out.printf(text);
		
	}
	
	// 之后可以在这个函数改善关键词提取结果
	public static String getKeywords(String text) throws MonkeyLearnException{
		
		// Use Monkey Learn to get keywords from idea
		ArrayList<ArrayList<Map<String,String>>> res_monkey = Monkey(text);
		
		return text;
	}
	
	
	public static ArrayList Monkey(String text) throws MonkeyLearnException {
		String API_KEY = "5340cb0f3513a405c29a29b1a81ec7c3bd6c73c9";
		String MODEL_ID = "ex_YCya9nrn";
        MonkeyLearn ml = new MonkeyLearn(API_KEY);
        String[] textList = {text};
        MonkeyLearnResponse res = ml.extractors.extract(MODEL_ID, textList);
        ArrayList result = res.arrayResult;
        System.out.println(result);
       
        
      	
        
        // X-Query-Limit-Limit  您当前的查询限制。
        // X-Query-Limit-Remaining 您的帐户可以使用的查询数量。
        // X-Query-Limit-Request-Queries 此请求消耗的查询数。
        
        return (ArrayList<ArrayList<Map<String,String>>>) result;
        
    }
}
