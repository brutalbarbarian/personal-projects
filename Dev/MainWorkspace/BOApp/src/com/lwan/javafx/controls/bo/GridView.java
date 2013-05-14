package com.lwan.javafx.controls.bo;

import com.lwan.bo.BusinessObject;

import javafx.scene.layout.BorderPane;

public class GridView <B extends BusinessObject> extends BorderPane{
	private BOGrid<B> grid;
	
	public GridView(BOGrid<B> grid) {
		this.grid = grid;
//		grid.la
		
		initFooter();
				
		setCenter(grid);
	}
	
	protected void initFooter() {
		
	}
	
	
	// the footer may be several layers deep.
	// there can be a field for everything.
	class GridFooter extends BorderPane{
		
	}
}
