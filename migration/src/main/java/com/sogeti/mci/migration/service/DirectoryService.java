package com.sogeti.mci.migration.service;

import com.google.api.services.admin.directory.Directory;
import com.google.api.services.admin.directory.model.Group;
import com.google.api.services.admin.directory.model.Groups;
import com.google.api.services.admin.directory.model.Member;
import com.google.api.services.admin.directory.model.User;
import com.sogeti.mci.migration.api.DirectoryAPI;
import com.sogeti.mci.migration.business.Launcher;
import com.sogeti.mci.migration.helper.PropertiesManager;


public class DirectoryService {
	
	static Directory directory = Launcher.getDirectory();

	public static Group insertGroup(String eventName, String mailbox) {
		Group group = getGroup(mailbox);
		if (group==null) {
			group = DirectoryAPI.createGroup(directory, eventName, mailbox);
		}		
		return group;		
	}

	public static Group getGroup(String mailbox) {
		String domain = PropertiesManager.getProperty("domain"); 
		Groups groups = DirectoryAPI.listGroup(directory, domain);
		for(Group group : groups.getGroups()) {
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




	

}
