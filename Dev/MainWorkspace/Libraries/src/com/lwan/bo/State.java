package com.lwan.bo;

public enum State {
	// If the object is loaded from a dataset or created
	Dataset, 
	
	// If the object has been modified since setActive(true) has been called
	Modified;
}
