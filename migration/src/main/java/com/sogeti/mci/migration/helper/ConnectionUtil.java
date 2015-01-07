package com.sogeti.mci.migration.helper;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionUtil {

	public static Connection getConnection() {
		String url = null;
		Connection conn = null;
		String user = null;
		String password = null;
		try {
				Class.forName("com.mysql.jdbc.Driver");
				url = PropertiesManager.getProperty("url_local");
				user = PropertiesManager.getProperty("user_local");
				password = PropertiesManager.getProperty("password_local");


		} catch (Exception e) {
			System.err.println(e.getMessage());
			return null;
		}
		try {
			conn = DriverManager.getConnection(url, user, password);
			return conn;
		} catch (SQLException e) {e.printStackTrace();
			System.err.println(e.getMessage());
		} 
		return conn;
	}
}
