package com.joyveb.tlol.mailnotice;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import com.joyveb.tlol.OnlineService;
import com.joyveb.tlol.mail.MailManager;
import com.joyveb.tlol.schedule.ScheduleManager;

public final class MailNoticeManager {

	private static MailNoticeManager noticeManager = new MailNoticeManager();
	private HashMap<MailNotice, HashSet<Integer>> noticeMap = new HashMap<MailNotice, HashSet<Integer>>();//邮件公告列表
	private static Long CHECK_DATE = 1000L * 60 * 60 * 24 * 30;//邮件公告有效时间

	public static MailNoticeManager getInstance() {
		return noticeManager;
	}

	public HashMap<MailNotice, HashSet<Integer>> getNoticeMap() {
		return noticeMap;
	}

	private MailNoticeManager() {
	}

	
	/**
	 * @function 发送邮件公告
	 * @author SunHL
	 * @param id
	 * @param date
	 * @param title
	 * @param content
	 * @date 2011-10-25
	 */
	public void sendMailToAllRoles(int id, Date date, String title, String content) {
		MailNotice mailNotice = new MailNotice(id, date, title, content);
		this.sendMailToAllRoles(mailNotice);
	}

	/**
	 * @function 发送邮件公告
	 * @author SunHL
	 * @param mailNotice
	 * @date 2011-10-25
	 */
	public void sendMailToAllRoles(MailNotice mailNotice) {

		HashSet<Integer> hashSet = new HashSet<Integer>();

		ArrayList<Integer> onlines = OnlineService.getAllOnlines();
		for (int roleid : onlines) {
			hashSet.add(roleid);
			MailManager.getInstance().sendSysMail(roleid,
					mailNotice.getTitle(), mailNotice.getContent(), 0, null);
		}

		MailNoticeManager.getInstance().getNoticeMap().put(mailNotice, hashSet);

	}

	/**
	 * @function 检查邮件公告是否过期
	 * @author SunHL
	 * @date 2011-10-25
	 */
	public void checkMailTimeOut() {

		Set<MailNotice> set = MailNoticeManager.getInstance().getNoticeMap()
				.keySet();
		if (!set.isEmpty()) {
			for (MailNotice notice : set) {
				if (notice.getDate().before(
						new Date((new Date()).getTime() - CHECK_DATE))) {
					MailNoticeManager.getInstance().getNoticeMap()
							.remove(notice);
				}
			}
		}
	}

	/**
	 * @function 按id删除邮件公告
	 * @author SunHL
	 * @param id
	 * @date 2011-10-25
	 */
	public void removeMail(int id) {

		HashMap<MailNotice, HashSet<Integer>> hashMap = MailNoticeManager
				.getInstance().getNoticeMap();
		Set<MailNotice> set = hashMap.keySet();
		if (!set.isEmpty()) {
			for (MailNotice notice : set) {
				if (notice.getId() == id) {
					ScheduleManager.INSTANCE.getScheduleQueue().add(notice);
				}
			}
		}
	}

	/**
	 * @function 检查该角色是否发送邮件了邮件公告
	 * @author SunHL
	 * @param roleid
	 * @date 2011-10-25
	 */
	public void checkMailSend(int roleid) {

		Set<MailNotice> set = MailNoticeManager.getInstance().getNoticeMap()
				.keySet();
		if (!set.isEmpty()) {
			for (MailNotice notice : set) {
				HashSet<Integer> hashSet = MailNoticeManager.getInstance()
						.getNoticeMap().get(notice);
				if (!hashSet.contains(roleid)) {
					hashSet.add(roleid);
					MailManager.getInstance().sendSysMail(roleid,
							notice.getTitle(), notice.getContent(), 0, null);
				}
			}
		}
	}
}
