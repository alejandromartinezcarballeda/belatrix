package com.belatrix;

public enum LogType {

	// error has higher priority, it should be value 3 
	NONE(0,"NONE"),MESSAGE(1,"MESSAGE"), ERROR(2,"ERROR"), WARNING(3,"WARNING");
	
	public int getLogType() {
		return logType;
	}

	public String getStringLogType() {
		return stringLogType;
	}

	private int logType;
	private String stringLogType;
	
	LogType(int logType,String stringLogType) {
		this.logType = logType;
		this.stringLogType = stringLogType;
	}
	

}
