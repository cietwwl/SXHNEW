package com.joyveb.tlol.schedule;

import com.joyveb.tlol.LuaService;
import com.joyveb.tlol.mailnotice.MailNoticeManager;
import com.joyveb.tlol.util.Cardinality;

public enum DaybreakSchedule implements ScheduleTask {
	INSTANCE;

	private int lastDay = -1;

	@Override
	public void execute() {
		int curDay = Cardinality.INSTANCE.getDay();
		if (curDay == lastDay)
			return;

		LuaService.callLuaFunction("daybreakSchedule", curDay);

		LuaService.resetStat();
		
		//检查邮件公告是否过期
		MailNoticeManager.getInstance().checkMailTimeOut();

		lastDay = curDay;
	}

	@Override
	public boolean isTimeOut() {
		return false;
	}

}
