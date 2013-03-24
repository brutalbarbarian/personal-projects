package com.lwan.javafx.app.util;

import java.util.HashSet;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.scene.control.IndexRange;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import com.lwan.util.StringUtil;
import com.lwan.util.containers.TrieMap;

public class AutocompleteController {
	private TrieMap<String> trie;
	private TextField editor;
	private BooleanProperty editingProperty;
	private BooleanProperty allowUniqueProperty;
	private String internalString;
	private String lastDisplayString;
	private boolean ignoreCase;
	private HashSet<KeyCode> activeKeys;
	
	public BooleanProperty editingProperty() {
		return editingProperty;
	}
	
	public boolean isEditing() {
		return editingProperty().get();
	}
	
	public void setEditing(boolean editing) {
		editingProperty().set(editing);
	}
	
	public BooleanProperty allowUniqueProperty() {
		return allowUniqueProperty;
	}
	
	public boolean allowUnique() {
		return allowUniqueProperty().get();
	}
	
	public void setAllowUnique(boolean allowUnique) {
		allowUniqueProperty().set(allowUnique);
	}
	
	private boolean replacing; 
	
	protected void selectRange(final int anchor, final int caret) {
		Platform.runLater(new Runnable() {
			public void run() {
				replacing = true;
				try {
					editor.selectRange(anchor, caret);
				} finally {
					replacing = false;
				}
			}			
		});
	}
	
	public AutocompleteController(TextField edit, boolean _ignoreCase) {
		ignoreCase = _ignoreCase;
		trie = new TrieMap<String>(ignoreCase);
		editor = edit;
		activeKeys = new HashSet<>();
		
		editingProperty = new SimpleBooleanProperty(this, "Editing", false);
		allowUniqueProperty = new SimpleBooleanProperty(this, "AllowUnique", true);	// by default true
		replacing = false;
		
		editingProperty.addListener(new ChangeListener<Boolean>() {
			public void changed(ObservableValue<? extends Boolean> arg0,
					Boolean arg1, Boolean arg2) {
				if (arg2) {
					internalString = editor.getText();	// reset it... don't offer recommendations at this point
					lastDisplayString = internalString;
				}
			}	
		});
		
		editor.setOnKeyPressed(new EventHandler<KeyEvent>(){
			public void handle(KeyEvent arg0) {
				activeKeys.add(arg0.getCode());
			}			
		});
		
		editor.setOnKeyReleased(new EventHandler<KeyEvent>() {
			public void handle(KeyEvent arg0) {
				activeKeys.remove(arg0.getCode());
			}			
		});
		
		editor.selectionProperty().addListener(new ChangeListener<IndexRange>(){
			public void changed(ObservableValue<? extends IndexRange> arg0,
					IndexRange arg1, IndexRange arg2) {
				if (arg2.getLength() == 0 && arg1.getLength() > 0 && 
						arg2.getEnd() == editor.getText().length() &&
						StringUtil.equals(lastDisplayString, editor.getText(), ignoreCase)) {
					internalString = editor.getText();
				}
			}			
		});
		
		// add appropriate listeners to the editor
		// we need to be able to allow user to still delete... 
		editor.textProperty().addListener(new ChangeListener<String>() {
			public void changed(ObservableValue<? extends String> arg0,
					String oldValue, String newValue) {				
				if (!replacing && isEditing()) try {
					
					replacing = true;	// we don't want this triggering recursive calls
					
					if (allowUnique() && activeKeys.contains(KeyCode.DELETE)) {
						return;	// do nothing...
					}
					
					if (StringUtil.equals(newValue, internalString, ignoreCase) && 
							activeKeys.contains(KeyCode.BACK_SPACE)) {
						editor.deletePreviousChar();
						newValue = internalString.substring(0, newValue.length() - 1);						
					}
					
					if (newValue.length() == 0) {
						editor.setText("");
						internalString = "";
						return;
					}
					
					// lets just get something working first...
					String best = trie.getNearest(newValue);					
					
					if (best != null) {
						if (allowUnique()) {
							editor.appendText(best.substring(newValue.length()));
						} else {
							editor.setText(best);
						}
						internalString = newValue;
						selectRange(best.length(), newValue.length());
					} else if (!allowUnique()) {
						// revert to previous string...
						best = trie.getNearest(internalString);
						if (best == null) {
							editor.setText("");
						} else {
							editor.setText(best);
							selectRange(best.length(), internalString.length());							
						}						
					} else {
						// no suggestions but unique is valid... do nothing
						internalString = newValue;
					}
					
				} finally {
					replacing = false;
					lastDisplayString = editor.getText();
				}
			}			
		});
		
	}
	
	public void setSource(Iterable<String> src) {
		trie.clear();
		trie.beginBulkUpdate();
		try {
			for (String s : src) {
				trie.put(s, s);
			}
		} finally {
			trie.endBulkUpdate();
		}
	}
}
