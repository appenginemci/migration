package com.sogeti.mci.migration.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.sogeti.mci.migration.helper.ConnectionUtil;
import com.sogeti.mci.migration.model.EventMember;
import com.sogeti.mci.migration.model.Member;

public class MemberDAO {
	
	
	public static Member insertMember(Member member) {
		Connection conn = ConnectionUtil.getConnection();
		try {
			try {
				String statement = "INSERT INTO member (userId, userName) VALUES  (?,?)";
				PreparedStatement stmt;

				stmt = conn.prepareStatement(statement);
				stmt.setString(1, member.getUserId());
				stmt.setString(2, member.getUserName());

				int result = stmt.executeUpdate();
				if(result == 1) {
					statement = "SELECT LAST_INSERT_ID() as id";
					stmt = conn.prepareStatement(statement);
					ResultSet resultSet = stmt.executeQuery();
					resultSet.next();
					member.setDbId(resultSet.getInt(1));
				}
			} finally {
				conn.close();
			}
		} catch (SQLException e1) {
			System.err.println("connection error");
		}		
		return member;
				
	}

	public static Member getMember(String userId) {
		Connection conn = ConnectionUtil.getConnection();
		try {
			try {
				String statement = "SELECT id,userId, userName FROM member where userId = ? ";
				PreparedStatement stmt;
				
				stmt = conn.prepareStatement(statement);
				stmt.setString(1, userId);
				
				ResultSet resultSet = stmt.executeQuery();
				if (resultSet.next()) {
					Member member = new Member();
					member.setDbId(resultSet.getInt("id"));
					member.setUserId(resultSet.getString("userId"));
					member.setUserName(resultSet.getString("userName"));
					return member;
				}
			} finally {
				conn.close();
			}
		} catch (SQLException e1) {
			System.err.println("connection error");
		}
		return null;
	}
	
	public static EventMember insertEventMember(EventMember eventMember) {
		Connection conn = ConnectionUtil.getConnection();
		try {
			try {
				String statement = "INSERT INTO eventmember (event_id, user_id, role, active, in_progress_folder_id, for_approval_folder_id) VALUES  (?,?,?,?,?,?)";
				PreparedStatement stmt;

				stmt = conn.prepareStatement(statement);
				stmt.setInt(1, eventMember.getEventId());
				stmt.setInt(2, eventMember.getUserId());
				stmt.setString(3, eventMember.getRole());
				stmt.setBoolean(4, eventMember.isActive());
				stmt.setString(5, eventMember.getInProgressFolderId());
				stmt.setString(6, eventMember.getForApprovalFolderId());

				int result = stmt.executeUpdate();
				if(result == 1) {
					statement = "SELECT LAST_INSERT_ID() as id";
					stmt = conn.prepareStatement(statement);
					ResultSet resultSet = stmt.executeQuery();
					resultSet.next();
					eventMember.setId(resultSet.getInt(1));
				}
			} finally {
				conn.close();
			}
		} catch (SQLException e1) {
			System.err.println("connection error");
		}		
		return eventMember;
				
	}

	public static EventMember getEventMember(int eventId, int userId) {
		Connection conn = ConnectionUtil.getConnection();
		try {
			try {
				String statement = "SELECT id,event_id, user_id, role, active, in_progress_folder_id, for_approval_folder_id FROM eventmember where event_id=? and user_id = ? ";
				PreparedStatement stmt;
				
				stmt = conn.prepareStatement(statement);
				stmt.setInt(1, eventId);
				stmt.setInt(2, userId);
				
				ResultSet resultSet = stmt.executeQuery();
				if (resultSet.next()) {
					EventMember eventMember = new EventMember();
					eventMember.setId(resultSet.getInt("id"));
					eventMember.setUserId(resultSet.getInt("user_id"));
					eventMember.setEventId(resultSet.getInt("event_id"));
					eventMember.setRole(resultSet.getString("role"));
					eventMember.setActive(resultSet.getBoolean("active"));
					eventMember.setInProgressFolderId(resultSet.getString("in_progress_folder_id"));
					eventMember.setForApprovalFolderId(resultSet.getString("for_approval_folder_id"));
					return eventMember;
				}
			} finally {
				conn.close();
			}
		} catch (SQLException e1) {
			System.err.println("connection error");
		}
		return null;
	}

	public static void addInProgressFolder(Member member, String parentFolderId) {
		Connection conn = ConnectionUtil.getConnection();
		try {
			try {
				String statement = "UPDATE eventmember set in_progress_folder_id=? where id=?";
				PreparedStatement stmt;

				stmt = conn.prepareStatement(statement);
				stmt.setString(1, parentFolderId);
				stmt.setInt(2, member.getDbId());

				stmt.executeUpdate();

			} finally {
				conn.close();
			}
		} catch (SQLException e1) {
			System.err.println("connection error");
		}		
		
	}

}
