package com.lwan.javafx.controls.bo;

import com.lwan.bo.BOAttribute;

import javafx.scene.Node;

public interface BOBoundControl <S, D> {
	public BOBoundProperty<S, D> dataBindingProperty();
	
	public void rebuildAttributeLinks();
	
	public Node node();
	
	public void update(BOAttribute<S> attri);
}
