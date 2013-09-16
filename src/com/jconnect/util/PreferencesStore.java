package com.jconnect.util;

import java.net.SocketAddress;
import java.net.SocketImpl;
import java.util.prefs.Preferences;

public class PreferencesStore {

	private Preferences prefs;

	private final static String TAG_UDP = "TAG_UDP";
	private final static String TAG_TCP = "TAG_TCP";
	private final static String TAG_MULTICAST = "TAG_MULTICAST";
	private final static String TAG_TCP_PORT = "TAG_TCP_PORT";
	private final static String TAG_UDP_PORT = "TAG_UDP_PORT";
	private final static String TAG_MULTICAST_PORT = "TAG_MULTICAST_PORT";

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

	

}
