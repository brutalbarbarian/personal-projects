package com.lwan.javafx.controls.other;

import com.lwan.bo.BusinessObject;

public interface BOSetControlTarget<T extends BusinessObject> {
	public int getSelectedIndex();
	public void select(T item);
	public void select(int index);
	public boolean inEditState();
}