package com.lwan.eaproj.bo.ref;

import java.sql.ResultSet;
import java.sql.SQLException;

import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;

import com.lwan.bo.LoadMode;
import com.lwan.bo.db.BODbSet;
import com.lwan.javafx.app.util.DbUtil;
import com.lwan.jdbc.GConnection;
import com.lwan.jdbc.StoredProc;

/**
 * Users is a passive set
 * 
 * @author Lu
 *
 */
public class BOUserSet extends BODbSet<BOUser>{
	private static BOUserSet cache;
	private static StoredProc spUsername;
	private Property<BOUser> activeUser;
	
	protected Property<BOUser> ActiveUser() {
		if (activeUser == null) {
			activeUser = new SimpleObjectProperty<>(this, "ActiveUser", null);
		}
		return activeUser;
	}
	
	public static BOUserSet getSet() {
		if (cache == null) {
			cache = new BOUserSet();
			cache.ensureActive();
		}
		return cache;
	} 
	
	public static boolean validateLogin(String username, String password) {
		BOUser user = findUserByUsername(username);
		return (user != null) && user.checkPassword(password);  
	}

	public static BOUser findUserByID(int id) {
		return getSet().findChildByID(id);
	}
	
	/**
	 * Gets the BOUser representing the currently active user
	 * 
	 * @return
	 */
	public static BOUser getActiveUser() {
		return getSet().ActiveUser().getValue();
	}
	
	/**
	 * Checks if there is currently an active user.
	 * 
	 * @return
	 */
	public static boolean hasActiveUser() {
		return getActiveUser() != null;
	}
	
	/**
	 * Clears the active user. This is safe to call multiple times.
	 * Effectively logs off the user.
	 * 
	 */
	public static void clearActiveUser() {
		getSet().ActiveUser().setValue(null);
	}
	
	/**
	 * Sets the active user. This will return false if either there is
	 * already a user active, or if the username and password combo
	 * is invalid (either the username dosen't exist, or the password
	 * is incorrect). 
	 * 
	 * @param username
	 * @param password
	 * @return
	 */
	public static boolean setActiveUser(String username, String password) {
		if (!hasActiveUser() && validateLogin(username, password)) {
			getSet().ActiveUser().setValue(findUserByUsername(username));
			return true;
		} else {
			// Failed to set active user... cannot override existing
			// active, or username and password combo not valid.
			return false;
		}
	}
	
	/**
	 * Finds the BOUser by username. Since usernames must be unique,
	 * this can never return more then one BOUser.
	 * Will return null if no matching username is found.
	 * 
	 * @param username
	 * @return
	 */
	public static BOUser findUserByUsername(String username) {
		if (spUsername == null) {
			spUsername = DbUtil.getDbStoredProc("PS_USR");
		}
		spUsername.clearParameters();
		spUsername.getParamByName("@usr_name").set(username);
		ResultSet rs = null;
		int id = 0;
		try {
			spUsername.execute(GConnection.getConnection());
			rs = spUsername.getResult();
			try {
				if (rs.next()) {
					id = rs.getInt("usr_id");
				}
			} finally {
				rs.getStatement().close(); 
			}
		} catch (SQLException e) {
			throw new RuntimeException("Failed to access database to find usr_id from usr_name", e);
		}
		if (id > 0) {
			return getSet().findChildByID(id);
		}
		return null;
	}

	private BOUserSet() {
		super(null, "UserCache", "UserID", "usr_id");
		loadModeProperty().setValue(LoadMode.PASSIVE);
	}

	@Override
	protected void createStoredProcs() {
		selectStoredProcProperty().setValue(DbUtil.getDbStoredProc("PS_USR_for_grid"));
	}

	@Override
	protected BOUser createChildInstance(Object id) {
		return new BOUser(this);
	}
}
