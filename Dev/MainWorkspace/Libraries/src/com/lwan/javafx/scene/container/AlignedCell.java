package com.lwan.javafx.scene.container;

import java.util.List;

import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.layout.Priority;

public interface AlignedCell {
	public Node getNode();	// Gets self without casting
	
	public AlignedCellProperties getProperties();
	public Orientation getOrientation();
	
	// Add/Remove children
	public void add(AlignedCell cell);
	public void remove(AlignedCell cell);
	
	public List<AlignedCell> getAlignedChildren();

	public boolean isParent();
	
	public Priority getVGrow(AlignedCell cell);
	public Priority getHGrow(AlignedCell cell);
}
