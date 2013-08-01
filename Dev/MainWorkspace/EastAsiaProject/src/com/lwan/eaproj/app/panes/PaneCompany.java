package com.lwan.eaproj.app.panes;

import com.lwan.bo.BOLinkEx;
import com.lwan.bo.BOSet;
import com.lwan.eaproj.app.frames.FrameContactDetails;
import com.lwan.eaproj.app.panes.base.PaneGridBase;
import com.lwan.eaproj.bo.ref.BOCompany;
import com.lwan.eaproj.bo.ref.BOCompanySet;
import com.lwan.eaproj.bo.ref.BOContactDetail;
import com.lwan.javafx.app.Lng;
import com.lwan.javafx.app.util.LngUtil;
import com.lwan.javafx.controls.bo.BOTextField;
import com.lwan.javafx.controls.other.BOGrid;
import com.lwan.javafx.controls.other.GridView;
import com.lwan.javafx.controls.panes.TVBox;
import com.lwan.javafx.scene.control.AlignedControlCell;
import com.lwan.util.StringUtil;
import com.lwan.util.wrappers.CallbackEx;

import javafx.scene.Node;

public class PaneCompany extends PaneGridBase<BOCompany>{
	protected BOLinkEx<BOContactDetail> linkCDT;
	protected FrameContactDetails frameContactDetail;
	private BOTextField txtCompanyName;
	
	private AlignedControlCell accCompanyName;
	
	@Override
	protected GridView<BOCompany> constructGrid(
			BOLinkEx<BOSet<BOCompany>> gridLink) {
		GridView<BOCompany> result = new GridView<>("pane_company", gridLink,
				new String[] {"CompanyName", BOGrid.PREFIX_CALCULATED + "Address"},
				LngUtil.translateArray(new String[]{"Company", "Address"}),
				null);
		
		result.getGrid().setDisplayValueCallback(new CallbackEx<BOCompany, String, String>() {
			public String call(BOCompany company, String b) {
				BOContactDetail cdt = company.contactDetail();
				return StringUtil.getDelimitedString(", ", 
						cdt.address1().getValue(), cdt.address2().getValue(),
						cdt.address3().getValue());
			}			
		});
		
		return result;
	}
	
	@Override
	protected void onNewSelection(BOCompany selection) {
		super.onNewSelection(selection);
		if (selection == null) {
			linkCDT.setLinkedObject(null);
		} else {
			linkCDT.setLinkedObject(selection.contactDetail());
		}
	}
	
	@Override
	protected Node initEditPane() {
		TVBox detail = new TVBox();
		
		txtCompanyName = new BOTextField(getMainLink(), "CompanyName");
		accCompanyName = new AlignedControlCell(Lng._("Name"), txtCompanyName, detail, 0);
		
		linkCDT = new BOLinkEx<>();
		frameContactDetail = new FrameContactDetails(detail, linkCDT);
		
		detail.getChildren().addAll(accCompanyName, frameContactDetail);
		
		return detail;
	}

	@Override
	protected void initGridLink(BOLinkEx<BOSet<BOCompany>> gridLink) {
		gridLink.setLinkedObject(BOCompanySet.getSet());
	}

	@Override
	protected Node initParamPane() {
		return null;
	}	
}
