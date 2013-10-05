package com.jconnect.util.uuid;

import java.util.UUID;

public class PeerID extends AbstractUUID {
	
private static final String prefix="peerID";
	
	public PeerID(UUID uuid)
	{
		super(uuid,prefix);
	}
	
	
	public PeerID(String uuid)
	{
		super(uuid);
		if(!getPrefix().equals(prefix))
			throw new IllegalArgumentException("Invalid prefix : "+getPrefix()+" instead of "+prefix);
		
	}

}
