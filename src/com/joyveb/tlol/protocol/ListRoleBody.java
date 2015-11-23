package com.joyveb.tlol.protocol;

import java.nio.ByteBuffer;

/** 获取用户角色消息 */
public final class ListRoleBody extends MsgBody {
	public static final ListRoleBody INSTANCE = new ListRoleBody();

	private ListRoleBody() {
	}

	/** 用户ID */
	private int userid;
	/** 用户所在大区ID */
	private short zoneid;
	@Override
	public boolean readBody(final ByteBuffer body) {
		if (body.remaining() < 10)
			return false;

		bodyLen = body.getInt();

		userid = body.getInt();
		zoneid = body.getShort();

		end = body.getShort(); // 保留数据长

		return true;
	}

	/**
	 *  用户ID
	 * @param userid 
	 */
	public void setUserid(final int userid) {
		this.userid = userid;
	}

	/**
	 * 用户ID 
	 * @return 用户ID
	 */
	public int getUserid() {
		return userid;
	}

	/** 
	 * 用户所在大区ID
	 * @param zoneid 
	 */
	public void setZoneid(final short zoneid) {
		this.zoneid = zoneid;
	}

	/** 
	 * 用户所在大区ID 
	 * @return 用户所在大区ID 
	 */
	public short getZoneid() {
		return zoneid;
	}

}
