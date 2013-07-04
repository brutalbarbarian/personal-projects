package com.lwan.eaproj.app.frames;

import com.lwan.bo.BOLinkEx;
import com.lwan.bo.ModifiedEvent;
import com.lwan.bo.ModifiedEventListener;
import com.lwan.bo.ModifiedEventType;
import com.lwan.eaproj.bo.ref.BOContactDetail;
import com.lwan.javafx.app.Lng;
import com.lwan.javafx.app.util.BOCtrlUtil;
import com.lwan.javafx.controls.bo.BOTextField;
import com.lwan.javafx.interfaces.BoundFrame;
import com.lwan.javafx.scene.control.AlignedControlCell;
import com.lwan.util.FxUtils;

import javafx.scene.Parent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

public class FrameContactDetails extends GridPane implements BoundFrame<BOContactDetail>{
	public BOLinkEx<BOContactDetail> link;
	private Parent alignNode;
	
	public FrameContactDetails(BOLinkEx<BOContactDetail>link) {
		this(null, link);
	}
	
	public FrameContactDetails(Parent alignNode, BOLinkEx<BOContactDetail> link) {
		this.alignNode = alignNode == null? this : alignNode;
		this.link = link;
		
		link.addListener(new ModifiedEventListener() {
			public void handleModified(ModifiedEvent event) {
				if (event.getType() == ModifiedEventType.Link) {
					doBuildAttributeLinks();
				}
			}
		});
		
		initControls();
		doBuildAttributeLinks();
	}
	
	BOTextField txtAddress1, txtAddress2, txtAddress3,
		txtCity, txtCountry, txtPostCode,
		txtPhone, txtMobile, txtFax, txtSite;
	
	private void initControls() {
		txtAddress1 = new BOTextField(link, "Address1");
		txtAddress2 = new BOTextField(link, "Address2");
		txtAddress3 = new BOTextField(link, "Address3");
		txtCity = new BOTextField(link, "City");
		txtCountry = new BOTextField(link, "Country");
		txtPostCode = new BOTextField(link, "PostCode");
		txtPhone = new BOTextField(link, "Phone");
		txtMobile = new BOTextField(link, "Mobile");
		txtFax = new BOTextField(link, "Fax");
		txtSite = new BOTextField(link, "Email");
		
		add(new AlignedControlCell(Lng._("Address"), txtAddress1, alignNode, 0), 0, 0, 2, 1);
		add(new AlignedControlCell("", txtAddress2, alignNode, 0), 0, 1, 2, 1);
		add(new AlignedControlCell("", txtAddress3, alignNode, 0), 0, 2, 2, 1);
		
		add(new AlignedControlCell("City", txtCity, alignNode, 0), 0, 3);		
		add(new AlignedControlCell("Post Code", txtPostCode, alignNode, 1), 1, 3);
		
		add(new AlignedControlCell("Country", txtCountry, alignNode, 0), 0, 4, 2, 1);
		
		add(new AlignedControlCell("Phone", txtPhone, alignNode, 0), 0, 5);
		add(new AlignedControlCell("Mobile", txtMobile, alignNode, 1), 1, 5);
		add(new AlignedControlCell("Fax", txtFax, alignNode, 0), 0, 6);
		add(new AlignedControlCell("Site", txtSite, alignNode, 1), 1, 6);
		
		FxUtils.setAllColumnHGrow(this, Priority.SOMETIMES);
	}
		
	public void doDisplayState() {
		// do nothing
	}

	@Override
	public void doBuildAttributeLinks() {
		BOCtrlUtil.buildAttributeLinks(this);
		
		doDisplayState();
	}

	@Override
	public BOLinkEx<BOContactDetail> getMainLink() {
		return link;
	}
	
}
