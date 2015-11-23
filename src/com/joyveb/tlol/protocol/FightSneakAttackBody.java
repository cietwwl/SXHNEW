package com.joyveb.tlol.protocol;

import java.nio.ByteBuffer;

public class FightSneakAttackBody extends MsgBody {

	private int attactedPlayerId;

	@Override
	public final boolean readBody(final ByteBuffer body) {
		bodyLen = body.getInt();
		attactedPlayerId = body.getInt();
		return true;
	}

	public final int getAttactedPlayerId() {
		return attactedPlayerId;
	}

	public final void setAttactedPlayerId(final int attactedPlayerId) {
		this.attactedPlayerId = attactedPlayerId;
	}

}
