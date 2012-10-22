import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import com.lwan.eaproj.bo.BOContactDetails;
import com.lwan.eaproj.sp.PS_CDT;
import com.lwan.jdbc.GConnection;
import com.lwan.jdbc.StoredProc;


public class TestDB {
	public static void main (String[] args) throws SQLException, ClassNotFoundException {
		String driverName = "sun.jdbc.odbc.JdbcOdbcDriver";
//		Class.forName("sun.jdbc.odbc.JdbcOdbcDriver");
		
		String fileName = "D:/User Files/Brutalbarbarian/Dropbox/EastAsiaProject/EastAsiaDB.mdb";
		String url = "jdbc:odbc:Driver={Microsoft Access Driver (*.mdb, *.accdb)};DBQ="+fileName;
//		Connection con = DriverManager.getConnection(url,"","");
		
		GConnection.initialise(driverName, url, "", "");

//		StoredProc addProc = new StoredProc(new String[] {"@cty_id", "@cty_name"}, new int[] {Types.NUMERIC, Types.VARCHAR}, new String[] {"insert into CTY_customer_type (cty_id, cty_name) values (@cty_id, @cty_name)"});
//		addProc.getParamByName("@cty_id").set(2);
//		addProc.getParamByName("@cty_name").set("TEST TYPE");
//		addProc.execute(con);
		
//		StoredProc deleteProc = new StoredProc(new String[] {"@cty_id"}, new int[] {Types.NUMERIC}, new String[] {"delete from CTY_customer_type where cty_id = @cty_id"});
//		deleteProc.getParamByName("@cty_id").set(2);
//		deleteProc.execute(con);
		
		
//		StoredProc proc = new StoredProc(new String[] {"@cty_id"}, new int []{Types.NUMERIC}, 
//				new String[] {
//				"select * into TEMP from CTY_customer_type where ((@cty_id is null) or (cty_id = @cty_id))", 
//				"select * from TEMP"});
//		StoredProc prcClean = new StoredProc(new String[]{
//				"drop table TEMP"});
//		
//		
//		proc.getParamByName("@cty_id").set(0);
//		proc.execute(con);
//		printResultSet(proc.getResult());
//		
//		proc.getResult().getStatement().close();
//		
//		prcClean.execute(con);
		
		BOContactDetails cd = new BOContactDetails(null);
//		cd.cdt_id.setValue(1);
		cd.ensureActive();
		
		System.out.println(cd.toString());
		cd.cdt_address_1.setValue("338 Riddell Road");
		cd.cdt_address_2.setValue("Glendowie");
		cd.cdt_city.setValue("Auckland");
		cd.cdt_country.setValue("New Zealand");
		cd.cdt_mobile.setValue("021 0220 0431");
//		cd.cdt_id.setValue(1);
		
		cd.save();
		
		PS_CDT prc = new PS_CDT();
		prc.execute(GConnection.getConnection());
		ResultSet rs = prc.getResult();
		printResultSet(rs);
		rs.getStatement().close();
		
//		con.close();
		GConnection.uninitialise();
	}
	
	static void printResultSet(ResultSet result) throws SQLException {
		int columns = result.getMetaData().getColumnCount();
		for (int i = 1; i <= columns; i++) {
			System.out.print(result.getMetaData().getColumnName(i));
			if (i != columns) {
				System.out.print(", ");
			}
		}
		System.out.println();
		
		while(result.next()) {
			for (int i = 1; i <= columns; i++) {
				System.out.print(result.getObject(i));
				if (i != columns) {
					System.out.print(", ");
				}
			}
			System.out.println();
		}
	}
}
