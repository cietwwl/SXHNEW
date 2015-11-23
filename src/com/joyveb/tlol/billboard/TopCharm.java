package com.joyveb.tlol.billboard;

import java.util.Comparator;

import com.joyveb.tlol.db.parser.DbConst;
import com.joyveb.tlol.role.RoleCard;
import com.joyveb.tlol.server.ServerMessage;

public class TopCharm extends TopRatedCard {
	public static final TopCharm INSTANCE = new TopCharm();

	/**
	 * 构造函数
	 */
	public TopCharm() {
		super(DbConst.Top_Charms, "ncharm", "声望排行榜", new Comparator<RoleCard>() {
			@Override
			public int compare(final RoleCard card1, final RoleCard card2) {
				return card2.getCharm() - card1.getCharm();
			}
		});
	}

	@Override
	public final String getDescribe(final int index, final RoleCard card) {
		return (index + 1) + ". " + card.getName() + " 声望：" + card.getCharm();
	}

	@Override
	public final void sendBulletin() {
		ServerMessage.sendBulletin("恭喜" + getItemName(0) + "、" + getItemName(1) + "、" + getItemName(2)
				+ "荣获声望排行榜前三甲，如此气势谁人能敌！！！");
	}

}
