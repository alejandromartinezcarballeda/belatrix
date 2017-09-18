package com.belatrix.connection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Map;
import java.util.Properties;

public class ConnectionManager {
	
	private static Connection conn;
	
	// assuming no big concurrency
	public static Connection getConnection(Map dbParams) {
		
		if (conn == null) {
			
			Properties connectionProps = new Properties();
			connectionProps.put("user", dbParams.get("userName"));
			connectionProps.put("password", dbParams.get("password"));

			try {
				conn = DriverManager.getConnection("jdbc:" + dbParams.get("dbms") + "://" + dbParams.get("serverName")
						+ ":" + dbParams.get("portNumber") + "/", connectionProps);
				
			} catch (SQLException e) {
				e.printStackTrace();
			}
			
		}
		return conn;
	}

}
