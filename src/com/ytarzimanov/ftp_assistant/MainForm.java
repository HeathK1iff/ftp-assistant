package com.ytarzimanov.ftp_assistant;
import java.awt.AWTException;
import java.awt.BorderLayout;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Insets;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.Rectangle;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import com.ytarzimanov.controls.navbar.NavBar;
import com.ytarzimanov.ftp_assistant.adapters.ModelSourceAdapter;
import com.ytarzimanov.ftp_assistant.models.PopupMenuController;
import com.ytarzimanov.ftp_assistant.models.ViewController;
import com.ytarzimanov.ftp_assistant.models.ViewController.OnDownloadListener;
import com.ytarzimanov.ftp_assistant.models.core.File;
import com.ytarzimanov.ftp_assistant.models.core.Global;
import com.ytarzimanov.ftp_assistant.net.Downloader;
import com.ytarzimanov.ftp_assistant.net.ListUpdater;
import com.ytarzimanov.ftp_assistant.net.Downloader.RemoteSource;
import com.nightfloppy.simplelog.SimpleLog;

import javax.swing.JButton;
import javax.swing.JToolBar;

/**
 * @see Ftp Assistant 
 * @version 1.0.0
 * @author Tarzimanov Yuri
 * @since 28.03.2013
 */

@SuppressWarnings("serial")
public class MainForm extends JDialog implements ActionListener, 
 com.ytarzimanov.ftp_assistant.net.ListUpdater.OnEventListener,
 com.ytarzimanov.ftp_assistant.net.Downloader.OnEventListener,
 OnDownloadListener{
	
	private static final String CAPTION_EXIT = "Exit";
	private static final String CAPTION_OPTIONS = "Options";
	private static final String CAPTION_REFRESH = "Refresh";
	private static final String CAPTION_COPYTO = "Copy to clipboard of last items";
	private static final String CAPTION_PROGRAMHEADER = "Ftp Assistant";
	private static final String ACTION_COPY = "copy";
	private static final String ACTION_REFRESH = "refresh";
	private static final String ACTION_SEARCH = "search";
	private static final String ACTION_OPTIONS = "options";
	private static final String ACTION_EXIT = "exit";
	
	
	//Data Vars
	private PopupMenuController popmenucontroller;
	private Boolean isVisibleWnd = true;
	private ViewController controller;
	
	//Swing Controls
	private NavBar List = new NavBar();
	private JTextField txtFilter = new JTextField();
	private TrayIcon trayIcon;
	private ModelSourceAdapter sourcemodel;
	
	public static MainForm frm;
	public MenuItem copyToItem;
	public JButton btn_copy;
	
	public static void main(final String[] args) throws Exception {
		SimpleLog.getInstance().setEnable(true, "FtpAssistant.log");
		SwingUtilities.invokeLater(new Runnable()
	      {
	         public void run()
	         {
	            try {
					UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
				} catch (UnsupportedLookAndFeelException | ClassNotFoundException | InstantiationException | IllegalAccessException e) {
					try {
						UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
					} catch (ClassNotFoundException | InstantiationException
							| IllegalAccessException
							| UnsupportedLookAndFeelException e1) {
						SimpleLog.getInstance().err(e1);
					}
				}	

	            try {
					frm = new MainForm(args);
				} catch (Exception e) {
					SimpleLog.getInstance().err(e);
				}
	     		frm.setVisible(true);
	         }
	      }
	      );
		
	}
	
	public void SnapToRightSide(){
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice gd = ge.getDefaultScreenDevice();
		GraphicsConfiguration gc = gd.getDefaultConfiguration();
		Insets ins = java.awt.Toolkit.getDefaultToolkit().getScreenInsets(gc);

		Rectangle maxRect = gc.getBounds();
		this.setBounds((maxRect.width - ins.right) - 200, Math.abs(maxRect.y - ins.top), 200, maxRect.height - ins.bottom);
	}
	
	public void PreparedControls(){
		txtFilter.addActionListener(this);
		getContentPane().add(txtFilter, BorderLayout.SOUTH);
		txtFilter.setVisible(false);
		JScrollPane scrollPane = new JScrollPane(List);
		getContentPane().add(scrollPane, BorderLayout.CENTER);
		
		JToolBar toolBar = new JToolBar();
		toolBar.setFloatable(false);
		scrollPane.setColumnHeaderView(toolBar);
		
		
		btn_copy = new JButton(new ImageIcon(Class.class.getResource("/copy.png")));
		toolBar.add(btn_copy);
		btn_copy.setActionCommand(ACTION_COPY);
		btn_copy.setFocusable(false);
		btn_copy.setMargin(new Insets(1, 1, 1, 1));
		
		JButton btn_refresh = new JButton(new ImageIcon(Class.class.getResource("/refresh.png")));
		toolBar.add(btn_refresh);
		btn_refresh.setActionCommand(ACTION_REFRESH);
		btn_refresh.setFocusable(false);
		btn_refresh.setMargin(new Insets(1, 1, 1, 1));
				
		JButton btn_settings = new JButton(new ImageIcon(Class.class.getResource("/settings.png")));
		toolBar.add(btn_settings);
		btn_settings.setFocusable(false);
		btn_settings.setActionCommand(ACTION_OPTIONS);
		btn_settings.setMargin(new Insets(1, 1, 1, 1));
		btn_settings.addActionListener(this);
		
		
		btn_refresh.addActionListener(this);
		btn_copy.addActionListener(this);
	}
	
	@SuppressWarnings("static-access")
	public void bindTray(){
		   if (SystemTray.getSystemTray().isSupported()) {
			   SimpleLog.getInstance().trace(this.getClass(),"System Tray was supported by system.");
			   final PopupMenu popup = new PopupMenu();
		        trayIcon = new TrayIcon(new ImageIcon(Class.class.getResource("/prog.png")).getImage());
		        trayIcon.setToolTip(CAPTION_PROGRAMHEADER);	        
		        trayIcon.setImageAutoSize(true);
		        trayIcon.addMouseListener(new MouseListener(){
		    		@Override
		    		public void mouseClicked(MouseEvent e) {
		    			if (SwingUtilities.isLeftMouseButton(e)){
		    				isVisibleWnd = !isVisibleWnd;
		    				frm.setVisible(isVisibleWnd);
		    				if (isVisibleWnd)
		    					frm.toFront();	
		    				}
		    		}

		    		@Override
		    		public void mouseEntered(MouseEvent e) {
		    		   ;
		    		}

		    		@Override
		    		public void mouseExited(MouseEvent e) {
		    			;
		    		}

		    		@Override
		    		public void mousePressed(MouseEvent e) {

		    		}

		    		@Override
		    		public void mouseReleased(MouseEvent e) {
		    			;
		    		}});
		        
		        MenuItem exitItem = new MenuItem(CAPTION_EXIT);
		        copyToItem = new MenuItem(CAPTION_COPYTO);
		        MenuItem optionsItem = new MenuItem(CAPTION_OPTIONS);
		        MenuItem refreshToItem = new MenuItem(CAPTION_REFRESH);
		        
		        optionsItem.setActionCommand(ACTION_OPTIONS);
		        refreshToItem.setActionCommand(ACTION_REFRESH);
		        copyToItem.setActionCommand(ACTION_COPY);
		        exitItem.setActionCommand(ACTION_EXIT);
		        
		        popup.add(refreshToItem);
		        popup.add(optionsItem);
		        popup.add(copyToItem);
		        popup.add(exitItem);
		        
		        refreshToItem.addActionListener(this);
		        exitItem.addActionListener(this);
		        copyToItem.addActionListener(this);
		        optionsItem.addActionListener(this);
		        
		        trayIcon.setPopupMenu(popup);
		        
		        try {
		        	SystemTray.getSystemTray().add(trayIcon);
		        } catch (AWTException e) {
		        	SimpleLog.getInstance().err(e);
		        }
	        }
	
	}
	
	
	public MainForm(String[] args) throws Exception{		
		SimpleLog.getInstance().warn(this.getClass(),"Initialization program.");
		setUndecorated(true);
		SnapToRightSide();		
		PreparedControls();
		bindTray();
			
		sourcemodel = new ModelSourceAdapter(Global.getInstane().getServerList());	
		popmenucontroller = new PopupMenuController(sourcemodel, List); 
		controller = new ViewController(sourcemodel); 
		controller.setOnDownloadListener(this);
		updateCopyToUI();
		
		List.setModel(sourcemodel);
		List.addOnChangeGroupCheckBoxListener(controller);
		List.setPopupMenu(popmenucontroller.getPopupMenu());
		List.setOnPopupMenuHandler(popmenucontroller);
		ListUpdater.getInstance();
		ListUpdater.getInstance().setListener(this);
		ListUpdater.getInstance().update();
		
		
		List.addOnClickListener(controller);
		List.addOnChangeGroupStateListener(sourcemodel);
		List.setDrawHandler(controller);
		List.update();
	}
	
	public void updateCopyToUI(){
		List.setCheckBoxEnabled(Global.getInstane().getEnableCopyToButtons());
		copyToItem.setEnabled(Global.getInstane().getEnableCopyToButtons());
		btn_copy.setEnabled(Global.getInstane().getEnableCopyToButtons());
	}
	
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		switch (arg0.getActionCommand()){
		case ACTION_COPY:
			int i = 0, j = 0;
		    StringBuffer str = new StringBuffer("");
			for (i = 0; i < Global.getInstane().getServerList().size(); i++){
				for (j = 0; j < Global.getInstane().getServerList().get(i).size(); j++){
				  if ((Global.getInstane().getServerList().get(i).get(j).size() > 0) &&
						 (Global.getInstane().getServerList().get(i).get(j).getCopyToClipBtnChecked() == true)){
					str.append(Global.getInstane().getServerList().get(i).get(j).get(0).getCopyToFormattedName());
					str.append(";");
				  }
				}
			}
			StringSelection ss = new StringSelection(str.toString());
			Toolkit.getDefaultToolkit().getSystemClipboard().setContents(ss, null);
			SimpleLog.getInstance().trace(this.getClass(),"List of selected last files was copied to clipboard.");
		break;
		case ACTION_EXIT:
			Global.getInstane().save();
			SimpleLog.getInstance().warn(this.getClass(),"User closes of program.");
			System.exit(ABORT);
		break;
		case ACTION_OPTIONS:
			ListUpdater.getInstance().pause();
			SettingForm form = new SettingForm();
			form.setModal(true);
			form.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
			form.setVisible(true);
			updateCopyToUI();
			List.update();
			ListUpdater.getInstance().start();
		break;
		case ACTION_REFRESH:
			ListUpdater.getInstance().update();
		break;
		case ACTION_SEARCH:
			
			List.update();
		break;		   
		}
	}


	@Override
	public void onAvailableNewFiles(String updates) {
		List.update();
		if (!updates.isEmpty())
		  trayIcon.displayMessage(CAPTION_PROGRAMHEADER, "Available a new files \n"+ updates.toString(), TrayIcon.MessageType.INFO);
	}

	@Override
	public void onInit() {
		List.update();
	}

	@Override
	public void onUpdate(int progress) {
		List.repaint();	
	}

	@Override
	public void onFinished(RemoteSource source) {
	  List.repaint();	
	  trayIcon.displayMessage(CAPTION_PROGRAMHEADER, "File " + source.getRemoteFileName() + " have been downloaded", TrayIcon.MessageType.INFO);
	}

	@Override
	public void onNeedDownload(File file) {
		Downloader.download(file, this, file.getDownloadFilePath());
	}

	@Override
	public void onDownload(File file) {
		Downloader.download(file, this, file.getDownloadFilePath());		
	}

	@Override
	public void onTerminated() {
		List.repaint();
		
	}




}
