package com.sogeti.mci.migration.api;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.google.api.services.admin.directory.Directory;
import com.google.api.services.admin.directory.model.Alias;
import com.google.api.services.admin.directory.model.Group;
import com.google.api.services.admin.directory.model.Groups;
import com.google.api.services.admin.directory.model.Member;
import com.google.api.services.admin.directory.model.User;
import com.sogeti.mci.migration.helper.APIException;

public class DirectoryAPI {
	
	public static Group createGroup(Directory directory, String groupName, String groupEmail) {
		Group addedGroup = null;
		try {
			addedGroup = new Group();
			addedGroup.setName(groupName);
			addedGroup.setEmail(groupEmail);
			addedGroup = directory.groups().insert(addedGroup).execute();
		} catch (IOException ge) {
			APIException.handleException(ge, "Failed to create group "+groupName, 409);
			addedGroup = null;
		} 
		return addedGroup;
	}

	public static List<Group> listGroup(Directory directory, String domain) {
		List<Group> listGroup = new ArrayList<Group>();
		Groups groups = null;
		try {
			 groups = directory.groups().list().setDomain(domain).execute();
			while (groups.getGroups() != null) {
				listGroup.addAll(groups.getGroups());
				if (groups.getNextPageToken() != null) {
					String pageToken = groups.getNextPageToken();
					groups = directory.groups().list().setDomain(domain).setPageToken(pageToken).execute();
				} else {
					break;
				}
			}
		} catch (IOException e) {
			APIException.handleException(e, "Failed to list group for this domain "+domain, 0);
		}
		return listGroup;
	}
	
	public static User getUser(Directory directory, String userId) {
		User user = null;
		try {
			 user = directory.users().get(userId).execute();
		} catch (IOException ge) {
			APIException.handleException(ge, "Failed to get "+userId, 404);
		}
		return user;
	}
	
	public static Member insertMemberToGroup(Directory directory, String groupId,  Member memberToAdd)  {
		Member member = null;
		try {
			member = directory.members().insert(groupId, memberToAdd).execute();
		} catch (IOException e) {
			APIException.handleException(e,"Error while inserting user",0);
		}
		return member;
	}
	
	public static Member getMemberFromGroup(Directory directory, String groupId,  String userId)  {
		Member member = null;
		try {
			member = directory.members().get(groupId, userId).execute();
		} catch (IOException ge) {
			APIException.handleException(ge, "Failed to add "+userId+" to "+groupId, 404);
		}
		return member;
	}

	public static List<Alias> getlistAlias(Directory directory, String userId) {
		List<Alias> listAlias = new ArrayList<Alias>();
		try {
			listAlias.addAll(directory.users().aliases().list(userId).execute().getAliases());
		} catch (IOException e) {
			APIException.handleException(e, "Failed to  for this  ", 0);
		}
		return listAlias;
	}
	
	public static Alias addAlias(Directory directory, String account, String alias) {
		Alias aliasForAdmin = new Alias();
		aliasForAdmin.setAlias(alias);
		try {
			aliasForAdmin =  directory.users().aliases().insert(account, aliasForAdmin).execute();
		} catch (IOException e) {
			APIException.handleException(e, "Failed to create alias "+aliasForAdmin, 0);
			aliasForAdmin = null;
		}
		return aliasForAdmin;
	}

}
