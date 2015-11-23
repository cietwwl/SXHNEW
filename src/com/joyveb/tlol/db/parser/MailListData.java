package com.joyveb.tlol.db.parser;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;

import com.joyveb.tlol.TianLongServer;
import com.joyveb.tlol.db.DataStruct;
import com.joyveb.tlol.db.PreSql;
import com.joyveb.tlol.mail.Mail;

public class MailListData extends DataStruct {

	private int receiverId;
	private String receiverName;
	private LinkedList<Mail> mailList = new LinkedList<Mail>();

	public final PreSql getPreSql_query() {
		PreSql preSql = new PreSql();
		preSql.sqlstr = "select t1.* from tbl_tianlong_mail" 
				+ "_" 
				+ TianLongServer.srvId
				+ " t1 where t1.receiver_id = ? order by t1.SEND_TIME desc";
//		preSql.sqlstr = "select t1.*, t2.snick from tbl_tianlong_mail"
//				+ "_"
//				+ TianLongServer.srvId
//				+ " t1, tbl_tianlong_role"
//				+ "_"
//				+ TianLongServer.srvId
//				+ " t2 "
//				+ "where t1.sender_id = t2.nid and t1.receiver_id = ? order by t1.SEND_TIME desc";
		preSql.parameter.add(receiverId);
		return preSql;
	}

	public MailListData(final int receiverId, final String receiverName) {
		this.receiverId = receiverId;
		this.receiverName = receiverName;
	}

	@Override
	public final boolean readFromRs(final ResultSet rs) throws SQLException {
		while (rs.next()) {
			MailData mailData = new MailData();
			mailData.readFromRs(rs);
			mailData.getMail().setReceiverName(receiverName);
			mailList.add(mailData.getMail());
		}
		return true;
	}

	public final int getReceiverId() {
		return receiverId;
	}

	public final void setReveiverId(final int receiverId) {
		this.receiverId = receiverId;
	}

	public final String getReceiverName() {
		return receiverName;
	}

	public final void setReceiverName(final String receiverName) {
		this.receiverName = receiverName;
	}

	public final LinkedList<Mail> getMailList() {
		return mailList;
	}

	public final void setMailList(final LinkedList<Mail> mailList) {
		this.mailList = mailList;
	}

}
