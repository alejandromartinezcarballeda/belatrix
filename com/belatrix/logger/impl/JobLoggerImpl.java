package com.belatrix.logger.impl;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.util.Date;
import java.util.Map;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.belatrix.logger.JobLogger;

import com.belatrix.LogType;
import com.belatrix.connection.ConnectionManager;

public class JobLoggerImpl extends JobLogger{
	private static boolean logToFile;
	private static boolean logToConsole;
	private static boolean logMessage;
	private static boolean logWarning;
	private static boolean logError;
	private static boolean logToDatabase;
	
	// variable not used
	//private boolean initialized;
	
	
	private static Map dbParams;
	private static Logger logger;

	// CAN CONSTRUCTOR BE CREATED WITH ALL "LOGTOXXX" AS TRUE?
	public JobLoggerImpl(boolean logToFileParam, 
			boolean logToConsoleParam, 
			boolean logToDatabaseParam,
			boolean logMessageParam, 
			boolean logWarningParam, 
			boolean logErrorParam, 
			Map dbParamsMap) throws Exception {
		
		// this could be throw on constructor
		if (!logToConsole && !logToFile && !logToDatabase) {
			throw new Exception("Invalid configuration");
		}
		
		logger = Logger.getLogger("MyLog");  
		logError = logErrorParam;
		logMessage = logMessageParam;
		logWarning = logWarningParam;
		logToDatabase = logToDatabaseParam;
		logToFile = logToFileParam;
		logToConsole = logToConsoleParam;
		dbParams = dbParamsMap;
		
	}

	
	
	public static void logMessage(String messageText, 
			boolean message, 
			boolean warning, 
			boolean error,
			LogType logT) throws Exception {
		
		/* 
		 * first, check if String is null, if not, trim it and compare to empty string.
		 * if its empty (or null), no log should be made
		 */	
		if (messageText != null) {
			messageText = messageText.trim();
			if ("".equals(messageText)) {
				return;
			}
		} else {
			// if string is null, no log should be made
			return;
		}
		
		
		
		// to make code easier to read, we compare string NONE to the string value of logtype
		if ((!logError && !logMessage && !logWarning)  || "NONE".equals(logT.getStringLogType())) {
			throw new Exception("Error or Warning or Message must be specified");
		}

		// create date variable to make it easier to change in the future.
		String date = DateFormat.getDateInstance(DateFormat.LONG).format(new Date());
		
		// use StringBuilder to minimize the quantity of String objects
		StringBuilder sb = new StringBuilder();
		
		// according on how log4j works, order on how sb should be built was change, so higher priority logtype overrides previous 
		/*
		 *  old approach could save 3 times the same data if error was the type of log,
		 *  now we log only 1 log level per method call.
		 *  i.e., if error was true, then the message would be 3 times on the string to log but with different log levels.
		 */
		sb.append(sb.toString())
		.append(logT.getStringLogType())
		.append(date)
		.append(messageText);
		
		if(logToFile) {
			
			// move this to if(logToFile) sentence, so it executed only when necessary
			logToFile(sb.toString());
		}
		
		if(logToConsole) {
			
			// move this to if(logToConsole) sentence, so it executed only when necessary
			logToConsole(sb.toString());
		}
		
		if(logToDatabase) {
			
			logToDatabase(sb.toString(),logT);
		}
	}
	
	public static void logToFile(String stringToLog) {
		// move this to if(logToFile) sentence, so it executed only when necessary
		// create a String of filelog to use here and *2
		String logFilePath = dbParams.get("logFileFolder") + "/logFile.txt";
		
		File logFile = new File(logFilePath);
		if (!logFile.exists()) {
			try {
				logFile.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		// *2
		FileHandler fh;
		try {
			fh = new FileHandler(logFilePath);
			
			logger.addHandler(fh);
			/*
			 *  as the stringbuilder has specific info (like log type, date and messageText itself), 
			 *  we assume that stringbuilder should be the one to be logged.
			 */
			logger.log(Level.INFO, stringToLog);
			
		} catch (SecurityException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}
		
	}
	
	public static void logToConsole(String stringToLog) {
		// move this to if(logToConsole) sentence, so it executed only when necessary
		ConsoleHandler ch = new ConsoleHandler();
		
		logger.addHandler(ch);
		/*
		 *  as the stringbuilder has specific info (like log type, date and messageText itself), 
		 *  we assume that stringbuilder should be the one to be logged.
		 */
		logger.log(Level.INFO, stringToLog);
	}
	
	
	// consider changing 3 booleans for LogType param to make method call easier to understand
	public static void logToDatabase(String stringToLog,
			LogType logT) {
		
		// this value is used only on DB insert, so it should be moved there
		// make it ENUM to minimize hardcode errors.

		LogType logtype = LogType.NONE;
		if ("MESSAGE".equals(logT.getStringLogType()) && logMessage) {
			logtype = LogType.MESSAGE;
		}
		
		if ("WARNING".equals(logT.getStringLogType()) && logWarning) {
			logtype = LogType.WARNING;
		}

		if ("ERROR".equals(logT.getStringLogType()) && logError) {
			logtype = LogType.ERROR;
		}

		// move this code to if(logToDatabase) sentence, so its executed only when necessary
		// check dbparams != null
		Connection connection = ConnectionManager.getConnection(dbParams);
		
		try {
			
			// move this code to if(logToDatabase) sentence, so its executed only when necessary
			Statement stmt = connection.createStatement();

			// message should be replaced by sb.toString
			/*
			 *  as the stringbuilder has specific info (like log type, date and messageText itself), 
			 *  we assume that stringbuilder should be the one to be logged.
			 */
			stmt.executeUpdate("insert into Log_Values('" + stringToLog + "', " + logtype.getLogType() + ")");
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		} finally {
			try {
				connection.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	
	
	
	
	
}
