package com.joyveb.tlol.billboard;

import java.util.Comparator;

import com.joyveb.tlol.db.parser.DbConst;
import com.joyveb.tlol.role.RoleCard;

public final class TopMark extends TopRatedCard {

	public static final TopMark INSTANCE = new TopMark();
	/**
	 * 本类构造方法
	 */
	private TopMark() {
		super(DbConst.Top_Marks, "nmark", "积分排行榜", new Comparator<RoleCard>() {
			@Override
			public int compare(final RoleCard card1, final RoleCard card2) {
				return card2.getMark() - card1.getMark();
			}
		});
	}

	@Override
	public String getDescribe(final int index, final RoleCard card) {
		return (index + 1) + ".  " + card.getName() + "  " + card.getMark() + "分";
	}

	@Override
	public void sendBulletin() {
		
	}

}
