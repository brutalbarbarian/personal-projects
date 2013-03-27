package com.lwan.bo.db;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

import javafx.beans.property.Property;
import javafx.beans.property.ReadOnlyProperty;
import javafx.beans.property.SimpleObjectProperty;

import com.lwan.bo.BOAttribute;
import com.lwan.bo.BOSet;
import com.lwan.bo.BusinessObject;
import com.lwan.jdbc.GConnection;
import com.lwan.jdbc.Parameter;
import com.lwan.jdbc.StoredProc;

public abstract class BODbSet<T extends BODbObject> extends BOSet<T> {
	private Property<StoredProc> selectStoredProcProperty;
	private Property<StoredProc> existsStoredProcProperty;
	private Property<String> childIdFieldNameProperty;
	
	private HashMap<String, BODbAttribute<?>> fields;
	
	// Exists and select stored procs are mutually exclusive.
	// Select is used upon populateAttributes with LoadMode = Active or Passive
	// Exists is used upon findChildByID with LoadMode = Cache
	public Property<StoredProc> existsStoredProcProperty() {
		if (existsStoredProcProperty == null) {
			existsStoredProcProperty = new SimpleObjectProperty<StoredProc>(this, "ExistsStoredProc");
		}
		return existsStoredProcProperty;
	}
	
	public Property<StoredProc> selectStoredProcProperty() {
		if (selectStoredProcProperty == null) {
			selectStoredProcProperty = new SimpleObjectProperty<StoredProc>(this, "SelectStoredProc");
		}
		return selectStoredProcProperty;
	}
	
	public ReadOnlyProperty<String> childIDFieldNameProperty () {
		return _childIdFieldNameProperty();
	}
	
	private Property<String> _childIdFieldNameProperty() {
		if (childIdFieldNameProperty == null) {
			childIdFieldNameProperty = new SimpleObjectProperty<String>(this, "ChildIDFieldName");
		}
		return childIdFieldNameProperty;
	}
	
	protected void initialise() {
		fields = new HashMap<>();
		super.initialise();
	}
	
	protected void removeChild(BusinessObject child) {
		if (fields != null && child instanceof BODbAttribute) {
			fields.entrySet().remove(child);
		}
		
		super.removeChild(child);
	}
	
	public void free() {
		fields.clear();
		
		super.free();
	}

	public BODbSet(BusinessObject owner, String name, String childIdName, String childIdFieldName) {
		super(owner, name, childIdName);
		_childIdFieldNameProperty().setValue(childIdFieldName);
		createStoredProcs();
	}
	
	/**
	 * Method called during initialisation.
	 * The user should be setting the properties SelectStoredProc,
	 * UpdateStoredProc, InsertStoredProc and DeleteStoredProc from this method.
	 * 
	 */
	protected abstract void createStoredProcs();
	
	protected <R extends BusinessObject> R addAsChild(R object) {
		super.addAsChild(object);
		if (object instanceof BODbAttribute) {
			BODbAttribute<?> attr = (BODbAttribute<?>)object;
			fields.put(attr.fieldNameProperty().getValue(), attr);
		}
		return object;
	}

	@Override
	protected boolean populateAttributes() {
		if (loadModeProperty().getValue() != LOADMODE_CACHE) {
			// if the SelectStoredProc requires any parameters...
			// go looking in the direct parent's attributes
			StoredProc sp = selectStoredProcProperty().getValue();
			if (sp != null) {
				try {
					populateParameters(sp);
					sp.execute(GConnection.getConnection());
					
					ResultSet rs = sp.getResult();
					if (rs == null) {
						throw new SQLException("No resultset found found from populate Attributes");
					}
					// look for column with same name as childID property?
					int col = rs.findColumn(childIDFieldNameProperty().getValue());
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
		} else {
			// Never load anything if loadmode is cache
			return true;
		}
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
	
	/**
	 * Same as findChildByAttribute, except instead of searching by attribute, search by fieldName.
	 * 
	 * @param attrName
	 * @param value
	 * @param childNum
	 * @return
	 */
	public T findChildByFieldName(String fieldName, Object value, int childNum) {
		T result = null;
		if (getCount() != 0) {
			T child = get(0);	// dosen't matter if inactive, just need to get attribute reference.
			BODbAttribute<?> attr = child.findAttributeByFieldName(fieldName);
			if (attr == null) {
				throw new IllegalArgumentException("Cannot find attribute of child with fieldname '" + fieldName + "'");
			}
			result = findChildByAttribute(attr.nameProperty().getValue(), value, childNum, false);
		}
		if (result == null && loadModeProperty().getValue() == LOADMODE_CACHE) {
			//... how would this work... is this a good idea?...will need a special stored
			// proc to be able to find a field like this... one that can take any
			// param..eww.. and what if we want to go 
			// TODO... do nothing for now.
		}
		return result;
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
		BODbObject owner = (BODbObject)ownerProperty().getValue(); 
		for (Parameter param : storedProc.getAllParameters()) {
			String name = param.name().substring(1);
			BOAttribute<?> attr = null;
			if (owner == null) {
				// this only an issue if parameters are required...
//				throw new SQLException("No owner found for class " + getClass().getName());
				attr = findAttributeByFieldName(name);	
			} else {
				attr = owner.findAttributeByFieldName(name);
			}
			if (attr == null) {
//				throw new SQLException("Attribute for param '" + param.name() + "' not found.");
				param.set(null);
			} else { 			
				param.set(attr.getValue());
			}
		}
	}
	
	protected boolean childExists(Object id) {
		StoredProc sp = existsStoredProcProperty().getValue();
		BODbObject owner = (BODbObject)ownerProperty().getValue();
		BODbAttribute<?> attr = null;
		if (sp != null) {
			for (Parameter p : sp.getAllParameters()) {
				String paramName = p.name().substring(1);
				if (paramName.equals(childIDFieldNameProperty().getValue())) {
					p.set(id);
				} else if (owner != null && 
						(attr = owner.findAttributeByFieldName("paramName")) != null) {
					// go look in parent
					p.set(attr.getValue());
				} else {
					// Just continue?... not that important really...
//					throw new RuntimeException("Cannot find param field " + paramName + " for " +
//							"storedproc " + sp.getClass().getName());
				}
			}
			try {
				sp.execute(GConnection.getConnection());
				ResultSet rs = null;
				try { 
					rs = sp.getResult();
					return rs.next();
				} finally {
					if (rs != null) {
						rs.getStatement().close();
					}
				}
			} catch (SQLException e) {
				throw new RuntimeException(e);
			}
			
			
		} else {
			return false;	// No way of checking.. assume false
		}
	}
}
