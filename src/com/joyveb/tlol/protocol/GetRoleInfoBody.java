package com.joyveb.tlol.protocol;

import java.nio.ByteBuffer;

public final class GetRoleInfoBody extends MsgBody {

	public static final GetRoleInfoBody INSTANCE = new GetRoleInfoBody();

	private GetRoleInfoBody() {
	}

	/** 角色id */
	private int roleid;
	@Override
	public boolean readBody(final ByteBuffer body) {
		if (body.remaining() < 4 + 4 + 2)
			return false;

		bodyLen = body.getInt(); // 包体长

		roleid = body.getInt();

		end = body.getShort();

		return true;
	}

	public void setRoleid(final byte roleid) {
		this.roleid = roleid;
	}

	public int getRoleid() {
		return roleid;
	}

}
