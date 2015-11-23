package com.joyveb.tlol.billboard;

import java.util.Comparator;

import com.joyveb.tlol.LuaService;
import com.joyveb.tlol.db.parser.DbConst;
import com.joyveb.tlol.role.RoleCard;
import com.joyveb.tlol.server.ServerMessage;

public final class TopGold extends TopRatedCard {
	public static final TopGold INSTANCE = new TopGold();

	/**
	 * 构造函数
	 */
	private TopGold() {
		super(DbConst.Top_Golds, "ngold", "金币排行榜", new Comparator<RoleCard>() {
			@Override
			public int compare(final RoleCard card1, final RoleCard card2) {
				return card2.getGold() - card1.getGold();
			}
		});
	}

	@Override
	public String getDescribe(final int index, final RoleCard card) {
		return (index + 1) + ". " + card.getName() + " " + LuaService.call4String("getValueDescribe", card.getGold());
	}

	@Override
	public void sendBulletin() {
		ServerMessage.sendBulletin("恭喜" + getItemName(0) + "、" + getItemName(1) + "、" + getItemName(2)
				+ "荣获财富排行榜前三甲，富甲天下无人可比！！！");
	}

}
