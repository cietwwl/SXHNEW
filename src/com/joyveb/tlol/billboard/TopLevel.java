package com.joyveb.tlol.billboard;

import java.util.Comparator;

import com.joyveb.tlol.db.parser.DbConst;
import com.joyveb.tlol.role.RoleCard;

public final class TopLevel extends TopRatedCard {
	public static final TopLevel INSTANCE = new TopLevel();

	/**
	 * 构造函数
	 */
	private TopLevel() {
		super(DbConst.Top_Levels, "nlevel", "等级排行榜", new Comparator<RoleCard>() {
			@Override
			public int compare(final RoleCard card1, final RoleCard card2) {
				if(card2.getLevel() > card1.getLevel()) {
					return 1;
				}else if(card2.getLevel() < card1.getLevel()) {
					return -1;
				}else if(card2.getExp() > card1.getExp()) {
					return 1;
				}else if(card2.getExp() < card1.getExp()) {
					return -1;
				}else {
					return 0;
				}
			}
		});
	}

	@Override
	public String getDescribe(final int index, final RoleCard card) {
		return (index + 1) + ".  " + card.getName() + "  " + card.getLevel() + "级  " + card.getExp() + "经验";
	}

	@Override
	public void sendBulletin() {
		
	}

}
