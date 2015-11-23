package com.joyveb.tlol.mailnotice;

import java.util.Date;

import com.joyveb.tlol.schedule.ScheduleTask;

public class MailNotice implements ScheduleTask {

	private int id;
	private Date date;
	private String title;
	private String content;

	public MailNotice(final int id, final Date date, final String title,
			final String content) {
		this.id = id;
		this.title = title;
		this.content = content;
		this.date = date;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	@Override
	public void execute() {
		MailNoticeManager.getInstance().getNoticeMap().remove(this);
	}

	@Override
	public boolean isTimeOut() {
		return false;
	}

}
