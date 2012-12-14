package com.lwan.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Global connection object.
 * initialise() must be called prior to use.
 * 
 * This is for use with a single connection application model.
 * 
 * @author Brutalbarbarian
 *
 */
public class GConnection {
	private static Connection con;
	private static boolean initialised = false;
	
	public static void initialise(String driverClassName, String connectionString, String username, String password) throws ClassNotFoundException, SQLException {
		Class.forName(driverClassName);
		String url = connectionString;
		con = DriverManager.getConnection(url,username,password);
		
		initialised = true;
	}
	
	public static Connection getConnection() {
		if (!initialised) {
			throw new RuntimeException("Connection has not been initialised");
		}
		return con;
	}
	
	public static boolean isInitialised() {
		return initialised;
	}
	
	public static void uninitialise() throws SQLException {
		if (initialised) {
			con.close();
			initialised = false;
			con = null;
		}
	}
}
