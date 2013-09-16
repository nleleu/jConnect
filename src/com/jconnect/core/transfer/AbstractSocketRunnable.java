package com.jconnect.core.transfer;

import java.util.Observable;

import com.jconnect.core.Gate;

public abstract class AbstractSocketRunnable implements Runnable {

	protected Gate parent;

	
	
	
	
	
	
	public AbstractSocketRunnable (Gate parent)
	{
		this.parent = parent;

	}
	
	

	

}
