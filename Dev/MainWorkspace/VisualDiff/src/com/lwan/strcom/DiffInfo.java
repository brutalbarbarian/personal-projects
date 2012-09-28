package com.lwan.strcom;

import java.util.LinkedList;
import java.util.List;

/**
 * 
 * @author Brutalbarbarian
 *
 */
public class DiffInfo {
	//constants for changetype
	public static final int TYPE_INSERT = 0;
	public static final int TYPE_DELETE = 1;
	public static final int TYPE_UPDATE = 2;
	public static final int TYPE_NO_CHANGE = 3;
	
	//properties
	public int ChangeType;
	protected List<PairLocale> newUpdateDiffs;
	protected List<PairLocale> oldUpdateDiffs;
	public PairLocale oldLocale, newLocale;
	protected PairLocale universalLocale;
	
	public DiffInfo (int changeType, PairLocale oldFile, PairLocale newFile) {
		ChangeType = changeType;
		oldLocale = oldFile;
		newLocale = newFile;
	}
	
	//these are offset from the start of the first line?
	public List<PairLocale> getNewUpdateDiffs () {
		if (newUpdateDiffs == null) newUpdateDiffs = new LinkedList<>();
		return newUpdateDiffs;
	}
	
	public List<PairLocale> getOldUpdateDiffs() {
		if (oldUpdateDiffs == null) oldUpdateDiffs = new LinkedList<> ();
		return oldUpdateDiffs;
	}
	
	
	public PairLocale getUniLocale() {
		if (universalLocale == null) universalLocale = new PairLocale(-1,-1);
		return universalLocale;
	}
	
	public DiffInfo clone () {
		return new DiffInfo(ChangeType, new PairLocale(oldLocale.Start, oldLocale.End), 
				new PairLocale(newLocale.Start, newLocale.End));
	}
	
	public String toString () {
		StringBuffer str = new StringBuffer();
		switch (ChangeType) {
		case TYPE_INSERT:
			str.append("Insert");
			break;
		case TYPE_DELETE:
			str.append("Delete");
			break;
		case TYPE_UPDATE:
			str.append("Update");
			break;
		case TYPE_NO_CHANGE:
			str.append("No Change");
		}
		str.append(": ");
		if (oldLocale != null) {
			str.append(oldLocale.toString());
			if (newLocale != null) str.append(", "); 
		}
		if (newLocale != null) str.append(newLocale.toString());
		
		if (universalLocale != null) {
			str.append(" : ").append(universalLocale);
		}
		
		return str.toString();
	}
}