package com.sogeti.mci.migration.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.sogeti.mci.migration.helper.ConnectionUtil;
import com.sogeti.mci.migration.model.Event;

public class EventDAO {
	

	
	public static Event insertEvent(Event event) {
		Connection conn = ConnectionUtil.getConnection();
		try {
			try {
				String statement = "INSERT INTO event(eventName,googleGroupId,folderId,email,inboxNewFolderId,attachmentFolderId,closedFolderId,site_id,evtType) VALUES  (?,?,?,?,?,?,?,?,?)";
				PreparedStatement stmt;

				stmt = conn.prepareStatement(statement);
				stmt.setString(1, event.getName());
				stmt.setString(2, event.getGroupId());
				stmt.setString(3, event.getEventFolderId());
				//stmt.setString(4, event.getMail() + "@" + PropertiesManager.getProperty("domain"));
				stmt.setString(4, event.getMail());
				stmt.setString(5, event.getNewFolderId());
				stmt.setString(6, event.getAttachmentsFolderId());
				stmt.setString(7, event.getClosedFolderId());
				stmt.setInt(8, event.getSiteId());
				stmt.setString(9, event.getType());

				int result = stmt.executeUpdate();
				if(result == 1) {
					statement = "SELECT LAST_INSERT_ID() as id";
					stmt = conn.prepareStatement(statement);
					ResultSet resultSet = stmt.executeQuery();
					resultSet.next();
					event.setDbId(resultSet.getInt(1));
				}
			} finally {
				conn.close();
			}
		} catch (SQLException e1) {
			System.err.println("connection error");
		}
		return event;
	}

	public static Event getEvent(String eventName, String mailbox) {
		Connection conn = ConnectionUtil.getConnection();
		try {
			try {
				String statement = "SELECT id,eventName,googleGroupId,folderId,email,inboxNewFolderId,attachmentFolderId,closedFolderId,site_id,evtType from event where eventName = ? and email=?";
				PreparedStatement stmt;
				
				stmt = conn.prepareStatement(statement);
				stmt.setString(1, eventName);
				stmt.setString(2, mailbox);
				
				ResultSet resultSet = stmt.executeQuery();
				if (resultSet.next()) {
					Event event = new Event();
					event.setDbId(resultSet.getInt("id"));
					event.setName(resultSet.getString("eventName"));
					event.setGroupId(resultSet.getString("googleGroupId"));
					event.setEventFolderId(resultSet.getString("folderId"));
					event.setMail(resultSet.getString("email"));
					event.setNewFolderId(resultSet.getString("inboxNewFolderId"));
					event.setAttachmentsFolderId(resultSet.getString("attachmentFolderId"));
					event.setClosedFolderId(resultSet.getString("closedFolderId"));
					event.setSiteId(resultSet.getInt("site_id"));
					event.setType(resultSet.getString("evtType"));
					return event;
				}
			} finally {
				conn.close();
			}
		} catch (SQLException e1) {
			System.err.println("connection error");
		}
		return null;
	}
	
	public static Event getEventByRecipient(String mailbox){
		Connection conn = ConnectionUtil.getConnection();
		try {
			try {
				String statement = "SELECT id,eventName,googleGroupId,folderId,email,inboxNewFolderId,attachmentFolderId,closedFolderId,site_id,evtType from event where email=?";
				PreparedStatement stmt;
				
				stmt = conn.prepareStatement(statement);
				stmt.setString(1, mailbox);
				
				ResultSet resultSet = stmt.executeQuery();
				if (resultSet.next()) {
					Event event = new Event();
					event.setDbId(resultSet.getInt("id"));
					event.setName(resultSet.getString("eventName"));
					event.setGroupId(resultSet.getString("googleGroupId"));
					event.setEventFolderId(resultSet.getString("folderId"));
					event.setMail(resultSet.getString("email"));
					event.setNewFolderId(resultSet.getString("inboxNewFolderId"));
					event.setAttachmentsFolderId(resultSet.getString("attachmentFolderId"));
					event.setClosedFolderId(resultSet.getString("closedFolderId"));
					event.setSiteId(resultSet.getInt("site_id"));
					event.setType(resultSet.getString("evtType"));
					return event;
				}
			} finally {
				conn.close();
			}
		} catch (SQLException e1) {
			System.err.println("connection error");
		}
		return null;
	}

}
