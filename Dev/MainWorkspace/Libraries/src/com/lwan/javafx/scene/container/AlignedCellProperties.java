package com.lwan.javafx.scene.container;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.geometry.Orientation;
import javafx.scene.Parent;

public class AlignedCellProperties {
	private AlignedCell cell;
	
	protected AlignedCellProperties(AlignedCell cell) {
		this(cell, 1);
	}
	
	protected AlignedCellProperties(AlignedCell cell, int span) {
		this.cell = cell;
		
		spanProperty = new SimpleIntegerProperty(this, "", span);
	}
	
	private IntegerProperty spanProperty;
	public IntegerProperty spanProperty() {
		return spanProperty;
	}
	
	public AlignedCell getParent() {
		Parent p = cell.getNode().getParent();
		if (p instanceof AlignedCell) {
			return (AlignedCell)p;
		} else {
			return null;
		}
	}
	
	public AlignedCell getBaseParent() {
		AlignedCell parent = getParent();
		if (parent == null) {
			return cell;
		} else {
			return parent.getProperties().getBaseParent();
		}
	}
	
	public int getRowOfChild(AlignedCell child) {
		if (cell.isParent()) {
			if (cell.getOrientation() == Orientation.VERTICAL) {
				return cell.getAlignedChildren().indexOf(child);
			} else {
				return cell.getAlignedChildren().contains(child) ? 0 : -1; 
			}
		} else {
			return -1;
		}
	}
	
	public int getColumnOfChld(AlignedCell child) {
		if (cell.isParent()) {
			if (cell.getOrientation() == Orientation.HORIZONTAL) {
				return cell.getAlignedChildren().indexOf(child);
			} else {
				return cell.getAlignedChildren().contains(child) ? 0 : -1; 
			}
			
		} else {
			return -1;
		}
	}
	
	public int getRow() {
		AlignedCell parent = getParent();
		if (parent == null) {
			return 0;
		} else {
			return parent.getProperties().getRowOfChild(cell);
		}
	}
	
	public int getColumn() {
		AlignedCell parent = getParent();
		if (parent == null) {
			return 0;
		} else {
			return parent.getProperties().getColumnOfChld(cell);
		}
	}
	
	public int getRowCount(){
		if (cell.isParent()) {
			int rows = 0;
			for (AlignedCell child : cell.getAlignedChildren()) {
				if (cell.getOrientation() == Orientation.VERTICAL) {
					// Get the sum of the row count of the children
					rows += child.getProperties().getRowCount();
				} else {
					// Get the max of the row count of the children
					rows = Math.max(rows, child.getProperties().getRowCount());
				}
			}
			return Math.max(rows, 1);
		} else {
			return 1;
		}
	}
	
	public int getColumnCount() {
		if (cell.isParent()) {
			int cols = 0;
			for (AlignedCell child : cell.getAlignedChildren()) {
				if (cell.getOrientation() == Orientation.HORIZONTAL) {
					// Get the sum of the column count of the children
					cols += child.getProperties().getColumnCount();
				} else {
					// Get the max of the column count of the children
					cols = Math.max(cols, child.getProperties().getColumnCount());
				}
			}
			return Math.max(cols, 1);
		} else {
			return 1;
		}
	}
	
	
//		// The details relative to getBaseParent()
//		public int getActualRow();
//		public int getActualColumn();
//		public int getActualRowSpan();
//		public int getActualColumnSpan();
	
	// every cell must keep track of the last known pref width of each column text???
}
