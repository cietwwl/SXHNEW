package com.joyveb.tlol.trigger;

import com.joyveb.tlol.role.RoleBean;

public interface RoleEventHandler {

	void handleEvent(RoleBean source, Object... args);

	void removeEvent(RoleBean source);
}
