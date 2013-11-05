package com.jconnect.core.event;

import com.jconnect.core.message.Message;

public class RequestEvent {
	
	public enum State{
		ANSWER_RECEIVED,
		TIME_OUT,
		SEND_FAIL
	}


	private State state;
	private Message message;
	
	
	public RequestEvent(State s, Message m) {
		state = s;
		this.message = m;
		
	}
	
	public Message getMessage() {
		return message;
	}
	
	public State getState() {
		return state;
	}


}
