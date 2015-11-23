package com.joyveb.tlol.protocol;

import java.nio.ByteBuffer;

public final class EnemyNameListAddBody extends MsgBody {
	public static final EnemyNameListAddBody INSTANCE = new EnemyNameListAddBody();

	private EnemyNameListAddBody() {
	}

	private int id;
	@Override
	public boolean readBody(final ByteBuffer body) {
		if (body.remaining() < 4 + 4)
			return false;

		bodyLen = body.getInt();
		id = body.getInt();
		if (id < 0)
			return false;
		return true;
	}

	public void setId(final int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}
}
