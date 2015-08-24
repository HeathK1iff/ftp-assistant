package com.ytarzimanov.ftp_assistant.net;

import java.net.InetAddress;
import java.util.Date;
import java.util.regex.Pattern;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

import com.nightfloppy.simplelog.SimpleLog;
import com.ytarzimanov.ftp_assistant.Utils;
import com.ytarzimanov.ftp_assistant.models.core.File;
import com.ytarzimanov.ftp_assistant.models.core.Global;
import com.ytarzimanov.ftp_assistant.models.core.Server;

public class ListUpdater {
	public interface OnEventListener {
		   public void onInit();	
		   public void onNeedDownload(File file);
		   public void onAvailableNewFiles(String updates);
	}
	private static final String CONNECTION_WAS_ESTABLISHED = "Connection was established to %s.";
	private static final String CONNECTION_WAS_NOT_ESTABLISHED = "Connection was not established to %s.";
	private static final String DISCONNECTED_FROM_HOST = "Disconnected from %s.";

	private InetAddress updateHost;
	private Date updateHostStamp;
    private Boolean pause;
	private OnEventListener listener;
	private Thread update;
	private Boolean terminated = false;
    private static ListUpdater instance;
    
    public static ListUpdater getInstance(){
    	if (instance == null){
    		instance = new ListUpdater();
    	}
    	return instance;
    }
	

	public void setListener(OnEventListener listener){
		this.listener = listener;
	}
	
	private void notifyOnInit(){
	    if (listener != null)
		  listener.onInit();;
	}
	
	private void notifyAvailableNewFiles(String str){
		if (listener != null)
		  listener.onAvailableNewFiles(str);
	}
	
	private void notifyNeedDownloadFile(File file){
		if (listener != null)
		  listener.onNeedDownload(file);
	}
	
	public void update() {
		if (update == null){
		  update = new Thread(){
				@Override
				public void run() {
					SimpleLog.getInstance().warn(this.getClass(),"Initialization of refresh thread");
						int i, j, n, idx;		
						StringBuffer listofnew = new StringBuffer();
						FTPClient Client = new FTPClient();
						
						Boolean isConnected = false;
						Boolean needToUpdateList = false;
						Boolean isNewFiles = false;
						Boolean isStatupLoad = true;
						Boolean isDirectoryHasUpdate = false;
						String folderFilter;
						pause = false;
						
						n = 0;
						while (!terminated){
						  if (pause){
							  try {
								sleep(1000000);
							} catch (InterruptedException e) {}
							continue;
						  }
							
						  listofnew.setLength(0);
					      
					      if (n > 100){
					    	  System.gc();
					    	  n = -1;
					      };
					      
					      n++;
				    	  for (int z = 0; z < Global.getInstane().getServerList().size(); z++){
				    		  try {
				    		    Server server = Global.getInstane().getServerList().get(z);
					    	    Client.enterLocalActiveMode();
					    	    if (server.getUsePassiveMode())
					    	    {
					    		  Client.enterLocalPassiveMode();
				           	    } 
					    	  
					    	    isConnected = false;
					    	    
					    	    try {
					    	      
					    	      Client.connect(server.getHost());
					    	      SimpleLog.getInstance().trace(this.getClass(),String.format(CONNECTION_WAS_ESTABLISHED, server.getHost()));
								  isConnected = true;
					    	    } catch (Exception e) {
					    	    	SimpleLog.getInstance().trace(this.getClass(),String.format(CONNECTION_WAS_NOT_ESTABLISHED, server.getHost()));
							    }
					    	  
					    	    if ((isConnected)&&(Client.login(server.getLogin(), Utils.decode(server.getPassword(), server.getHost()))==true)){
					    		  i = 0;
					    		  while (i < server.size()){ 
					    			  if (server.get(i).updateLocalListOfFiles())
					    				needToUpdateList = true;
					    			  folderFilter = "(.+)";
					    			  
					    			  if (!server.get(i).getSourceFolderFilter().equals(""))
					    				  folderFilter = server.get(i).getSourceFolderFilter();
					    			  
					    			  isDirectoryHasUpdate = false;
					    			  FTPFile Files[] = Client.listFiles(server.get(i).getRemoteFolder());
					    			  j = 0;
					    			  while (j < Files.length){
					    				  if (Files[j].isFile() == true){
					    					  if (Pattern.matches(folderFilter, Files[j].getName())){	  
					    						  idx = server.get(i).indexOf(Files[j].getName());
					    						  if (idx < 0){
					    							  File item = server.get(i).append();
					    							  item.setFileSize(Files[j].getSize());
					    							  item.setDateTimeStamp(Files[j].getTimestamp().getTime());
					    							  item.setName(Files[j].getName());
					    							  item.setIsRemoteFile(true);
					    							  
					    							  isDirectoryHasUpdate = (Global.getInstane().getUpdateTimeStamp() == null)||
					    									  (Files[j].getTimestamp().getTime().after(Global.getInstane().getUpdateTimeStamp()));
					    							  
					    							  needToUpdateList = true;
					    							  
					    							  if (isDirectoryHasUpdate){
					    								item.setLock(true);
					    								SimpleLog.getInstance().trace(this.getClass(),server.get(i).getRemoteFolder() + ": Detected a new file: " + Files[j].getName()+" File was blocked.");					    								
					    							  };						    						     
					    						  }	 
					    						  else
					    						  {
					    							  server.get(i).get(idx).setDateTimeStamp(Files[j].getTimestamp().getTime());
					    							  server.get(i).get(idx).setIsRemoteFile(true);
					    							  if ((server.get(i).get(idx).getLocked()==true)&&(server.get(i).get(idx).getFileSize() == Files[j].getSize())){
					    								  server.get(i).get(idx).setLock(false);
					    								  listofnew.append(Files[j].getName()).append('\n');    
					    								  isNewFiles = true;
					    								  needToUpdateList = true;
						    							  if ((server.get(i).getAutoDownload())&&
						    								  (new java.io.File(server.get(i).get(idx).getDownloadFilePath()).exists() == false))						    								  
					    									notifyNeedDownloadFile(server.get(i).get(idx));
					    							  };
					    								  
					    							  server.get(i).get(idx).setFileSize(Files[j].getSize()); 
					    						  }
					    						  
					    					  };
					    				  };
					    				  j++;
					    			  };
					    			  
					    			  if (server.get(i).removeDeletedFiles())
					    			     needToUpdateList = true;

					    			  server.get(i).sort();
					    			  i++;
					    		  };
					    		  
					    		  Client.disconnect();
					    		  SimpleLog.getInstance().trace(this.getClass(),String.format(DISCONNECTED_FROM_HOST, server.getHost()));
					    	    } else {
					    		  i = 0;
					    		  while (i < server.size()){
					    			server.get(i).updateLocalListOfFiles();
				    			  	i++;
					    		  };
					    	  }
					    
				    	    } catch (Exception e) {
				    	    	SimpleLog.getInstance().err(e);
							}
				    	  
				    	  };
				    	  
				    	
					    if (needToUpdateList == true){
						   if (isNewFiles == true){
							 SimpleLog.getInstance().trace(this.getClass(),"Availabled a new updates.");
						     Global.getInstane().doUpdateTimeStamp();
						   }
						   notifyAvailableNewFiles(listofnew.toString());
						   isNewFiles = false;		    
						   needToUpdateList = false;
						};
							
						if (isStatupLoad == true){
							notifyOnInit();
							SimpleLog.getInstance().trace(this.getClass(),"List files were downloaded from ftp server");
							isStatupLoad = false;
						}

						try {							
							sleep(Global.getInstane().getTimerMiliSec());
						} catch (InterruptedException e) {
						}
					}	
				}
		    };
		  update.setDaemon(true);
		  update.setName("FTP-Updater");
		  update.start();
		}
		else{
			update.interrupt();
		}
	}
	
	
	public void stop(){
		terminated = true;
	}
	
	public void pause(){
		pause = true;
	}
	
	public void start(){
		pause = false;
		update();
	}
	
	public InetAddress getUpdateHost(){
		return updateHost;
	}
	
	public Date getDateStampUpdateHost(){
		return updateHostStamp;
	}
	
	public void setUpdateHost(InetAddress adress, Date stamp){
		updateHost = adress;
		updateHostStamp = stamp;
	}
}