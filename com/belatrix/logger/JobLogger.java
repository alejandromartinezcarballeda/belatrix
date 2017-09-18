package com.belatrix.logger;

import com.belatrix.LogType;

public abstract class JobLogger {
	
	public static void logMessage(String messageText, 
			boolean message, 
			boolean warning, 
			boolean error,
			LogType logT) throws Exception {}
	
	public static void logToFile(String stringToLog) {}
	
	public static void logToConsole(String stringToLog) {}
	
	public static void logToDatabase(String stringToLog,
			LogType logT) {}
	
	
}
