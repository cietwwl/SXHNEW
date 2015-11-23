package com.joyveb.tlol.protocol;

import java.nio.ByteBuffer;

/** 获取任务列表消息体 */
public final class ListTaskBody extends MsgBody {
	public static final ListTaskBody INSTANCE = new ListTaskBody();

	private ListTaskBody() {
	}

	private int listType;
	@Override
	public boolean readBody(final ByteBuffer body) {
		if (body.remaining() < 4 + 1 + 2)
			return false;

		bodyLen = body.getInt();
		listType = body.get();

		end = body.getShort();

		return true;
	}

	public void setListType(final int listType) {
		this.listType = listType;
	}

	public int getListType() {
		return listType;
	}

}
