package com.lwan.swing;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Insets;

import javax.swing.JTextField;

import com.lwan.util.GraphicsUtil;

@SuppressWarnings("serial")
public class RoundedTextField extends JTextField {
	public RoundedTextField() {
		super();
		setup();
	}
	
	public RoundedTextField (int columns) {
		super(columns);
		setup();
	}
	
	public RoundedTextField (String text) {
		super(text);
		setup();
	}
	
	public RoundedTextField (String text, int columns) {
		super(text, columns);
		setup();
	}
	
	private void setup() {
		setMargin(new Insets(1, 10, 1, 10));
		setOpaque(false);
	}
	
	protected void paintComponent(Graphics g) {
		GraphicsUtil.setAntiAliasing(g);
		g.setColor(getBackground());
		g.fillRoundRect(0, 0, getWidth()-1, getHeight()-1, 25, 25);
		g.setColor(Color.GRAY);
		g.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 25, 25);
		
		super.paintComponent(g);
	}
	
	protected void paintBorder(Graphics g) {}
}
