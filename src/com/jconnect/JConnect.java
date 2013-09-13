package com.jconnect;

import com.jconnect.core.Gate;
import com.jconnect.peergroup.PeerGroupManager;
import com.jconnect.util.PreferencesStore;

public class JConnect {

	private PreferencesStore prefs;
	private Gate gate;

	private PeerGroupManager peerGroupManager;

	public JConnect() {
		this(null);
	}

	public JConnect(String prefPath) {
		prefs = new PreferencesStore(prefPath);
		gate = new Gate();
		peerGroupManager = new PeerGroupManager();
		
	}

}
