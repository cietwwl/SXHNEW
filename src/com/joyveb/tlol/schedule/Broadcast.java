package com.joyveb.tlol.schedule;

import com.joyveb.tlol.server.ServerMessage;

public class Broadcast extends OneOffSchedule {

	private String bulletin;

	private Broadcast(final String bulletin) {
		this.bulletin = bulletin;
	}

	@Override
	public final void execute() {
		ServerMessage.sendBulletin(bulletin);
	}

	public static void send(String bulletin) {
		ScheduleManager.INSTANCE.offerTask(new Broadcast(bulletin));
	}
}
