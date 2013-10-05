package com.jconnect.core.message;

import java.security.InvalidKeyException;
import java.security.Key;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.jconnect.core.security.CryptionUtil;
import com.jconnect.util.uuid.PeerGroupID;

/**
 * Message used for network operations
 *
 */
public class Message {

	private final static String TAG_DATE = "date";
	private final static String TAG_GROUP = "group_id";
	private final static String TAG_DATA = "data";

	private long date;
	private PeerGroupID group;
	private AbstractContentMessage content;
	private String encodedContent;

	public Message(String msg) {
		JsonParser parser = new JsonParser();
		JsonObject json = (JsonObject) parser.parse(msg);
		date = json.get(TAG_DATE).getAsLong();
		group = new PeerGroupID(json.get(TAG_DATA).getAsString());
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
		String c = content.toString();
		if(key!=null)
			c = CryptionUtil.encrypt(key, content.toString());
		json.addProperty(TAG_DATA, c);
		return json.toString();

	}

	public PeerGroupID getGroup() {
		return group;
	}

}
