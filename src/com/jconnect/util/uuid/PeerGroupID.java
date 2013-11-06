package com.jconnect.util.uuid;

import java.util.UUID;

public class PeerGroupID extends AbstractUUID {
	
	private static final String prefix="peerGroupID";
	public static final PeerGroupID NULL = new PeerGroupID(prefix+"39ccec4a-3c4c-4e1b-aa4d-537c23a4a26b");
	
	@Override
	public String getPrefix() {
		return prefix;
	}
	
	public static PeerGroupID generate(){
		
		return new PeerGroupID(AbstractUUID.generateUUID());
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
