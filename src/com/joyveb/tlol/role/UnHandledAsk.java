package com.joyveb.tlol.role;

import com.joyveb.tlol.protocol.MsgID;

public class UnHandledAsk {
	public UnHandledAsk(final int requestId, final MsgID cmd) {
		this.requestId = requestId;
		this.cmd = cmd;
		this.createTime = System.currentTimeMillis();
	}

	private int requestId; // 请求的玩家Id
	private MsgID cmd; // 请求做什么事
	private long createTime;

	public final int getRequestId() {
		return requestId;
	}

	public final void setRequestId(final int requestId) {
		this.requestId = requestId;
	}

	public final MsgID getCmd() {
		return cmd;
	}

	public final void setCmd(final MsgID cmd) {
		this.cmd = cmd;
	}

	public final void tick(final RoleBean role) {
		long curTime = System.currentTimeMillis();
		if ((curTime - this.createTime) > 1000 * 30) {
			role.setUnHandledAsk(null);
		}
	}
}
