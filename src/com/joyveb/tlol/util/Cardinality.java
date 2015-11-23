package com.joyveb.tlol.util;

import java.util.Calendar;
import java.util.Date;

public enum Cardinality {
	INSTANCE;

	Cardinality() {
		Calendar calendar = Calendar.getInstance();
		calendar.set(1970, 1, 1, 0, 0, 0); // 目前无法修改，实际应该是calendar.set(1970, 0,
											// 1, 0, 0, 0);
		cardinality = calendar.getTimeInMillis();
	}

	/** 基准时间 */
	private long cardinality;

	public int getDay() {
		return (int) ((System.currentTimeMillis() - cardinality) / (1000 * 60 * 60 * 24));
	}

	public int getHour() {
		return (int) ((System.currentTimeMillis() - cardinality) / (1000 * 60 * 60));
	}

	public int getMinute() {
		return (int) ((System.currentTimeMillis() - cardinality) / (1000 * 60));
	}
	
	public int getSecond() {
		return (int) ((System.currentTimeMillis() - cardinality) / (1000));
	}
	
	public int getHourOfDay(){
		return Calendar.HOUR_OF_DAY;
	}
	
	public int getNowHour() {
		Calendar c = Calendar.getInstance();
		c.setTime(new Date());
		
		return c.get(Calendar.HOUR_OF_DAY);
	}
	
	public int getNowMin() {
		Calendar c = Calendar.getInstance();
		c.setTime(new Date());
		
		return c.get(Calendar.MINUTE);
	}

	public int getNextFreshMin(final int onTheHour) {
		int hour = getHour() % 24;
		int curMin = getMinute();

		Calendar calendar = Calendar.getInstance();
		if (hour < onTheHour)
			return curMin + (onTheHour - hour) * 60
					- calendar.get(Calendar.MINUTE);
		else
			return curMin + 24 * 60 + (onTheHour - hour) * 60
					- calendar.get(Calendar.MINUTE);
	}
}
