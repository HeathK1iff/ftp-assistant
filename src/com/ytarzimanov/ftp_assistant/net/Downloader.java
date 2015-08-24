package com.ytarzimanov.ftp_assistant.net;

import java.io.BufferedInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import com.nightfloppy.simplelog.SimpleLog;
import com.ytarzimanov.ftp_assistant.models.core.File;

public class Downloader extends Thread{
	static class DownloaderHolder extends Thread{
		
		private ArrayList<Downloader> group;
		private Boolean pause = false;
		private Boolean stopped = false;
		
		public DownloaderHolder(){
			group = new ArrayList<Downloader>();
			setName("DownloaderHolder");
		}
		
		public void add(Downloader item){
			pause = true;
			try {
			  group.add(item);
			} finally {
			  pause = false;
			}
		}
		
		
		public void remove(Downloader item){
			pause = true;
			try {
			  group.remove(item);
			} finally {
			  pause = false;
			}
		}
		
		public Downloader getDownloader(RemoteSource source){
			int i = 0;
			Downloader found = null;
		
			while (i < group.size()) {
			  if (group.get(i).source.equals(source)){
				found = group.get(i);
				break;
			  }
			 i++;
			};
			return found;
		}
		
		public void terminate(){
			stopped = true;
			interrupt();
		}
		
		@Override
	    public void run(){
			while (!stopped){
				try {
				  sleep(10000);
				} catch (InterruptedException e) {
				}
				
				if (pause)
			      continue;
				
				int i = group.size() - 1;
				while (i >= 0){
					if ((group.get(i).isFinished())||(group.get(i).isTerminated))
					  group.remove(i);
					i--;
				}
			}
		}
	}
	
	
	public interface RemoteSource{
		  public String getRemoteHost();
		  public String getRemoteLogin();
		  public String getRemotePassword();
		  public String getRemoteFilePath();
		  public String getRemoteFileName();
		  public long getRemoteFileSize();
		  
		  public void doPostDownloadingEvents();
	}
	
	private interface DownloadFunc{
		public void download(RemoteSource source, OutputStream output);
	}
	
	
	public interface OnEventListener {
		public void onUpdate(int progress);
		public void onFinished(RemoteSource source);
		public void onTerminated();
	}  

	protected int progress = -1;
	public static final String CMD_INIT_FORMAT = "%s;%s";
	protected OnEventListener listener;
	private Boolean isFinished = false;
	protected Boolean isTerminated = false;
	private static DownloaderHolder pool;
	protected String targetFolder;
	protected RemoteSource source;
	protected OutputStream output;
	private DownloadFunc func;
	
	public static Boolean isDownloading(final RemoteSource source){
		Boolean result = false;
		Downloader item = getDownloader(source);
		if (item != null){
			result = true;
		}
		return result;
	}
	
	
	public void setListiner(OnEventListener listener){
		this.listener = listener;
	}
	
	public static DownloaderHolder getHolder(){
		if (pool == null){
		  pool = new DownloaderHolder();
		  pool.start();
		}
		return pool;
	}
	
	public static Downloader getDownloader(RemoteSource source){
		return getHolder().getDownloader(source);
	}
		

	public static void terminate(RemoteSource source){
		Downloader item = getDownloader(source);
		if (item != null){
			getHolder().remove(item);
			item.terminate();
			item = null;
			SimpleLog.getInstance().trace(Downloader.class, "Downloading file has been terminated for " + source.getRemoteFileName());
		}
	}
	  
	 public static void download(final File source, OnEventListener listener, String target){
			Downloader item = new Downloader(listener, source, target);
			getHolder().add(item);
			item.download();
			item = null;
	 }
	 
	 
	 public void doUpdateProgress(int progress){
		 if (listener != null){
			 listener.onUpdate(progress);
		 }
	 }
	 
	 public void doFinished(RemoteSource source){
		 if (listener != null){
			 listener.onFinished(source);
		 }
	 }
	 
	 public Downloader(final OnEventListener listener, File source, String targetFolder){
		  
		  this.source = source;
		  setListiner(listener);
		  this.source = source;
		  this.targetFolder = targetFolder;
		  this.func = new DownloadFunc(){
				@Override
				public void download(RemoteSource source, OutputStream output) {
					  
					  try { 
							  URL url = new URL("ftp://"+source.getRemoteLogin()+":"+source.getRemotePassword() +
										  "@"+source.getRemoteHost()+source.getRemoteFilePath() + '/' + source.getRemoteFileName()+";type=i");

							  URLConnection con = url.openConnection();
							  long fileSize = source.getRemoteFileSize();
							  BufferedInputStream input = new BufferedInputStream(con.getInputStream());
							
						      byte[] buffer = new byte[1024];
						      int fileSizeKb = (int) (((fileSize / 1024) / 1024));		   
							  int ProcentDelimiter = 10;
							  
							  if (fileSizeKb > 20)
							    ProcentDelimiter = 2; 
							 
							  long downloadSize = 0;
							  progress = 0;
							  doUpdateProgress(progress);
							  int bufSize;
							  while ((bufSize = input.read(buffer)) >= 0) {
								if (isTerminated){
									break;
								}
								downloadSize += bufSize;
								output.write(buffer, 0, bufSize);
								
								output.flush();
								
								progress = (int) ((100 * downloadSize) / fileSize);
								if ((progress % ProcentDelimiter) == 1){
									doUpdateProgress(progress);
								}
							  }
							  
							  input.close(); 
							} catch (IOException e) {
								SimpleLog.getInstance().err(e);
							}
					  };
				};
	  } 
	 
	  public void terminate(){
		  isTerminated = true;
	  }
	 
	  	  
	  @Override
      public void run(){

		Boolean isOutputExist = (output != null);
		if (!isOutputExist){
		  try {
			output = new FileOutputStream(targetFolder);
		  } catch (FileNotFoundException e1) {
			 SimpleLog.getInstance().err(e1);
		  }
		}
		
		
		try {
		  isTerminated = false;	
		  isFinished = false;
		  
		  func.download(source, output);
		 
		} catch (Exception e) {
			SimpleLog.getInstance().err(e);
			isTerminated = true;
		}
	   
	  doUpdateProgress(100);

	   isFinished = true;
	   if (!isOutputExist){
	   try {
		  output.flush();
		  output.close();
		  output = null;
	  } catch (IOException e) {
		  SimpleLog.getInstance().err(e);
	  }
	  }
      
      
	    if (!isTerminated){
		  source.doPostDownloadingEvents();
		  doFinished(source);
	    }else{
	      new java.io.File(targetFolder).delete();
	      if (listener != null)
	   	    listener.onTerminated();
	    }
	}	
			
      public Boolean isFinished(){
    	  return isFinished;
      }
	  
	  public void download(){
		  this.start();
	  }
	  
	  public int getProgress(){
		  return progress;
	  }	  
}
