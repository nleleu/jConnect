package com.jconnect;

import com.jconnect.core.Gate;
import com.jconnect.core.peergroup.PeerGroupManager;
import com.jconnect.util.PreferencesStore;
import com.jconnect.util.uuid.PeerID;

public class JConnect {

	private PreferencesStore prefs;
	private Gate gate;

	private PeerGroupManager peerGroupManager;
	private final PeerID peerID;

	public JConnect() {
		this(null);
	}
	

	public PeerGroupManager getPeerGroupManager() {
		return peerGroupManager;
	}
	
	public JConnect(String prefPath) {
		prefs = new PreferencesStore(prefPath);
		peerID = prefs.getPeerID();
		gate = new Gate(this);
		peerGroupManager = new PeerGroupManager(this);
		
	}
	
	public PreferencesStore getPrefs() {
		return prefs;
	};
	
	public Gate getGate() {
		return gate;
	}


	public PeerID getPeerID() {
		return peerID;
	}

}
