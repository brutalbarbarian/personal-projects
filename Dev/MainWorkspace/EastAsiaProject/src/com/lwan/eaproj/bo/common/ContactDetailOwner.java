package com.lwan.eaproj.bo.common;

import com.lwan.bo.BOAttribute;
import com.lwan.eaproj.bo.ref.BOContactDetail;

/**
 * Any business object which has contact details should implement this.
 * 
 * @author Lu
 *
 */
public interface ContactDetailOwner {
	public int getSourceType(BOContactDetail cdt);
	public BOAttribute<Integer> getID();
}
