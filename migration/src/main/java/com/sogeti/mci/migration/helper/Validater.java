package com.sogeti.mci.migration.helper;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.routines.EmailValidator;

import com.sogeti.mci.migration.model.Input;

public class Validater {
	
	public static boolean validateInput(Input input) {
		
		boolean isValid = true;
		isValid = isValid && input!=null;
		if (isValid) {
			isValid = isValid && StringUtils.isNotEmpty(input.getEventName());
					if (isValid) {
						isValid = isValid && StringUtils.isNotEmpty(input.getSite());
								if (isValid) {
									isValid = isValid && EmailValidator.getInstance().isValid(input.getTemporaryEventMailbox());
											if (isValid) {
												isValid = isValid && StringUtils.isNotEmpty(input.getEventType());
														if (isValid) {
															isValid = isValid && EmailValidator.getInstance().isValid(input.getLeaderName());
																	if (isValid) {
																		String[] tab = input.getTeamMembersInArray();
																		for (int i = 0; i < tab.length; i++) {
																			isValid = isValid && EmailValidator.getInstance().isValid(tab[i]);
																		}
																		if (!isValid) {
																			System.out.println(input.getEventName()+": Invalid members ");
																		} 
																		
																	} else {
																		System.out.println(input.getEventName()+": Invalid team leader ");
																	}
														} else {
															System.out.println(input.getEventName()+": Invalid type ");
														}
											} else {
												System.out.println(input.getEventName()+": Invalid applicative account ");
											}
								} else {
									System.out.println(input.getEventName()+": Invalid site ");
								}
					} else {
						System.out.println(input.getEventName()+": Invalid event ");
					}
		} else {
			System.out.println(" null event ");
		}

		return isValid;
	}
	
	public static Input validateInput(String args[]) {
		boolean isValid = true;
		isValid = isValid && args.length>=6;
		Input input = new Input();
		if (isValid) {
			isValid = isValid && StringUtils.isNotEmpty(args[0]);
					if (isValid) {
						input.setEventName(args[0]);
						isValid = isValid && EmailValidator.getInstance().isValid(args[1]);
							if (isValid) {
								input.setEventEmailAddress(args[1]);
								isValid = isValid && StringUtils.isNotEmpty(args[2]);
											if (isValid) {
												input.setSite(args[2]);
												isValid = isValid && EmailValidator.getInstance().isValid(args[3]);
														if (isValid) {
															input.setTemporaryEventMailbox(args[3]);
															isValid = isValid && StringUtils.isNotEmpty(args[4]);
																	if (isValid) {
																		input.setEventType(args[4]);
																		isValid = isValid && EmailValidator.getInstance().isValid(args[5]);
																				if (isValid) {
																					input.setLeaderName(args[5]);
																					String[] tab = args[6].split(",");
																					for (int i = 0; i < tab.length; i++) {
																						isValid = isValid && EmailValidator.getInstance().isValid(tab[i]);
																					}
																					if (isValid) {
																						input.setTeamMembers(args[6]);
																					} else {
																						System.err.println(args[0]+": Invalid members in "+ args[6]);
																					}
																					
																				} else {
																					System.err.println(args[0]+": Invalid team leader in "+ args[5]);
																				}
																	} else {
																		System.err.println(args[0]+": Invalid type in "+ args[4]);
																	}
														} else {
															System.err.println(args[0]+": Invalid temporary mailbox in "+ args[3]);
														}
											} else {
												System.err.println(args[0]+": Invalid site in "+ args[2]);
											}
							} else {
								System.err.println(args[0]+": Invalid event email address in "+ args[1]);
							}
					} else {
						System.err.println(" Invalid event in "+ args[0]);
					}
			} else {
				System.err.println(" Not enough arguments");
			}
		if (!isValid) {
			input=null;
		}
		return input;
	}
		


}
