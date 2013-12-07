package com.jconnect.core.message;

import java.security.InvalidKeyException;
import java.security.Key;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.jconnect.core.peergroup.AbstractPeerGroup;
import com.jconnect.core.security.CryptionUtil;
import com.jconnect.util.uuid.ConversationID;
import com.jconnect.util.uuid.PeerGroupID;
import com.jconnect.util.uuid.PeerID;

/**
 * Message used for network operations
 *
 */
public class Message {

	//TODO merger content message et message
	//TODO ajouter champ receiver
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
	private ConversationID conversationID;

	public static Message parse(String msg){
		return new Message(msg);
	}
	
	public static Message getEmptyMessage(PeerID peerid){
		return new Message(peerid);
		
	}
	
	public Message(AbstractPeerGroup group, AbstractContentMessage content){
		this(group, content, ConversationID.generate());
		
	}
	
	public Message(AbstractPeerGroup group, AbstractContentMessage content, ConversationID cID){
		this.conversationID =cID;
		this.group = group.getuUID();
		this.peer = group.getPeerID();
		this.content = content;
		
	}
	
	private Message(PeerID peerid) {
		this.conversationID = ConversationID.generate();
		group = PeerGroupID.NULL;
		peer = peerid;
	}
	
	private Message(String msg) throws JsonSyntaxException{
		JsonParser parser = new JsonParser();
		JsonObject json = (JsonObject) parser.parse(msg);
		conversationID = new ConversationID(json.get(TAG_ID).getAsString());
		date = json.get(TAG_DATE).getAsLong();
		group = new PeerGroupID(json.get(TAG_GROUP).getAsString());
		peer = new PeerID(json.get(TAG_PEER_ID).getAsString());
		encodedContent = json.get(TAG_DATA).getAsString();
	}

	public void decrypt(Key key) throws InvalidKeyException {
		String decodedContent = encodedContent;
		if(key!=null){
			decodedContent = CryptionUtil.decrypt(key, encodedContent);
		}
		 
		content = MessageContentFactory.createMessageContent(decodedContent);
	}

	public String toString(){
		date = System.currentTimeMillis();

		JsonObject json = new JsonObject();
		json.addProperty(TAG_ID, conversationID.toString());
		json.addProperty(TAG_DATE, date);
		json.addProperty(TAG_GROUP, group.toString());
		json.addProperty(TAG_PEER_ID, peer.toString());
		String c ="";
		if(encodedContent!=null){
			c = encodedContent;
		}else if(content!=null){
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

	public ConversationID getID() {
		return conversationID;
	}

	public long getDate() {
		return date;
	}
	
	public AbstractContentMessage getContent() {
		return content;
	}

}
