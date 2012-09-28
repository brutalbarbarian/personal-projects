package com.lwan.swing;

import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.DocumentFilter;

/**
 * A document filter that allows the toggling of editability of a document.</br>
 * This is better then setEdit in JTextArea as it allows the carat to still be shown
 * and used. 
 * 
 * @author Brutalbarbarian
 *
 */
public class UneditableDocumentFilter extends DocumentFilter {
	protected boolean isEditable;
	
	/**
	 * Constructor for UneditableDocumentFilter</br>
	 * </br>
	 * Will initialise editable to false
	 */
	public UneditableDocumentFilter() {
		isEditable = false;
	}
	
	/**
	 * Set a document filter on a document.
	 * This will throw an UnsupportedOperationException 
	 * if the document passed in does not extend from AbstractDocument.
	 * 
	 * @param doc
	 * @return
	 */
	public static UneditableDocumentFilter SetFilterOnDocument (Document doc) {
		if (doc instanceof AbstractDocument) {
			UneditableDocumentFilter filter = new UneditableDocumentFilter();
			((AbstractDocument)doc).setDocumentFilter(filter);
			return filter;
		} else {
			throw new UnsupportedOperationException ("Filter can only be set on documents which extends" +
					" from javax.swing.text.AbstractDocument");
		}
	}
	
	public void setEdit (boolean editable) {
		isEditable = editable;
	}
	
	public boolean isEditable() {
		return isEditable;
	}
	
	@Override
	public void remove(FilterBypass fb, int offset, int length) throws BadLocationException {
		if (isEditable) {
			super.remove(fb, offset, length);
		}
	}

	@Override
	public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
		if (isEditable) {
			super.replace(fb, offset, length, text, attrs);
		}
	}
	
	@Override
	public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
		if (isEditable) {
			super.insertString(fb, offset, string, attr);
		}
	}
}
