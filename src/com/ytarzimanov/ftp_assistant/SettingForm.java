package com.ytarzimanov.ftp_assistant;
import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import javax.swing.JTree;
import javax.swing.JTabbedPane;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.JPasswordField;

import java.awt.CardLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.SpinnerNumberModel;
import javax.swing.JPopupMenu;

import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JMenuItem;

import com.ytarzimanov.ftp_assistant.models.TreeModel;
import com.ytarzimanov.ftp_assistant.models.core.Directory;
import com.ytarzimanov.ftp_assistant.models.core.Global;
import com.ytarzimanov.ftp_assistant.models.core.Server;

public class SettingForm extends JDialog implements ActionListener, TreeSelectionListener{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final JPanel contentPanel = new JPanel();
	private static final String TREE_LEVEL_1 = "SRV_LEVEL";
	private static final String TREE_LEVEL_2 = "DIR_LEVEL";
	private static final String TREE_LEVEL_EMPTY = "EMPTY_LEVEL";
	private static final String TREE_LEVEL_ADD = "ADD_LEVEL";
	private static final String TREE_LEVEL_UP = "UP_LEVEL";
	private static final String TREE_LEVEL_DOWN = "DOWN_LEVEL";
	private static final String TREE_LEVEL_REMOVE = "REMOVE_LEVEL";	
	private static final String IS_EMPTY = "IS_EMPTY";	
	private static final String TOOLTIP_DISPLAY_FIELD = "<html>It is format for displaying files in list. <br>" +
	"We can use @gN when N  is number of group according group in reqular expressions for Filter field. <br> " +
	"Moreover we can use following additional symbols: @13-next line and @d-created date of file. <br>"+
	"Example: myfile@g1@13@d = myfile001 </html>";
	private static final String TOOLTIP_DATETIME_FIELD = "<html>It is datetime format for @d symbol.</html>";
	private static final String TOOLTIP_COPY_TO_FIELD = "<html>It is format of the text will be copied to clipboard, "+
	"<br> when user click to CopyTo button. <br>" +
	"Format is the same that for Display format</html>";
	
	private JTextField tfSrvHost;
	private JTextField tfSrvLogin;
	private JPasswordField pfSrvUserPassword;
	private JTextField tfDirCaption;
	private JTextField tfDirSource;
	private JTextField tfDirDownload;
	private JTextField tfDirFilter;
	private JTextField tfDirDisplayFormat;
	private JTextField tfDirDateFormat;
	private JTextField tfDirCopyToFormat;
	private JTree tvServers; 
	private JCheckBox chkDirAutoDownload;
	private JSpinner spUpdateTime;
	private CardLayout levelLayout;
	private JPanel pnlDirLevel;
	private JPanel pnlSrvLevel;
	private JPanel pnlLevels;
	private Object prevEditObject;
	private JCheckBox chkSrvUsePassiveMode;
	private JCheckBox chkEnabledCopyTo;
	
	public void createGUI(){
		setTitle("Options");
		setBounds(920, 50, 481, 468);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new BorderLayout(0, 0));
		{
			JTabbedPane tpMain = new JTabbedPane(JTabbedPane.TOP);
			contentPanel.add(tpMain, BorderLayout.CENTER);
			{
				JPanel pnCommon = new JPanel();
				tpMain.addTab("Common", null, pnCommon, null);
				
				JLabel lbUpdateEvery = new JLabel("Update every");
				
				spUpdateTime = new JSpinner();
				spUpdateTime.setModel(new SpinnerNumberModel(60, 60, 600, 10));
				lbUpdateEvery.setLabelFor(spUpdateTime);
				
				JLabel lblSec = new JLabel("sec");
				
				chkEnabledCopyTo = new JCheckBox("Enabled \"copy to clipboard\" buttons");
				GroupLayout gl_pnCommon = new GroupLayout(pnCommon);
				gl_pnCommon.setHorizontalGroup(
					gl_pnCommon.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_pnCommon.createSequentialGroup()
							.addContainerGap()
							.addGroup(gl_pnCommon.createParallelGroup(Alignment.LEADING)
								.addGroup(gl_pnCommon.createSequentialGroup()
									.addComponent(lbUpdateEvery)
									.addPreferredGap(ComponentPlacement.UNRELATED)
									.addComponent(spUpdateTime, GroupLayout.PREFERRED_SIZE, 48, GroupLayout.PREFERRED_SIZE)
									.addPreferredGap(ComponentPlacement.RELATED)
									.addComponent(lblSec))
								.addComponent(chkEnabledCopyTo))
							.addContainerGap(243, Short.MAX_VALUE))
				);
				gl_pnCommon.setVerticalGroup(
					gl_pnCommon.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_pnCommon.createSequentialGroup()
							.addContainerGap()
							.addGroup(gl_pnCommon.createParallelGroup(Alignment.BASELINE)
								.addComponent(lbUpdateEvery)
								.addComponent(spUpdateTime, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
								.addComponent(lblSec))
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(chkEnabledCopyTo)
							.addContainerGap(302, Short.MAX_VALUE))
				);
				pnCommon.setLayout(gl_pnCommon);
			}
			{
				JPanel pnServerSettings = new JPanel();
				tpMain.addTab("Connections", null, pnServerSettings, null);
				pnServerSettings.setLayout(new BorderLayout(0, 0));
				{
					JPanel pnlDetails = new JPanel();
					pnServerSettings.add(pnlDetails);
					pnlDetails.setLayout(new BorderLayout(0, 0));
					
					pnlLevels = new JPanel();
					pnlDetails.add(pnlLevels, BorderLayout.CENTER);
					levelLayout = new CardLayout(0, 0);
					
					pnlLevels.setLayout(levelLayout);
					
					pnlDirLevel = new JPanel();
					pnlDirLevel.setName(TREE_LEVEL_2);
					pnlLevels.add(pnlDirLevel, TREE_LEVEL_2);
					
					JLabel lblDirCaption = new JLabel("Caption:");
					
					tfDirCaption = new JTextField();
					tfDirCaption.setToolTipText("Caption for group");
					tfDirCaption.setColumns(10);
					
					tfDirSource = new JTextField();
					tfDirSource.setToolTipText("Path of ftp source");
					tfDirSource.setColumns(10);
					
					JLabel lbDirSource = new JLabel("Source:");
					
					JLabel lbDirFilter = new JLabel("Filter:");
					
					JLabel lbDirDisplay = new JLabel("Display:");
					
					JLabel lbDirDownloadDir = new JLabel("Directory:");
					
					tfDirDownload = new JTextField();
					tfDirDownload.setColumns(10);
					
					tfDirFilter = new JTextField();
					tfDirFilter.setToolTipText("It is filter for display. You should use reqular expressions for select necessary files.");
					tfDirFilter.setColumns(10);
					
					tfDirDisplayFormat = new JTextField();
					tfDirDisplayFormat.setToolTipText(TOOLTIP_DISPLAY_FIELD);
					tfDirDisplayFormat.setColumns(10);
					
					tfDirDateFormat = new JTextField();
					tfDirDateFormat.setToolTipText(TOOLTIP_DATETIME_FIELD);
					tfDirDateFormat.setColumns(10);
					
					tfDirCopyToFormat = new JTextField();
					tfDirCopyToFormat.setToolTipText(TOOLTIP_COPY_TO_FIELD);
					tfDirCopyToFormat.setColumns(10);
					
					chkDirAutoDownload = new JCheckBox("Auto donwload of new files");
					
					JLabel ldDirDateFormat = new JLabel("Date:");
					
					JLabel lbDirCopyFormat = new JLabel("Copy To:");
					GroupLayout gl_pnlDirLevel = new GroupLayout(pnlDirLevel);
					gl_pnlDirLevel.setHorizontalGroup(
						gl_pnlDirLevel.createParallelGroup(Alignment.LEADING)
							.addGroup(gl_pnlDirLevel.createSequentialGroup()
								.addContainerGap()
								.addGroup(gl_pnlDirLevel.createParallelGroup(Alignment.LEADING)
									.addGroup(gl_pnlDirLevel.createSequentialGroup()
										.addGroup(gl_pnlDirLevel.createParallelGroup(Alignment.LEADING)
											.addGroup(gl_pnlDirLevel.createParallelGroup(Alignment.LEADING, false)
												.addComponent(lbDirDownloadDir, GroupLayout.DEFAULT_SIZE, 50, Short.MAX_VALUE)
												.addComponent(lbDirFilter, GroupLayout.DEFAULT_SIZE, 50, Short.MAX_VALUE)
												.addComponent(lbDirSource, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
											.addComponent(lblDirCaption, GroupLayout.PREFERRED_SIZE, 50, GroupLayout.PREFERRED_SIZE)
											.addComponent(lbDirDisplay, GroupLayout.PREFERRED_SIZE, 50, GroupLayout.PREFERRED_SIZE)
											.addComponent(ldDirDateFormat, GroupLayout.PREFERRED_SIZE, 50, GroupLayout.PREFERRED_SIZE)
											.addComponent(lbDirCopyFormat, GroupLayout.PREFERRED_SIZE, 50, GroupLayout.PREFERRED_SIZE))
										.addGap(12)
										.addGroup(gl_pnlDirLevel.createParallelGroup(Alignment.TRAILING)
											.addComponent(tfDirCopyToFormat, GroupLayout.DEFAULT_SIZE, 207, Short.MAX_VALUE)
											.addComponent(tfDirDateFormat, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 207, Short.MAX_VALUE)
											.addComponent(tfDirDisplayFormat, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 207, Short.MAX_VALUE)
											.addComponent(tfDirCaption, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 207, Short.MAX_VALUE)
											.addComponent(tfDirFilter, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 207, Short.MAX_VALUE)
											.addComponent(tfDirDownload, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 207, Short.MAX_VALUE)
											.addComponent(tfDirSource, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 207, Short.MAX_VALUE))
										.addGap(21))
									.addGroup(gl_pnlDirLevel.createSequentialGroup()
										.addComponent(chkDirAutoDownload)
										.addContainerGap(135, Short.MAX_VALUE))))
					);
					gl_pnlDirLevel.setVerticalGroup(
						gl_pnlDirLevel.createParallelGroup(Alignment.LEADING)
							.addGroup(gl_pnlDirLevel.createSequentialGroup()
								.addGap(9)
								.addGroup(gl_pnlDirLevel.createParallelGroup(Alignment.BASELINE)
									.addComponent(tfDirCaption, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
									.addComponent(lblDirCaption))
								.addPreferredGap(ComponentPlacement.UNRELATED)
								.addGroup(gl_pnlDirLevel.createParallelGroup(Alignment.BASELINE)
									.addComponent(tfDirSource, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
									.addComponent(lbDirSource))
								.addPreferredGap(ComponentPlacement.UNRELATED)
								.addGroup(gl_pnlDirLevel.createParallelGroup(Alignment.BASELINE)
									.addComponent(lbDirDownloadDir)
									.addComponent(tfDirDownload, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
								.addPreferredGap(ComponentPlacement.UNRELATED)
								.addGroup(gl_pnlDirLevel.createParallelGroup(Alignment.BASELINE)
									.addComponent(tfDirFilter, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
									.addComponent(lbDirFilter))
								.addPreferredGap(ComponentPlacement.UNRELATED)
								.addGroup(gl_pnlDirLevel.createParallelGroup(Alignment.BASELINE)
									.addComponent(lbDirDisplay)
									.addComponent(tfDirDisplayFormat, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
								.addGap(14)
								.addGroup(gl_pnlDirLevel.createParallelGroup(Alignment.BASELINE)
									.addComponent(tfDirDateFormat, GroupLayout.PREFERRED_SIZE, 20, GroupLayout.PREFERRED_SIZE)
									.addComponent(ldDirDateFormat))
								.addPreferredGap(ComponentPlacement.UNRELATED)
								.addGroup(gl_pnlDirLevel.createParallelGroup(Alignment.BASELINE)
									.addComponent(lbDirCopyFormat)
									.addComponent(tfDirCopyToFormat, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
								.addGap(18)
								.addComponent(chkDirAutoDownload)
								.addGap(99))
					);
					pnlDirLevel.setLayout(gl_pnlDirLevel);
					
					pnlSrvLevel = new JPanel();
					pnlLevels.add(pnlSrvLevel, TREE_LEVEL_1);
					pnlSrvLevel.setName(TREE_LEVEL_1);
					JLabel lbSrvHost = new JLabel("Host:");
					
					JLabel lbSrvLogin = new JLabel("Login:");
					
					JLabel lbSrvUserPassword = new JLabel("Password:");
					
					chkSrvUsePassiveMode = new JCheckBox("Use Passive Mode");
					chkSrvUsePassiveMode.setToolTipText("Passive mode");
					
					tfSrvHost = new JTextField();
					tfSrvHost.setToolTipText("The name host of ftp server");
					lbSrvHost.setLabelFor(tfSrvHost);
					tfSrvHost.setColumns(10);
					
					tfSrvLogin = new JTextField();
					tfSrvLogin.setToolTipText("Login");
					lbSrvLogin.setLabelFor(tfSrvLogin);
					tfSrvLogin.setColumns(10);
					
					pfSrvUserPassword = new JPasswordField();
					pfSrvUserPassword.setToolTipText("Password");
					lbSrvUserPassword.setLabelFor(pfSrvUserPassword);
					GroupLayout gl_pnlSrvLevel = new GroupLayout(pnlSrvLevel);
					gl_pnlSrvLevel.setHorizontalGroup(
						gl_pnlSrvLevel.createParallelGroup(Alignment.LEADING)
							.addGroup(gl_pnlSrvLevel.createSequentialGroup()
								.addContainerGap()
								.addGroup(gl_pnlSrvLevel.createParallelGroup(Alignment.LEADING)
									.addComponent(chkSrvUsePassiveMode)
									.addGroup(gl_pnlSrvLevel.createSequentialGroup()
										.addGroup(gl_pnlSrvLevel.createParallelGroup(Alignment.LEADING)
											.addComponent(lbSrvHost)
											.addComponent(lbSrvUserPassword)
											.addComponent(lbSrvLogin))
										.addGap(10)
										.addGroup(gl_pnlSrvLevel.createParallelGroup(Alignment.LEADING, false)
											.addComponent(pfSrvUserPassword, 197, 197, Short.MAX_VALUE)
											.addComponent(tfSrvLogin)
											.addComponent(tfSrvHost, GroupLayout.PREFERRED_SIZE, 211, GroupLayout.PREFERRED_SIZE))))
								.addContainerGap(19, Short.MAX_VALUE))
					);
					gl_pnlSrvLevel.setVerticalGroup(
						gl_pnlSrvLevel.createParallelGroup(Alignment.LEADING)
							.addGroup(gl_pnlSrvLevel.createSequentialGroup()
								.addContainerGap()
								.addGroup(gl_pnlSrvLevel.createParallelGroup(Alignment.BASELINE)
									.addComponent(lbSrvHost)
									.addComponent(tfSrvHost, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
								.addGap(9)
								.addGroup(gl_pnlSrvLevel.createParallelGroup(Alignment.BASELINE)
									.addComponent(tfSrvLogin, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
									.addComponent(lbSrvLogin))
								.addPreferredGap(ComponentPlacement.UNRELATED)
								.addGroup(gl_pnlSrvLevel.createParallelGroup(Alignment.BASELINE)
									.addComponent(pfSrvUserPassword, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
									.addComponent(lbSrvUserPassword))
								.addPreferredGap(ComponentPlacement.UNRELATED)
								.addComponent(chkSrvUsePassiveMode)
								.addContainerGap(237, Short.MAX_VALUE))
					);
					pnlSrvLevel.setLayout(gl_pnlSrvLevel);	
					
					JPanel pnlEmpty = new JPanel();
					pnlLevels.add(pnlEmpty, TREE_LEVEL_EMPTY);
				}
				{
					tvServers = new JTree();
					pnServerSettings.add(tvServers, BorderLayout.WEST);
					tvServers.setPreferredSize(new Dimension(150,100));
					tvServers.setExpandsSelectedPaths(true);
				}
			}
		}
		{
			JPanel buttonPane = new JPanel();
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			buttonPane.setLayout(new BorderLayout(0, 0));
			
			JPanel panel = new JPanel();
			buttonPane.add(panel, BorderLayout.EAST);
			{
				JButton btnClose = new JButton("Close");
				panel.add(btnClose);
				btnClose.setActionCommand("OK");
				btnClose.addActionListener(this);
			}
		}
		
		JPopupMenu popupMenu = new JPopupMenu();
		addPopup(tvServers, popupMenu);
		
		JMenuItem miAdd = new JMenuItem("Add");
		miAdd.addActionListener(this);
		miAdd.setActionCommand(TREE_LEVEL_ADD);
		popupMenu.add(miAdd);
		
		JMenuItem miRemove = new JMenuItem("Remove");
		popupMenu.add(miRemove);
		miRemove.setActionCommand(TREE_LEVEL_REMOVE);
		miRemove.addActionListener(this);
		popupMenu.addSeparator();
		
		JMenuItem miUp = new JMenuItem("Up");
		miUp.addActionListener(this);
		miUp.setActionCommand(TREE_LEVEL_UP);
		popupMenu.add(miUp);
		
		JMenuItem miDown = new JMenuItem("Down");
		popupMenu.add(miDown);
		miDown.setActionCommand(TREE_LEVEL_DOWN);
		miDown.addActionListener(this);
		
		levelLayout.show(pnlLevels, TREE_LEVEL_EMPTY);
	}
	
    public void loadCommonOptions(){
    	spUpdateTime.setValue(Global.getInstane().getTimerSec());
    	chkEnabledCopyTo.setSelected(Global.getInstane().getEnableCopyToButtons());
    }
    
    public void saveCommonOptions(){
    	Global.getInstane().setTimerSec((Integer)spUpdateTime.getValue());
    	Global.getInstane().setEnableCopyToButtons(chkEnabledCopyTo.isSelected());
    }
	
	public SettingForm() {
		createGUI();
		tvServers.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		tvServers.setModel(new TreeModel(Global.getInstane()));
		tvServers.updateUI();
		tvServers.addTreeSelectionListener(this);

		loadCommonOptions();
	}
	
	private void loadServerInfo(Server item){
		tfSrvHost.setText(item.getHost());
		tfSrvLogin.setText(item.getLogin());
		pfSrvUserPassword.setText(IS_EMPTY);
		chkSrvUsePassiveMode.setSelected(item.getUsePassiveMode());
		prevEditObject = item;
	}
	
	private void saveServerInfo(Server item){
		item.setHost(tfSrvHost.getText());
		item.setLogin(tfSrvLogin.getText());
		item.setUsePassiveMode(chkSrvUsePassiveMode.isSelected());
		if (!pfSrvUserPassword.getText().equals(IS_EMPTY))
			item.setPassword(Utils.encode(pfSrvUserPassword.getText(), item.getHost()));
		prevEditObject = item;
	}
	
	private void saveDirectoryInfo(Directory Item){
		Item.setCaption(tfDirCaption.getText()); 
		Item.setSourceFolder(tfDirSource.getText());
		Item.setDownloadFolder(tfDirDownload.getText());
		Item.setSourceFolderFilter(tfDirFilter.getText());
		Item.setDisplayFormat(tfDirDisplayFormat.getText());
		Item.setDisplayDateTimeFormat(tfDirDateFormat.getText());
		Item.setCopyToClipboardFormat(tfDirCopyToFormat.getText());
		Item.setAutoDownload(chkDirAutoDownload.isSelected());
		prevEditObject = Item;
	}
	
	
	private void savePreviousObject(){
		if (prevEditObject != null){
			if (prevEditObject instanceof Directory){
				saveDirectoryInfo((Directory)prevEditObject);
			}
			else
			{
				if (prevEditObject instanceof Server){
					saveServerInfo((Server)prevEditObject);
				}
			}
		}
	}
	
	private void loadDirectoryInfo(Directory Item){
		tfDirCaption.setText(Item.getCaption()); 
		tfDirSource.setText(Item.getRemoteFolder());
		tfDirDownload.setText(Item.getDownloadFolder());
		tfDirFilter.setText(Item.getSourceFolderFilter());
		tfDirDisplayFormat.setText(Item.getDisplayFormat());
		tfDirDateFormat.setText(Item.getDisplayDateTimeFormat());
		tfDirCopyToFormat.setText(Item.getCopyToClipboardFormat());
		chkDirAutoDownload.setSelected(Item.getAutoDownload());
		prevEditObject = Item;
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		if (arg0.getActionCommand() == "OK"){
			saveCommonOptions();
			savePreviousObject();
			Global.getInstane().save();
		}
		
		TreePath path = null;
		if (arg0.getActionCommand() == TREE_LEVEL_ADD){	
			Object obj = tvServers.getLastSelectedPathComponent();
			if (obj instanceof Server){
				Server srvnode = (Server)obj;
				prevEditObject = srvnode.append();
				loadDirectoryInfo((Directory)prevEditObject);
				levelLayout.show(pnlLevels, TREE_LEVEL_2);
				path = new TreePath(new Object[]{Global.getInstane(), ((Directory)prevEditObject).getServer(), prevEditObject});
			} else {
				if (obj instanceof Global){
				  levelLayout.show(pnlLevels, TREE_LEVEL_1);
				  prevEditObject = new Server();
				  Global.getInstane().getServerList().add((Server)prevEditObject);
				  loadServerInfo((Server)prevEditObject);	
				  path = new TreePath(new Object[]{Global.getInstane(), prevEditObject});
				}
			}
			
		tvServers.setSelectionPath(path);
    	tvServers.updateUI();
		}
		
		if (arg0.getActionCommand() == TREE_LEVEL_REMOVE){
			if (tvServers.getLastSelectedPathComponent() == null)
				return;
			
			savePreviousObject();
			if (tvServers.getLastSelectedPathComponent() instanceof Directory){
				Directory dir = (Directory)tvServers.getLastSelectedPathComponent();
				Server srv = dir.getServer();
				srv.remove(dir);
				path = new TreePath(new Object[]{Global.getInstane(), srv});
			}
			else
			{
				if (tvServers.getLastSelectedPathComponent() instanceof Server){
					((Server)tvServers.getLastSelectedPathComponent()).remove();
					Global.getInstane().getServerList().remove(tvServers.getLastSelectedPathComponent());
					loadServerInfo((Server)tvServers.getLastSelectedPathComponent());
					levelLayout.show(pnlLevels, TREE_LEVEL_1);
					path = new TreePath(Global.getInstane());
				}
			}
			
			levelLayout.show(pnlLevels, TREE_LEVEL_EMPTY);
			tvServers.setSelectionPath(path);
			tvServers.updateUI();
		}
		
		if (arg0.getActionCommand() == TREE_LEVEL_UP){
			if (tvServers.getLastSelectedPathComponent() == null)
				return;	
			savePreviousObject();
			if (tvServers.getLastSelectedPathComponent() instanceof Directory){
				Directory dir = (Directory)tvServers.getLastSelectedPathComponent();
				Server srv = dir.getServer();
				srv.move(dir, -1);
				path = new TreePath(new Object[]{Global.getInstane(), srv, dir});
			}			
			tvServers.setSelectionPath(path);
			tvServers.updateUI();
		}
		
		
		if (arg0.getActionCommand() == TREE_LEVEL_DOWN){
			if (tvServers.getLastSelectedPathComponent() == null)
				return;	
			savePreviousObject();
			if (tvServers.getLastSelectedPathComponent() instanceof Directory){
				Directory dir = (Directory)tvServers.getLastSelectedPathComponent();
				Server srv = dir.getServer();
				srv.move(dir, 1);
				path = new TreePath(new Object[]{Global.getInstane(), srv, dir});
			}			
			tvServers.setSelectionPath(path);
			tvServers.updateUI();
		}		
		
		Global.getInstane().save();
		if (arg0.getActionCommand() == "OK")
			dispose();
	}

	@Override
	public void valueChanged(TreeSelectionEvent arg0) {
		if (tvServers.getLastSelectedPathComponent() == null)
			return;
		
		savePreviousObject();
		if (tvServers.getLastSelectedPathComponent() instanceof Directory){
			loadDirectoryInfo((Directory)tvServers.getLastSelectedPathComponent());
			levelLayout.show(pnlLevels, TREE_LEVEL_2);
		}
		else
		{
			if (tvServers.getLastSelectedPathComponent() instanceof Server){
				loadServerInfo((Server)tvServers.getLastSelectedPathComponent());
				levelLayout.show(pnlLevels, TREE_LEVEL_1);
			}
			else
				levelLayout.show(pnlLevels, TREE_LEVEL_EMPTY);
		}
		
	}
	private static void addPopup(Component component, final JPopupMenu popup) {
		component.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				if (e.isPopupTrigger()) {
					showMenu(e);
				}
			}
			public void mouseReleased(MouseEvent e) {
				if (e.isPopupTrigger()) {
					showMenu(e);
				}
			}
			private void showMenu(MouseEvent e) {
				popup.show(e.getComponent(), e.getX(), e.getY());
			}
		});
	}
}
