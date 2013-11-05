package com.jconnect.util.uuid;

import java.util.UUID;

public class MessageID extends AbstractUUID {
	
private static final String prefix="messageID";
	

	@Override
	public String getPrefix() {
		return prefix;
	}

	public static MessageID generate(){
		
		return new MessageID(AbstractUUID.generateUUID());
	}

	public MessageID(UUID uuid)
	{
		super(uuid);
	}
	
	
	public MessageID(String uuid)
	{
		super(uuid);
		if(!getPrefix().equals(prefix))
			throw new IllegalArgumentException("Invalid prefix : "+getPrefix()+" instead of "+prefix);
		
	}

	

}
