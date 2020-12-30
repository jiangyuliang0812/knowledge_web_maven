package com.cn;

import java.io.InputStream; 
import java.util.Properties;  

// use class Utils to get user, password ... from database

public class Utils {
	
    private static Properties properties=new Properties();  
    static{  
        ClassLoader classLoader=Utils.class.getClassLoader();  
        InputStream ips=classLoader.getResourceAsStream("com/cn/mysql.properties");  
        try{  
            properties.load(ips);  
        }catch (Exception e) {  
            e.printStackTrace();  
        }  
    }  
      
    public static String getValue(String key){  
        return properties.getProperty(key);  
    }  
} 
