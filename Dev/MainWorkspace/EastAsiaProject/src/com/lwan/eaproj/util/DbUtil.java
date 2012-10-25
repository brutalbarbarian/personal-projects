package com.lwan.eaproj.util;

import java.security.InvalidParameterException;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.lwan.eaproj.sp.PA_PKC_new_id;
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
}