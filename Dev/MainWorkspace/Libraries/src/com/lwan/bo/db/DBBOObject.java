package com.lwan.bo.db;

import java.sql.Connection;
import java.sql.PreparedStatement;

import com.lwan.bo.BOObject;
import com.lwan.bo.ModifiedEvent;

public class DBBOObject extends BOObject{
	public DBBOObject(BOObject owner, String name) {
		super(owner, name);
	}

	@Override
	protected void doSave() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void doDelete() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected boolean populateAttributes() {
		// TODO Auto-generated method stub
//		Connection con;
//		PreparedStatement ps = con.prepareStatement("") ;
		
		return false;
	}

	@Override
	protected void createAttributes() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void clearAttributes() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void handleModified(ModifiedEvent source) {
		// TODO Auto-generated method stub
		
	}

}
