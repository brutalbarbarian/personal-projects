package com.lwan.javafx.app.util;

import java.lang.reflect.Constructor;
import java.security.InvalidParameterException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.lwan.javafx.app.sp.PA_PKC_new_id;
import com.lwan.javafx.app.sp.PA_SP_QUERY;
import com.lwan.jdbc.GConnection;
import com.lwan.jdbc.StoredProc;
import com.lwan.util.CollectionUtil;

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
		StoredProc sp = getStoredProcCache().get(name);
		if (sp == null) {
			try {
				Class<T> cls = (Class<T>) Class.forName(rootPackage + name);
				Constructor<T> co = (Constructor<T>)cls.getConstructor();
				sp = co.newInstance();
				getStoredProcCache().put(name, sp);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		
		return (T)sp;
	}
	
	protected static Map<String, StoredProc> getStoredProcCache() {
		if (storedProcCache == null) {
			storedProcCache = new HashMap<>();
		}	
		return storedProcCache;
	}
	
	public static StoredProc getDbStoredProc(String name) {
		StoredProc sp = getStoredProcCache().get(name);
		if (sp == null) {
			StoredProc spQuery = getStoredProcCache().get("PA_SP_QUERY");
			if (spQuery == null) {
				getStoredProcCache().put("PA_SP_QUERY", spQuery = new PA_SP_QUERY());
			}
			spQuery.getParamByName("@sp_name").set(name);
			ResultSet rs = null;
			try {
				spQuery.execute(GConnection.getConnection());
				rs = spQuery.getResult();
				
				ArrayList<String> paramNames = new ArrayList<>();
				ArrayList<Integer> paramTypes = new ArrayList<>();
				while (rs.next()) {
					String paramName = rs.getString("parameter_name");
					int type = getSQLType(rs.getString("data_type"));
					paramNames.add("@" + paramName.substring(1));
					paramTypes.add(type);
				}
				String[] names = CollectionUtil.toArray(paramNames, String.class);
				int[] types = CollectionUtil.toIntArray(paramTypes);
				sp = new StoredProc(names, types, new String[]{
						"call " + name + "(" + CollectionUtil.CollapseStringList(paramNames, ",") + ");"
				});
				getStoredProcCache().put(name, sp);
			} catch (SQLException e) {
				throw new RuntimeException(e);
			} finally {
				if (rs != null) {
					try {
						rs.close();
					} catch (SQLException e) {
						throw new RuntimeException(e);
					}
				}
			}
		}
		
		return sp;
	}
	
	protected static int getSQLType(String name) {
		switch(name) {
		case "varchar":
		case "text":
			return Types.VARCHAR;
		case "int":
		case "double":
		case "float":
			return Types.NUMERIC;
		case "date":
			return Types.DATE;
		}
		
		return 0;
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
				System.out.print((o == null? "null" : o.toString()) + ":" + 
						(o == null? "null" : o.getClass().getName()) + ":" + 
						result.getMetaData().getColumnTypeName(i));
				if (i != columns) {
					System.out.print(", ");
				}
			}
			System.out.println();
		}
	}
}
