package com.jconnect.util.uuid;

import java.util.UUID;

/**
 *  Abstract class of JConnect's UUID
 *
 */
public abstract class AbstractUUID {
	
	private UUID uuid;
	private String prefix;
	
	public AbstractUUID(UUID uuid,String prefix)
	{
		this.uuid = uuid;
		this.prefix=prefix;
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
	
	
	public boolean equals(AbstractUUID other)
	{
		return other.toString().equals(toString());
	}
	
	public String getPrefix()
	{
		return prefix;
	}
	

}
