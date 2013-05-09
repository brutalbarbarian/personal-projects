package com.lwan.javafx.scene.control;

import java.util.List;

import javafx.geometry.Orientation;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SplitPane;
import javafx.scene.control.SplitPaneBuilder;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import com.lwan.strcom.DiffInfo;
import com.lwan.util.CollectionUtil;
import com.lwan.util.FxUtils;

public class ComparisonPaneFX extends BorderPane{
	//components
	ScrollBar scbVert, scbHori;
	ScrollPane scpOld, scpNew;
	TextArea txtOld, txtNew;
	SplitPane sppCent;

	public ComparisonPaneFX() {
		initialiseSettings();
		initialiseComponents();
	}

	protected void initialiseSettings() {

	}

	protected void initialiseComponents() {
		//scrollbars
		scbVert = new ScrollBar();
		scbHori = new ScrollBar();
		scbVert.setOrientation(Orientation.VERTICAL);
		scbHori.setOrientation(Orientation.HORIZONTAL);

		setRight(scbVert);
		setBottom(scbHori);

		//central pane
		scpOld = new ScrollPane();
		scpNew = new ScrollPane();
		scpOld.setVbarPolicy(ScrollBarPolicy.NEVER);
		scpOld.setHbarPolicy(ScrollBarPolicy.NEVER);
		scpNew.setVbarPolicy(ScrollBarPolicy.NEVER);
		scpNew.setHbarPolicy(ScrollBarPolicy.NEVER);
		scpOld.setFitToHeight(true);
		scpOld.setFitToWidth(true);
		scpNew.setFitToHeight(true);
		scpNew.setFitToWidth(true);

		SplitPaneBuilder<?> sppBuilder = SplitPaneBuilder.create();
		sppBuilder.items(scpOld, scpNew);
		sppBuilder.orientation(Orientation.HORIZONTAL);
		//		sppBuilder.skin(new SplitPaneSkin());

		sppCent = sppBuilder.build();
//		sppCent.setSkin(new SplitPaneSkin(sppCent));
		setCenter(sppCent);

		//setup textfields
		txtOld = new TextArea();
		txtNew = new TextArea();
		scpOld.setContent(txtOld);
		scpNew.setContent(txtNew);

		//		TextAreaBuilder<?> builder = TextAreaBuilder.create();

	}

	public boolean hasPrevHighlight() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean hasNextHighlight() {
		// TODO Auto-generated method stub
		return false;
	}

	public void setData(List<String> s1, List<String> s2, List<DiffInfo> res) {
		String sOld = CollectionUtil.CollapseStringList(s1, "\n");
		String sNew = CollectionUtil.CollapseStringList(s2, "\n");

		txtOld.setText(sOld);
		txtNew.setText(sNew);

		CollectionUtil.printV(res, "\n");

		FxUtils.printNodeTree(scpOld.getSkin().getNode());
	}

}
