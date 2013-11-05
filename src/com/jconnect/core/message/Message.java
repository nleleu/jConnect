package com.jconnect.core.message;

import java.security.InvalidKeyException;
import java.security.Key;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.jconnect.core.security.CryptionUtil;
import com.jconnect.util.uuid.MessageID;
import com.jconnect.util.uuid.PeerGroupID;
import com.jconnect.util.uuid.PeerID;

/**
 * Message used for network operations
 *
 */
public class Message {

	
	private final static String TAG_ID = "id";
	private final static String TAG_DATE = "date";
	private final static String TAG_GROUP = "group_id";
	private final static String TAG_PEER_ID = "peer_id";
	private final static String TAG_DATA = "data";

	private long date;
	private PeerGroupID group;
	private AbstractContentMessage content;
	private String encodedContent;
	private PeerID peer;
	private MessageID messageID;

	public static Message parse(String msg){
		return new Message(msg);
	}
	
	private Message(String msg) {
		
		JsonParser parser = new JsonParser();
		JsonObject json = (JsonObject) parser.parse(msg);
		messageID = new MessageID(json.get(TAG_ID).getAsString());
		date = json.get(TAG_DATE).getAsLong();
		group = new PeerGroupID(json.get(TAG_GROUP).getAsString());
		peer = new PeerID(json.get(TAG_PEER_ID).getAsString());
		encodedContent = json.get(TAG_DATA).getAsString();
	}

	public void decrypt(Key key) throws InvalidKeyException {
		String decodedContent = CryptionUtil.decrypt(key, encodedContent);
		content = MessageContentFactory.createMessageContent(decodedContent);
	}

	public String toString(){
		date = System.currentTimeMillis();

		JsonObject json = new JsonObject();
		json.addProperty(TAG_ID, messageID.toString());
		json.addProperty(TAG_DATE, date);
		json.addProperty(TAG_GROUP, group.toString());
		json.addProperty(TAG_PEER_ID, peer.toString());
		String c = content.toString();
		if(encodedContent!=null){
			c = encodedContent;
		}else{
			c = content.toString();
		}
		json.addProperty(TAG_DATA, c);
		return json.toString();
		
	}
	public void encode(Key key)  {
		try {
			encodedContent = CryptionUtil.encrypt(key, content.toString());
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		}
	}

	public PeerGroupID getGroup() {
		return group;
	}
	public PeerID getPeer() {
		return peer;
	}

	public MessageID getID() {
		return messageID;
	}

}
