package com.jconnect.util;

import java.util.prefs.Preferences;

public class PreferencesStore {

	private Preferences prefs;


	public PreferencesStore(String prefPath) {
		if (prefPath == null)
			prefPath = this.getClass().getName();
		prefs = Preferences.userRoot().node(prefPath);
	}

}
