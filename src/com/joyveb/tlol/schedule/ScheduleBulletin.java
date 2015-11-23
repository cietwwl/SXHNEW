package com.joyveb.tlol.schedule;

import com.joyveb.tlol.server.ServerMessage;

/**
 * 系统定期自动公告
 */
public class ScheduleBulletin implements ScheduleTask {

	private int id;

	private long start;
	private long end;
	private long last;
	/** 两次公告间隔时间，单位为分钟 */
	private int interval;
	/** 公告内容 */
	private String bulletin;

	public ScheduleBulletin(final int id, final long start, final long end, final int interval,
			final String bulletin) {
		this.id = id;
		this.start = start;
		this.end = end;
		this.interval = interval;
		this.setBulletin(bulletin);
	}

	@Override
	public final void execute() {
		long curTime = System.currentTimeMillis(); // 当前时间

		if (curTime < start || curTime >= end)
			return;

		if (curTime - last < (long) interval * 1000 * 60)
			return;

		ServerMessage.sendBulletin(bulletin);

		last = curTime;
	}

	@Override
	public final boolean isTimeOut() {
		return System.currentTimeMillis() >= end;
	}

	public final void setId(final int id) {
		this.id = id;
	}

	public final int getId() {
		return id;
	}

	public final void setStart(final long start) {
		this.start = start;
	}

	public final long getStart() {
		return start;
	}

	public final void setEnd(final long end) {
		this.end = end;
	}

	public final long getEnd() {
		return end;
	}

	public final void setLast(final long last) {
		this.last = last;
	}

	public final long getLast() {
		return last;
	}

	public final void setInterval(final int interval) {
		this.interval = interval;
	}

	public final int getInterval() {
		return interval;
	}

	public final void setBulletin(final String bulletin) {
		this.bulletin = bulletin;
	}

	public final String getBulletin() {
		return bulletin;
	}

}
