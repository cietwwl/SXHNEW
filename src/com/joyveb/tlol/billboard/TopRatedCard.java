package com.joyveb.tlol.billboard;

import java.util.Collections;
import java.util.Comparator;

import com.joyveb.tlol.db.DataStruct;
import com.joyveb.tlol.db.parser.CommonParser;
import com.joyveb.tlol.db.parser.DbConst;
import com.joyveb.tlol.db.parser.TopRoleCardData;
import com.joyveb.tlol.role.RoleBean;
import com.joyveb.tlol.role.RoleCard;
import com.joyveb.tlol.role.RoleCardService;
import com.joyveb.tlol.schedule.Broadcast;
import com.joyveb.tlol.trigger.RoleEventHandler;
import com.joyveb.tlol.util.Log;

public abstract class TopRatedCard extends TopRated<RoleCard> implements RoleEventHandler {
	private DbConst dbconst;
	private String field;

	public TopRatedCard(final DbConst dbconst, final String field, final String topRatedName,
			final Comparator<RoleCard> comparator) {
		super(topRatedName, comparator);

		this.dbconst = dbconst;
		this.field = field;
	}
	
	@Override
	public final void handleEvent(final RoleBean source, final Object... args) {
		RoleCard element = RoleCardService.INSTANCE.synchronous(source);

		if(topCards.isEmpty()) {
			topCards.add(element);

			Broadcast.send(getBulletinDescribe(0));

			return;
		}

		final int rawindex = this.getRawIndex(element);
		boolean newComer = (rawindex == topCards.size());

		if(newComer && topCards.size() == max && comparator.compare(element, topCards.get(topCards.size() - 1)) >= 0)
			return;

		if(newComer)
			topCards.add(element);

		Collections.sort(topCards, comparator);

		final int index = this.getRawIndex(element);

		if(newComer && topCards.size() > max)
			topCards.remove(topCards.size() - 1);

		if(index == rawindex || Math.min(index, rawindex) > 2) // 未更新（包括没挤进排行榜或排名未变）或没进前三
			return;
		else 
			Broadcast.send(getBulletinDescribe(index < rawindex ? index : rawindex));
	}

	
	@Override
	public final void removeEvent(final RoleBean source) {
		RoleCard element = RoleCardService.INSTANCE.synchronous(source);
		try {
			topCards.remove(element);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	
	public String getBulletinDescribe(final int index) {
		return "恭喜【" + getItemName(index) + "】荣登" + this.topRatedName + "第" + (index + 1) + "位";
	}

	@Override
	public final void loadTopRated() {
		CommonParser.getInstance().postTask(dbconst, this, new TopRoleCardData(this), true);
	}

	@Override
	public final void handle(final DbConst eventID, final boolean flag, final DataStruct ds) {
		TopRoleCardData topMarkData = (TopRoleCardData) ds;
		topCards.addAll(topMarkData.getCards());
		this.loaded = true;
		
		Log.info(Log.STDOUT, "TopRated", "加载" + topRatedName + "完成");
	}

	public final void setField(final String field) {
		this.field = field;
	}

	public final String getField() {
		return field;
	}

}
