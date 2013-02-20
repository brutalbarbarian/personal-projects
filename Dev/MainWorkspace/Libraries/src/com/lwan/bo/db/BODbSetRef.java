package com.lwan.bo.db;

import java.sql.ResultSet;
import java.sql.SQLException;

import javafx.util.Callback;

import com.lwan.bo.BOSet;
import com.lwan.bo.BOSetRef;
import com.lwan.bo.BusinessObject;
import com.lwan.jdbc.GConnection;
import com.lwan.jdbc.StoredProc;

public class BODbSetRef<T extends BODbObject> extends BOSetRef<T> implements BODbCustomObject{
	protected StoredProc storedProc;

	public BODbSetRef(BOSet<T> source, final StoredProc sp) {
		super(source, new Callback<BOSetRef<T>, Iterable<Integer>>() {
			public Iterable<Integer> call(BOSetRef<T> arg0) {
//				BODbUtil.assignParamsFromBO(sp, this, false);
				
				return null;
			}			
		}, MODE_SUBSET);
	}
	
	private BODbSetRef<T> getSet() {
		return this;
	}
	
	@SuppressWarnings("unchecked")
	public BODbSet<T> getSource() {
		return (BODbSet<T>)super.getSource();
	}
	
	private class PopulateParams implements Callback<BOSetRef<T>, Iterable<Integer>> {		
		public Iterable<Integer> call(BOSetRef<T> arg0) {
			storedProc.clearParameters();
			BODbUtil.assignParamsFromBO(storedProc, getSet(), false);
			try {
				storedProc.execute(GConnection.getConnection());				
				ResultSet rs = storedProc.getResult();
				BODbSet<T> set = getSource();
				if (rs != null) {			
					while (rs.next()) {
						
//						T child = set.findChildByID(
//								rs.getInt(set.childIDFieldNameProperty().getValue()));
//						if (child != null) {
//							Entry e = new Entry(child);
//							
//						}
					}
				}
				
			} catch (SQLException e) {
				throw new RuntimeException(e);
			}
			return null;
		}		
	}

//	public static <T extends BusinessObject> BODbSetRef<T> createSubset(
//			BOSet<T> set, final StoredProc sp) {
//		Callback<BOSetRef<T>, Iterable<Integer>> callback = 
//				new Callback<BOSetRef<T>, Iterable<Integer>>() {
//			public Iterable<Integer> call(BOSetRef<T> arg0) {
////				BODbUtil.assignParamsFromBO(sp, , false);
//				
//				return null;
//			}			
//		};
//		return null;
//	}

	@Override
	public BODbAttribute<?> findAttributeByFieldName(String fieldName) {
		// TODO Auto-generated method stub
		return null;
	}
}
