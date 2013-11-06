package com.jconnect.util.uuid;

import java.util.UUID;

public class PeerID extends AbstractUUID {
	
private static final String prefix="peerID";
//public static final PeerID NULL = new PeerID(prefix+"1475b87f-b0ee-4e54-b268-ad0de2eec1be");
	

	@Override
	public String getPrefix() {
		return prefix;
	}
	
	public static PeerID generate(){
		
		return new PeerID(AbstractUUID.generateUUID());
	}

	public PeerID(UUID uuid)
	{
		super(uuid);
	}
	
	
	public PeerID(String uuid)
	{
		super(uuid);
		if(!getPrefix().equals(prefix))
			throw new IllegalArgumentException("Invalid prefix : "+getPrefix()+" instead of "+prefix);
		
	}

}
