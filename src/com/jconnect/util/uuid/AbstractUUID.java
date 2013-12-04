package com.jconnect.util.uuid;

import java.util.UUID;

import javax.jws.HandlerChain;

/**
 *  Abstract class of JConnect's UUID
 *
 */
public abstract class AbstractUUID {
	
	private UUID uuid;
	private String prefix;
	
	public abstract String getPrefix();
	
	
	public AbstractUUID(UUID uuid)
	{
		this.uuid = uuid;
		this.prefix=getPrefix();
	}
	
	public AbstractUUID(String uuid)
	{
		if(uuid.indexOf(":")<0){
			throw new IllegalArgumentException("Invalid UID pathern");
		}
		this.prefix = uuid.substring(0, uuid.indexOf(":"));
		
		this.uuid = UUID.fromString(uuid.substring(uuid.indexOf(":")+1));
	}
	
	public String toString()
	{
		return prefix+":"+uuid.toString();
	}
	
	@Override
	public boolean equals(Object obj) {
		return obj.toString().equals(toString());
		
	};
	
	
	@Override
	public int hashCode() {
		return toString().hashCode();
	};
	

	protected static UUID generateUUID() {
		return UUID.randomUUID();
		
	}
	

}
