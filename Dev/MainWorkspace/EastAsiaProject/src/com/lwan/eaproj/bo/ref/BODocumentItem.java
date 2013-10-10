package com.lwan.eaproj.bo.ref;

import com.lwan.bo.AttributeType;
import com.lwan.bo.BOLink;
import com.lwan.bo.BusinessObject;
import com.lwan.bo.db.BODbAttribute;
import com.lwan.bo.db.BODbObject;

public abstract class BODocumentItem <D extends BODocument> extends BODbObject{
	private BODbAttribute<Integer> documentID;
	private BODbAttribute<Integer> occurrence;
	
	private BOLink<D> document;
	
	public BODbAttribute<Integer> documentID() {
		return documentID;
	}
	public BODbAttribute<Integer> occurrence() {
		return occurrence;
	}
	public D document() {
		return document.getReferencedObject();
	}
	
	public BODocumentItem(BusinessObject owner, String name) {
		super(owner, name);
	}

	@Override
	protected void ensureIDExists() {
		documentID.assign(document().documentID());
		if (occurrence.isNull()) {
			// TODO
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	protected <T extends BusinessObject> T getLinkedChild(BOLink<T> link) {
		if (link == document) {
			return (T)findOwnerByClass(BODocument.class);
		} else {
			return super.getLinkedChild(link);
		}
	}

	@Override
	protected void createAttributes() {
		documentID = addAsChild(new BODbAttribute<Integer>(this, "DocumentID", "doc_id", AttributeType.ID));
		occurrence = addAsChild(new BODbAttribute<Integer>(this, "Occurrence", "doc_occurrence", AttributeType.ID));
		
		document = addAsChildLink(document, null, "documentID");
	}

	@Override
	public void clearAttributes() {
		// TODO
	}
	
}
