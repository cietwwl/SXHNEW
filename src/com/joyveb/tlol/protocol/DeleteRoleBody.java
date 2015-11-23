package com.joyveb.tlol.protocol;

import java.nio.ByteBuffer;

/** 删除角色消息体 */
public final class DeleteRoleBody extends MsgBody {
	public static final DeleteRoleBody INSTANCE = new DeleteRoleBody();

	private DeleteRoleBody() {
	}

	/** 角色id */
	private int roleid;
	@Override
	public boolean readBody(final ByteBuffer body) {
		body.position(0);

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
