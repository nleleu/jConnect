package com.jconnect.message.content;

import com.google.gson.JsonObject;

public class PingContentMessage extends AbstractContentMessage{

	private static final String TAG_DATE_PING = "date_ping";
	private long datePing = 0;
	

	
	@Override
	protected JsonObject exportFields(JsonObject json) {
		if(datePing!=0)
			json.addProperty(TAG_DATE_PING, datePing);
		return json;
	}



	@Override
	protected void importFields(JsonObject json) {
		if(json.has(TAG_DATE_PING))
			datePing =json.get(TAG_DATE_PING).getAsLong();
			
	}



}
