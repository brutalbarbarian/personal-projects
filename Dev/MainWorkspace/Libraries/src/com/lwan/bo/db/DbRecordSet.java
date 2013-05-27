package com.lwan.bo.db;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import com.lwan.bo.AttributeType;
import com.lwan.bo.BOAttribute;
import com.lwan.bo.BOException;
import com.lwan.bo.BOSet;
import com.lwan.jdbc.GConnection;
import com.lwan.jdbc.Parameter;
import com.lwan.jdbc.StoredProc;

public class DbRecordSet extends BOSet<DbRecord>{
	private StoredProc selectStoredProc, updateStoredProc,
			insertStoredProc, deleteStoredProc;
	public StoredProc getSelectStoredProc() {
		return selectStoredProc;
	}
	public StoredProc getUpdateStoredProc() {
		return updateStoredProc;
	}
	public StoredProc getInsertStoredProc() {
		return insertStoredProc;
	}
	public StoredProc getDeleteStoredProc() {
		return deleteStoredProc;
	}

	public DbRecordSet(String childIdName, StoredProc selectStoredProc) {
		super(null, selectStoredProc.getClass().getName(), childIdName);
		this.selectStoredProc = selectStoredProc;
		
		// create attributes here as we had no access to the selectStoredProc during createAttributes()
		for (Parameter param : selectStoredProc.getAllParameters()) {
			AttributeType attrType = AttributeType.typeForSQLType(param.getType());
			String name = param.name();
			if (attrType == AttributeType.Integer && 
					name.endsWith("_id") ||
					name.contains("_id_")) {
				attrType = AttributeType.ID;
			}
			BOAttribute<?> attr = new BOAttribute<>(this, name, attrType);
			addAsChild(attr);
		}
	}
	
	public DbRecordSet(String childIDName, StoredProc selectStoredProc, 
			StoredProc insertStoredProc, StoredProc updateStoredProc, 
			StoredProc deleteStoredProc) {
		this(childIDName, selectStoredProc);
		
		this.updateStoredProc = updateStoredProc;
		this.insertStoredProc = insertStoredProc;
		this.deleteStoredProc = deleteStoredProc;
	}

	@Override
	protected boolean childExists(Object id) {
		return false;
	}

	@Override
	protected DbRecord createChildInstance(Object id) {
		if (result != null || allowInsert()) {
			return new DbRecord(this);
		} else {
			throw new BOException("Creation of new records not allowed as " +
					"insert storedproc is not set", this);
		}
	}
	
	private ResultSet result;
	private ResultSetMetaData metadata;
	
	protected ResultSet getResultSet() {
		return result;
	}
	protected ResultSetMetaData getMetadata() {
		return metadata;
	}
	
	public boolean allowInsert() {
		return insertStoredProc != null;
	}
	
	public boolean allowDelete() {
		return deleteStoredProc != null;
	}
	
	public boolean allowUpdate() {
		return updateStoredProc != null;
	}

	@Override
	protected boolean populateAttributes() {
		// populate stored proc parameters
		selectStoredProc.clearParameters();
		for (Parameter param : selectStoredProc.getAllParameters()) {
			BOAttribute<?> attr = findAttributeByName(param.name());
			if (attr != null) {
				param.set(attr.getValue());
			}
		}
		
		result = null;
		try {
			selectStoredProc.execute(GConnection.getConnection());
			result = selectStoredProc.getResult();
			metadata = result.getMetaData();
			
			// before starting...
			getExampleChild();	// since we have a metadata... this will
			
			int col = result.findColumn(childIDNameProperty().getValue());
			while (result.next()) {
				// need to activate the child now, or otherwise result will no longer be there
				populateChild(result.getObject(col), true);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (result != null) {
				try {
					result.close();
				} catch (SQLException e) {
					e.printStackTrace();
				} finally {
					result = null;
					metadata = null;
				}
			}
		}
		return true;
	}

}
