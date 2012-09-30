package com.lwan.strcom.gui;

//import com.lwan.javafx.scene.control.StrComFX;
import com.lwan.swing.StrCom;
import com.lwan.util.SwingUtil;

public class Main {
	public static void main (String[] args) {
		try {
			SwingUtil.setSystemLookAndFeel();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		//check parameters
		if (args.length > 0) {
			switch(args[0]) {
			case "--help":
				System.out.println("String Comparator v1.0 by brutalbarbarian\n" +
						"Simply start up to launch the GUI interface" +
						"or pass in two paths of files as parameters to initilise comparison");
				return;
			default:
				if (args.length == 2) {
					new StrCom(args[0], args[1]);
				}
			}
		}  else {
			new StrCom();			
		}
		

//		StrComFX.launchApp(args);
	}
}
