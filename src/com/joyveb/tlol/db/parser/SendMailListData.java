package com.joyveb.tlol.db.parser;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import com.joyveb.tlol.TianLongServer;
import com.joyveb.tlol.db.DataStruct;
import com.joyveb.tlol.db.PreSql;
import com.joyveb.tlol.item.Item;
import com.joyveb.tlol.mail.Mail;
import com.joyveb.tlol.util.Log;

public class SendMailListData extends DataStruct {

	private int senderId;
	private String senderName;
	private LinkedList<Mail> sendMailList = new LinkedList<Mail>();

	public final PreSql getPreSql_query() {
		PreSql preSql = new PreSql();
		preSql.sqlstr = "select t1.*, t2.snick from tbl_tianlong_mail_" +
				TianLongServer.srvId +
				" t1, tbl_tianlong_role_" + TianLongServer.srvId
				+ " t2 where t1.receiver_id = t2.nid and t1.is_paymail = '1'  and t1.sender_id = ?";
		preSql.parameter.add(senderId);
		return preSql;
	}

	public SendMailListData(final int senderId, final String receiverName) {
		this.senderId = senderId;
		this.senderName = receiverName;
	}

	@Override
	public final boolean readFromRs(final ResultSet rs) throws SQLException {
		while (rs.next()) {
			Mail mail = new Mail();
			mail.setMailId(rs.getLong(1));
			mail.setPayMail(rs.getBoolean(2));
			mail.setRead(rs.getBoolean(3));
			mail.setSenderId(rs.getInt(4));
			mail.setReceiverId(rs.getInt(5));
			mail.setSubject(rs.getString(6));
			mail.setContent(rs.getString(7));
			try {
				mail.setAttachment(Item.readItem(rs.getString(8)));
			}catch(Exception e) {
				Log.error(Log.ERROR, "SendMailListData.readFromRs", "邮件反序列化失败！", e);
			}
			mail.setGold(rs.getInt(9));
			mail.setPayGold(rs.getInt(10));
			mail.setSendTime(rs.getTimestamp(11).getTime());
			mail.setLevel(rs.getByte(12));
			mail.setReceiverName(rs.getString(13));
			sendMailList.add(mail);

		}
		return true;
	}

	public final int getSenderId() {
		return senderId;
	}

	public final void setSenderId(final int senderId) {
		this.senderId = senderId;
	}

	public final LinkedList<Mail> getSendMailList() {
		return sendMailList;
	}

	public final void setSendMailList(final LinkedList<Mail> sendMailList) {
		this.sendMailList = sendMailList;
	}

	public String getSenderName() {
		return senderName;
	}

	public void setSenderName(String senderName) {
		this.senderName = senderName;
	}
	
}
