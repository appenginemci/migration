package com.sogeti.mci.migration.helper;
 
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
 
public class PropertiesManager {
	
	private static Properties properties = getProperties("admin-api-settings.properties");
	
	private static Properties getProperties(String path){
		Properties properties = new Properties();
		
		try {
			properties.load(PropertiesManager.class.getResourceAsStream("/" + path));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return properties;
	}
	
	public static String getProperty(String key){
		if(properties == null) properties = getProperties("config.properties");
		return properties.getProperty(key);
	}
}