package com.lwan.javafx.controls.pagecontrol;

import javafx.scene.layout.Pane;

public abstract class LoadingPage extends Pane{
	public abstract void startLoading();
	public abstract void update(double offset);
	public abstract void stop();
}
