package com.jconnect.message.content;

import com.google.gson.JsonObject;

public abstract class AbstractContentMessage {
	
	public AbstractContentMessage() {
	}
	
	
	public final static String TAG_CLASS_NAME = "class_name";
	@Override
	public String toString() {
		JsonObject json = new JsonObject();
		json.addProperty(TAG_CLASS_NAME, getClass().getName());
		json = exportFields(json);		
		return json.toString();
	}

	protected abstract JsonObject exportFields(JsonObject json);
	protected abstract void importFields(JsonObject json);
}
