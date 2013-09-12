package com.jconnect.core.network;

import java.net.Socket;

import com.jconnect.core.Gate;

public abstract class AbstractSocketThread extends Thread {

	protected Gate parent;
	protected Socket usingSocket;
	
	public static final int SOCKET_STATUS_OK = 0;
	public static final int SOCKET_STATUS_TIMEOUT = 1;
	public static final int SOCKET_STATUS_CLOSED = 2;
	public static final int SOCKET_STATUS_UNKNOWN_ERROR = 3;
	
	
	public AbstractSocketThread (Gate parent, Socket usingSocket)
	{
		this.parent = parent;
		this.usingSocket = usingSocket;

	}
	
	

	

}
