package com.joyveb.tlol.protocol;

import java.nio.ByteBuffer;
import java.util.ArrayList;

public final class OtherInitBody extends MsgBody {
	public static final OtherInitBody INSTANCE = new OtherInitBody();

	private OtherInitBody() {
	}

	private ArrayList<Integer> rids = new ArrayList<Integer>();

	@Override
	public boolean readBody(final ByteBuffer body) {
		if (body.remaining() <= 4 + 2)
			return false;

		bodyLen = body.getInt();
		short count = body.getShort(); // 请求人数
		if (count <= 0)
			return false;

		if (body.remaining() < 4 * count + 2)
			return false;

		for (int i = 0; i < count; i++)
			rids.add(body.getInt());

		end = body.getShort();

		return true;
	}

	public ArrayList<Integer> getRids() {
		return rids;
	}

	public void setRids(final ArrayList<Integer> rids) {
		this.rids = rids;
	}

}
