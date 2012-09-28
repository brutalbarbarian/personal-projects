package com.lwan.swing;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Extension of standard JList which renders each cell as a checkbox, with several functions which should 
 * be used in favor of standard JList functions 
 * 
 * @author Brutalbarbarian
 *
 */
@SuppressWarnings("serial")
public class JCheckList extends JList<Object> {
	protected static Border noFocusBorder = new EmptyBorder(1, 1, 1, 1);

	/**
	 * Constructor for JCheckList
	 * 
	 */
	public JCheckList() {
		setCellRenderer(new CellRenderer());

		addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				int index = locationToIndex(e.getPoint());

				if (index != -1) {
					JCheckBox checkbox = (JCheckBox)
					getModel().getElementAt(index);
					checkbox.setSelected(
							!checkbox.isSelected());
					repaint();
				}
			}
		});

		setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	}

	/**
	 * add a item to the list
	 * 
	 * @param label
	 */
	public void addCheckbox(String label) {
		ListModel<?> currentList = this.getModel();
		JCheckBox[] newList = new JCheckBox[currentList.getSize() + 1];
		for (int i = 0; i < currentList.getSize(); i++) {
			newList[i] = (JCheckBox) currentList.getElementAt(i);
		}
		newList[newList.length - 1] = new JCheckBox(label);
		setListData(newList);
	}
	
	/**
	 * Set the data of this list
	 * 
	 * @param data
	 */
	public void setData (String [] data) {
		JCheckBox[] list = new JCheckBox[data.length];
		for (int i = 0; i < data.length; i++) {
			list[i] = new JCheckBox(data[i]);
		}
		setListData(list);
	}
	
	/**
	 * Get the name of the list item at index
	 * 
	 * @param index
	 * @return
	 */
	public String getListAt (int index) {
		if (index < 0 || index >= getModel().getSize()) {
			throw new IndexOutOfBoundsException ();
		}
		return ((JCheckBox)getModel().getElementAt(index)).getText();
	}
	
	/**
	 * Selects all the checkboxes in the list
	 */
	public void selectAll () {
		ListModel<?> model = getModel();
		for (int i = 0; i < model.getSize(); i++) {
			((JCheckBox)model.getElementAt(i)).setSelected(true);
		}
	}
	
	/**
	 * Clears selection on all checkboxes
	 */
	public void clearSelection () {
		ListModel<?> model = getModel();
		for (int i = 0; i < model.getSize(); i++) {
			((JCheckBox)model.getElementAt(i)).setSelected(false);
		}		
	}
	
	/**
	 * Get the names of all selected checkboxes
	 * 
	 * @return
	 */
	public String [] getSelectedNames () {
		ListModel<?> model = getModel();
		String [] selected = new String [model.getSize()];
		int count = 0;
		
		for (int i = 0; i < model.getSize(); i++) {
			JCheckBox box = (JCheckBox)model.getElementAt(i);
			if (box.isSelected()) {
				selected[count++] = box.getText();
			}
		}
		String [] ret = new String [count];
		if (count > 0) {
			System.arraycopy(selected, 0, ret, 0, count);
		}
		return ret;
	}
	
	/**
	 * Get the indexes that are selected
	 * 
	 * @return
	 */
	public int [] getSelectedIndexes () {
		ListModel<?> model = getModel();
		int [] selected = new int [model.getSize()];
		int count = 0;
		
		for (int i = 0; i < model.getSize(); i++) {
			JCheckBox box = (JCheckBox)model.getElementAt(i);
			if (box.isSelected()) {
				selected[count++] = i;
			}
		}
		int [] ret = new int [count];
		if (count > 0) {
			System.arraycopy(selected, 0, ret, 0, count);
		}
		return ret;
	}
	
	/**
	 * Get the selected number of checkboxes
	 * 
	 * @return
	 */
	public int getSelectedNum () {
		int count = 0;
		ListModel<?> model = getModel();
		for (int i = 0; i < model.getSize(); i++) {
			if (((JCheckBox)model.getElementAt(i)).isSelected()) {
				count++;
			}
		}
		return count;
	}

	/**
	 * Internal Cell Renderer Class
	 * 
	 * @author Brutalbarbarian
	 *
	 */
	protected class CellRenderer implements ListCellRenderer<Object> {
		public Component getListCellRendererComponent(JList<?> list, Object value, int index,
					boolean isSelected, boolean cellHasFocus) {
			JCheckBox checkbox = (JCheckBox) value;
			checkbox.setEnabled(isEnabled());
			checkbox.setFont(getFont());
			checkbox.setFocusPainted(false);
			checkbox.setBorderPainted(true);
			checkbox.setBackground(getBackground());
			checkbox.setBorder(noFocusBorder);
			return checkbox;
		}
	}
}