package com.joyveb.tlol.protocol;

import java.nio.ByteBuffer;

public final class EnemyNameListDelBody extends MsgBody {
	public static final EnemyNameListDelBody INSTANCE = new EnemyNameListDelBody();

	private EnemyNameListDelBody() {
	}
	private int roleid;
	@Override
	public boolean readBody(final ByteBuffer body) {
		if (body.remaining() < 4 + 4)
			return false;

		bodyLen = body.getInt();
		roleid = body.getInt();

		return true;
	}

	public void setRoleid(final int roleid) {
		this.roleid = roleid;
	}

	public int getRoleid() {
		return roleid;
	}
}
