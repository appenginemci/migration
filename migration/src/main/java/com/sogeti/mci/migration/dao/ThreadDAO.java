package com.sogeti.mci.migration.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;

import com.sogeti.mci.migration.helper.ConnectionUtil;

public class ThreadDAO {
	private static LoggerDAO logger = LoggerDAO.getInstance();

	final static String INSERT_THREAD = "INSERT INTO thread ( thread_id, event_id, creation_date, modification_date) VALUES (?,?,?,?)";

	public static boolean insertThread(Long eventId, String threadId) {
		int executionOK = 0;
		
		PreparedStatement stmt = null;
		Connection conn = null;
		try {
			conn = ConnectionUtil.getConnection();
			stmt = conn.prepareStatement(INSERT_THREAD);
			stmt.setString(1, threadId);
			stmt.setLong(2, eventId);
			stmt.setDate(3, new Date(new java.util.Date().getTime()));
			stmt.setDate(4, new Date(new java.util.Date().getTime()));
			executionOK = stmt.executeUpdate();
			if (executionOK>0) {
				logger.debug("inserted eventId and threadId");
			}
		} catch (Exception e){
			logger.error("Error while inserting eventIds and threadId to database", e);e.printStackTrace();
		} finally {
			try {
				if (stmt != null) stmt.close();
				if (conn != null) conn.close();	
			} catch (Exception e){
				// do not log
			}
		}
		
		return executionOK>0;
	}
	
}
