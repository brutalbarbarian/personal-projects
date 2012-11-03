package com.lwan.bo.db;

import java.sql.ResultSet;
import java.sql.SQLException;

import javafx.beans.property.Property;
import javafx.beans.property.ReadOnlyProperty;
import javafx.beans.property.SimpleObjectProperty;
import com.lwan.bo.BOSet;
import com.lwan.bo.BusinessObject;
import com.lwan.jdbc.GConnection;
import com.lwan.jdbc.Parameter;
import com.lwan.jdbc.StoredProc;

public abstract class BODbSet<T extends BODbObject> extends BOSet<T> {
	Property<StoredProc> select_stored_proc;
	Property<String> child_id_field_name;
	
	public Property<StoredProc> SelectStoredProc() {
		if (select_stored_proc == null) {
			select_stored_proc = new SimpleObjectProperty<StoredProc>(this, "SelectStoredProc");
		}
		return select_stored_proc;
	}
	
	public ReadOnlyProperty<String> ChildIDFieldName () {
		return _child_id_field_name();
	}
	
	private Property<String> _child_id_field_name() {
		if (child_id_field_name == null) {
			child_id_field_name = new SimpleObjectProperty<String>(this, "ChildIDFieldName");
		}
		return child_id_field_name;
	}
	

	public BODbSet(BusinessObject owner, String name, String childIdName, String childIdFieldName) {
		super(owner, name, childIdName);
		_child_id_field_name().setValue(childIdFieldName);
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
		// go looking in the direct parent's attributes
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
				int col = rs.findColumn(ChildIDFieldName().getValue());
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
	 * Same as findChildByAttribute, except instead of searching by attribute, search by fieldName.
	 * 
	 * @param attrName
	 * @param value
	 * @param childNum
	 * @return
	 */
	public T findChildByFieldName(String fieldName, Object value, int childNum) {
		if (getCount() == 0) {
			// There's no chance of finding anything anyway if there's no children
			return null;
		}
		T child = get(0);	// dosen't matter if inactive, just need to get attribute reference.
		BODbAttribute<?> attr = child.getAttributeByFieldName(fieldName);
		if (attr == null) {
			throw new IllegalArgumentException("Cannot find attribute of child with fieldname '" + fieldName + "'");
		}
		return findChildByAttribute(attr.Name().getValue(), value, childNum);
	}
	
	/**
	 * Find the first child with an attribute who's fieldName is equal to the passed in fieldName,
	 * with the same value as the passed in value.
	 * 
	 * @param fieldName
	 * @param value
	 * @return
	 */
	public T findChildByFieldName(String fieldName, Object value) {
		return findChildByFieldName(fieldName, value, 1);
	}

	/**
	 * Called prior to executing the select stored proc within
	 * populateAttributes(). By default this will attempt to match
	 * parameters to attribute values in the direct
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
		BODbObject owner = (BODbObject)Owner().getValue(); 
		for (Parameter param : storedProc.getAllParameters()) {
			String name = param.name().substring(1);
			BODbAttribute<?> attr = null;
			if (owner == null) {
				// this only an issue if parameters are required...
				throw new SQLException("No owner found for class " + getClass().getName());
			} else {
				attr = owner.getAttributeByFieldName(name);
			}
			if (attr == null) {
				throw new SQLException("Attribute for param '" + param.name() + "' not found.");
			}
			param.set(attr.getValue());
		}
	}
}
