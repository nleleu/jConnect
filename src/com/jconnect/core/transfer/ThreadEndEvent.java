package com.jconnect.core.transfer;

import java.net.SocketAddress;

public  class  ThreadEndEvent extends TransferEvent {


	public static final int SOCKET_STATUS_OK = 0;
	public static final int SOCKET_STATUS_TIMEOUT = 1;
	public static final int SOCKET_STATUS_CLOSED = 2;
	public static final int SOCKET_STATUS_UNKNOWN_ERROR = 3;
	
	private int returnCode;
	
	
	
	public ThreadEndEvent(SocketAddress sa,int returnCode)
	{
		super(sa);
		this.setReturnCode(returnCode);
	}



	public int getReturnCode() {
		return returnCode;
	}



	public void setReturnCode(int returnCode) {
		this.returnCode = returnCode;
	}


	
	
	
}
