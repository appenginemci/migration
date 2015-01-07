package com.sogeti.mci.migration.service;

import java.util.Date;

import com.sogeti.mci.migration.dao.SiteDAO;
import com.sogeti.mci.migration.model.Site;

public class SiteService {
	
	public static Site insertSite (String name, String folderId) {
		Site site = getSite(name, folderId);
		if (site==null) {
			site = new Site();
			site.setName(name);
			site.setFolderId(folderId);
			site.setCreationDate(new Date());
			site = SiteDAO.insertSite(site);
		}
		return site;
	}
	
	public static Site getSite (String name, String folderId) {
		Site site = SiteDAO.getSite(name, folderId);
		return site;
	}

}
