package com.jconnect.util.uuid;

import java.util.UUID;

public class PeerGroupID extends AbstractUUID {
	
	private static final String prefix="peerGroupID";
	
	@Override
	public String getPrefix() {
		return prefix;
	}
	
	public PeerGroupID(UUID uuid)
	{
		super(uuid);
	}
	
	
	public PeerGroupID(String uuid)
	{
		super(uuid);
		if(!getPrefix().equals(prefix))
			throw new IllegalArgumentException("Invalid prefix : "+getPrefix()+" instead of "+prefix);
		
	}

}
