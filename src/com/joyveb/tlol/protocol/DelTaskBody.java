package com.joyveb.tlol.protocol;

import java.nio.ByteBuffer;

public final class DelTaskBody extends MsgBody {
	public static final DelTaskBody INSTANCE = new DelTaskBody();

	private DelTaskBody() {
	}

	private int taskID;
	@Override
	public boolean readBody(final ByteBuffer body) {
		if (body.remaining() < 4 + 4 + 2)
			return false;

		bodyLen = body.getInt();
		taskID = body.getInt();

		end = body.getShort();

		return true;
	}

	public void setTaskID(final int taskID) {
		this.taskID = taskID;
	}

	public int getTaskID() {
		return taskID;
	}

}
