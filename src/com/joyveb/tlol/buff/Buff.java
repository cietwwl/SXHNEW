package com.joyveb.tlol.buff;

import com.joyveb.tlol.LuaService;
import com.joyveb.tlol.MessageSend;
import com.joyveb.tlol.SubModules;
import com.joyveb.tlol.protocol.MsgID;
import com.joyveb.tlol.role.RoleBean;

public abstract class Buff {
	/**
	 * 作用属性
	 */
	protected byte effectProp;

	/**
	 * buff级别用于高级别buff覆盖低级别buff
	 */
	protected byte buffLevel;

	/**
	 * 剩余时间 单位:秒
	 */
	protected long overduetime;

	/**
	 * 生效时间：毫秒
	 */
	protected long effectTime;

	/**
	 * buff效果ID 执行脚本中的效果函数来修正属性值
	 */
	protected byte effectId;

	/**
	 * buff作用值 如用作加倍属性时这是倍数
	 */
	protected int effectValue;

	public final void copy(final Buff similar) {
		this.effectProp = similar.effectProp;
		this.buffLevel = similar.buffLevel;
		this.effectId = similar.effectId;
		this.effectValue = similar.effectValue;
		this.effectTime = similar.effectTime;
		this.overduetime = similar.overduetime;
	}

	public final void setAttr(final byte buffLevel, final long overduetime, final byte effectId,
			final int effectValue) {
		this.buffLevel = buffLevel;
		this.effectId = effectId;
		this.effectValue = effectValue;
		this.overduetime = overduetime;
	}

	public abstract boolean isTimeOut();

	public abstract void serialize(StringBuilder builder);

	public abstract void initOnline(RoleBean player);

	public final int fixValue(final int value) {
		return LuaService.call4Int("effect", this, value);
	}

	public abstract void merge(final Buff buff);

	public void addToRole(final RoleBean player) {
		MessageSend.prepareBody();
		SubModules.fillBuffAdd(effectProp);
		MessageSend.putShort((short) 0);
		MessageSend.sendMsg(player, MsgID.MsgID_Special_Train);
	}

	public void delFromRole(final RoleBean player) {
		MessageSend.prepareBody();
		SubModules.fillBuffDel(effectProp);
		MessageSend.putShort((short) 0);
		MessageSend.sendMsg(player, MsgID.MsgID_Special_Train);
	}

	public final Buff setEffectProp(final byte effectProp) {
		this.effectProp = effectProp;
		return this;
	}

	public final byte getEffectProp() {
		return effectProp;
	}

	public final Buff setBuffLevel(final byte buffLevel) {
		this.buffLevel = buffLevel;
		return this;
	}

	public final byte getBuffLevel() {
		return buffLevel;
	}

	public final Buff setOverduetime(final long overduetime) {
		this.overduetime = overduetime;
		return this;
	}

	public final long getOverduetime() {
		return overduetime;
	}

	public final Buff setEffectTime(final long effectTime) {
		this.effectTime = effectTime;
		return this;
	}

	public final long getEffectTime() {
		return effectTime;
	}

	public final Buff setEffectId(final byte effectId) {
		this.effectId = effectId;
		return this;
	}

	public final byte getEffectId() {
		return effectId;
	}

	public final Buff setEffectValue(final int effectValue) {
		this.effectValue = effectValue;
		return this;
	}

	public final int getEffectValue() {
		return effectValue;
	}

}
