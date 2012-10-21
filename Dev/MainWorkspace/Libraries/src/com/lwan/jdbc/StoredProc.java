package com.lwan.jdbc;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.List;

public class StoredProc {
	
	HashMap<String, Object> params;
	List<String> origStrings;
	List<PreparedStatement> statements;
	
	
	public void execute() {
		// for each statement
		
			// replace all params with '?'
		
			// pass in the correct corrosponding params 
	}
	
	public void clearParameters() {
//		for ()
	}
	
	public void getParamByName(String name) {
		
	}
	
	public Parameter getParam(int index) {
		return null;
	}
	
//	public Parameter<?>
	
	public ResultSet getResult() {
		return null;
	}
}
