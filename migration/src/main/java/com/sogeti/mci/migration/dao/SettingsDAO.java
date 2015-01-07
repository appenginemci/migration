package com.sogeti.mci.migration.dao;


import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import com.sogeti.mci.migration.helper.ConnectionUtil;

public class SettingsDAO {
	
	private HashMap<String, String> settings = new HashMap<String, String>();
	private static String[] values = {"logLevel_MailConverter", "outboxTempFolderId", "templateDocId", "patternEventEmail"} ;
	private static final String QUERY = "SELECT data FROM globalsettings WHERE value='";
	private static SettingsDAO instance = null;
	
	public static SettingsDAO getInstance() {
		if(instance == null) {
			synchronized (SettingsDAO.class){
				if(instance == null) {
					instance = new SettingsDAO();
				}
			}
	    }
	    return instance;
	}
	
	protected SettingsDAO()  {	
		init();
	}

	private void init(){
		for (int i=0; i<values.length; i++){
			Statement stmt = null;
			Connection conn = null;
			try {
				conn = ConnectionUtil.getConnection();
				stmt = conn.createStatement();
				ResultSet resultSet = stmt.executeQuery(QUERY + values[i] + "'");
				if (resultSet.first()) {
					settings.put(values[i], resultSet.getString(1));
				}
			} catch (Exception e){
				System.err.println(new Date() + "Unable to retrieve value " + values[i] + " from database");
				System.err.println(LoggerDAO.getStackTrace(e));
			} finally {
				try {
					if (stmt != null) stmt.close();
					if (conn != null) conn.close();	
				} catch (Exception e){
					// do not log
				}
			}
			System.out.println(new Date() + "Starting with the following global settings: " +settingsToString() );
		}
		
	}
	
	public String getSetting(String value){
		return settings.get(value);
	}
	
	public String settingsToString(){
		String result = "";
		Iterator<Entry<String, String>> it = settings.entrySet().iterator();
		while (it.hasNext()){
			 Map.Entry<String, String> pairs = (Map.Entry<String, String>) it.next();
			 if (!result.equals("")) {
				 result = result + "\n" + pairs.getKey() + " = " + pairs.getValue();
			 } else {
				 result = pairs.getKey() + " = " + pairs.getValue();
			 }
		}
		return result;
	}
	
	
}
