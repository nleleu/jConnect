package com.jconnect.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.jconnect.JConnect;
import com.jconnect.util.uuid.PeerGroupID;
import com.jconnect.util.uuid.PeerID;

public class PreferencesStore {

	
	private final static String DefaultPath = "jconnect";
	private final static String DefaultFileName = "conf.ini";
	
	private Preferences prefs;

	private final static String TAG_UDP = "TAG_UDP";
	private final static String TAG_TCP = "TAG_TCP";
	private final static String TAG_MULTICAST = "TAG_MULTICAST";
	private final static String TAG_TCP_PORT = "TAG_TCP_PORT";
	private final static String TAG_UDP_PORT = "TAG_UDP_PORT";
	private final static String TAG_MULTICAST_PORT = "TAG_MULTICAST_PORT";
	private final static String TAG_SEND_ATTEMPT = "TAG_SEND_ATTEMPT";
	private final static String TAG_PEERGROUPS = "TAG_PEERGROUPS";
	private final static String TAG_PEERID = "TAG_PEERID";

	public PreferencesStore(String prefPath) {
		if (prefPath == null)
			prefPath = DefaultPath;
		
		File f = new File(prefPath+File.separator+DefaultFileName);
		if(!f.exists()){
			try {
				f.getParentFile().mkdirs();
				f.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		System.setProperty("java.util.prefs.PreferencesFactory", FilePreferencesFactory.class.getName());
		
		
	    System.setProperty(FilePreferencesFactory.SYSTEM_PROPERTY_FILE, prefPath+File.separator+DefaultFileName);
	 
	    prefs = Preferences.userNodeForPackage(JConnect.class);
		
	}
	
	private void save() {
		try {
			prefs.sync();
		} catch (BackingStoreException e) {
			e.printStackTrace();
		}
	}
	

	public boolean isTCP() {
		return prefs.getBoolean(TAG_TCP, true);
	}

	public boolean isUDP() {
		return prefs.getBoolean(TAG_UDP, true);
	}

	public boolean isMulticast() {
		return prefs.getBoolean(TAG_MULTICAST, true);

	}

	public int getTCPPort() {
		return prefs.getInt(TAG_TCP_PORT, 45201);
	}
	public void setTCPPort(int port) {
		prefs.putInt(TAG_TCP_PORT, port);
		save();
	}
	

	public int getUDPPort() {
		return prefs.getInt(TAG_UDP_PORT, 45202);
	}
	public void setUDPPort(int port) {
		prefs.putInt(TAG_UDP_PORT, port);
		save();
	}
	

	public int getMulticastPort() {
		return prefs.getInt(TAG_MULTICAST_PORT, 45203);
	}
	public void setMulticastPort(int port) {
		prefs.putInt(TAG_MULTICAST_PORT, port);
		save();
	}
	
	public int getTCPSendAttempt() {
		return prefs.getInt(TAG_SEND_ATTEMPT, 3);
	}

	public PeerID getPeerID() {
		String id = prefs.get(TAG_PEERID, null);
		PeerID peerId;
		if (id == null) {
			peerId = PeerID.generate();
			prefs.put(TAG_PEERID, peerId.toString());
			save();
		} else {
			peerId = new PeerID(id);
		}
		return peerId;
	}

	public List<PeerGroupID> getPeerGroups() {
		List<PeerGroupID> peerGroups = new ArrayList<PeerGroupID>();
		String pg = prefs.get(TAG_PEERGROUPS, "");
		JsonParser jsonParser = new JsonParser();
		JsonArray json = (JsonArray) jsonParser.parse(pg);
		for (int i = 0; i < json.size(); i++) {
			peerGroups.add(new PeerGroupID(((JsonObject) json.get(i)).get("id")
					.getAsString()));
		}
		return peerGroups;

	}

	public void savePeerGroups(List<PeerGroupID> peerGroups) {
		JsonArray json = new JsonArray();

		for (int i = 0; i < peerGroups.size(); i++) {
			JsonObject obj = new JsonObject();
			obj.addProperty("id", peerGroups.get(i).toString());
			json.add(obj);
		}
		prefs.put(TAG_PEERGROUPS, json.toString());
		save();

	}

	

}
