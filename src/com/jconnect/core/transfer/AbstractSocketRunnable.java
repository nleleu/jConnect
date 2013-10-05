package com.jconnect.core.transfer;

import com.jconnect.core.Gate;

/**
 * Abstract class for socket runnable
 */
public abstract class AbstractSocketRunnable implements Runnable {

	
	protected Gate parent;

	
	
	
	
	
	
	public AbstractSocketRunnable (Gate parent)
	{
		this.parent = parent;

	}
	
	

	

}
