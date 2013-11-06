package com.jconnect.util.uuid;

import java.util.UUID;

public class ConversationID extends AbstractUUID {
	
private static final String prefix="messageID";
	

	@Override
	public String getPrefix() {
		return prefix;
	}

	public static ConversationID generate(){
		
		return new ConversationID(AbstractUUID.generateUUID());
	}

	public ConversationID(UUID uuid)
	{
		super(uuid);
	}
	
	
	public ConversationID(String uuid)
	{
		super(uuid);
		if(!getPrefix().equals(prefix))
			throw new IllegalArgumentException("Invalid prefix : "+getPrefix()+" instead of "+prefix);
		
	}

	

}
