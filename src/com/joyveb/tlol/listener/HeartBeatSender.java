package com.joyveb.tlol.listener;

import com.joyveb.tlol.role.RoleBean;
import com.joyveb.tlol.server.ServerMessage;

public class HeartBeatSender extends RoleMinListener {

	public static int Heart_Beat_Interval = 1;

	public HeartBeatSender(final RoleBean owner) {
		super(owner);
	}

	@Override
	public final void minTick(final int curMin) {
		ServerMessage.sendHeartBeat(owner);
	}

	@Override
	public final boolean isTimeOut() {
		return false;
	}

}
