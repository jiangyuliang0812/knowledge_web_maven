package com.cn;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SimCacu {

	public static void main(String[] args) throws IOException {
		
		String text1 = "Reports that the NSA eavesdropped on world leaders have severely shaken relations between Europe and the U.S., German Chancellor Angela Merkel said.";
		String text2 = "Germany and France are to seek talks with the US to settle a row over spying, as espionage claims continue to overshadow an EU summit in Brussels.";
		Dandelion(text1, text2);
	
	}
	//this is first push
	public static float Dandelion(String text1,String text2) throws IOException{
		// TODO Auto-generated method stub
		
		String token = "31d664bec47a44ba9857e0fbfe3c38b7";
		
		URL url = new URL("https://api.dandelion.eu/datatxt/sim/v1/?text1="+text1.replace(" ", "%20")+"&text2="+text2.replace(" ", "%20")+"&token="+token+"&lang=en");
		
		HttpURLConnection urlcon = (HttpURLConnection)url.openConnection();
        urlcon.connect();         //获取连接
        InputStream is = urlcon.getInputStream();
        BufferedReader buffer = new BufferedReader(new InputStreamReader(is));
        
        String l = null;
        String result = null;
        float similarity = 0;
        while((l=buffer.readLine())!=null){
        	System.out.println(l);
        
        	//处理String l 得到similarity  
        	l = l.replace("{", "").replace("}", "").replace(":", ",");
        	ArrayList<String> list = new ArrayList();        	
        	for(String a: l.split(",")){
        		list.add(a);
        	}
        	result = list.get(3);
        	similarity = Float.parseFloat(result);
        }
        
        System.out.println(similarity);
        return similarity;
	}

}
