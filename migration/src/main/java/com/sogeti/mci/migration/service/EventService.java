package com.sogeti.mci.migration.service;

import javax.mail.Address;
import javax.mail.internet.InternetAddress;

import com.google.api.services.admin.directory.model.Group;
import com.google.api.services.drive.model.File;
import com.sogeti.mci.migration.dao.EventDAO;
import com.sogeti.mci.migration.dao.LoggerDAO;
import com.sogeti.mci.migration.dao.MemberDAO;
import com.sogeti.mci.migration.dao.SettingsDAO;
import com.sogeti.mci.migration.model.Event;
import com.sogeti.mci.migration.model.EventMember;
import com.sogeti.mci.migration.model.Member;
import com.sogeti.mci.migration.model.Site;
import com.sogeti.mci.migration.model.UserRoleEnum;

public class EventService {


	public static Event insertEvent(String eventName, String mailbox,
			String eventType, Site site, File eventFolder) {
		Event event = EventDAO.getEvent(eventName, mailbox);		
		if (event==null) {
			event = new Event();
			event.setName(eventName);
			event.setType(eventType);
			Group group = DirectoryService.insertGroup(eventName, mailbox);
			event.setGroupId(group!=null?group.getId():null);
			event.setEventFolderId(eventFolder.getId());
			event.setAttachmentsFolderId(eventFolder.getId());
			event.setClosedFolderId(eventFolder.getId());
			event.setMail(mailbox);
			event.setNewFolderId(eventFolder.getId());	
			event.setSiteId(site.getDbId());
			event = EventDAO.insertEvent(event);
		}
		return event;	
	}
	
	public static Event attachMemberToEvent(Member member,
			UserRoleEnum role, Event event) {
		EventMember em = MemberDAO.getEventMember(event.getDbId(), member.getDbId());
		if (em==null) {
			em = new EventMember();
			em.setActive(true);
			em.setEventId(event.getDbId());
			em.setRole(role.getUserRole());
			em.setUserId(member.getDbId());
			em = MemberDAO.insertEventMember(em);
			event.addEvent(em);
		}
		return event;
	}
	
	public static Event getEventByRecipient(Address[] addresses) {
		Event ev = null;
		String pattern = SettingsDAO.getInstance().getSetting("patternEventEmail");
		if (addresses != null && addresses.length > 0){
			for (int i=0; i<addresses.length; i++){
				InternetAddress tmpAddr = (InternetAddress)addresses[i];
				if (tmpAddr.getAddress().matches(pattern)){
					ev = EventDAO.getEventByRecipient(tmpAddr.getAddress());
					if (ev != null) break;
				} else {
					LoggerDAO.getInstance().debug(tmpAddr.getAddress() + " does not match with the pattern " + pattern);
					System.out.println(tmpAddr.getAddress() + " does not match with the pattern " + pattern);
				}
			}
		}
		return ev;
	}
	
	public static Event getEventByAccount(String account) {
		Event ev = null;
		String pattern = SettingsDAO.getInstance().getSetting("patternEventEmail");
		if (account.matches(pattern)){
			ev = EventDAO.getEventByRecipient(account);
		} else {
			LoggerDAO.getInstance().debug(account + " does not match with the pattern " + pattern);
			System.out.println(account + " does not match with the pattern " + pattern);
		}
		return ev;
	}

}
