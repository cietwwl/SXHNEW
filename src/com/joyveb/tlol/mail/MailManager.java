package com.joyveb.tlol.mail;

import com.joyveb.tlol.LuaService;
import com.joyveb.tlol.OnlineService;
import com.joyveb.tlol.db.parser.CommonParser;
import com.joyveb.tlol.db.parser.DbConst;
import com.joyveb.tlol.db.parser.MailData;
import com.joyveb.tlol.item.Item;
import com.joyveb.tlol.role.RoleBean;

public final class MailManager {
	private static MailManager mailManager = new MailManager();

	public static MailManager getInstance() {
		return mailManager;
	}

	private MailManager() {
	}

	public void sendSysMail(final int playerId, final String subject, final String content,
			final int gold, final Item attach) {
		Mail mail = new Mail(1, playerId, "系统", false, subject, content,
				attach, gold, 0, Mail.LV_SYS);
		RoleBean role = OnlineService.getOnline(playerId);

		if (role != null) {
			role.getMailAgent().addNewMail(mail);
			role.getMailAgent().sendMailNotify(role, Mail.STATE_NEW);
		}

		MailData mailData = new MailData(mail);
		CommonParser.getInstance().postTask(DbConst.MAIL_SEND, null, mailData);
	}

	public void sendSysMail(final int playerId, final int gold, final Item attach) {
		sendSysMail(playerId, "系统邮件", "请查收！", gold, attach);
	}

	public void send_GM_Mail(final int playerId, final String subject, final String content,
			final int gold, final int tid) {
		Item item = null;
		if (LuaService.getBool(Item.LUA_CONTAINER, tid)) 
			item = LuaService.callOO4Object(2, Item.LUA_CONTAINER, tid, "creatJavaItemSingle");

		Mail mail = new Mail(2, playerId, "GM", false, subject, content, item,
				gold < 0 ? 0 : gold, 0, Mail.LV_SYS);
		RoleBean role = OnlineService.getOnline(playerId);

		if (role != null) {
			role.getMailAgent().addNewMail(mail);
			role.getMailAgent().sendMailNotify(role, Mail.STATE_NEW);
		}

		MailData mailData = new MailData(mail);
		CommonParser.getInstance().postTask(DbConst.MAIL_SEND, null, mailData);
	}
	public void send_GM_Mail_Num(final int playerId, final String subject, final String content,
			final int gold, final int tid,final int num) {
		Item item = null;
		if (LuaService.getBool(Item.LUA_CONTAINER, tid)) 
			item = LuaService.callOO4Object(2, Item.LUA_CONTAINER, tid, "creatJavaItemSingle",num);

		Mail mail = new Mail(2, playerId, "GM", false, subject, content, item,
				gold < 0 ? 0 : gold, 0, Mail.LV_SYS);
		RoleBean role = OnlineService.getOnline(playerId);

		if (role != null) {
			role.getMailAgent().addNewMail(mail);
			role.getMailAgent().sendMailNotify(role, Mail.STATE_NEW);
		}

		MailData mailData = new MailData(mail);
		CommonParser.getInstance().postTask(DbConst.MAIL_SEND, null, mailData);
	}
}
