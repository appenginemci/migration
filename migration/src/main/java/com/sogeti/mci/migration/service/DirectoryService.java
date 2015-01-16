package com.sogeti.mci.migration.service;

import java.util.List;

import com.google.api.services.admin.directory.Directory;
import com.google.api.services.admin.directory.model.Alias;
import com.google.api.services.admin.directory.model.Group;
import com.google.api.services.admin.directory.model.Member;
import com.google.api.services.admin.directory.model.User;
import com.sogeti.mci.migration.api.DirectoryAPI;
import com.sogeti.mci.migration.business.EventMigrator;
import com.sogeti.mci.migration.helper.PropertiesManager;


public class DirectoryService {
	
	static Directory directory = EventMigrator.getDirectory();

	public static Group insertGroup(String eventName, String mailbox) {
		Group group = getGroup(mailbox);
		if (group==null) {
			group = DirectoryAPI.createGroup(directory, eventName, mailbox);
		}		
		return group;		
	}

	public static Group getGroup(String mailbox) {
		String domain = PropertiesManager.getProperty("domain"); 
		List<Group> groups = DirectoryAPI.listGroup(directory, domain);
		for(Group group : groups) {
			if (group.getEmail().equals(mailbox)) {
				return group;
			}
		}
		return null;
	}
	
	public static Member addUsersToGroup(String groupId, String userId) {
		Member googleMember = null;
		User user = DirectoryAPI.getUser(directory, userId);		
		if (user!=null) {
			googleMember = DirectoryAPI.getMemberFromGroup(directory, groupId, userId);
			if (googleMember==null) {
				googleMember = new Member();
				googleMember.setEmail(user.getPrimaryEmail());
				googleMember = DirectoryAPI.insertMemberToGroup(directory, groupId, googleMember);
			}
		} 
		return googleMember;
	}
	
	public static Alias addAlias( String account, String aliasName) {
		Alias alias = getAlias(aliasName);
		if (alias==null) {
			alias = DirectoryAPI.addAlias(directory, account, aliasName);
		}
		return alias;
	}
	
	public static Alias getAlias(String aliasName) {
		List<Alias> aliases = DirectoryAPI.getlistAlias(directory,aliasName);
		for (Alias alias : aliases) {
			if (alias.getAlias().equals(aliasName)) {
				return alias;
			}
		}
		return null;
	}
	

}
