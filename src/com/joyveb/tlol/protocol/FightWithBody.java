package com.joyveb.tlol.protocol;

import java.nio.ByteBuffer;

/** 请求战斗消息体 */
public final class FightWithBody extends MsgBody {
	public static final FightWithBody INSTANCE = new FightWithBody();

	private FightWithBody() {
	}

	/** 和人战斗（偷袭），还是怪物（杀怪）(0,人，1怪物) */
	private byte type;
	/** 二级类型（保留） */
	private byte subType;
	/** 敌人id */
	private int foeID;
	/** 是否强制 */
	private byte force;
	@Override
	public boolean readBody(final ByteBuffer body) {
		if (body.remaining() < 4 + 1 + 1 + 4 + 1 + 2)
			return false;

		bodyLen = body.getInt();
		type = body.get();
		subType = body.get();
		foeID = body.getInt();
		force = body.get();

		end = body.getShort();

		return true;
	}

	public void setType(final byte type) {
		this.type = type;
	}

	public byte getType() {
		return type;
	}

	public void setSubType(final byte subType) {
		this.subType = subType;
	}

	public byte getSubType() {
		return subType;
	}

	public void setFoeID(final int foeID) {
		this.foeID = foeID;
	}

	public int getFoeID() {
		return foeID;
	}

	public void setForce(final byte force) {
		this.force = force;
	}

	public byte getForce() {
		return force;
	}

}
