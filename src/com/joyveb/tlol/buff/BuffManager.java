package com.joyveb.tlol.buff;

import java.util.HashMap;
import java.util.Iterator;

import com.joyveb.tlol.role.RoleBean;

public class BuffManager {

	RoleBean player = null;

	HashMap<Byte, Buff> buffMap = new HashMap<Byte, Buff>();

	public BuffManager(final RoleBean player) {
		this.player = player;
	}

	public final void addBuff(final Buff buff) {
		if (buffMap.containsKey(buff.getEffectProp()))
			buffMap.get(buff.getEffectProp()).merge(buff);
		else {
			buff.addToRole(player);
			buffMap.put(buff.getEffectProp(), buff);
		}
	}

	public final boolean canAddBuff(final byte buffType, final byte buffLevel) {
		return (buffMap.containsKey(buffType)) ? buffLevel >= buffMap.get(
				buffType).getBuffLevel() : true;
	}

	/**
	 * 用于检测Buff过期
	 */
	public final void tick() {
		Iterator<Buff> iterator = buffMap.values().iterator();
		while (iterator.hasNext()) {
			Buff buff = iterator.next();

			if (buff.isTimeOut()) {
				buff.delFromRole(player);
				iterator.remove();
			}
		}
	}

	/**
	 * 修正BUFF后的值 如果BUFF不存在则返回0 !!! 调用此函数前一定要判断BUFF存在与否 !!!
	 * 
	 * @param buffType
	 * @param value
	 * @return 经过BUFF修正后的值 如果BUFF不存在返回0
	 */
	public final int fixValueAfterBuff(final byte buffType, final int value) {
		Buff buff = buffMap.get(buffType);

		if (buff != null) {
			return buff.fixValue(value);
		}

		return 0;
	}

	public final String serialize() {
		StringBuilder buffStr = new StringBuilder();

		for (Buff buff : buffMap.values())
			buff.serialize(buffStr);

		return buffStr.toString();
	}

	public final void deserialize(final String buffStr) {
		if (buffStr != null) {
			String buffList = buffStr.trim();
			if (!buffList.equals("")) {
				String[] buffs = buffList.split(";");
				for (int i = 0; i < buffs.length; i++) {
					String[] buffAttr = buffs[i].split(",");

					BuffType type = BuffType.getType(Byte.parseByte(buffAttr[0]));

					Buff buff = type.creatBuff();

					buff.setOverduetime(Long.parseLong(buffAttr[1])).setBuffLevel(Byte.parseByte(buffAttr[2])).setEffectId(Byte.parseByte(buffAttr[3]));

					if (buffAttr.length > 4)
						buff.setEffectValue(Integer.parseInt(buffAttr[4]));

					buff.initOnline(player);

					buffMap.put(buff.getEffectProp(), buff);
				}
			}
		}
	}

	/** 角色上线的时候，进入地图以后调用，通知客户端所拥有的buff */
	public final void send() {
		if (buffMap.isEmpty())
			return;

		for (Buff buff : buffMap.values())
			buff.addToRole(player);
	}

	public final int buffNum() {
		return buffMap.size();
	}

	public final HashMap<Byte, Buff> getBuffs() {
		return buffMap;
	}

	public final boolean hasBuff(final byte type) {
		return buffMap.containsKey(type);
	}

	public final Buff getBuff(final byte type) {
		return buffMap.get(type);
	}

}
