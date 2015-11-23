package com.joyveb.tlol.buff;

import java.util.HashMap;

public enum BuffType {
	EXP(1, OnlineTimeBuff.class),
	GOLD(2, OnlineTimeBuff.class),
	ATK(3, OnlineTimeBuff.class), // 攻击力UP
	AFTER_BATTLE_HP(4, AfterBattleHPMP.class),
	VIP(5, VIPBuff.class),
	OFFLINE_EXP(6, OnlineTimeBuff.class),
	AFTER_BATTLE_MP(7, AfterBattleHPMP.class),
	DFC(8, OnlineTimeBuff.class), // 防御力UP
	MARK(9, OnlineTimeBuff.class);

	private final byte id;
	private final Class<? extends Buff> typename;

	private BuffType(final int id, final Class<? extends Buff> typename) {
		this.id = (byte) id;
		this.typename = typename;
	}

	private static HashMap<Byte, BuffType> mapping = new HashMap<Byte, BuffType>();

	public static BuffType getType(final byte id) {
		if(mapping.isEmpty()) {
			for (BuffType type : BuffType.values()) {
				mapping.put(type.getId(), type);
			}
		}
		return mapping.get(id);
	}

	public byte getId() {
		return id;
	}

	public Buff creatBuff() {
		try {
			return typename.newInstance().setEffectProp(id);
		}catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
