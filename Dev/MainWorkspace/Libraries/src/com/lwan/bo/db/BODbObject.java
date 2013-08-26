package com.lwan.bo.db;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;

import com.lwan.bo.BusinessObject;
import com.lwan.bo.State;
import com.lwan.jdbc.GConnection;
import com.lwan.jdbc.Parameter;
import com.lwan.jdbc.StoredProc;

public abstract class BODbObject extends BusinessObject implements BODbCustomObject{
	public static final int SP_SELECT = 0;
	public static final int SP_INSERT = 1;
	public static final int SP_UPDATE = 2;
	public static final int SP_DELETE = 3;
	
	class StoredProcSet {
		StoredProc update, insert, delete, select;
		
		StoredProc get(int type) {
			switch (type) {
			case SP_SELECT : return select;
			case SP_INSERT : return insert;
			case SP_UPDATE : return update;
			case SP_DELETE : return delete;
			default : return null;
			}
		}
		
		void set(int type, StoredProc sp) {
			switch (type) {
			case SP_SELECT : select = sp; break;
			case SP_INSERT : insert = sp; break;
			case SP_UPDATE : update = sp; break;
			case SP_DELETE : delete = sp; break;
			}
		}
		
		void clear() {
			update = null;
			insert = null;
			delete = null;
			select = null;
		}
	}
	
	private StoredProcSet storedProcs;
	
	private StoredProcSet storedProcs () {
		if (storedProcs == null) {
			storedProcs = new StoredProcSet();
		}
		return storedProcs;
	}
	
	private HashMap<String, BODbAttribute<?>> fields;
	
	/**
	 * Set the stored proc of this business object.
	 * type is either SP_SELECT, SP_INSERT, SP_UPDATE, or SP_DELETE
	 * It is important to pass in the current class by
	 * 'ClassName.class' as opposed to the getClass() method, as getClass()
	 * will always return the lowest member in the object hierachy.
	 * 
	 * @param sp
	 * @param type
	 */
	protected void setSP(StoredProc sp, int type) {
		storedProcs().set(type, sp);
	}
	
	/**
	 * Get the stored proc of specified type, keyed to the class passed in
	 * 
	 * @param type
	 * @return
	 */
	protected StoredProc getSP (int type) {
		return storedProcs().get(type);
	}
	
	public BODbObject(BusinessObject owner, String name) {
		super(owner, name);
		createStoredProcs();
	}
	
	public void dispose() {
		// clear the fields cache
		fields.clear();
		
		// clear the stored procs
		storedProcs.clear();
		
		super.dispose();
	}
	
	protected void removeChild(BusinessObject child) {
		if (fields != null && child instanceof BODbAttribute<?>) {
			fields.entrySet().remove(child);
		}
		super.removeChild(child);
	}
	
	protected void initialise () {
		fields = new HashMap<>();
		super.initialise();
	}
	
	protected <T extends BusinessObject> T addAsChild(T object) {
		super.addAsChild(object);
		if (object instanceof BODbAttribute) {
			BODbAttribute<?> attr = (BODbAttribute<?>)object;
			fields.put(attr.fieldNameProperty().getValue(), attr);
		}
		return object;
	}
	
	/**
	 * Get the BODbAttribute with the same field name as the value passed in
	 * 
	 * @param fieldName
	 * @return
	 */
	public BODbAttribute<?> findAttributeByFieldName(String fieldName)   {
		return fields.get(fieldName);
	}
	
	protected String doVerifyState() {
		// need to allow the object to assign id's first 
		// prior to checking verification.
		ensureIDExists();
		return null;
	}
	
	private void executeStoredProc(int type) throws SQLException {
		Class<?> c = getClass();
		StoredProc sp = null;
		try {
			while (c != BODbObject.class) {
				sp = getSP(type);
				executeStoredProc(sp);
				
				c = c.getSuperclass();
			}
			
			sp = null;
		} catch (SQLException e) {
			// If this was thrown, impossible for sp to be null
			throw new SQLException(sp.getName() + ":" + e.getMessage(), e);
		}
	}

	@Override
	protected void doSave() {
		int type;
		if (stateProperty().getValue().contains(State.Dataset)) {
			type = SP_UPDATE;
		} else {
			type = SP_INSERT;
		}
		
		try {
			executeStoredProc(type);
		} catch (SQLException e) {
			throw new RuntimeException("Failed to save object: " + getClass().getName() + 
					" while attempting to call stored procedure: " + e.getMessage(), e);
		}
	}
	
	/**
	 * Method called on doVerifyState() which will allows the user to
	 * check if the id exists (and is valid) prior to calling save.
	 * 
	 * As this is called after all independent children has been checked, this can also be
	 * used to ensure all links are correct to those children. i.e. ensure the foreign key
	 * id fields has the same id as the independent children.
	 * 
	 */
	protected abstract void ensureIDExists();
	
	/**
	 * Method called during initialisation.
	 * The user should be setting the properties SelectStoredProc,
	 * UpdateStoredProc, InsertStoredProc and DeleteStoredProc from this method.
	 * 
	 */
	protected abstract void createStoredProcs();

	@Override
	protected void doDelete() {
		try{
			executeStoredProc(SP_DELETE);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	protected boolean populateAttributes() {
		Class<?> c = getClass();
		boolean result = false;
		while (c != BODbObject.class) {
			StoredProc sp = getSP(SP_SELECT);
			c = c.getSuperclass();
			if (sp != null) {
				try {
					executeStoredProc(sp);
					ResultSet rs = sp.getResult();
					if (!rs.next()) {
						// something went wrong clearly...
						// can't have a stored proc returning nothing... can we?
						// or ignore???
						return false;
					} else {
						ResultSetMetaData meta = rs.getMetaData();
						int count = meta.getColumnCount();
						for (int i = 1; i <= count; i++) {
							String colName = meta.getColumnName(i);
							Object value = rs.getObject(i);
							
							BODbAttribute<?> attr = findAttributeByFieldName(colName);
							// Just ignore any ones that don't map across... not important.
							if (attr != null) {
								attr.setAsObject(value);
							}
						}
						
						result = true;
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		
		
		return result;
	}
	
	/**
	 * Called prior to executing the select stored proc within
	 * executeStoredProc(). By default this will attempt to match
	 * parameters to attribute values in this object.
	 * 
	 * If a param cannot be found, an exception will be thrown.
	 * 
	 * The stored proc should correspond directly to either update, 
	 * insert, delete or select stored procs.
	 * Override this if you desire different/improved functionality.
	 * 
	 * @param storedProc
	 * @throws SQLException
	 */ 
	protected void populateParameters(StoredProc sp) throws SQLException {
		for (Parameter param : sp.getAllParameters()) {
			// shave off the '@'
			String attriName = param.name().substring(1);
			BODbAttribute<?> attr = findAttributeByFieldName(attriName);
			if (attr != null) {	
				param.set(attr.getValue());
			} else {
				System.out.println(this.getClass().getName() + ":" + sp.getName());
				// if attr is null... something serious has gone wrong... bad stored proc or bad boobject.
				throw new SQLException("Attribute for param '" + param.name() + "' not found.");
			}
		}		
	}
	
	/**
	 * Execute a stored proc.
	 * Will call populateParameters which by default assumes all parameters for
	 * the passed in stored proc are map directly too attributes within
	 * this BOobject.
	 * 
	 * @param sp
	 * @throws SQLException 
	 */
	protected void executeStoredProc(StoredProc sp) throws SQLException {
		if (sp != null) {
			sp.clearParameters();
			populateParameters(sp);
			sp.execute(GConnection.getConnection());
		}
	}
}
