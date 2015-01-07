package com.sogeti.mci.migration.service;

import com.sogeti.mci.migration.dao.MemberDAO;
import com.sogeti.mci.migration.model.Member;

public class MemberService {
	
	public static Member insertMember(String userId) {
		return insertMember(userId, null);
	}
	
	public static Member insertMember(String userId, String userName) {
		Member member = getMember(userId);
		if (member==null) {
			member = new Member();
			member.setUserId(userId);
			if (userName==null) {
				int index = userId.indexOf("@");
				if (index>-1) {
					userName = userId.substring(0,index).replaceAll("\\.", " ");
				}
			}
			member.setUserName(userName);
			member = MemberDAO.insertMember(member);
		}
		return member;
	}
	
	public static Member getMember (String userId) {
		Member member = MemberDAO.getMember(userId);
		return member;
	}


}
