package com.joyveb.tlol.protocol;

import java.nio.ByteBuffer;

public final class ChangeNameBody extends MsgBody {
	public static final ChangeNameBody INSTANCE = new ChangeNameBody();

	private ChangeNameBody() {
	}

	private int roleId;
	private String newName;
	@Override
	public boolean readBody(final ByteBuffer body) {
		bodyLen = body.getInt();
		roleId = body.getInt();
		newName = getStrByLen(body, body.getShort());
		return true;
	}

	public long getRoleId() {
		return roleId;
	}

	public String getNewName() {
		return newName;
	}
	
}
