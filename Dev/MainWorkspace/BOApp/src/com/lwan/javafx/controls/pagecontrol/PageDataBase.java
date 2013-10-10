package com.lwan.javafx.controls.pagecontrol;

import java.util.List;
import java.util.Vector;

import javafx.scene.Node;

import com.lwan.util.containers.Params;
import com.lwan.util.wrappers.Disposable;

public abstract class PageDataBase <T extends Node> implements PageData<T>{

	public PageDataBase(String displayTitle, String UID, PageData<?> parent) {
		this(displayTitle, UID, parent, false, null);
	}
	
	public PageDataBase(String displayTitle, String UID, PageData<?> parent, boolean hasPageNode,
			PageData<?> preferredChild) {
		this.displayTitle = displayTitle;
		this.UID = UID;
		this.parent = parent;
		this.preferredChild = preferredChild;
		this.hasPageNode = hasPageNode;
	}

	private String displayTitle;
	private Object UID;
	private PageData<?> parent, preferredChild;
	private boolean hasPageNode;
	
	@Override
	public boolean allowClosePageNode(T pageNode) {
		// override to change behavior
		return true;
	}
	
	@Override
	public boolean hasPageNode() {
		return hasPageNode;
	}
	
	@Override
	public PageData<?> preferredChild() {
		return preferredChild;
	}
	
	@Override
	public T getPageNode(Params params) {
		// override to change behavior
		return null;
	}
	
	@Override
	public String getDisplayTitle() {
		return displayTitle;
	}

	@Override
	public Object getUID() {
		return UID;
	}

	@Override
	public PageData<?> getParent() {		
		return parent;
	}

	List<PageData<?>> children;
	@Override
	public List<PageData<?>> getChildren() {
		if (children == null) {
			children = new Vector<>();
		}
		return children;
	}

	@Override
	public boolean hasChildren() {
		return children != null && !children.isEmpty();
	}
	
	private Page page;
	@Override
	public void setPage(Page page) {
		this.page = page;
	}

	@Override
	public Page getPage() {
		return page;
	}
	
	@Override
	public void closePageNode(T pageNode) {
		// Override to do anything mode
		
		if (pageNode instanceof Disposable) {
			((Disposable)pageNode).dispose();
		}
	}
}
