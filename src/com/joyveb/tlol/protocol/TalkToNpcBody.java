package com.joyveb.tlol.protocol;

import java.nio.ByteBuffer;

/** 与NPC对话消息体 */
public final class TalkToNpcBody extends MsgBody {
	public static final TalkToNpcBody INSTANCE = new TalkToNpcBody();

	private TalkToNpcBody() {
	}

	private int npcid;
	private byte deep;
	private int taskid;
	private long uid;
	private short num;
	@Override
	public boolean readBody(final ByteBuffer body) {
		if (body.remaining() < 4 + 4 + 1 + 4 + 8 + 2 + 2)
			return false;

		bodyLen = body.getInt();

		npcid = body.getInt();
		deep = body.get();
		taskid = body.getInt();

		uid = body.getLong();
		num = body.getShort();

		if (npcid < 0 || deep < 0 || taskid < 0 || uid < 0 || num < 0)
			return false;

		if (deep > 0 && taskid == 0)
			return false;

		body.getShort();

		return true;
	}

	public int getNpcid() {
		return npcid;
	}

	public void setNpcid(final int npcid) {
		this.npcid = npcid;
	}

	public byte getDeep() {
		return deep;
	}

	public void setDeep(final byte deep) {
		this.deep = deep;
	}

	public int getTaskid() {
		return taskid;
	}

	public void setTaskid(final int taskid) {
		this.taskid = taskid;
	}

	public void setUid(final long uid) {
		this.uid = uid;
	}

	public long getUid() {
		return uid;
	}

	public void setNum(final short num) {
		this.num = num;
	}

	public short getNum() {
		return num;
	}
}
