package com.lwan.musicsync.main;

import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.util.Callback;

public class CustomRowFactory extends TableRow<AudioInfo>{
	private static Callback<TableView<AudioInfo>, TableRow<AudioInfo>> factory;
	private static int RowCount;
	
	public static Callback<TableView<AudioInfo>, TableRow<AudioInfo>> getRowFactory () {
		if (factory == null) {
			factory = new Callback<TableView<AudioInfo>, TableRow<AudioInfo>> () {
				public TableRow<AudioInfo> call(TableView<AudioInfo> table) {
					if (RowCount == 0) {
						return new CustomRowFactory();
					} else {
						return null;
					}
				}
			};
		}
		RowCount = 0;
		return factory;
	}
}
