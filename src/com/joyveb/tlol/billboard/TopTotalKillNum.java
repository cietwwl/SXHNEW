package com.joyveb.tlol.billboard;

import java.util.Comparator;

import com.joyveb.tlol.db.parser.DbConst;
import com.joyveb.tlol.role.RoleCard;
import com.joyveb.tlol.server.ServerMessage;

public final class TopTotalKillNum extends TopRatedCard {
	public static final TopTotalKillNum INSTANCE = new TopTotalKillNum();

	private TopTotalKillNum() {
		super(DbConst.Top_TotalKillNum, "nevil", "邪神榜", new Comparator<RoleCard>() {
			@Override
			public int compare(final RoleCard card1, final RoleCard card2) {
				return (int) (card2.getEvil() - card1.getEvil());
			}
		});
	}

	@Override
	public String getDescribe(final int index, final RoleCard card) {
		return (index + 1) + ".  " + card.getName() + "  罪恶值：" + card.getEvil();
	}

	public String getBulletinDescribe(final int index) {
		return "玩家【" + getItemName(index) + "】登上了邪神榜第" + (index + 1) + "位，各位玩家见到此人，请注意人身安全！";
	}

	@Override
	public void sendBulletin() {
		String message = null;
		if(topCards.size() == 1) {
			message = "邪神榜第一名为：" + getItemName(0) + "，他干净利落的手法令人防不胜防，请各位多加小心。";
		}else if(topCards.size() == 2) {
			message = "邪神榜前两名为：" + getItemName(0) + "、" + getItemName(1) + "，他们干净利落的手法令人防不胜防，请各位多加小心。";
		}else {
			message = "邪神榜前三名为：" + getItemName(0) + "、" + getItemName(1) + "、" + getItemName(2)
					+ "，他们干净利落的手法令人防不胜防，请各位多加小心。";
		}
		ServerMessage.sendBulletin(message);
	}

}
