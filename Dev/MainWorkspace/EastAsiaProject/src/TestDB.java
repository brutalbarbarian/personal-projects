import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.lwan.bo.State;
import com.lwan.eaproj.bo.BOCompany;
import com.lwan.eaproj.bo.BOEmployee;
import com.lwan.jdbc.GConnection;
import com.lwan.jdbc.StoredProc;
import com.lwan.util.CollectionUtil;


public class TestDB {
	public static void main (String[] args) throws SQLException, ClassNotFoundException {
		String driverName = "sun.jdbc.odbc.JdbcOdbcDriver";
//		Class.forName("sun.jdbc.odbc.JdbcOdbcDriver");
		
		String fileName = "D:/User Files/Brutalbarbarian/Dropbox/EastAsiaProject/EastAsiaDB.mdb";
//		String fileName = "C:/Users/Brutalbarbarian/Dropbox/EastAsiaProject/EastAsiaDB.mdb";
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
		
//		BOContactDetails cd = new BOContactDetails(null);
//		cd.cdt_id.setValue(3);
//		cd.ensureActive();
		
//		System.out.println(cd.toString());
		
//		cd.cdt_address_1.setValue("38 Some Different Place");
//		cd.cdt_address_1.setValue("338 Riddell Road");
//		cd.cdt_address_2.setValue("Glendowie");
//		cd.cdt_city.setValue("Auckland");
//		cd.cdt_country.setValue("New Zealand");
//		cd.cdt_mobile.setValue("021 0220 0431");
//		cd.cdt_id.setValue(1);
//		cd.Active().setValue(false);
		
//		cd.save();
		
//		BOCompany com = new BOCompany(null);
//		com.com_id.setValue(2);
//		com.ensureActive();
//		System.out.println(com.toString());
//		
//		com.Active().setValue(false);
		
//		com.cdt_id.setValue(5);
//		com.com_name.setValue("New Name for Company");
//		com.contactDwetails.cdt_address_1.setValue("28 Beach Road");
//		com.contactDetails.cdt_address_2.setValue("Waterfront CBD");
//		com.contactDetails.cdt_city.setValue("Auckland");
//		com.contactDetails.cdt_mobile.setValue("123 4567");
//		com.contactDetails.cdt_site.setValue("www.company.com");
		
//		System.out.println(com.toString());
		
//		CollectionUtil.printV(com.State().getValue(), ",");
//		com.save();
		
		BOEmployee emp = new BOEmployee(null);
//		emp.emp_id.setValue(1);
		emp.ensureActive();
		emp.com_id.setValue(1);
		emp.emp_name_first.setValue("first");
		emp.emp_name_last.setValue("last");
		
		
		
		System.out.println(emp.toString());
		
//		StoredProc prc = new StoredProc("select * from EMP_employee");
//		prc.execute(GConnection.getConnection());
//		ResultSet rs = prc.getResult();
//		printResultSet(rs);
//		rs.getStatement().close();
		
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
				Object o = result.getObject(i);
				System.out.print(o.toString() + ":" + o.getClass().getName());
				
				// currency -> BigDecimal. timedate -> TimeStamp
//				if (i == 6) {
//					System.out.println(((BigDecimal)o).doubleValue());
//					System.out.println(result.getMetaData().getColumnTypeName(i));
////					Type.
//				}
////				java.sql.Timestamp.
////				System.out.print(result.getObject(i).getClass());
				if (i != columns) {
					System.out.print(", ");
				}
			}
			System.out.println();
		}
	}
}