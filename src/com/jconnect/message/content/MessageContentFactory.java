package com.jconnect.message.content;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class MessageContentFactory {

	public static AbstractContentMessage createMessageContent(String content) {
		JsonParser jsonParser = new JsonParser();
		JsonObject json = (JsonObject) jsonParser.parse(content);
		String contentClass = json.get(AbstractContentMessage.TAG_CLASS_NAME).getAsString();
		try {
			AbstractContentMessage contentMessage =  (AbstractContentMessage) MessageContentFactory.class.getClassLoader().loadClass(contentClass).newInstance();
			contentMessage.importFields(json);
			return contentMessage;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		
		return null;
		
	}

}
