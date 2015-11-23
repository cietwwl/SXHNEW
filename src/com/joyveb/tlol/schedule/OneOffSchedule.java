package com.joyveb.tlol.schedule;

public abstract class OneOffSchedule implements ScheduleTask {

	@Override
	public abstract void execute();

	@Override
	public final boolean isTimeOut() {
		return true;
	}
}
