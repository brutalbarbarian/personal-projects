package com.lwan.eaproj.cache;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.lwan.bo.db.BODbSet;
import com.lwan.eaproj.bo.BOCustomer;
import com.lwan.eaproj.bo.BOStudent;
import com.lwan.eaproj.sp.PS_CUS;
import com.lwan.eaproj.util.BOConstants;
import com.lwan.jdbc.GConnection;
import com.lwan.jdbc.StoredProc;

public class GCustomers extends BODbSet<BOCustomer> {
	private static GCustomers cache;
	
	public static GCustomers get() {
		if (cache == null) {
			cache = new GCustomers();
		}
		return cache;
	}
	
	public static BOCustomer findCustomerID(int id) {
		return get().findChildByID(id);
	}
	
	private StoredProc childType;
	
	public GCustomers() {
		super(null, "CustomerCache", "CustomerID", "cus_id");
		LoadMode().setValue(LOADMODE_CACHE);
	}

	@Override
	protected void createStoredProcs() {
		childType = new PS_CUS();
	}

	@Override
	protected BOCustomer createChildInstance(Object id) {
		// Needs to dynamically create the child instance depending on what the
		// type is.
		
		childType.getParamByName("@" + ChildIDFieldName().getValue()).set(id);
		ResultSet res = null;
		try {
			childType.execute(GConnection.getConnection());
			res = childType.getResult();
			int type = res.getInt("cty_id");
			switch (type) {
			case BOConstants.CTY_DEFAULT : return new BOCustomer(this);
			case BOConstants.CTY_STUDENT : return new BOStudent(this);
			default : return null; // Shouldn't happen???
			}		
		} catch (SQLException e) {
			throw new RuntimeException("Failed to create child instance", e);
		} finally {
			if (res != null) {
				try {
					res.getStatement().close();
				} catch (SQLException e) {
					throw new RuntimeException(
							"Failed to close resultset from creating child instance", e);
				}
			}
		}
	}

}
