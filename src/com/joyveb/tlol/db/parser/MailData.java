package com.joyveb.tlol.db.parser;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import com.joyveb.tlol.TianLongServer;
import com.joyveb.tlol.db.DataStruct;
import com.joyveb.tlol.db.PreSql;
import com.joyveb.tlol.item.Item;
import com.joyveb.tlol.mail.Mail;
import com.joyveb.tlol.util.Log;

public class MailData extends DataStruct {

	private Mail mail = null;

	public MailData(final Mail mail) {
		this.mail = mail;
	}

	public MailData() {
		
	}

	@Override
	public final boolean readFromRs(final ResultSet rs) throws SQLException {
		mail = new Mail();
		try {
			mail.setAttachment(Item.readItem(rs.getString(8)));
		}catch(Exception e) {
			Log.error(Log.ERROR, "MailData.readFromRs", "邮件反序列化失败！", e);
		}
		
		mail.setMailId(rs.getLong(1));
		mail.setPayMail(rs.getBoolean(2));
		mail.setRead(rs.getBoolean(3));
		mail.setSenderId(rs.getInt(4));
		mail.setReceiverId(rs.getInt(5));
		mail.setSubject(rs.getString(6));
		mail.setContent(rs.getString(7));
		mail.setGold(rs.getInt(9));
		mail.setPayGold(rs.getInt(10));
		mail.setSendTime(rs.getTimestamp(11).getTime());
		mail.setLevel(rs.getByte(12));
		mail.setSenderName(rs.getString(13));
		return true;
	}

	@Override
	public final PreSql getPreSql_insert() {
		PreSql preSql = new PreSql();
		preSql.sqlstr = "insert into Tbl_Tianlong_Mail"
				+ "_"
				+ TianLongServer.srvId
				+ " (ID, IS_PAYMAIL, IS_READ, SENDER_ID, RECEIVER_ID, SUBJECT, CONTENT, ATTACHMENT, GOLD, PAY_GOLD, SEND_TIME, MAIL_LEVEL, SENDER_NAME) "
				+ "values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
		preSql.parameter.add(mail.getMailId());
		preSql.parameter.add(mail.isPayMail());
		preSql.parameter.add(mail.isRead());
		preSql.parameter.add(mail.getSenderId());
		preSql.parameter.add(mail.getReceiverId());
		preSql.parameter.add(mail.getSubject());
		preSql.parameter.add(mail.getContent());
		preSql.parameter.add(mail.getAttachment() == null ? "" : "#" + Mail.VERSION + mail.getAttachment().serialize());
		preSql.parameter.add(mail.getGold());
		preSql.parameter.add(mail.getPayGold());
		preSql.parameter.add(new Timestamp(mail.getSendTime()));
		preSql.parameter.add(mail.getLevel());
		preSql.parameter.add(mail.getSenderName());
		return preSql;
	}

	@Override
	public final PreSql getPreSql_update() {
		PreSql preSql = new PreSql();
		preSql.sqlstr = "update Tbl_Tianlong_Mail"
				+ "_"
				+ TianLongServer.srvId
				+ " t "
				+ "set IS_PAYMAIL = ?, IS_READ = ?, SENDER_ID = ?, RECEIVER_ID = ?, SUBJECT = ?, CONTENT = ?, ATTACHMENT = ?, GOLD = ?, PAY_GOLD = ?, MAIL_LEVEL = ?"
				+ "where t.id = ?";
		preSql.parameter.add(mail.isPayMail());
		preSql.parameter.add(mail.isRead());
		preSql.parameter.add(mail.getSenderId());
		preSql.parameter.add(mail.getReceiverId());
		preSql.parameter.add(mail.getSubject());
		preSql.parameter.add(mail.getContent());
		preSql.parameter.add(mail.getAttachment() == null ? "" : "#" + Mail.VERSION + mail.getAttachment().serialize());
		preSql.parameter.add(mail.getGold());
		preSql.parameter.add(mail.getPayGold());
		preSql.parameter.add(mail.getLevel());
		preSql.parameter.add(mail.getMailId());
		return preSql;
	}
	/**
	 * @return 返回delete语句
	 */
	@Override
	public final PreSql getPreSql_delete() {
		PreSql preSql = new PreSql();
		preSql.sqlstr = "delete from Tbl_Tianlong_Mail" + "_" + TianLongServer.srvId + " t where t.id = ?";
		preSql.parameter.add(mail.getMailId());
		return preSql;
	}

	public final Mail getMail() {
		return mail;
	}

	public final void setMail(final Mail mail) {
		this.mail = mail;
	}

}
