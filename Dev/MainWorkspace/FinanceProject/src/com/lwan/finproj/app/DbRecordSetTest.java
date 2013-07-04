package com.lwan.finproj.app;

import java.util.Collection;

import javafx.scene.Scene;
import javafx.stage.Stage;

import com.lwan.bo.BOLinkEx;
import com.lwan.bo.BOSet;
import com.lwan.bo.db.DbRecord;
import com.lwan.bo.db.DbRecordSet;
import com.lwan.javafx.app.App;
import com.lwan.javafx.controls.other.GridView;
import com.lwan.jdbc.StoredProc;

public class DbRecordSetTest extends App{
	protected void initialiseStage(Stage stage) {
		StoredProc sp = new StoredProc("select * from TM_TRN_Transactions trn " +
				"inner join TM_SRC_source src " +
				"on trn.src_id = src.src_id");

		
//		try {
//			sp.execute(GConnection.getConnection());
//			ResultSet rs = sp.getResult();
//			DbUtil.printResultSet(rs);
//			rs.close();
//		} catch (SQLException e) {
//			e.printStackTrace();
//		}
		
		
		DbRecordSet set = new DbRecordSet("trn_id", sp);
		set.ensureActive();
		BOLinkEx<BOSet<DbRecord>> link = new BOLinkEx<>();
		
		GridView<DbRecord> grid = new GridView<DbRecord>("test", link, 
				new String[]{"trn_id", "trn_amount", "trn_notes", "trn_date", "src_name"},
				new String[]{"ID", "Amount", "Notes", "Date", "Source"}, null);
		
		link.setLinkedObject(set);
		
		Scene sc = new Scene(grid);
		stage.setScene(sc);
		

//		System.out.println(set.toStringAll());
		
		stage.show();
	}

	@Override
	protected void initStylesheets(Collection<String> stylesheets) {}
	
	public static void main(String[] args) {
		launch(args);
	}
}
