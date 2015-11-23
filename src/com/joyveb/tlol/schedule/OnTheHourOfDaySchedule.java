package com.joyveb.tlol.schedule;

import java.util.ArrayList;
import java.util.Date;

import com.joyveb.tlol.OnlineService;
import com.joyveb.tlol.role.RoleBean;
import com.joyveb.tlol.util.Cardinality;
import com.joyveb.tlol.util.Log;

public enum OnTheHourOfDaySchedule implements ScheduleTask {
	INSTANCE;

	@Override
	public void execute() {
		boolean isRefresh = false;
		int curHour = Cardinality.INSTANCE.getHourOfDay();
		if (curHour == 4 && isRefresh == false){
			try {
				ArrayList<Integer> onlines = OnlineService.getAllOnlines();
				Date date = new Date();
				for(int roleid : onlines) {
					RoleBean role = OnlineService.getOnline(roleid);
					role.setLastBattleTime(date);
					role.setSneakAttackNum(0);
				}
			}catch(Exception e){
				Log.error(Log.ERROR, e);
			}
			isRefresh = true;
		}else if(curHour != 4){
			isRefresh = false;
		}
	}

	@Override
	public boolean isTimeOut() {
		return false;
	}

}
