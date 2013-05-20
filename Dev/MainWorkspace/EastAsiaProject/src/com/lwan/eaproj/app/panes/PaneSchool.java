package com.lwan.eaproj.app.panes;

import java.util.Arrays;

import javafx.scene.Node;

import com.lwan.bo.BOLinkEx;
import com.lwan.bo.BOSet;
import com.lwan.eaproj.app.EAConstants;
import com.lwan.eaproj.app.EAUtils;
import com.lwan.eaproj.bo.ref.BOContactDetail;
import com.lwan.eaproj.bo.ref.BOSchool;
import com.lwan.eaproj.bo.ref.BOSchoolSet;
import com.lwan.javafx.app.util.LngUtil;
import com.lwan.javafx.controls.bo.GridView;
import com.lwan.util.CollectionUtil;
import com.lwan.util.StringUtil;
import com.lwan.util.wrappers.CallbackEx;

public class PaneSchool extends PaneGridBase<BOSchool>{

	@Override
	protected Node initEditPane() {
		
		return null;
	}

	@Override
	protected void initGridLink(BOLinkEx<BOSet<BOSchool>> gridLink) {
		gridLink.setLinkedObject(BOSchoolSet.getSchoolSet());
	}

	@Override
	protected GridView<BOSchool> constructGrid(
			BOLinkEx<BOSet<BOSchool>> gridLink) {
		GridView<BOSchool> result = new GridView<>("pane_school", gridLink, 
				CollectionUtil.concatArrays(new String[]{"SchoolName", "ContactName", "Notes"}, 
						EAUtils.getContactDetailFields("ContactDetail.")),
				LngUtil.translateArray(CollectionUtil.concatArrays(new String[]{"Name", "Contact", "Notes"},
						EAConstants.CONTACT_DISPLAY_STRINGS)),
				null);
		result.getGrid().setDisplayValueCallback(new CallbackEx<BOSchool, String, String>() {
			public String call(BOSchool school, String field) {
				BOContactDetail cdt = school.contactDetail();
				return StringUtil.getDelimitedString(", ", 
						cdt.address1().getValue(), cdt.address2().getValue(),
						cdt.address3().getValue());
			}
		});
		
		return result;
	}
	
}
