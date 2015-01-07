package com.sogeti.mci.migration.dao;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import com.sogeti.mci.migration.helper.ConnectionUtil;

public class LoggerDAO {
	
	private static final String QUERY_INSERTLOG = "INSERT INTO mailconverter_logs (date, level, message, stacktrace) VALUES (?,?,?,?)";
	private static LoggerDAO instance = null;
	private String level = "DEBUG";
	
	
	public static LoggerDAO getInstance() {
		if(instance == null) {
			synchronized (LoggerDAO.class){
				if(instance == null) {
					instance = new LoggerDAO();
				}
			}
	    }
	    return instance;
	}
	
	protected LoggerDAO()  {	
		init();
	}
	
	private void init(){
		String levelFromDB = SettingsDAO.getInstance().getSetting("logLevel_MailConverter");
		if (levelFromDB != null && (levelFromDB.equals("DEBUG") || levelFromDB.equals("ERROR") || levelFromDB.equals("INFO"))) {
			level = levelFromDB;
		}
	}
	
	
	public void debug(String message, Exception e){
		if (level.equals("DEBUG")){
			String stacktrace = "";
			if (e != null){
				stacktrace = getStackTrace(e);
			} 
			logToDB("DEBUG",  message, stacktrace);
		}
	}

	public void debug(String message){
		if (level.equals("DEBUG")){
			logToDB("DEBUG", message, "");
		}
	}

	public void info(String message, Exception e){
		if (level.equals("INFO") || level.equals("DEBUG")){
			String stacktrace = "";
			if (e != null){
				stacktrace = getStackTrace(e);
			} 
			logToDB("INFO", message, stacktrace);
		}
	}

	public void info(String message){
		if (level.equals("INFO") || level.equals("DEBUG")){
			logToDB("INFO", message, "");
		}
	}
	
	public void error(String message, Exception e){
	    String stacktrace = "";
		if (e != null){
			stacktrace = getStackTrace(e);
		} 
		logToDB("ERROR", message, stacktrace);
	}
	
	public void error(String message){
		logToDB("ERROR", message, "");
	}
	
	
	private void logToDB(String zlevel, String message, String stacktrace ) {	
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		TimeZone tz = TimeZone.getTimeZone("Europe/Berlin"); 
		sdf.setTimeZone(tz);
		Connection conn = null;
		PreparedStatement ps = null;
			try {
				conn = ConnectionUtil.getConnection();
				ps = conn.prepareStatement(QUERY_INSERTLOG);
				ps.setString(1, sdf.format(new Date()));
				ps.setString(2, zlevel);
				ps.setString(3, message);
				ps.setString(4, stacktrace);
				ps.executeUpdate();
			} catch (Exception e){
				System.out.println("Unable to the event to database");
				System.out.println(LoggerDAO.getStackTrace(e));
			} finally {
				try {
					if (ps != null) ps.close();
					if (conn != null) conn.close();	
				} catch (Exception e){
					// do not log
				}
			}
		}
	
	/**
	 * Returns the stack trace as string
	 * @param the exception
	 * @return the stack trace as string
	 */
	public static String getStackTrace(Exception e){
		StringWriter errors = new StringWriter();
		e.printStackTrace(new PrintWriter(errors));
		return errors.toString();
	}
}
