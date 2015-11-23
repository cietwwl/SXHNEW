package com.joyveb.tlol;

import java.awt.Color;

import com.joyveb.tlol.gang.GangJobTitle;
import com.joyveb.tlol.item.Equip;
import com.joyveb.tlol.item.Item;
import com.joyveb.tlol.role.RoleBean;
import com.joyveb.tlol.store.Store;

public final class SubModules extends MessageSend {
	private static final int GRAY = new Color(225, 228, 237).getRGB();

	private SubModules() {
	}

	public static void fillAttributes(final RoleBean role) {
		putShort((short) 100); // Serial
		putShort(role.getLevel()); // Level
		putInt(role.getGold()); // 金币
		putInt(role.getMark()); // 积分
		putInt(role.getMoney()); // 游戏币
		putInt(role.getHP()); // 生命值
		putInt(role.getMaxHP()); // 生命值最大
		putInt(role.getMP()); // 魔法值
		putInt(role.getMaxMP()); // 魔法值最大
		putInt(role.getEXP()); // 经验值
		putInt(role.getMaxEXP()); // 经验值最大
		putInt(role.getLeftPoint()); // 未加的技能点
		putShort(role.getStore().getBag().getCapacity()); // 当前包裹的格子数最大值
		putInt(role.getColor()); // 玩家名字颜色
	}

	public static void fillAttributesDes(final RoleBean role) {
		putShort((short) 101); // Serial

		putByte((byte) 11);

		putString("力量：" + role.getStrength());
		putString("智力：" + role.getIntellect());
		putString("敏捷：" + role.getAgility());
		putString("体质：" + role.getVitality());
		putString("物攻：" + role.getMinPAtk() + "~" + role.getMaxPAtk());
		putString("物防：" + role.getpDef());
		putString("法攻：" + role.getMinMAtk() + "~" + role.getMaxMAtk());
		putString("法防：" + role.getmDef());
		putString("命中：" + role.getHit());
		putString("躲闪：" + role.getEvade());
		putString("暴击：" + role.getCrit());

		putByte((byte) 4);

		putString("等级：" + role.getLevel());
		putString("职业：" + role.getVocation());
		putString("声望：" + role.getCharm());
		putString("积分：" + role.getMark());
	}

	public static void fillVIPInfo(final RoleBean role) {
		putShort((short) 104); // Serial

		if (role.isVIP())
			putByte(role.getVIPLevel());
		else
			putByte((byte) 0);
	}
	
	public static void fillEpithet(final RoleBean role) {
		putShort((short) 105); // Serial

		putString(role.getEpithet());
		putInt(Color.MAGENTA.getRGB());
	}
	
	public static void fillBuffAdd(final byte type) {
		putShort((short) 500); // Serial
		putByte(type);
	}

	public static void fillBuffDel(final byte type) {
		putShort((short) 501); // Serial
		putByte(type);
	}
	
	public static void fillDepotsInfo(final RoleBean role) {
		putShort((short) 600); // Serial

		putByte(Store.DEPOT_MAX);

		int depotCount = role.getStore().getDepotCount();

		for (int i = 1; i <= Store.DEPOT_MAX; i++) {
			if (i <= depotCount) {
				putByte((byte) 1);
				putByte((byte) i);
				putString("仓库" + i);
				putInt(Color.BLACK.getRGB());
			} else {
				putByte((byte) 0);
				putByte((byte) i);
				putString("仓库" + i + "（未开通）");
				putInt(GRAY);
			}
		}
	}

	public static void fillDepotAdd(final RoleBean role, final byte packid, final Item item) {
		putShort((short) 601); // Serial

		putByte(packid);
		putLong(item.getUid());
		putString(item.getName());
		putShort(item.getStorage());
		putInt(item.getColor());
		
		if(LuaService.getBool(Item.LUA_CONTAINER, item.getTid(), "overlay"))
			putShort(LuaService.getShort(Item.LUA_CONTAINER, item.getTid(), "overlay"));
		else
			putShort((short) 20);
	}

	public static void fillDepotDel(final RoleBean role, final byte packid, final Item item) {
		putShort((short) 602); // Serial

		putByte(packid);
		putLong(item.getUid());
		putShort(item.getStorage());
	}

	public static void fillGangJobTitle(final GangJobTitle jobTitle, final String gangName) {
		putShort((short) 801); // Serial
		putByte(jobTitle.getJobTitleVaule());
		putString(gangName);
	}
	
	public static void fillFlushEquip(Equip equip) {
		putShort((short) 207); // Serial
		
		putLong(equip.getUid());
		putString(equip.getName());
		putInt(equip.getColor());
	}

}
