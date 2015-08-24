package com.ytarzimanov.ftp_assistant.models.core;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import com.nightfloppy.simplelog.SimpleLog;
import com.nightfloppy.simplelog.SimpleLog.LogLevel;

public class Global {
	private static Global instance;
	private static final Logger log = Logger.getLogger(Global.class.getName());
	static final public String ROOT_PROGRAM = "com.ytazrimanov.ftp_assistant";
	static final public String ATT_UPDATE_EVERY = "UPDATE_EVERY";
	static final public String ATT_LOG = "LOG";
	static final public String ATT_ENABLED_COPYTO_BUTTON = "ENABLED_COPYTO_BUTTON";

	static final private String ATT_LAST_UPDATE = "LAST_UPDATE";

	private ArrayList<Server> mServerConf = null;
	private int mTimerSec = 20;
	private Boolean mAutoRunEnabled = true;
	private Date mUpdateTimeStamp;
	private Boolean mEnableCopyToButtons;
	private String path;

	public static Global getInstane() {
		if (instance == null) {
			instance = new Global();
		}
		return instance;
	}

	public Date getUpdateTimeStamp() {
		return mUpdateTimeStamp;
	}

	public Boolean getEnableCopyToButtons() {
		return mEnableCopyToButtons;
	}

	public void setEnableCopyToButtons(Boolean enabled) {
		mEnableCopyToButtons = enabled;
	}

	public void doUpdateTimeStamp() {
		mUpdateTimeStamp = Calendar.getInstance().getTime();
		Preferences.userRoot().node(ROOT_PROGRAM)
				.putLong(ATT_LAST_UPDATE, mUpdateTimeStamp.getTime());
	}

	public void setUpdateTimeStamp(long date) {
		mUpdateTimeStamp = new Date(date);
	}

	public Global() {
		mServerConf = new ArrayList<Server>();
		load(mServerConf);
	}

	public File getFile(String name) {
		int s = 0, d, f;
		while (s < Global.getInstane().getServerList().size()) {
			d = 0;
			while (d < Global.getInstane().getServerList().get(s).size()) {
				f = Global.getInstane().getServerList().get(s).get(d)
						.indexOf(name);

				if (f >= 0) {
					File file = Global.getInstane().getServerList().get(s)
							.get(d).get(f);
					return file;
				}
				;
				d++;
			}
			;
		}
		;
		return null;
	}

	public File getFile(int hashcode) {
		for (int z = 0; z < Global.getInstane().getServerList().size(); z++) {
			for (int i = 0; i < Global.getInstane().getServerList().get(z)
					.size(); i++) {
				for (int j = 0; j < Global.getInstane().getServerList().get(z)
						.get(i).size(); j++) {
					if (hashcode == Global.getInstane().getServerList().get(z)
							.get(i).get(j).hashCode()) {
						return Global.getInstane().getServerList().get(z)
								.get(i).get(j);
					}
				}
			}
		}
		return null;
	}

	public ArrayList<Server> getServerList() {
		if (mServerConf == null) {
			mServerConf = new ArrayList<Server>();
			try {
				if (hasPreferences() == false) {
					save();
				} else
					loadFromPreferences(mServerConf);

			} catch (IOException e) {
				log.log(Level.WARNING, "Problem of loading preferences.", e);
			}
		}

		return mServerConf;
	}

	public int getTimerSec() {
		return mTimerSec;
	}

	public int getTimerMiliSec() {
		return mTimerSec * 1000;
	}

	public void setTimerSec(int sec) {
		this.mTimerSec = sec;
	}

	public void save(ArrayList<Server> settings) {
		try {
			saveToPreferences(settings, this);
		} catch (IOException | BackingStoreException e) {
			SimpleLog.getInstance().err(e);
		}
	}

	public void save() {
		save(mServerConf);
	}

	public void load(ArrayList<Server> settings) {
		try {
			if (hasPreferences() == false) {
				save();
			} else
				loadFromPreferences(settings);

		} catch (IOException e) {
			SimpleLog.getInstance().err(e);
		}
	}

	private boolean hasPreferences() {
		try {
			return Preferences.userRoot().nodeExists(ROOT_PROGRAM);
		} catch (BackingStoreException e) {
			return false;
		}
	}

	public Boolean getAutoRunEnabled(){
		return mAutoRunEnabled;
	}

	private void loadFromPreferences(ArrayList<Server> servers)
			throws IOException {
		try {

			if (Preferences.userRoot().nodeExists(ROOT_PROGRAM) == true) {
				Preferences userPrefs = Preferences.userRoot().node(ROOT_PROGRAM);
				mTimerSec = userPrefs.getInt(ATT_UPDATE_EVERY, 60);
				if (mTimerSec < 60) {
					mTimerSec = 60;
				}
				
				SimpleLog.getInstance().setLevel(LogLevel.values()[userPrefs.getInt(ATT_LOG, LogLevel.lvError.ordinal())]);
				
				path = Global.class.getProtectionDomain().getCodeSource()
						.getLocation().getPath();
				path = new java.io.File(new String(path.substring(1, path.length()))).getAbsolutePath();

				SimpleLog.getInstance().warn(Global.class,
						"Path jar of file " + path);

				mEnableCopyToButtons = userPrefs.getBoolean(
						ATT_ENABLED_COPYTO_BUTTON, false);
				setUpdateTimeStamp(userPrefs.getLong(ATT_LAST_UPDATE, 0));
				for (int iServer = 0; iServer < userPrefs.childrenNames().length; iServer++) {
					Preferences serverPrefs = userPrefs.node(userPrefs
							.childrenNames()[iServer]);
					Server server = new Server(serverPrefs);
					for (int iDir = 0; iDir < serverPrefs.childrenNames().length; iDir++) {
						Preferences dirPrefs = serverPrefs.node(serverPrefs
								.childrenNames()[iDir]);
						Directory dir = server.append();
						dir.load(dirPrefs);
						if (dir.getVisibleIndex() == 0) {
							dir.setVisibleIndex(iDir);
						}
					}
					server.sort();
					for (int iDir = 0; iDir < serverPrefs.childrenNames().length; iDir++) {
						server.get(iDir).setVisibleIndex(iDir + 1);
					}
					servers.add(server);
				}
			}
		} catch (BackingStoreException e) {
			SimpleLog.getInstance().err(e);
		}
	}

	public void saveToPreferences(ArrayList<Server> servers, Global config)
			throws IOException, BackingStoreException {
		Preferences userPrefs = Preferences.userRoot().node(ROOT_PROGRAM);
		userPrefs.putInt(ATT_UPDATE_EVERY, config.getTimerSec());
		userPrefs.putBoolean(ATT_ENABLED_COPYTO_BUTTON,
				config.mEnableCopyToButtons);
		if (config.getUpdateTimeStamp() != null)
			userPrefs.putLong(ATT_LAST_UPDATE, config.getUpdateTimeStamp()
					.getTime());
		for (int iServer = 0; iServer < servers.size(); iServer++) {
			Server server = servers.get(iServer);
			Preferences userPrefsServer = userPrefs.node(server.getUid());
			server.save(userPrefsServer);
			for (int iDir = 0; iDir < server.size(); iDir++) {
				server.get(iDir).save(
						userPrefsServer.node(server.get(iDir).getUid()));
			}
		}
	}

	public String toString() {
		return "Servers";
	}
}