import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.lwan.bo.BOException;
import com.lwan.bo.BOSet;
import com.lwan.eaproj.app.Constants;
import com.lwan.eaproj.bo.*;
//import com.lwan.eaproj.bo.cache.BOCompanySet;
//import com.lwan.eaproj.bo.cache.BOCustomerSet;
import com.lwan.eaproj.bo.cache.BOUserSet;
import com.lwan.jdbc.GConnection;


public class TestDB {
	public static void main (String[] args) throws SQLException, ClassNotFoundException {
		String driverName = "sun.jdbc.odbc.JdbcOdbcDriver";
//		Class.forName("sun.jdbc.odbc.JdbcOdbcDriver");
		
//		String fileName = "D:/User Files/Brutalbarbarian/Dropbox/EastAsiaProject/EastAsiaDB.mdb";
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
//		com.com_id.setValue(1);
//		com.ensureActive();
//		com.Active().setValue(false);
		
//		com.employees.ensureChildActive(0);// create a child
//		com.employees.getActive(0).emp_name_first.setValue("secondEmpFirst");
		
//		com.save();
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
		
//		BOEmployee emp = new BOEmployee(null);
//		emp.emp_id.setValue(1);
//		emp.ensureActive();
		
//		emp.Active().setValue(false);
//		emp.com_id.setValue(1);
//		emp.emp_name_first.setValue("newFirstName");
//		emp.emp_name_last.setValue("last");
//		emp.emp_is_active.setValue(true);
//		emp.emp_employment_start.setValue(Date.valueOf("2000-12-24"));
//		emp.emp_payment_monthly.setValue(2000.0);
//		emp.contactDetails.cdt_address_1.setValue("address1");
//		emp.contactDetails.cdt_address_2.setValue("address2");
//		emp.contactDetails.cdt_address_3.setValue("address3");
		
//		System.out.println(emp.toString());
		
//		emp.save();
		
//		BOCompany com = new BOCompany(null);
//		com.companyID.setValue(10);
//		com.ensureActive();
//		BOEmployeePayment pay = com.employees.getActive(0).employeePayments.createNewChild();
//		pay.datePaid.setValue(Date.valueOf("2012-08-21"));
//		pay.notes.setValue("Overpriced");
//		pay.paidAmount.setValue(99.95);
//		pay.
//		com.companyName.setValue("EastAsia");
//		com.contactDetails.address1.setValue("32 Some Place");
//		com.contactDetails.city.setValue("Auckland");
//		com.contactDetails.site.setValue("www.ea.com");
//		
//		BOEmployee emp = com.employees.createNewChild();
//		emp.nameFirst.setValue("Bob");
//		emp.nameLast.setValue("Doe");
//		emp.contactDetails.address1.setValue("Bob's Din");
//		
//		emp = com.employees.createNewChild();
//		emp.nameFirst.setValue("Jane");
//		emp.nameLast.setValue("Doe");
//		emp.contactDetails.address1.setValue("Jane's Place");
//		
//		System.out.println(com.toString());
		
//		try {
//			com.trySave();
//		} catch (BOException e) {
//			e.printStackTrace();
//		}
		
//		BOEmployeePayment eph = new BOEmployeePayment(null);
//		eph.employeePaymentID.setValue(2);
//		eph.ensureActive();
//		eph.Active().setValue(false);
		
//		eph.employeeID.setValue(18);
//		eph.notes.setValue("Less Overpriced");
//		eph.paidAmount.setValue(1999.95);
//		eph.datePaid.setValue(Date.valueOf("2012-04-21"));
//		try {
//			eph.trySave();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		StoredProc prc = new StoredProc("select * from EMP_employee");
//		prc.execute(GConnection.getConnection());
//		ResultSet rs = prc.getResult();
//		printResultSet(rs);
//		rs.getStatement().close();
		
//		BOUser user = new BOUser(null);
//		user.userID.setValue(9);
//		user.ensureActive();
//		user.userName.setValue("sa");
//		user.password.setValue("password");
//		user.description.setValue("insecure password");
		
//		System.out.println(user.checkPassword("passwor"));
		
//		System.out.println(user.toString());
		
//		try {
//			user.trySave();
//		} catch (BOException e) {
//			e.printStackTrace();
//		}
		
//		BOCompany com = new BOCompany(null);
		
//		com.ensureActive();
//		com.contactDetails.address1.setValue("21 Somewhere Road");
//		com.contactDetails.city.setValue("Happyvile");
//		com.companyName.setValue("Cool place");
//		
//		BOEmployee emp = com.employees.createNewChild();
//		emp.nameFirst.setValue("Sarah");
//		emp.contactDetails.city.setValue("Sad place");
//		emp.employmentStart.setValue(Date.valueOf("2012-11-03"));
//		emp.isActive.setValue(true);
//		emp.payMonthly.setValue(100.50);
//		emp.taxCode.setValue("M");
		
//		BOCompany com = new BOCompany(null);
//		com.companyID.setValue(11);
//		com.employees.LoadMode().setValue(BOSet.LOADMODE_PASSIVE);
//		com.ensureActive();
//		
//		com.employees.getActive(0).nameFirst.setValue("Different Name");
//		BOEmployee emp = com.employees.createNewChild();
//		emp.nameFirst.setValue("New Kid");
//		emp.contactDetails.address1.setValue("Some Place");
//		
//		System.out.println(com.toString());
//		
//		try {
//			com.trySave();
//		} catch (BOException e) {
//			e.printStackTrace();
//		}
		
//		BOSchool sch = new BOSchool(null);
//		sch.schoolID.setValue(7);
//		sch.ensureActive();
//		sch.Active().setValue(false);
		
//		sch.schoolName.setValue("primary school");
//		sch.contactDetails.address1.setValue("32 some st");
//		sch.contactDetails.city.setValue("our city");
//		
//		System.out.println(sch.toString());
		
//		try {
//			sch.trySave();
//		} catch (BOException e) {
//			e.printStackTrace();
//		}
		
//		con.close();
		
//		BOCustomer cus = new BOStudent(null);
//		cus.customerID.setValue(4);
//		cus.ensureActive();
		
//		BOCustomer cus = GCustomers.findCustomerID(4);
//		System.out.println(cus.toString());
		
//		System.out.println(GUsers.validateLogin("sa", "password"));
		
////		cus.Active().setValue(false);
//		
//		cus.nameFirst.setValue("student name");
//		cus.nameLast.setValue("last name");
//		cus.reference.setValue("studying");
//		cus.contactDetails.address1.setValue("CUS Address 1");
//		cus.customerType.setValue(BOConstants.CTY_STUDENT);
//		
//		BOStudent stu = cus.students.createNewChild();
//		stu.schoolID.setValue(8);
//		stu.startDate.setValue(Date.valueOf("2012-08-11"));
//		stu.notes.setValue("Redoing same courses as previous year");
//		
//		System.out.println(cus.toString());
//		
//		try {
//			cus.trySave();
//		} catch (BOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		
		
//		System.out.println(cus.toString());
		
//		BOProduct prd = new BOProduct(null);
////		prd.productID.Value().set(1);
//		prd.ensureActive();
//		prd.name.setValue("Test Product");
//		prd.productCategoryID.setValue(1);	// misc
//		prd.description.setValue("Some description");
//		prd.defaultPrice.setValue(999.99);
////		prd.setActive(false);
//		try {
//			prd.trySave();
//		} catch (BOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
		
//		System.out.println(new Date(System.currentTimeMillis()));
		
//		System.out.println(prd.toString());
		
		if (BOUserSet.setActiveUser("sa", "password")) {
			System.out.println("Login success");
			
//			BOInvoice inv = new BOInvoice(null);
//			inv.invoiceID.setValue(12);
//			inv.ensureActive();
//			inv.companyID.setValue(10);
//			inv.customerID.setValue(4);
//			inv.dateRequired.setValue(Date.valueOf("2012-04-08"));
//			BOInvoiceItem ini = inv.invoiceItems.createNewChild();
//			ini.productID.setValue(2);
//			ini.price.setValue(400d);
//			ini.notes.setValue("Owed for services");
//			BOInvoiceSentHistory ish = inv.invoiceSentHistory.createNewChild();
//			ish.paidAmount.setValue(200d);
			
//			System.out.println(inv.toStringAll());
//			
//			try {
//				inv.trySave();
//			} catch (BOException e) {
//				e.printStackTrace();
//			}
			
		} else {
			System.out.println("Login failed");
		}
		
		
		
		BOUserSet.clearActiveUser();
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
				System.out.print(o.toString() + ":" + o.getClass().getName() + ":" + result.getMetaData().getColumnTypeName(i));
				if (i != columns) {
					System.out.print(", ");
				}
			}
			System.out.println();
		}
	}
}
