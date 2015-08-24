package com.ytarzimanov.ftp_assistant.models;

import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;

import com.ytarzimanov.controls.basic.graphics.GraphicObject;
import com.ytarzimanov.controls.basic.interfaces.OnPopupMenu;
import com.ytarzimanov.controls.navbar.NavBar;
import com.ytarzimanov.controls.navbar.graphics.Header;
import com.ytarzimanov.ftp_assistant.adapters.ModelAdapter;
import com.ytarzimanov.ftp_assistant.models.core.Directory;

public class PopupMenuController implements OnPopupMenu, ActionListener{
	final private static String capt3Item = "3 items";
	final private static String capt5Item = "5 items";
	final private static String capt10Item = "10 items";
	final private static String capt20Item = "20 items";
	final private static String captOpenBrowser = "Open folder";
	
	private JPopupMenu menu = new JPopupMenu();
	private JMenuItem item3 = new JMenuItem(capt3Item); 
	private JMenuItem item5 = new JMenuItem(capt5Item); 
	private JMenuItem item10 = new JMenuItem(capt10Item);
	private JMenuItem item20 = new JMenuItem(capt20Item);
	private JSeparator separatorMenu = new JSeparator();
	private JMenuItem itemOpenBrowser = new JMenuItem(captOpenBrowser);
    private Directory selected; 
    private NavBar navigator;
    private Desktop desktop;
	 
	public PopupMenuController(ModelAdapter adapter, NavBar navigator){
		this.navigator = navigator;
		if (Desktop.isDesktopSupported()) {
		    desktop = Desktop.getDesktop();
		}
		
		menu.add(itemOpenBrowser);	
		itemOpenBrowser.addActionListener(this);
		menu.add(separatorMenu);
		menu.add(item3);
		item3.addActionListener(this);
		menu.add(item5);
		item5.addActionListener(this);
		menu.add(item10);
		item10.addActionListener(this);
		menu.add(item20);	
		item20.addActionListener(this);	
	}
	
	public JPopupMenu getPopupMenu(){
		return menu;
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		Directory dir = selected;
		if (arg0.getSource().equals(item3)){
			dir.setVisibleCountItems(3);
        }else
        if (arg0.getSource().equals(item5)){
        	dir.setVisibleCountItems(5);    
        }else
        if (arg0.getSource().equals(item10)){
        	dir.setVisibleCountItems(10);
        }else
        if (arg0.getSource().equals(item20)){
        	dir.setVisibleCountItems(20);     
        }else
        if (arg0.getSource().equals(itemOpenBrowser)){
        	if (desktop != null){
				try {
					desktop.open(new java.io.File(dir.getDownloadFolder()));
				} catch (IOException e) {
					e.printStackTrace();
				}
        	}    
        }
		navigator.update();
	}


	@Override
	public Boolean onPopupMenu(GraphicObject Object) {
		selected = null;
		if (Object instanceof Header) {
			selected = (Directory) Object.getObject();	
		}
		return (Object.getClass() == Header.class);
	}
	
}
