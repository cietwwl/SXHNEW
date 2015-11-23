package com.joyveb.tlol.battle;

public class UserFightBean {

	/** 选择的攻击类型 0.普通 1.技能 2.物品使用 3.逃跑 */
	private byte atkType;

	/** 二级类型(比如技能或物品的类型) */
	private byte atkSubType;

	/** 应用技能或物品ID */
	private long appID;

	/** 打击目标 */
	private byte[] dest = { -1, -1, -1, -1, -1, -1 };

	public final void setAtkType(final byte atkType) {
		this.atkType = atkType;
	}

	public final byte getAtkType() {
		return atkType;
	}

	public final void setAtkSubType(final byte atkSubType) {
		this.atkSubType = atkSubType;
	}

	public final byte getAtkSubType() {
		return atkSubType;
	}

	public final void setAppID(final long appID) {
		this.appID = appID;
	}

	public final long getAppID() {
		return appID;
	}

	public final void setDest(final int dest) {
		if (dest >= 0 && dest < 6) {
			this.dest[dest] = 1;
		}
	}

	public final byte[] getDest() {
		return dest;
	}

	public final void resetDest() {
		for (int i = 0; i < dest.length; i++)
			dest[i] = -1;
	}
}
