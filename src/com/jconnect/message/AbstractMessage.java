package com.jconnect.message;

import java.security.InvalidKeyException;
import java.security.Key;
import java.util.UUID;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.jconnect.message.content.AbstractContentMessage;
import com.jconnect.message.content.MessageContentFactory;
import com.jconnect.security.CryptionUtil;

public class AbstractMessage {

	
	private final static String TAG_DATE="date";
	private final static String TAG_GROUP="group_id";
	private final static String TAG_DATA="data";
	
	
	private long date;
	private UUID group;
	private AbstractContentMessage content;
	private String encodedContent;
	
	
	public AbstractMessage(String msg){
		JsonParser parser = new JsonParser();
		JsonObject json = (JsonObject) parser.parse(msg);
		date = json.get(TAG_DATE).getAsLong();
		group = UUID.fromString(json.get(TAG_DATA).getAsString());
		encodedContent = json.get(TAG_DATA).getAsString();
	}
	
	
	public void decrypt(Key key) throws InvalidKeyException {
		String decodedContent = CryptionUtil.decrypt(key, encodedContent);
		content = MessageContentFactory.createMessageContent(decodedContent);
	}
	
	
	
	public String generate(Key key) throws InvalidKeyException {
		date = System.currentTimeMillis();
		
		JsonObject json = new JsonObject();
		json.addProperty(TAG_DATE, date);
		json.addProperty(TAG_GROUP, group.toString());
		String c  = CryptionUtil.encrypt(key, content.toString());
		//TODO encoder
		json.addProperty(TAG_DATA, c);
		return json.toString();
		
	}
	
	


}
