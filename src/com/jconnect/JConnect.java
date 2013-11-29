package com.jconnect;

import java.lang.reflect.InvocationTargetException;

import com.jconnect.core.Gate;
import com.jconnect.core.IGate;
import com.jconnect.core.peergroup.AbstractPeerGroup;
import com.jconnect.core.peergroup.PeerGroupManager;
import com.jconnect.util.PreferencesStore;
import com.jconnect.util.uuid.PeerGroupID;

public class JConnect {

	private PreferencesStore prefs;
	private Gate gate;

	private PeerGroupManager peerGroupManager;

	public JConnect() {
		this(null);
	}
	

	public PeerGroupManager getPeerGroupManager() {
		return peerGroupManager;
	}
	
	public JConnect(String prefPath) {
		prefs = new PreferencesStore(prefPath);
		gate = new Gate(this);
		peerGroupManager = new PeerGroupManager(this);
		
		
		
	}
	
	public PreferencesStore getPrefs() {
		return prefs;
	}

//TODO remove
	public Gate getGate() {
		return gate;
	}

}
