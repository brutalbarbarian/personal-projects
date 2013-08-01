package com.lwan.eaproj.app.panes;

import javafx.scene.Node;
import javafx.scene.layout.Priority;
import com.lwan.bo.BOLinkEx;
import com.lwan.bo.BOSet;
import com.lwan.bo.db.BODbSetRef;
import com.lwan.eaproj.app.panes.base.PaneGridBase;
import com.lwan.eaproj.bo.ref.BOProduct;
import com.lwan.eaproj.bo.ref.BOProductSet;
import com.lwan.javafx.app.Lng;
import com.lwan.javafx.app.util.DbUtil;
import com.lwan.javafx.controls.bo.BOTextArea;
import com.lwan.javafx.controls.bo.BOTextField;
import com.lwan.javafx.controls.other.GridView;
import com.lwan.javafx.controls.panes.THBox;
import com.lwan.javafx.controls.panes.TTitledPane;
import com.lwan.javafx.controls.panes.TVBox;
import com.lwan.javafx.scene.control.AlignedControlCell;

public class PaneProduct extends PaneGridBase<BOProduct>{
	THBox pMain;
	TTitledPane tpDetail, tpComments;
	TVBox pDetail;
	
	BOTextField tfName, tfDescription, tfPrice;
	BOTextArea taComments;
	
	AlignedControlCell accName, accDescription, accPrice;
	
	@Override
	protected Node initEditPane() {
		tfName = new BOTextField(getMainLink(), "Name");
		tfDescription = new BOTextField(getMainLink(), "Description");
		tfPrice = new BOTextField(getMainLink(), "Price");
		
		pDetail = new TVBox();
		accName = new AlignedControlCell(Lng._("Name"), tfName, pDetail);
		accDescription = new AlignedControlCell(Lng._("Description"), tfDescription, pDetail);
		accPrice = new AlignedControlCell(Lng._("Price"), tfPrice, pDetail);
		
		pDetail.getChildren().addAll(accName, accDescription, accPrice);
		tpDetail = new TTitledPane(Lng._("Details"), pDetail);
		
		taComments = new BOTextArea(getMainLink(), "Comments");
		tpComments = new TTitledPane(Lng._("Comments"), taComments);
		
		pMain = new THBox();
		pMain.getChildren().addAll(tpDetail, tpComments);
		THBox.setHgrow(tpDetail, Priority.NEVER);
		THBox.setHgrow(tpComments, Priority.ALWAYS);
		
		tpComments.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
		tpComments.setMinSize(0, 0);
		
		return pMain;
	}

	@Override
	protected Node initParamPane() {
		return null;
	}

	private BOProductSetRef setProducts;
	@Override
	protected void initGridLink(BOLinkEx<BOSet<BOProduct>> gridLink) {
		setProducts = new BOProductSetRef();
		setProducts.ensureActive();
		gridLink.setLinkedObject(setProducts);
	}

	@Override
	protected GridView<BOProduct> constructGrid(
			BOLinkEx<BOSet<BOProduct>> gridLink) {
		GridView<BOProduct> result = new GridView<>("pane_product", gridLink,
				new String[]{"Name", "Description", "Price"},
				new String[]{"Name", "Description", "Price"}, null);
		
		return result;
	}

	protected class BOProductSetRef extends BODbSetRef<BOProduct> {
		public BOProductSetRef() {
			super(BOProductSet.getSet(), DbUtil.getDbStoredProc("PS_PRD_all"));
		}
		
		@Override
		protected void createAttributes() {
			super.createAttributes();
		}
	}
}