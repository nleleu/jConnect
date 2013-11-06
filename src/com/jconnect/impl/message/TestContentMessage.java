package com.jconnect.impl.message;

import com.google.gson.JsonObject;
import com.jconnect.core.message.AbstractContentMessage;

public class TestContentMessage extends AbstractContentMessage{

	private static final String TAG_DATE_PING = "test";
	private String testValue = "testv";
	

	
	@Override
	protected JsonObject exportFields(JsonObject json) {
		json.addProperty(TAG_DATE_PING, testValue);
		return json;
	}



	@Override
	protected void importFields(JsonObject json) {
		if(json.has(TAG_DATE_PING))
			testValue =json.get(TAG_DATE_PING).getAsString();
			
	}



}
