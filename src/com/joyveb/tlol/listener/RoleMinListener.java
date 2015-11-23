package com.joyveb.tlol.listener;

import com.joyveb.tlol.role.RoleBean;
import com.joyveb.tlol.schedule.MinTickHandler;

public abstract class RoleMinListener implements MinTickHandler {

	protected RoleBean owner;

	public RoleMinListener(final RoleBean owner) {
		this.owner = owner;
	};

	public abstract boolean isTimeOut();

}
