package com.ytarzimanov.ftp_assistant.models.core;

import com.ytarzimanov.ftp_assistant.Utils;
import com.ytarzimanov.ftp_assistant.net.Downloader.RemoteSource;

public class File implements RemoteSource{
  private static final String D_TAG = "@d";
  private Directory owner;
  private String mName = "";
  private java.util.Date mDateTimeStamp;
  private long mSize = 0;
  private Boolean mLock = false;
  private Boolean mIsLocalFile = false;
  private Boolean mIsRemoteFile = false;
  
  public File(Directory dir){
	  owner = dir;
  }
  
  public void setIsRemoteFile(Boolean isRemoteFile){
	  mIsRemoteFile = isRemoteFile;
  }
  
  public Boolean getIsRemoteFile(){
	  return mIsRemoteFile;
  }
  
  public Boolean getIsLocalFile(){
	  return mIsLocalFile;
  }
  
  public void setIsLocalState(Boolean state){
	  mIsLocalFile = state;
  }
 
  public void setLock(Boolean blocked){
	  this.mLock = blocked;
  }
  
  public Boolean getLocked(){
	  return mLock == true;
  }
 
  public Directory getDirectory(){
	  return owner;
  }

  
  public String getDownloadFilePath(){
	  return owner.getDownloadFolder() + java.io.File.separatorChar + mName; 
  }
  
  public String getCopyToFormattedName(){
	  return Utils.convertByPattern(owner.getSourceFolderFilter(), owner.getCopyToClipboardFormat(), mName);
  }
  
  public String getName(){
      return mName;
  }
  
  public void setName(String name){
	  mName = name;
  }
  
  public java.util.Date getDateTimeStamp(){
    return mDateTimeStamp;
  }
  
  public void setDateTimeStamp(java.util.Date date){
	  mDateTimeStamp = date;
  }
  
  public long getFileSize(){
    return mSize;
  }
  
  public void setFileSize(long size){
	  this.mSize = size;
  }
  
  public String toString(){
	  return new String(Utils.convertByPattern(getDirectory().getSourceFolderFilter(), 
			  getDirectory().getDisplayFormat(), 
			  getName()).replaceAll(D_TAG, getDirectory().getDateFormatter().format(getDateTimeStamp())));
  }

@Override
public String getRemoteHost() {
	return owner.getServer().getHost();
}

@Override
public String getRemoteLogin() {
	return owner.getServer().getLogin();
}

@Override
public String getRemotePassword() {
	return Utils.decode(owner.getServer().getPassword(), owner.getServer().getHost());
}

@Override
public String getRemoteFileName() {
	return mName;
}

@Override
public String getRemoteFilePath() {
	return owner.getRemoteFolder();
}

@Override
public void doPostDownloadingEvents() {
	setIsLocalState(true);
}

@Override
public long getRemoteFileSize() {
	return mSize;
}

  
}

