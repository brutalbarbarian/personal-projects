package com.lwan.bo.db;

import java.sql.ResultSet;
import java.sql.SQLException;

import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import com.lwan.bo.BOAttribute;
import com.lwan.bo.BOSet;
import com.lwan.bo.BusinessObject;
import com.lwan.jdbc.GConnection;
import com.lwan.jdbc.Parameter;
import com.lwan.jdbc.StoredProc;

public abstract class BODatabaseSet<T extends BODatabaseObject> extends BOSet<T> {
	Property<StoredProc> select_stored_proc;
	
	public Property<StoredProc> SelectStoredProc() {
		if (select_stored_proc == null) {
			select_stored_proc = new SimpleObjectProperty<StoredProc>(this, "SelectStoredProc");
		}
		return select_stored_proc;
	}
	

	public BODatabaseSet(BusinessObject owner, String name, String childIdName) {
		super(owner, name, childIdName);
		createStoredProcs();
	}
	
	/**
	 * Method called during initialisation.
	 * The user should be setting the properties SelectStoredProc,
	 * UpdateStoredProc, InsertStoredProc and DeleteStoredProc from this method.
	 * 
	 */
	protected abstract void createStoredProcs();

	@Override
	protected boolean populateAttributes() {
		// if the SelectStoredProc requires any parameters...
		// try see if this object has them... if it dosen't..
		// go looking in the direct parent's attributes?
		StoredProc sp = SelectStoredProc().getValue();
		if (sp != null) {
			try {
				populateParameters(sp);
				sp.execute(GConnection.getConnection());
				
				ResultSet rs = sp.getResult();
				if (rs == null) {
					throw new SQLException("No resultset found found from populate Attributes");
				}
				
				// look for column with same name as childID property?
				int col = rs.findColumn(ChildIDName().getValue());
				while (rs.next()) {
					// The child will be set active straight after this anyway.
					populateChild(rs.getObject(col));
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}			
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Called prior to executing the select stored proc within
	 * populateAttributes(). By default this will attempt to match
	 * parameters to attribute values - first in this object,
	 * or if not found, will attempt to match them to objects in the direct
	 * owner of this object.
	 * 
	 * If a param cannot be found, an exception will be thrown.
	 * 
	 * Note this will only ever be called with the selectStoredProc.
	 * Override this if you desire different/improved functionality.
	 * 
	 * @param storedProc
	 * @throws SQLException 
	 */
	protected void populateParameters(StoredProc storedProc) throws SQLException {
		BusinessObject owner = Owner().getValue();
		for (Parameter param : storedProc.getAllParameters()) {
			String name = param.name().substring(1);
			// try find local attri first...
			BOAttribute<?> attr = (BOAttribute<?>) getChildByName(name);
			if (attr == null && owner != null) {
				attr = (BOAttribute<?>) owner.getChildByName(name);
			}
			if (attr == null) {
				throw new SQLException("Attribute for param '" + param.name() + "' not found.");
			}
			param.set(attr.getValue());
		}
	}
}
