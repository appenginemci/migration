package com.sogeti.mci.migration.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.sogeti.mci.migration.helper.ConnectionUtil;


public class DocumentDAO {
	
	private static LoggerDAO logger = LoggerDAO.getInstance();

	final static String GET_DOCID_BY_THRDID = "SELECT document_id FROM document WHERE thread_id=?";
	final static String INSERT_DOCUMENT = "INSERT INTO document (document_id, thread_id, document_name, creation_date, modification_date) VALUES (?,?,?,?,?)";
	final static String UPDATE_DOCUMENT = "UPDATE document SET document_id=?, modification_date=? WHERE thread_id=?";

	public static String getDocumentIdByThreadId(String threadId) {
		String documentId = null;

		PreparedStatement stmt = null;
		Connection conn = null;
		try {
			conn = ConnectionUtil.getConnection();
			stmt = conn.prepareStatement(GET_DOCID_BY_THRDID);
			stmt.setString(1, threadId);
			ResultSet resultSet = stmt.executeQuery();
			if (resultSet.next()) {
				documentId = resultSet.getString("document_id");
				logger.debug("Retrieved documentId : " + documentId);
			}
		} catch (Exception e){
			logger.error("Error while retrieving document Id from database", e);
		} finally {
			try {
				if (stmt != null) stmt.close();
				if (conn != null) conn.close();	
			} catch (Exception e){
				// do not log
			}
		}
		
		return documentId;
	}
	
	public static boolean insertDocument(String documentId, String threadId, String documentName) {
		int executionOK = 0;
		
		PreparedStatement stmt = null;
		Connection conn = null;
		try {
			conn = ConnectionUtil.getConnection();
			stmt = conn.prepareStatement(INSERT_DOCUMENT);
			stmt.setString(1, documentId);
			stmt.setString(2, threadId);
			stmt.setString(3, documentName);
			stmt.setDate(4, new Date(new java.util.Date().getTime()));
			stmt.setDate(5, new Date(new java.util.Date().getTime()));
			executionOK = stmt.executeUpdate();
			if (executionOK>0) {
				logger.debug("inserted documentId and threadId");
			}
		} catch (Exception e){
			logger.error("Error while inserting documentId and threadId to database", e);e.printStackTrace();
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
	
	public static boolean updateDocument(String documentId, String threadId) {
		int executionOK = 0;
		
		PreparedStatement stmt = null;
		Connection conn = null;
		try {
			conn = ConnectionUtil.getConnection();
			stmt = conn.prepareStatement(UPDATE_DOCUMENT);
			stmt.setString(1, documentId);
			stmt.setDate(2, new Date(new java.util.Date().getTime()));
			stmt.setString(3, threadId);
			executionOK = stmt.executeUpdate();
			if (executionOK>0) {
				logger.debug("UPDATED documentId and threadId");
			}
		} catch (Exception e){
			logger.error("Error while UPDATING documentId and threadId to database", e);e.printStackTrace();
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
