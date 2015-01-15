package com.sogeti.mci.migration.business;

import java.util.List;

import com.sogeti.mci.migration.model.Input;
import com.sogeti.mci.migration.service.CsvService;

public class External {

	public static void main(String args[]) {
		String csvFilename = "events.csv";
		csvFilename = "."+java.io.File.separator+"src"+java.io.File.separator+"main"+java.io.File.separator+"resources"+java.io.File.separator+csvFilename;			      
		List<Input> inputs = CsvService.getEvents(csvFilename);		
		for (Input input : inputs) {
			String[] argv = new String[]{input.getEventName(),input.getSite(),input.getTemporaryEventMailbox(), input.getEventType(), input.getLeaderName(), input.getTeamMembers()};
			Migrator.main(argv);
		}
	}
}
