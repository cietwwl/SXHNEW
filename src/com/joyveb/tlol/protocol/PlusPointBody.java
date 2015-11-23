package com.joyveb.tlol.protocol;

import java.nio.ByteBuffer;

/** 加点消息体 */
public final class PlusPointBody extends MsgBody {
	public static final PlusPointBody INSTANCE = new PlusPointBody();

	private PlusPointBody() {
	}

	private int[] change;
	@Override
	public String toString() {
		return "bodyLen[" + bodyLen + "] " + "change[" + change[0] + ", "
				+ change[1] + ", " + change[2] + ", " + change[3] + "]";
	}
	@Override
	public boolean readBody(final ByteBuffer body) {
		if (body.remaining() <= 4 + 1)
			return false;

		bodyLen = body.getInt();

		int addNum = body.get();
		if (addNum <= 0 || body.remaining() < addNum * (1 + 4) + 2)
			return false;

		change = new int[4];
		for (int i = 0; i < addNum; i++) {
			int type = body.get();

			int plus = body.getInt();
			if (plus <= 0)
				return false;

			change[type] = plus;
		}

		end = body.getShort();

		return true;
	}

	public int[] getPlus() {
		return change;
	}
}
