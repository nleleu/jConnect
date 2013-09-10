package com.jconnect;

import com.jconnect.core.InputGate;
import com.jconnect.core.OutputGate;
import com.jconnect.peergroup.PeerGroupManager;
import com.jconnect.util.PreferencesStore;

public class JConnect {

	private PreferencesStore prefs;
	private InputGate inputGate;
	private OutputGate outputGate;
	private PeerGroupManager peerGroupManager;

	public JConnect() {
		this(null);
	}

	public JConnect(String prefPath) {
		prefs = new PreferencesStore(prefPath);
		inputGate = new InputGate();
		outputGate = new OutputGate();
		peerGroupManager = new PeerGroupManager();
		
	}

}
