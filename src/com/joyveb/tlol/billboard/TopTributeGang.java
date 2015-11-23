package com.joyveb.tlol.billboard;

import java.util.Collections;
import java.util.Comparator;

import com.joyveb.tlol.db.DataStruct;
import com.joyveb.tlol.db.parser.CommonParser;
import com.joyveb.tlol.db.parser.DbConst;
import com.joyveb.tlol.db.parser.TopTributeGangData;
import com.joyveb.tlol.gang.GangCard;
import com.joyveb.tlol.util.Log;

public class TopTributeGang extends TopRated<GangCard> {

	public static final TopTributeGang INSTANCE = new TopTributeGang();
	/**
	 * 本类构造方法
	 */
	public TopTributeGang() {
		super("帮派排行榜", new Comparator<GangCard>() {
			@Override
			public int compare(final GangCard card1, final GangCard card2) {
				return card2.getTribute() - card1.getTribute();
			}
		});
	}

	@Override
	public final void handle(final DbConst eventID, final boolean flag, final DataStruct ds) {
		TopTributeGangData topTributeGangData = (TopTributeGangData) ds;
		topCards.addAll(topTributeGangData.getCards());
		this.loaded = true;
		
		Log.info(Log.STDOUT, "TopRated", "加载" + topRatedName + "完成");
	}

	@Override
	public void sendBulletin() {
		// ServerMessage.sendBulletin("恭喜" + getItemName(0) + "、" +
		// getItemName(1) + "、" + getItemName(2) +
		// "荣登" + topRatedName + "前三甲！！！");
	}

	@Override
	public final void loadTopRated() {
		CommonParser.getInstance().postTask(DbConst.Top_TributeGangs, this, new TopTributeGangData());
	}

	public final void update(final GangCard card) {
		if(topCards.isEmpty()) {
			topCards.add(card.clone());
			// final String bulletin = "恭喜【" + this.getItemName(0) + "】荣登" +
			// this.topRatedName + "第1位";
			//
			// ScheduleManager.INSTANCE.offerTask(new OneOffSchedule() {
			// public void execute() {
			// ServerMessage.sendBulletin(bulletin);
			// }
			// });

			return;
		}

		int rawindex = this.getRawIndex(card);
		boolean newComer = (rawindex == topCards.size());

		if(newComer && topCards.size() == max && comparator.compare(card, topCards.get(topCards.size() - 1)) >= 0) {
			return;
		}
		if(newComer) {
			topCards.add(card.clone());
		}else {
			topCards.get(rawindex).copy(card);
		}
		Collections.sort(topCards, comparator);

		// int index = this.getRawIndex(card);

		if(newComer && topCards.size() > max) {
			topCards.remove(topCards.size() - 1);
		}

		// if(index == rawindex || Math.min(index, rawindex) > 2)
		// //未更新（包括没挤进排行榜或排名未变）或没进前三
		// return;
		// else {
		// final String bulletin = index < rawindex ? //排名是否上升
		// "恭喜【" + getItemName(index) + "】荣登" + this.topRatedName+ "第" + (index
		// + 1) + "位" :
		// "恭喜【" + getItemName(rawindex) + "】荣登" + this.topRatedName+ "第" +
		// (rawindex + 1) + "位";
		//
		//
		// ScheduleManager.INSTANCE.offerTask(new OneOffSchedule() {
		// public void execute() {
		// ServerMessage.sendBulletin(bulletin);
		// }
		// });
		// }
	}

	@Override
	public final String getDescribe(final int index, final GangCard card) {
		return (index + 1) + ". " + card.getName() + " 帮贡" + card.getTribute();
	}

}
