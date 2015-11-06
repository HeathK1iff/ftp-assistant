package com.ytarzimanov.ftp_assistant.models.core;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.UUID;
import java.util.prefs.Preferences;
import java.util.regex.Pattern;


public class Directory{
  static final public String ATT_CAPTION = "CAPTION";
  static final public String ATT_FILTER = "FILTER";
  static final public String ATT_DATETIMEFORMAT = "DATETIME_FORMAT";
  static final public String ATT_DISPLAYFILTER = "DISPLAY_FILTER"; 
  static final public String ATT_SOURCE_FOLDER = "SOURCE_FOLDER";
  static final public String ATT_DEFAULT_DOWNLOAD_FOLDER = "DOWNLOAD_FOLDER"; 
  static final public String ATT_SHOW = "SHOW";
  static final public String ATT_EXPAND = "EXPAND";
  static final public String ATT_COPY_TO = "COPY_TO";
  static final public String ATT_VISIBLE_INDEX = "VISIBLE_INDEX";
  static final public String ATT_COPYTO_FORMAT = "COPYTO_FORMAT";
  static final public String ATT_AUTO_DOWNLOAD_NEW_FILES = "AUTO_DOWNLOAD_NEW_FILES";
	
  private String mCaption ="New directory";	
  private String mRemoteFolder = "/";
  private String mDownloadFolder ="";
  private String mSourceFolderFilter ="";
  private String mDisplayFormat = "";
  private String mDisplayDateTimeFormat = "dd.MM.yy HH:mm";
  private String mCopyToClipboardFormat = ""; 
  private int mVisibleCountItems = 20;
  private int mVisibleIndex = -1;
  private Boolean mExpandedGroup = true;
  private Boolean mAutoDownload = false;
  private Boolean mCopyToClipChecked = false;
  private String mUid;
  private ArrayList<File> mFiles = new ArrayList<File>();
  private Server owner;
  private SimpleDateFormat dateFormatter = new SimpleDateFormat(mDisplayDateTimeFormat);
  
  public SimpleDateFormat getDateFormatter(){
	  return dateFormatter;
  }
  
  public String getUid(){
	 if (mUid == null) mUid = UUID.randomUUID().toString();
	 return mUid;
  }
  
  public void setVisibleIndex(int index){
	  mVisibleIndex = index;
  }
  
  public int getVisibleIndex(){
	  return mVisibleIndex;
  }
   
  public boolean updateLocalListOfFiles(){
	  Boolean result = false;
	  int j = 0;
	  int countAvailableFilesByList = 0;
	  
	  while (j < this.size()){
		  
		  if (this.get(j).getIsLocalFile())
			  countAvailableFilesByList++;
		  
		  this.get(j).setIsRemoteFile(false);
		  this.get(j).setIsLocalState(false);
		  j++;
	  }
	  
	  j = 0;
	  java.io.File[] files = new java.io.File(getDownloadFolder()).listFiles();
	  int idx = 0;
	  while (j < files.length){
		  if (files[j].isFile() == true){
			  if (Pattern.matches(getSourceFolderFilter(), files[j].getName())){	  
				  idx = indexOf(files[j].getName());
				  if (idx < 0){
					  File item = this.append();
					  item.setFileSize(files[j].length());
					  item.setDateTimeStamp(new Date(files[j].lastModified()));
					  item.setName(files[j].getName());  
					  
					  item.setIsLocalState(true);
					  result = true;
				  } else {
					  this.get(idx).setIsLocalState(true);
					  countAvailableFilesByList--;
				  }
			  };
		  };
		  j++;
	  };
	  
	  // if < 0 then files was added; otherwise if > 0 then was deleted
	  if (countAvailableFilesByList != 0){
		  result = true;
	  }
	  
      return result;
  }
  
  public boolean removeDeletedFiles(){
	  Boolean result = false;
	  for (int i = 0; i < mFiles.size(); i++){
		 if (!mFiles.get(i).getIsRemoteFile()&&(!mFiles.get(i).getIsLocalFile())) {
			 mFiles.remove(i);
			 result = true;
		 }
	  }
	  return result;
  }
  
  public int size(){
	  return mFiles.size();
  }
  
  public File get(int Index){
	  return mFiles.get(Index);
  }
  
  public void sort(){
	  Collections.sort(mFiles, new Comparator<File>() {
			@Override
			public int compare(File arg0, File arg1) {
			if ((arg0.getDateTimeStamp() != null) && (arg0.getDateTimeStamp() != null)){
			  return arg1.getDateTimeStamp().compareTo(arg0.getDateTimeStamp());
	  		} else {
			  return 0;
	  		}
			}});
  }
  
  
  public int indexOf(String fileName){
	  int i = 0;
	  while (i < mFiles.size()){
		  if (mFiles.get(i).getName().equals(fileName)){
			  return i;
		  }
		  i++;
	  }
	  return -1;
  }
  
  public File append(){
	  File item = new File(this);
	  mFiles.add(item);
	  return item;
  }
  
  
  public Server getServer(){
	  return owner;
  }
  
  public Directory(Server owner, Directory item){
	this.owner = owner;
	setSourceFolder(item.getRemoteFolder());
	setCaption(item.getCaption());
	setSourceFolderFilter(item.getSourceFolderFilter());
	setVisibleCountItems(item.getVisibleCountItems());
	setDisplayDateTimeFormat(item.getDisplayDateTimeFormat());
	setAutoDownload(item.getAutoDownload());
	setCopyToClipboardFormat(item.getCopyToClipboardFormat());
	setDisplayFormat(item.getDisplayFormat());
	setDownloadFolder(item.getDownloadFolder());
	setCopyToClipBtnChecked(item.getCopyToClipBtnChecked());  
	setExpandedGroup(item.getExpandedGroup()); 
	setVisibleIndex(item.getVisibleIndex());
  }
  
  public Directory(Preferences node){
	load(node);
  }
  
  public void load(Preferences node){
	mUid = node.name();
	mRemoteFolder= node.get(ATT_SOURCE_FOLDER,"/");
	mCaption = node.get(ATT_CAPTION, "");
	mSourceFolderFilter = node.get(ATT_FILTER, "");
	mVisibleCountItems = node.getInt(ATT_SHOW, 20);
	mDisplayDateTimeFormat = node.get(ATT_DATETIMEFORMAT, "dd.MM.yy HH:mm");
	mAutoDownload = node.getBoolean(ATT_AUTO_DOWNLOAD_NEW_FILES, false);
	mCopyToClipboardFormat = node.get(ATT_COPYTO_FORMAT, "");
	mDisplayFormat = node.get(ATT_DISPLAYFILTER, "");
	setDownloadFolder(node.get(ATT_DEFAULT_DOWNLOAD_FOLDER, ""));
	mCopyToClipChecked = node.getBoolean(ATT_COPY_TO, false);  
	mExpandedGroup = node.getBoolean(ATT_EXPAND, false); 
	dateFormatter = new SimpleDateFormat(mDisplayDateTimeFormat);
	if (node.getInt(ATT_VISIBLE_INDEX, 0) != 0)
	  mVisibleIndex = node.getInt(ATT_VISIBLE_INDEX, 0);
  }
  
  
  public void save(Preferences node){
	if (node != null){
	  node.put(ATT_SOURCE_FOLDER, mRemoteFolder);
	  node.put(ATT_CAPTION, mCaption);
	  node.put(ATT_FILTER, mSourceFolderFilter);
	  node.putInt(ATT_SHOW, mVisibleCountItems);
	  node.put(ATT_DATETIMEFORMAT, mDisplayDateTimeFormat);
	  node.putBoolean(ATT_AUTO_DOWNLOAD_NEW_FILES, mAutoDownload);
	  node.put(ATT_COPYTO_FORMAT, mCopyToClipboardFormat);
	  node.put(ATT_DISPLAYFILTER, mDisplayFormat);
	  node.put(ATT_DEFAULT_DOWNLOAD_FOLDER, mDownloadFolder);
	  node.putBoolean(ATT_COPY_TO, mCopyToClipChecked);
	  node.putBoolean(ATT_EXPAND, mExpandedGroup);
	  node.putInt(ATT_VISIBLE_INDEX, mVisibleIndex);
	}
  }
  
  public Boolean getAutoDownload() {
	return mAutoDownload;
  }

  public void setAutoDownload(Boolean mAutoDownload) {
	this.mAutoDownload = mAutoDownload;
  }

  public Boolean getCopyToClipBtnChecked() {
	return mCopyToClipChecked;
  }

  public void setCopyToClipBtnChecked(Boolean checked) {
	this.mCopyToClipChecked = checked;
  }

  public Boolean getExpandedGroup() {
	return mExpandedGroup;
  }

  public void setExpandedGroup(Boolean mExpandedGroup) {
	this.mExpandedGroup = mExpandedGroup;
  }

  public int getVisibleCountItems() {
	return mVisibleCountItems;
  }

  public void setVisibleCountItems(int count) {
	this.mVisibleCountItems = count;
  }

  public String getCopyToClipboardFormat() {
    if ((mCopyToClipboardFormat == null)||(mCopyToClipboardFormat.isEmpty())){
	  return getDisplayFormat();
	}
	else
	  return mCopyToClipboardFormat;
  }

  public void setCopyToClipboardFormat(String mCopyToClipboardFormat) {
	this.mCopyToClipboardFormat = mCopyToClipboardFormat;
  }

  public String getDisplayDateTimeFormat() {
	return mDisplayDateTimeFormat;
  }

  public void setDisplayDateTimeFormat(String mDisplayDateTimeFormat) {
	this.mDisplayDateTimeFormat = mDisplayDateTimeFormat;
  }

  public String getDisplayFormat(){
	 return mDisplayFormat;
  }
  
  public void setDisplayFormat(String format){
	  mDisplayFormat = format;
  }
  
  public String getSourceFolderFilter() {
	return mSourceFolderFilter;
  }

  public void setSourceFolderFilter(String sourceFolderFilter) {
	this.mSourceFolderFilter = sourceFolderFilter;
  }

  public String getDownloadFolder() {
	return mDownloadFolder;
  }

  public void setDownloadFolder(String downloadFolder) {
	if (new java.io.File(downloadFolder).exists()){
	  this.mDownloadFolder = downloadFolder;
	} else {
	  this.mDownloadFolder = System.getProperty("user.dir"); 
	}
  }

  public String getRemoteFolder() {
	return mRemoteFolder;
  }

  public void setSourceFolder(String sourceFolder) {
    this.mRemoteFolder = sourceFolder;
  }

  public String getCaption() {
	return mCaption;
  }

  public void setCaption(String caption) {
	this.mCaption = caption;
  }


  public Directory(Server owner){
    this.owner = owner;
  } 

  public String toString(){
	return mCaption;
  }
	
}