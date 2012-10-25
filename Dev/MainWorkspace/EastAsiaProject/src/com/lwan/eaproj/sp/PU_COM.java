package com.lwan.eaproj.sp;

import java.sql.Types;

import com.lwan.jdbc.StoredProc;

public class PU_COM extends StoredProc{
	private static final String[] PARAMS = {"@com_id", "@cdt_id", "@com_name"};
	private static final int[] PARAM_TYPES = {Types.INTEGER, Types.INTEGER, Types.VARCHAR};
	private static String[] STATEMENTS = {
//		// get the old cdt_id
//		"select cdt_id " +
//		"from COM_company " +
//		"where com_id = @com_id",
//		
//		// delete old cdt_id.. this is only executed if the old cdt_id is diff from current
//		"delete from CDT_contact_details " +
//		"where cdt_id = @cdt_id",
		
		// update the record
		"update COM_company " +
		"set cdt_id = @cdt_id, " +
		"	com_name = @com_name " +
		"where com_id = @com_id"
	};
	
//	public void execute(Connection con) throws SQLException {
//		// this will effectively null result.. not that there should be anything in it anyway
//		getResult();	
//		
//		doExecute(con, 0);
//		
//		ResultSet rs = getResult();	// this will ensure the user never sees the result
//		rs.next();	// there should be one and only one record returned from this
//		int oldCDTid = rs.getInt(1);
//		rs.getStatement().close();
//
//		doExecute(con, 2);
//		
//		Parameter cdtId = getParamByName("@cdt_id");
//		if (!GenericsUtil.Equals(cdtId.get(), oldCDTid)) {
//			int newCDTid = (int) cdtId.get();
//			cdtId.set(oldCDTid);
//			doExecute(con, 1);
//			cdtId.set(newCDTid);
//		}
//	}
	
	public PU_COM () {
		super(PARAMS, PARAM_TYPES, STATEMENTS);
	}
}
