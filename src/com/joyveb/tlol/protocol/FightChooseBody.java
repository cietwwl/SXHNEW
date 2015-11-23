package com.joyveb.tlol.protocol;

import java.nio.ByteBuffer;

import com.joyveb.tlol.battle.UserFightBean;

public final class FightChooseBody extends MsgBody {
	public static final FightChooseBody INSTANCE = new FightChooseBody();

	private FightChooseBody() {
	}

	private UserFightBean fightBean;
	@Override
	public boolean readBody(final ByteBuffer body) {
		if (body.remaining() <= 4 + 1 + 1 + 8 + 1)
			return false;

		fightBean = new UserFightBean();

		bodyLen = body.getInt();

		fightBean.setAtkType(body.get());
		fightBean.setAtkSubType(body.get());
		fightBean.setAppID(body.getLong());

		byte destCount = body.get();
		if (destCount < 0 || body.remaining() < destCount + 2)
			return false;

		for (int i = 0; i < destCount; i++) {
			byte seat = body.get();
			if (seat < 0)
				return false;
			fightBean.getDest()[seat % 6] = 1;
		}

		end = body.getShort();

		return true;
	}

	public void setFightBean(final UserFightBean fightBean) {
		this.fightBean = fightBean;
	}

	public UserFightBean getFightBean() {
		return fightBean;
	}
}
