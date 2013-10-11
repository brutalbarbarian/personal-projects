package com.lwan.bo.db;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import com.lwan.bo.AttributeType;
import com.lwan.bo.BOAttribute;
import com.lwan.bo.BOException;
import com.lwan.bo.BusinessObject;
import com.lwan.bo.ModifiedEvent;
import com.lwan.jdbc.GConnection;
import com.lwan.jdbc.Parameter;
import com.lwan.jdbc.StoredProc;

public class DbRecord extends BusinessObject{

	public DbRecord(BusinessObject owner) {
		super(owner, "record");
	}

	@Override
	protected void doSave() {
		StoredProc sp;
		if (isFromDataset()) {
			if (getOwner().allowUpdate()) {
				sp = getOwner().getUpdateStoredProc();
			} else {
				throw new BOException("Updating is not valid for " + getOwner().getName(), this);
			}
		} else {
			if (getOwner().allowInsert()) {
				sp = getOwner().getInsertStoredProc();
			} else {
				throw new BOException("Inserting is not valid for " + getOwner().getName(), this);
			}
		}
		executeStoredProc(sp);
	}

	@Override
	protected void doDelete() {
		if (getOwner().allowDelete()) {
			executeStoredProc(getOwner().getDeleteStoredProc());
		} else {
			throw new BOException("Deleting is not valid for " + getOwner().getName(), this);
		}
	}
	
	protected void executeStoredProc(StoredProc sp) {
		sp.clearParameters();
		for (Parameter param : sp.getAllParameters()) {
			BOAttribute<?> attr = findAttributeByName(param.name());
			if (attr != null) {
				param.set(attr.getValue());
			}
		}
		try {
			sp.execute(GConnection.getConnection());
		} catch (SQLException e) {
			throw new BOException(e, this);
		}
	}
	
	public DbRecordSet getOwner() {
		return (DbRecordSet)super.getOwner();
	}

	@Override
	protected boolean populateAttributes() {
		if (getOwner().getResultSet() == null) {
			// owner DBRecordSet needs to provide the result set
			return false;
		} else {
			ResultSetMetaData metadata = getOwner().getMetadata();
			ResultSet result = getOwner().getResultSet();
			try {
				for (int i = 1; i <= metadata.getColumnCount(); i++) {
					String name = metadata.getColumnName(i);
					if (name.equalsIgnoreCase(getOwner().childIDNameProperty().getValue())) {
						// already set... calling getObject() again will throw an error.
						continue;
					}
					BOAttribute<?> attr = findAttributeByName(name);
					if (attr == null) {
						// This really shouldn't be possible
						throw new BOException(name + " not found", this);
					}
					Object item = result.getObject(i);
					attr.setAsObject(item);
				}
				return true;
			} catch (SQLException e) {
				throw new BOException(e, this);
			}
		}
	}

	@Override
	protected void createAttributes() {
		if(getOwner().getMetadata() == null && getOwner().getInsertStoredProc() != null) {
			// copy the attributes from the example child
			DbRecord example = getOwner().getExampleChild();
			for (BusinessObject bo : example.getChildren()) {
				BOAttribute<?> attr = (BOAttribute<?>)bo;
				attr = new BOAttribute<>(this, attr.getName(), attr.getAttributeType());
			}
			return;
		}
		ResultSetMetaData metadata = getOwner().getMetadata();
		try {
			for (int i = 1; i <= metadata.getColumnCount(); i++){
				String name = metadata.getColumnName(i);
				int type = metadata.getColumnType(i);
				AttributeType attrType = AttributeType.typeForSQLType(type);
				// should get majority of cases... can't imagine false positives occurring with this
				if (attrType == AttributeType.Integer && 
						name.endsWith("_id") ||
						name.contains("_id_")) {
					attrType = AttributeType.ID;
				}
				BOAttribute<?> attr = new BOAttribute<>(this, name, attrType);
				addAsChild(attr);
			}
		} catch (SQLException e) {
			throw new BOException(e, this);
		}
	}

	@Override
	public void clearAttributes() {
		for (BusinessObject bo : getChildren()) {
			BOAttribute<?> attr = (BOAttribute<?>)bo;
			if (attr.getName().equals(getOwner().childIDNameProperty().getValue())) {
				continue;
			}
			attr.clearAttributes();
		}
	}

	@Override
	public void handleModified(ModifiedEvent source) {}

}
