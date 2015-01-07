package com.sogeti.mci.migration.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.sogeti.mci.migration.helper.ConnectionUtil;
import com.sogeti.mci.migration.model.Site;

public class SiteDAO {
	
	public static Site insertSite(Site site) {
		Connection conn = ConnectionUtil.getConnection();
		try {
			try {
				String statement = "INSERT INTO site (name,folder_id, creation_date) VALUES  (?,?,?)";
				PreparedStatement stmt;

				stmt = conn.prepareStatement(statement);
				stmt.setString(1, site.getName());
				stmt.setString(2, site.getFolderId());
				stmt.setDate(3, new java.sql.Date(site.getCreationDate().getTime()));

				int result = stmt.executeUpdate();
				if(result == 1) {
					statement = "SELECT LAST_INSERT_ID() as id";
					stmt = conn.prepareStatement(statement);
					ResultSet resultSet = stmt.executeQuery();
					resultSet.next();
					site.setDbId(resultSet.getInt(1));
				}
			} finally {
				conn.close();
			}
		} catch (SQLException e1) {
			System.err.println("connection error");
		}		
		return site;
				
	}

	public static Site getSite(String name, String folderId) {
		Connection conn = ConnectionUtil.getConnection();
		try {
			try {
				String statement = "SELECT id,name, folder_id, creation_date FROM site where name = ? and folder_id=?";
				PreparedStatement stmt;
				
				stmt = conn.prepareStatement(statement);
				stmt.setString(1, name);
				stmt.setString(2, folderId);
				
				ResultSet resultSet = stmt.executeQuery();
				if (resultSet.next()) {
					Site site = new Site();
					site.setDbId(resultSet.getInt("id"));
					site.setFolderId(resultSet.getString("folder_id"));
					site.setName(resultSet.getString("name"));
					site.setCreationDate(resultSet.getDate("creation_date"));
					return site;
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
