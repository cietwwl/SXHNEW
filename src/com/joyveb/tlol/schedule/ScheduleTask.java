package com.joyveb.tlol.schedule;

public interface ScheduleTask {
	void execute();

	boolean isTimeOut();
}
