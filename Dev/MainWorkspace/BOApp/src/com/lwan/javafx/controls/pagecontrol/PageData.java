package com.lwan.javafx.controls.pagecontrol;

import java.util.List;

import com.lwan.util.containers.Params;

import javafx.scene.Node;

public interface PageData <T extends Node> {
	public String getDisplayTitle();
	public Object getUID();
	
	public PageData<?> getParent();
	public List<PageData<?>> getChildren();
	public boolean hasChildren();
	public PageData<?> preferredChild();
	
	public void setPage(Page page);
	public Page getPage();
	
	public T getPageNode(Params params);		// create and initialise the page node
	public boolean hasPageNode();	// if this page has a page node.
	public boolean allowClosePageNode(T pageNode);	// check if the page node is in a state that can be closed
	public void closePageNode(T pageNode);	// perform any required uninitialization actions
}
