package com.lwan.bo.db;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Vector;

import javafx.util.Callback;

import com.lwan.bo.BOSet;
import com.lwan.bo.BOSetRef;
import com.lwan.jdbc.GConnection;
import com.lwan.jdbc.StoredProc;

public class BODbSetRef<T extends BODbObject> extends BOSetRef<T> implements BODbCustomObject{
	protected StoredProc storedProc;

	public BODbSetRef(BOSet<T> source, StoredProc sp) {
		super(source, null, MODE_SUBSET);
		filter = new PopulateParams();
		storedProc = sp;
	}
	
	private BODbSetRef<T> getSet() {
		return this;
	}
	
	public BODbSet<T> getSource() {
		return (BODbSet<T>)super.getSource();
	}
	
	private class PopulateParams implements Callback<BOSetRef<T>, Iterable<Integer>> {		
		public Iterable<Integer> call(BOSetRef<T> arg0) {
			storedProc.clearParameters();
			BODbUtil.assignParamsFromBO(storedProc, getSet(), false);
			ResultSet rs = null;
			try {
				storedProc.execute(GConnection.getConnection());
				rs = storedProc.getResult();
				BODbSet<T> set = getSource();
				String fieldName = set.childIDFieldNameProperty().getValue();
				List<Integer> result = new Vector<>();
				if (rs != null) {			
					while (rs.next()) {
						result.add(rs.getInt(fieldName));
					}
				}
				return result;
			} catch (SQLException e) {
				throw new RuntimeException(e);
			} finally {
				if (rs != null) {
					try {
						rs.getStatement().close();
					} catch (SQLException e) {}
				}
			}
		}		
	}


	@Override
	public BODbAttribute<?> findAttributeByFieldName(String fieldName) {
		return null;
	}
}
