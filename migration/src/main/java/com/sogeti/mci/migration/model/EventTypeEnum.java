package com.sogeti.mci.migration.model;

public enum EventTypeEnum {
	ABSTRACT("abstract"),
	CONGRESS("congress");
	
	private final String consumerType;
	private EventTypeEnum(String consumerType) {
		this.consumerType = consumerType;
	}
	public String getConsumerType() {
		return consumerType;
	}
}
