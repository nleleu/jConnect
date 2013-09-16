package com.jconnect.core.transfer;

import java.net.SocketAddress;

public abstract class  MessageReceivedEvent extends TransferEvent {


	
	private String data;
	
	public MessageReceivedEvent(SocketAddress sa,String data)
	{
		super(sa);
		this.setData(data);
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}


	
	
	
}
