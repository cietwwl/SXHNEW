package com.joyveb.tlol.schedule;

import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.joyveb.tlol.util.Log;

/**
 * 系统计划任务
 * 
 * @author Sid
 * 
 */
public enum ScheduleManager {
	INSTANCE();

	private ConcurrentLinkedQueue<ScheduleTask> scheduleQueue = new ConcurrentLinkedQueue<ScheduleTask>();

	private ScheduleManager() {
		scheduleQueue.offer(DaybreakSchedule.INSTANCE);
		scheduleQueue.offer(OnTheHourSchedule.INSTANCE);
		scheduleQueue.offer(OnTheMinuteSchedule.INSTANCE);
		scheduleQueue.offer(OnTheSeconedSchedule.INSTANCE);
		scheduleQueue.offer(OnTheHourOfDaySchedule.INSTANCE);
	}

	public void offerTask(final ScheduleTask task) {
		if (task == null)
			Log.error(Log.ERROR, "错误的计划任务！");
		else
			scheduleQueue.offer(task);
	}

	public void execute() {
		Iterator<ScheduleTask> iterator = scheduleQueue.iterator();
		while (iterator.hasNext()) {
			ScheduleTask executable = iterator.next();
			try {
				executable.execute();
			} catch (Exception e) {
				Log.error(Log.ERROR, "ScheduleManager", e);
			}

			if (executable.isTimeOut())
				iterator.remove();
		}
	}

	public ConcurrentLinkedQueue<ScheduleTask> getScheduleQueue() {
		return scheduleQueue;
	}
}
