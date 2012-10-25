package com.lwan.bo.db;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;

import com.lwan.bo.BOAttribute;
import com.lwan.bo.BusinessObject;
import com.lwan.bo.State;
import com.lwan.jdbc.GConnection;
import com.lwan.jdbc.Parameter;
import com.lwan.jdbc.StoredProc;

public abstract class BODatabaseObject extends BusinessObject{
	private Property<StoredProc> update_stored_proc;
	private Property<StoredProc> insert_stored_proc;
	private Property<StoredProc> delete_stored_proc;
	private Property<StoredProc> select_stored_proc;
	
	public Property<StoredProc> SelectStoredProc () {
		if (select_stored_proc == null) {
			select_stored_proc = new SimpleObjectProperty<> ();
		}
		return select_stored_proc;
	}
	
	public Property<StoredProc> UpdateStoredProc () {
		if (update_stored_proc == null) {
			update_stored_proc = new SimpleObjectProperty<>();
		}
		return update_stored_proc;
	}
	
	/**
	 * This stored proc will be called when doSave is called while w
	 * 
	 * @return
	 */
	public Property<StoredProc> InsertStoredProc () {
		if (insert_stored_proc == null) {
			insert_stored_proc = new SimpleObjectProperty<>();
		}
		return insert_stored_proc;
	}
	
	/**
	 * This stored proc will be called when doDelete is called
	 * 
	 * @return
	 */
	public Property<StoredProc> DeleteStoredProc () {
		if (delete_stored_proc == null) {
			delete_stored_proc = new SimpleObjectProperty<>();
		}
		return delete_stored_proc;
	}
	
	public BODatabaseObject(BusinessObject owner, String name) {
		super(owner, name);
		createStoredProcs();
	}
	

	@Override
	protected void doSave() {
		StoredProc sp;
		if (State().getValue().contains(State.Dataset)) {
			sp = UpdateStoredProc().getValue();
		} else {
			sp = InsertStoredProc().getValue();
		}
		
		try {
			ensureIDExists();
			executeStoredProc(sp);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Method called on doSave() which will allows the user to
	 * check if the id exists (and is valid) prior to calling executeStoredProc.
	 * 
	 * As this is called after all independent children has saved, this can also be
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
	public void doDelete() {
		try{
			executeStoredProc(DeleteStoredProc().getValue());
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	protected boolean populateAttributes() {
		StoredProc sp = SelectStoredProc().getValue();
		if (sp != null) {
			try {
				executeStoredProc(sp);
				ResultSet rs = sp.getResult();
				if (!rs.next()) {
					return false;
				} else {
					ResultSetMetaData meta = rs.getMetaData();
					int count = meta.getColumnCount();
					for (int i = 1; i <= count; i++) {
						String colName = meta.getColumnName(i);
						Object value = rs.getObject(i);
						
						BOAttribute<?> attr = (BOAttribute<?>) getChildByName(colName);
						// Just ignore any ones that don't map across... not important.
						if (attr != null) {
							attr.setAsObject(value);
						}
					}
					
					return true;
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			
		}
		return false;
	}
	
	/**
	 * Execute a stored proc, assuming all parameters for
	 * the passed in stored proc are map directly too attributes within
	 * this BOobject.
	 * 
	 * @param sp
	 * @throws SQLException 
	 */
	protected void executeStoredProc(StoredProc sp) throws SQLException {
		if (sp != null) {
			sp.clearParameters();
			for (Parameter param : sp.getAllParameters()) {
				// shave off the '@'
				String attriName = param.name().substring(1);
				BOAttribute<?> attr = (BOAttribute<?>) getChildByName(attriName);
				if (attr != null) {	
					param.set(attr.getValue());
				} else {
					// if attr is null... something serious has gone wrong... bad stored proc or bad boobject.
					throw new SQLException("Attribute for param '" + param.name() + "' not found.");
				}
			}
			sp.execute(GConnection.getConnection());
		}
	}

}