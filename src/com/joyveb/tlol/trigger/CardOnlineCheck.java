package com.joyveb.tlol.trigger;

import com.joyveb.tlol.role.RoleBean;
import com.joyveb.tlol.role.RoleCard;
import com.joyveb.tlol.role.RoleCardService;

public enum CardOnlineCheck implements RoleEventHandler {
	INSTANCE;

	@Override
	public void handleEvent(final RoleBean source, final Object... args) {
		RoleCard card = RoleCardService.INSTANCE.getCard(source.getRoleid());

		if (card == null)
			RoleCardService.INSTANCE.synchronousCard(source.makeCard());
	}

	@Override
	public void removeEvent(RoleBean source) {
		// TODO Auto-generated method stub
		
	}

}
