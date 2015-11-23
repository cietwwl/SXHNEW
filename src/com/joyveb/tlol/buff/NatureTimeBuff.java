package com.joyveb.tlol.buff;

import com.joyveb.tlol.util.Cardinality;
import com.joyveb.tlol.util.Log;

public abstract class NatureTimeBuff extends Buff {

	public NatureTimeBuff() {
		effectTime = Cardinality.INSTANCE.getMinute();
	}
	/**
	 * @param buff
	 */
	public final void merge(final Buff buff) {
		if (buff.effectProp != this.effectProp)
			return;

		if (buffLevel == buff.buffLevel)
			overduetime += buff.overduetime;
		else if (buffLevel < buff.buffLevel)
			copy(buff);
		else
			Log.error(Log.STDOUT, "addBuff", "当前buff等级高于使用Buff等级!");

	}
	
	@Override
	public final boolean isTimeOut() {
		return Cardinality.INSTANCE.getMinute() > effectTime + overduetime / 60;
	}

	@Override
	public final void serialize(final StringBuilder builder) {
		builder.append(effectProp + "," + overduetime + "," + buffLevel + ","
				+ effectId + "," + effectValue + ";");
	}

}
