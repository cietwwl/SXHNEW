package com.joyveb.tlol.billboard;

import java.util.Comparator;

import com.joyveb.tlol.db.parser.DbConst;
import com.joyveb.tlol.role.RoleCard;
import com.joyveb.tlol.server.ServerMessage;

public final class TopHonor extends TopRatedCard {
	public static final TopHonor INSTANCE = new TopHonor();

	private TopHonor() {
		super(DbConst.Top_Honor, "nhonor", "英雄榜", new Comparator<RoleCard>() {
			@Override
			public int compare(final RoleCard card1, final RoleCard card2) {
				return card2.getHonor() - card1.getHonor();
			}
		});
	}

	@Override
	public String getDescribe(final int index, final RoleCard card) {
		return (index + 1) + ".  " + card.getName() + "  荣誉值：" + card.getHonor();
	}

	public String getBulletinDescribe(final int index) {
		return "玩家【" + getItemName(index) + "】登上了英雄榜第" + (index + 1) + "位，各位玩家见到此人，请注意人身安全！";
	}

	@Override
	public void sendBulletin() {
		String message = null;
		if(topCards.size() == 1) {
			message = "英雄榜第一名为：" + getItemName(0) + "，他收割生命的效率令人不寒而栗，谁敢与其争锋！";
		}else if(topCards.size() == 2) {
			message = "英雄榜前两名为：" + getItemName(0) + "、" + getItemName(1) + "，他们收割生命的效率令人不寒而栗，谁敢与其争锋！";
		}else {
			message = "英雄榜前三名为：" + getItemName(0) + "、" + getItemName(1) + "、" + getItemName(2)
					+ "，他们收割生命的效率令人不寒而栗，谁敢与其争锋！";
		}
		ServerMessage.sendBulletin(message);
	}

}
