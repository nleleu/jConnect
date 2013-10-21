package com.jconnect.util;

import java.util.ArrayList;
import java.util.List;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.jconnect.util.uuid.PeerGroupID;

public class PreferencesStore {

	private Preferences prefs;

	private final static String TAG_UDP = "TAG_UDP";
	private final static String TAG_TCP = "TAG_TCP";
	private final static String TAG_MULTICAST = "TAG_MULTICAST";
	private final static String TAG_TCP_PORT = "TAG_TCP_PORT";
	private final static String TAG_UDP_PORT = "TAG_UDP_PORT";
	private final static String TAG_MULTICAST_PORT = "TAG_MULTICAST_PORT";
	private final static String TAG_SEND_ATTEMPT = "TAG_SEND_ATTEMPT";
	private final static String TAG_PEERGROUPS = "TAG_PEERGROUPS";

	public PreferencesStore(String prefPath) {
		if (prefPath == null)
			prefPath = this.getClass().getName();
		prefs = Preferences.userRoot().node(prefPath);
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
		return prefs.getInt(TAG_TCP_PORT, 3009);
	}

	public int getUDPPort() {
		return prefs.getInt(TAG_UDP_PORT, 3009);
	}

	public int getMulticastPort() {
		return prefs.getInt(TAG_MULTICAST_PORT, 3010);
	}

	public int getTCPSendAttempt() {
		return prefs.getInt(TAG_SEND_ATTEMPT, 3);
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
		try {
			prefs.sync();
		} catch (BackingStoreException e) {
			e.printStackTrace();
		}

	}
}
