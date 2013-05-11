package com.lwan.javafx.app.util;

import java.lang.reflect.Constructor;
import java.security.InvalidParameterException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import com.lwan.javafx.app.sp.PA_PKC_new_id;
import com.lwan.jdbc.GConnection;
import com.lwan.jdbc.StoredProc;

public class DbUtil {
	
	private static StoredProc idProc;
	public static int getNextID(String fieldName) {
		if (idProc == null) {
			idProc = new PA_PKC_new_id();
		}
		int result = 0;
		try {
			idProc.getParamByName("@pkc_name").set(fieldName);
			idProc.execute(GConnection.getConnection());

			ResultSet rs = idProc.getResult();
			if (rs.next()) {
				result = rs.getInt(1);
				rs.getStatement().close();
			} else {
				throw new InvalidParameterException("fieldName: " + fieldName + " dosen't exist in PKC_primary_key_counter");
			}
		
		} catch (SQLException e) {
			e.printStackTrace();
		}		
		
		return result;
	}
	
	private static Map<String, StoredProc> storedProcCache;
	private static String rootPackage;
	
	public static String getRootPackage() {
		if (rootPackage == null) {
			rootPackage = "";
		}
		return rootPackage;
	}
	public static void setRootPackage(String root) {
		rootPackage = root;
		if (root.length() > 0) {
			rootPackage = root + ".";
		}
	}
	
	@SuppressWarnings("unchecked")
	public static <T extends StoredProc> T getStoredProc (String name) {
		if (storedProcCache == null) {
			storedProcCache = new HashMap<>();
		}
		StoredProc sp = storedProcCache.get(name);
		if (sp == null) {
			try {
				Class<T> cls = (Class<T>) Class.forName(rootPackage + name);
				Constructor<T> co = (Constructor<T>)cls.getConstructor();
				sp = co.newInstance();
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		
		return (T)sp;
	}
	
	public static void printResultSet(ResultSet result) throws SQLException {
		int columns = result.getMetaData().getColumnCount();
		for (int i = 1; i <= columns; i++) {
			System.out.print(result.getMetaData().getColumnName(i));
			if (i != columns) {
				System.out.print(", ");
			}
		}
		System.out.println();
		
		while(result.next()) {
			for (int i = 1; i <= columns; i++) {
				Object o = result.getObject(i);
				System.out.print(o.toString() + ":" + o.getClass().getName() + ":" + result.getMetaData().getColumnTypeName(i));
				if (i != columns) {
					System.out.print(", ");
				}
			}
			System.out.println();
		}
	}
}
