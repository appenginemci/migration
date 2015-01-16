package com.sogeti.mci.migration.service;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.bean.ColumnPositionMappingStrategy;
import au.com.bytecode.opencsv.bean.CsvToBean;

import com.sogeti.mci.migration.model.Input;


public class CsvService {

	private final static char DEFAULT_SEPARATOR = ';';
   
   @SuppressWarnings({"rawtypes", "unchecked"})
   public static List<Input> getEvents(String csvFilename)
   {
      CsvToBean csv = new CsvToBean();
      List<Input> list = new ArrayList<Input>();
      
      CSVReader csvReader;
	try {
		  csvReader = new CSVReader(new FileReader(csvFilename), DEFAULT_SEPARATOR );
	      list = csv.parse(setColumMapping(), csvReader);
	} catch (FileNotFoundException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
      
      return list;
   }

   @SuppressWarnings({"rawtypes", "unchecked"})
   private static ColumnPositionMappingStrategy setColumMapping()
   {
      ColumnPositionMappingStrategy strategy = new ColumnPositionMappingStrategy();
      strategy.setType(Input.class);
      String[] columns = new String[] {"eventName", "eventEmailAddress","site", "temporaryEventMailbox", "eventType", "leaderName", "teamMembers"};
      strategy.setColumnMapping(columns);
      return strategy;
   }	   
}
