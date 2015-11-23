package com.joyveb.tlol.protocol;

import java.nio.ByteBuffer;

public final class RoleQuickEnterBody extends MsgBody {
	public static final RoleQuickEnterBody INSTANCE = new RoleQuickEnterBody();

	private RoleQuickEnterBody() {
	}

	private short zoneId;
	private int userId;
	private int roleId;
	private byte sex;
	private byte vocation;

	@Override
	public boolean readBody(final ByteBuffer body) {
		bodyLen = body.getInt();
		userId = body.getInt();
		zoneId = body.getShort();
		roleId = body.getInt();
		if (bodyLen > 10) {
			sex = (byte) body.getShort();
			vocation = (byte) body.getShort();
		} else {
			sex = -1;
			vocation = -1;
		}

		return true;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(final int userId) {
		this.userId = userId;
	}

	public int getRoleId() {
		return roleId;
	}

	public void setRoleId(final int roleId) {
		this.roleId = roleId;
	}

	public short getZoneId() {
		return zoneId;
	}

	public void setZoneId(final short zoneId) {
		this.zoneId = zoneId;
	}

	public byte getSex() {
		return sex;
	}

	public void setSex(byte sex) {
		this.sex = sex;
	}

	public byte getVocation() {
		return vocation;
	}

	public void setVocation(byte vocation) {
		this.vocation = vocation;
	}

}
