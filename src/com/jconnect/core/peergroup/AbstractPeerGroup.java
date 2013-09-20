package com.jconnect.core.peergroup;

import java.util.List;
import java.util.Stack;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.jconnect.core.event.MessageEvent;
import com.jconnect.core.peergroup.services.AbstractService;

public abstract class AbstractPeerGroup {

	private Logger log = Logger.getLogger(AbstractPeerGroup.class.getName());

	private AbstractPeerGroup parentGroup;
	private List<AbstractService> services;
	private Thread thread;

	private UUID uUID;
	
	private Stack<MessageEvent> messageEvents;

	public AbstractPeerGroup(UUID uuid, AbstractPeerGroup pGroup) {
		parentGroup = pGroup;
		this.uUID = uuid;
		thread = new GroupThread();
	}

	public void addService(AbstractService service) {
		service.setPeerGroup(this);
		services.add(service);
	}
	
	public AbstractService getService(String serviceName){
		for (AbstractService service : services) {
			if(service.getClass().getName().equals(serviceName)){
				return service;
			}
		}
		return null;
	}

	public void start() {
		switch (thread.getState()) {
		case TERMINATED:
			thread = new GroupThread();
		case NEW:
			thread.start();
			break;
		case BLOCKED:
		case WAITING:
		case TIMED_WAITING:
		case RUNNABLE:
			log.log(Level.WARNING, "Group " + uUID
					+ " already running. Thread state:" + thread.getState());
			break;

		}
	}

	public void stop() {
		switch (thread.getState()) {
		case TERMINATED:
		case NEW:
			log.log(Level.WARNING, "Group " + uUID
					+ " not running. Thread state:" + thread.getState());

			break;
		case BLOCKED:
		case WAITING:
		case TIMED_WAITING:
		case RUNNABLE:
			thread.interrupt();
			break;

		}
	}

	private class GroupThread extends Thread {

		private long timeToSleep = 0;
		private Timer timer = new Timer();

		@Override
		public void run() {

			log.log(Level.FINER, "Group " + uUID + " started");

			try {
				while (thread.getState() == Thread.State.RUNNABLE) {
					update();

				}
			} catch (InterruptedException e) {

			}

			log.log(Level.FINER, "Group " + uUID + " stopped");

		}

		private void update() throws InterruptedException {
			//TODO gestions des messageevents
			
			
			for (AbstractService service : services) {
				service.update();
			}

			for (AbstractService service : services) {
				if (service.needsUpdate())
					service.update();
				timeToSleep = timeToSleep == 0 ? service.getNextExecutionTime()
						: Math.min(timeToSleep, service.getNextExecutionTime());
			}

			timeToSleep = timeToSleep - System.currentTimeMillis();

			if (timeToSleep > 0) {

				timer.schedule(new TimerTask() {

					@Override
					public void run() {
						if (thread != null) {
							synchronized (thread) {
								thread.notifyAll();
							}
						}
					}
				}, timeToSleep);
				synchronized (this) {
					wait();
				}

			}
			timeToSleep = 0;

		}

	}

	public UUID getuUID() {
		return uUID;
	}

	public void addMessageEvent(MessageEvent mEvent) {
		synchronized (messageEvents) {
			messageEvents.add(mEvent);
		}
	}

}
