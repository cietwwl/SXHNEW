package com.joyveb.tlol.trigger;

import com.joyveb.tlol.OnlineService;
import com.joyveb.tlol.billboard.TopCharm;
import com.joyveb.tlol.billboard.TopGold;
import com.joyveb.tlol.billboard.TopHonor;
import com.joyveb.tlol.billboard.TopLevel;
import com.joyveb.tlol.billboard.TopMark;
import com.joyveb.tlol.billboard.TopTotalKillNum;
import com.joyveb.tlol.role.RoleBean;
import com.joyveb.tlol.schedule.OneOffSchedule;
import com.joyveb.tlol.schedule.ScheduleManager;

/** 角色基本事件处理 */
public enum RoleEvent implements RoleEventHandler {
	/** 上线事件，需要保证首先检查名片是否存在 */
	OnlineEvent(CardOnlineCheck.INSTANCE, GangOnlineCheck.INSTANCE),
	/** 声望变化事件 */
	CharmEvent(TopCharm.INSTANCE),
	/** 等级变化事件 */
	LevelEvent(TopLevel.INSTANCE, UpgradeTrigger.INSTANCE),
	/** 经验变化事件 */
	ExpEvent(TopLevel.INSTANCE),
	/** 金币变化事件 */
	GoldEvent(TopGold.INSTANCE),
	/** 积分变化事件 */
	MarkEvent(TopMark.INSTANCE),
	/** 总击杀数变化事件 */
	TotalKillNumEvent(TopTotalKillNum.INSTANCE),
	/** 总荣誉值变化事件 */
	TotalHonor(TopHonor.INSTANCE);

	/** 固定的事件处理器 */
	private RoleEventHandler[] handlers;

	/**
	 * @param handlers 事件处理器
	 */
	private RoleEvent(final RoleEventHandler... handlers) {
		this.handlers = handlers;
	}
	

	@Override
	public void handleEvent(final RoleBean source, final Object... args) {
		if (handlers == null)
			return;

		final int roleid = source.getRoleid();

		ScheduleManager.INSTANCE.offerTask(new OneOffSchedule() {

			@Override
			public void execute() {
				if (OnlineService.getOnline(roleid) != null)
					for (RoleEventHandler handler : handlers)
						handler.handleEvent(OnlineService.getOnline(roleid),
								args);
			}
		});

	}


	@Override
	public void removeEvent(RoleBean source) {
		if (OnlineService.getOnline(source.getRoleid()) != null)
			for (RoleEventHandler handler : handlers)
				handler.removeEvent((OnlineService.getOnline(source.getRoleid())));

	}

}
