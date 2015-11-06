package com.ytarzimanov.ftp_assistant;

import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import java.awt.BorderLayout;

import javax.swing.JButton;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.FlowLayout;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JList;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

import com.ytarzimanov.ftp_assistant.models.core.Directory;

import javax.swing.JTextField;
import javax.swing.JScrollPane;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.SwingConstants;

@SuppressWarnings("serial")
public class RemoteBrowser extends JDialog {
	private static final long serialVersionUID = -3891590166589696754L;
	private FTPClient ftp = new FTPClient();
	private DefaultListModel listModel = new DefaultListModel();
	private JTextField fldPath;
	private ArrayList<String> listPath;
	private JList list;
	private Directory selDir;
	
	@SuppressWarnings("unchecked")
	public void loadList(ArrayList<String> listPath) throws IOException{
		String path = "/";
		for (int i = 0; i < listPath.size(); i++){
			path += listPath.get(i) + "/";
		}
		FTPFile[] files =  ftp.listDirectories(path);
		listModel.clear();
		for (int i = 0; i < files.length; i++){
			listModel.addElement(files[i].getName());
		}
		fldPath.setText(path);
	}
	
	
	public void createGUI(){
		JPanel pnlBottom = new JPanel();
		FlowLayout fl_pnlBottom = (FlowLayout) pnlBottom.getLayout();
		fl_pnlBottom.setAlignment(FlowLayout.RIGHT);
		getContentPane().add(pnlBottom, BorderLayout.SOUTH);
		
		JButton btnOk = new JButton("OK");
		btnOk.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				selDir.setSourceFolder(fldPath.getText().trim());
				dispose();
			}
		});
		pnlBottom.add(btnOk);
		
		JButton btnCancel = new JButton("Cancel");
		btnCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		pnlBottom.add(btnCancel);
		
		JPanel pnlTop = new JPanel();
		getContentPane().add(pnlTop, BorderLayout.NORTH);
		pnlTop.setLayout(new BorderLayout(0, 0));
		
		JPanel panel = new JPanel();
		pnlTop.add(panel, BorderLayout.WEST);
		panel.setLayout(new BorderLayout(0, 0));
		
		JButton btnDown = new JButton(new ImageIcon(ClassLoader.getSystemResource("right.png")));
		btnDown.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (list.getSelectedValue() != null){
					listPath.add((String)list.getSelectedValue());
					  try {
						loadList(listPath);
					  } catch (IOException e) {}
				}
			}
		});
		panel.add(btnDown, BorderLayout.CENTER);
		
		JButton btnUp = new JButton(new ImageIcon(ClassLoader.getSystemResource("left.png")));
		btnUp.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (listPath.size() > 0){
				  listPath.remove(listPath.size()-1);
				  try {
					loadList(listPath);
				  } catch (IOException e) {}
				}
			}
		});
		panel.add(btnUp, BorderLayout.WEST);
		
		fldPath = new JTextField();
		pnlTop.add(fldPath, BorderLayout.CENTER);
		fldPath.setHorizontalAlignment(SwingConstants.LEFT);
		fldPath.setEnabled(false);
		fldPath.setEditable(false);
		fldPath.setColumns(10);
		
		JPanel pnlCenter = new JPanel();
		getContentPane().add(pnlCenter, BorderLayout.CENTER);
		pnlCenter.setLayout(new BorderLayout(0, 0));
		
		JScrollPane scrollPane = new JScrollPane();
		pnlCenter.add(scrollPane, BorderLayout.CENTER);
		
		list = new JList(listModel);
		list.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				JList list = (JList)arg0.getSource();
		        if (arg0.getClickCount() == 2) {
		            int index = list.locationToIndex(arg0.getPoint());
		            listPath.add((String)listModel.get(index));
		            try {
						loadList(listPath);
					} catch (IOException e) {}
		            
		        } 
			}
		});
		scrollPane.setViewportView(list);
	}
	
	
	public RemoteBrowser(Directory dir) {
		setTitle("Browser");
		setBounds(920, 50, 481, 468);
		createGUI();
		selDir = dir;
		try {
			ftp.enterLocalActiveMode();
    	    if (dir.getServer().getUsePassiveMode())
    	    {
    	    	ftp.enterLocalPassiveMode();
       	    } 
			
			ftp.connect(dir.getServer().getHost());
			if (ftp.login(dir.getServer().getLogin(), Utils.decode(dir.getServer().getPassword(), dir.getServer().getHost()))) {
			
			  String path = dir.getRemoteFolder();
			  if ((!path.isEmpty())&&(path != null)){
			    if (dir.getRemoteFolder().charAt(0) == '/'){
			    	path = path.substring(1, dir.getRemoteFolder().length());
			    }
			    listPath = new ArrayList(Arrays.asList(path.split("/")));
			    loadList(listPath);
			  }		  	  	
			}
			else
			  JOptionPane.showMessageDialog(this, "Connection was not estiblished. Please check connection setting");
			 
			
		} catch (IOException e) {};
	}
}


