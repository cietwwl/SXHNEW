package com.joyveb.tlol.protocol;

import java.nio.ByteBuffer;

public final class ItemDoBody extends MsgBody {
	public static final ItemDoBody INSTANCE = new ItemDoBody();

	private ItemDoBody() {
	}

	/** 0.使用1装备2.比较3丢弃 */
	private byte type;
	private long itemid;
	private byte itemnum;
	@Override
	public boolean readBody(final ByteBuffer body) {
		if (body.remaining() < 4 + 1 + 8 + 1 + 2)
			return false;

		bodyLen = body.getInt();
		type = body.get();
		itemid = body.getLong();
		itemnum = body.get();

		end = body.getShort();

		return true;
	}

	/**
	 *  0.使用1装备2.比较3丢弃 
	 * @return 装备状态  
	 */
	public byte getType() {
		return type;
	}

	public void setType(final byte type) {
		this.type = type;
	}

	public void setItemid(final long itemid) {
		this.itemid = itemid;
	}

	public long getItemid() {
		return itemid;
	}

	public byte getItemnum() {
		return itemnum;
	}

	public void setItemnum(final byte itemnum) {
		this.itemnum = itemnum;
	}
}
