package com.ytarzimanov.ftp_assistant.models.core;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.UUID;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;


public class Server {
    static final public String ATT_USERDATA1 = "USERDATA1";
    static final public String ATT_USEPASSIVEMODE = "USEPASSIVEMODE";
    static final public String ATT_USERDATA2 = "USERDATA2";
    static final public String ATT_HOST = "HOST";
    private String mHost = "New host";
    private String mLogin = "";
    private String mPassword = "";
    private Boolean mUsePassiveMode = true;
    private String mUid;
    private ArrayList<Directory> mDirs = new ArrayList<Directory>();
      
    public Directory get(int index){
		return mDirs.get(index);
    }
      
    public void sort(){
  	  Collections.sort(mDirs, new Comparator<Directory>() {
  		@Override
  		public int compare(Directory arg0, Directory arg1) {
  		  if (arg1.getVisibleIndex() > arg0.getVisibleIndex())
  		  { return -1; } else
  		  { if (arg1.getVisibleIndex() < arg0.getVisibleIndex())
  			return 1;  
  		  }
  		  return 0;
  		}});
    }
    
    public void move(Directory item, int Delta){
    	int i = mDirs.indexOf(item);
    	Directory item1 = mDirs.get(i);
    	int new_i = i + Delta;
    	
    	if ((new_i < 0)||(new_i >= mDirs.size())){
    		return;
    	}
    	
    	Directory item2 = mDirs.get(new_i);
    	
    	int vi = item1.getVisibleIndex();
    	item1.setVisibleIndex(item2.getVisibleIndex());
    	item2.setVisibleIndex(vi);
  	   	
    	mDirs.set(new_i, item1);
    	mDirs.set(i, item2);

    }
    
    
    public void remove(){
     	Preferences prefUser = Preferences.userRoot().node(Global.ROOT_PROGRAM);
    	try {
			prefUser.node(getUid()).removeNode();
    	} catch (BackingStoreException e) {
		}	
    }
    
    public Directory clone(Directory item){
    	Directory item1 = new Directory(this, item);
    	item1.setVisibleIndex(mDirs.size());
		mDirs.add(item1);
    	return item1;
    }
    
    public void remove(Directory item){
    	Preferences prefUser = Preferences.userRoot().node(Global.ROOT_PROGRAM);
    	try {
			prefUser.node(getUid()).node(item.getUid()).removeNode();
    	} catch (BackingStoreException e) {
		}
    	mDirs.remove(item);
    }
    
    public Directory append(){
		Directory item = new Directory(this);
		item.setVisibleIndex(mDirs.size());
		mDirs.add(item);
    	return item;
    }
    
    public int size(){
		return mDirs.size();	
    }
        
    public Server(){
    }   
    
    public Server(Preferences node){
    	mUid = node.name();
    	mHost =  node.get(ATT_HOST, "");
		mLogin = node.get(ATT_USERDATA1, "");
		mPassword = node.get(ATT_USERDATA2, "");
		mUsePassiveMode = node.getBoolean(ATT_USEPASSIVEMODE, false);
    }
    
    public void save(Preferences node){
    	node.put(ATT_HOST, mHost);
		node.put(ATT_USERDATA1, mLogin);
		node.put(ATT_USERDATA2, mPassword);
		node.putBoolean(ATT_USEPASSIVEMODE, mUsePassiveMode);
    }
    
    
    public String getUid(){
   	  if (mUid == null) mUid = UUID.randomUUID().toString();
   	  return mUid;
    }
    
    public void setUsePassiveMode(Boolean active){
	    mUsePassiveMode = active; 
    }
  
    public Boolean getUsePassiveMode(){
       return mUsePassiveMode; 
    }
   
    public String getHost(){
	   return mHost;
    }
    
    public void setHost(String host){
	   this.mHost = host;
    }
  
    public String getLogin() {
		return mLogin;
	}

	public void setLogin(String login) {
		this.mLogin = login;
	}

	public String getPassword() {
		return mPassword;
	}

	public void setPassword(String mPassword) {
		this.mPassword = mPassword;
	}

	public String toString(){
	    return mHost;
	}
}