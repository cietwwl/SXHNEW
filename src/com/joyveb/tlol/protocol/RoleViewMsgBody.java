package com.joyveb.tlol.protocol;

import java.nio.ByteBuffer;

/** 查看信息消息体 */
public final class RoleViewMsgBody extends MsgBody {
	public static final RoleViewMsgBody INSTANCE = new RoleViewMsgBody();

	private RoleViewMsgBody() {
	}

	private int userid;

	@Override
	public boolean readBody(final ByteBuffer body) {
		bodyLen = body.getInt();
		userid = body.getInt();
		return true;
	}

	public void setUserid(final int userid) {
		this.userid = userid;
	}

	public int getUserid() {
		return userid;
	}
}
