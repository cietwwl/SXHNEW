package com.joyveb.tlol.protocol;

import java.nio.ByteBuffer;

/** 创建角色消息体 */
public final class AddRoleBody extends MsgBody {
	public static final AddRoleBody INSTANCE = new AddRoleBody();
	/**
	 * 构造方法
	 */
	private AddRoleBody() {
	}

	/** 新建角色昵称 */
	private String nick;
	/** 新建角色性别 */
	private byte sex;
	/** 新建角色职业 */
	private byte vocation;
	/** 新建角色动画组id */
	private short groupid;
	/** 新建角色动画id */
	private short aminiid;
	@Override
	public boolean readBody(final ByteBuffer body) {
		body.position(0);
		if (body.remaining() <= 4 + 2)
			return false;

		bodyLen = body.getInt();

		short nickLen = body.getShort();

		if (nickLen <= 0 || body.remaining() <= nickLen)
			return false;

		nick = getStrByLen(body, nickLen).trim();

		if (body.remaining() < 1 + 1 + 2 + 2 + 2)
			return false;

		sex = body.get();
		vocation = body.get();
		groupid = body.getShort();
		aminiid = body.getShort();

		end = body.getShort();

		return true;
	}

	/** 
	 * 新建角色昵称
	 * @param nick 
	 */
	public void setNick(final String nick) {
		this.nick = nick;
	}

	/**
	 *  新建角色昵称 
	 *  @return 新建角色昵称 
	 */
	public String getNick() {
		return nick;
	}

	/**
	 *  新建角色性别 
	 *  @param sex 
	 */
	public void setSex(final byte sex) {
		this.sex = sex;
	}

	/**
	 *  新建角色性别 
	 *  @return 新建角色性别
	 */
	public byte getSex() {
		return sex;
	}

	/**
	 *  新建角色职业
	 *  @param vocation 
	 */
	public void setVocation(final byte vocation) {
		this.vocation = vocation;
	}

	/**
	 *  新建角色职业 
	 * @return 新建角色职业 
	 */
	public byte getVocation() {
		return vocation;
	}

	/** 
	 * 新建角色动画组id 
	 * @param groupid 
	 */
	public void setGroupid(final short groupid) {
		this.groupid = groupid;
	}

	/**
	 *  新建角色动画组id 
	 *  @return 新建角色动画组id
	 */
	public short getGroupid() {
		return groupid;
	}

	/** 
	 * 新建角色动画id 
	 * @param aminiid 
	 */
	public void setAminiid(final short aminiid) {
		this.aminiid = aminiid;
	}

	/** 
	 * 新建角色动画id 
	 * @return 新建角色动画id 
	 */
	public short getAminiid() {
		return aminiid;
	}

}
