package com.joyveb.tlol.buff;

import com.joyveb.tlol.util.Log;

public class AfterBattleHPMP extends OnlineTimeBuff {

	@Override
	public final void merge(final Buff buff) {
		if (buff.effectProp != this.effectProp)
			return;

		OnlineTimeBuff similar = (OnlineTimeBuff) buff;
		if (buffLevel == similar.buffLevel)
			effectValue = similar.effectValue;
		else if (buffLevel < similar.buffLevel)
			copy(similar);
		else
			Log.error(Log.STDOUT, "addBuff", "当前buff等级高于使用Buff等级!");
	}
}
