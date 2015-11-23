package com.joyveb.tlol.schedule;

import com.joyveb.tlol.LuaService;
import com.joyveb.tlol.billboard.TopRatedService;
import com.joyveb.tlol.util.Cardinality;

public enum OnTheHourSchedule implements ScheduleTask {
	INSTANCE;

	private int lastHour = -1;

	@Override
	public void execute() {
		int curHour = Cardinality.INSTANCE.getHour();
		if (curHour == lastHour)
			return;

		TopRatedService.INSTANCE.sendBulletin();

		LuaService.callLuaFunction("onTheHourSchedule", curHour);

		lastHour = curHour;
	}

	@Override
	public boolean isTimeOut() {
		return false;
	}
}
