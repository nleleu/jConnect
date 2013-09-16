package com.jconnect.core.transfer;

import java.util.Observable;

import com.jconnect.core.Gate;

public abstract class AbstractSocketThread implements Runnable {

	protected Gate parent;

	
	
	
	
	
	
	public AbstractSocketThread (Gate parent)
	{
		this.parent = parent;

	}
	
	

	

}
