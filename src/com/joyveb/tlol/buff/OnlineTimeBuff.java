package com.joyveb.tlol.buff;

import com.joyveb.tlol.role.RoleBean;
import com.joyveb.tlol.util.Log;

public class OnlineTimeBuff extends Buff {

	public OnlineTimeBuff() {
		effectTime = System.currentTimeMillis();
	}

	@Override
	public void merge(final Buff buff) {
		if (buff.effectProp != this.effectProp) {
			return;
		}
		OnlineTimeBuff similar = (OnlineTimeBuff) buff;
		if (buffLevel == similar.buffLevel) {
			overduetime = overduetime + similar.overduetime > 31104000 ? 31104000
					: overduetime + similar.overduetime;
		}else if (buffLevel < similar.buffLevel) {
			copy(similar);
		}else {
			Log.error(Log.STDOUT, "addBuff", "当前buff等级高于使用Buff等级!");
		}
	}

	@Override
	public final void serialize(final StringBuilder builder) {
		long leftTime = overduetime - (System.currentTimeMillis() - effectTime)
				/ 1000;

		if (leftTime > 0) {
			builder.append(effectProp + "," + leftTime + "," + buffLevel + ","
					+ effectId + "," + effectValue + ";");
		}
	}

	@Override
	public final boolean isTimeOut() {
		return overduetime < (System.currentTimeMillis() - effectTime) / 1000
				|| effectValue <= 0;
	}

	@Override
	public void initOnline(final RoleBean player) {
	}

}
