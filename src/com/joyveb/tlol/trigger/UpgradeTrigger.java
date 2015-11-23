package com.joyveb.tlol.trigger;

import com.joyveb.tlol.LuaService;
import com.joyveb.tlol.role.RoleBean;

/**
 * 升级事件
 * @author Sid
 */
public enum UpgradeTrigger implements RoleEventHandler {
	/** 事件处理器单例 */
	INSTANCE;

	@Override
	public void handleEvent(final RoleBean source, final Object... args) {
		LuaService.call(1, "event", "upgrade", source, args[0], args[1]);
	}

	@Override
	public void removeEvent(RoleBean source) {
		// TODO Auto-generated method stub
		
	}

}
