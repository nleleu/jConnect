package com.jconnect.core.event;

import com.jconnect.core.message.Message;


public class MessageEvent {

	public enum State {
		MESSAGE_RECEIVED, SEND_SUCCESS, SEND_FAIL
	}

	private State state;
	private Message message;

	public MessageEvent(State state) {
		this.state = state;
	}

	public State getState() {
		return state;
	}

	public void setMessage(Message message) {
		this.message = message;
	}
	
	public Message getMessage() {
		return message;
	}

}
