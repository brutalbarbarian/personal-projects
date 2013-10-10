package com.lwan.eaproj.bo.ref;

import com.lwan.bo.BusinessObject;
import com.lwan.bo.db.BODbSet;

public abstract class BODocumentItemSet <T extends BODocumentItem<?>> extends BODbSet<T>{
	public BODocumentItemSet(BusinessObject owner, String name) {
		super(owner, name, "Occurrence", "doc_occurrence");
	}
}
