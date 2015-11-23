package com.joyveb.tlol.battle;

public class FightOne {

	/** 攻击类型(0.Miss,1.普通，2.重击3.致命) */
	private byte fightType;

	/** 他攻击哪个座位的 */
	private byte fightSeat;
	/** 被攻击角色的动画组 */
	private short fightGroupId;
	/** 被攻击角色的动画 */
	private short fightAnimId;
	/**
	 * 对什么什么属性攻击 (0,红1,蓝2,防御3,攻击)
	 */
	private byte fightValueType;
	/** (0增加属性值1减少属性值） */
	private byte fightValueMethod;
	/** 具体值 */
	private int fightValue;

	private short otherDataLength;
	private byte[] otherData;

	public FightOne() {
		
	}

	public FightOne(final byte fightType, final byte fightSeat, final short fightGroupId, final short fightAnimId,
			final byte fightValueType, final byte fightValueMethod, final int fightValue) {
		this.fightType = fightType;
		this.fightSeat = fightSeat;
		this.fightGroupId = fightGroupId;
		this.fightAnimId = fightAnimId;
		this.fightValueType = fightValueType;
		this.fightValueMethod = fightValueMethod;
		this.fightValue = fightValue;
	}

	public final byte getFightType() {
		return fightType;
	}

	public final void setFightType(final byte fightType) {
		this.fightType = fightType;
	}

	public final byte getFightSeat() {
		return fightSeat;
	}

	public final void setFightSeat(final byte fightSeat) {
		this.fightSeat = fightSeat;
	}

	public final short getFightGroupId() {
		return fightGroupId;
	}

	public final void setFightGroupId(final short fightGroupId) {
		this.fightGroupId = fightGroupId;
	}

	public final short getFightAnimId() {
		return fightAnimId;
	}

	public final void setFightAnimId(final short fightAnimId) {
		this.fightAnimId = fightAnimId;
	}

	public final byte getFightValueType() {
		return fightValueType;
	}

	public final void setFightValueType(final byte fightValueType) {
		this.fightValueType = fightValueType;
	}

	public final byte getFightValueMethod() {
		return fightValueMethod;
	}

	public final void setFightValueMethod(final byte fightValueMethod) {
		this.fightValueMethod = fightValueMethod;
	}

	public final int getFightValue() {
		return fightValue;
	}

	public final void setFightValue(final int fightValue) {
		this.fightValue = fightValue;
	}

	public final short getOtherDataLength() {
		return otherDataLength;
	}

	public final void setOtherDataLength(final short otherDataLength) {
		this.otherDataLength = otherDataLength;
	}

	public final byte[] getOtherData() {
		return otherData;
	}

	public final void setOtherData(final byte[] otherData) {
		this.otherData = otherData;
	}

}
