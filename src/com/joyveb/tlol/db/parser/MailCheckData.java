package com.joyveb.tlol.db.parser;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.joyveb.tlol.TianLongServer;
import com.joyveb.tlol.db.DataStruct;
import com.joyveb.tlol.db.PreSql;

public class MailCheckData extends DataStruct {

	private String receiverName;
	private int receiverId;
	private int receiverMailBoxNum;
	private short zoneid;

	public MailCheckData(final short zoneid) {
//		this.zoneid = zoneid;
	}

	@Override
	public final boolean readFromRs(final ResultSet rs) throws SQLException {
		if (rs.next()) {
			receiverId = rs.getInt(1);
			receiverMailBoxNum = rs.getInt(2);
		}
		return true;
	}

	public final PreSql getPreSql_query() {
		PreSql preSql = new PreSql();

		preSql.sqlstr = "select (select nid from tbl_tianlong_role" + "_"
				+ TianLongServer.srvId + " where SNICK = ?  and SRACINGMARK=0) " + "as nid, "
				+ "(select count(*) from tbl_tianlong_mail" + "_"
				+ TianLongServer.srvId + " where RECEIVER_ID = "
				+ "(select nid from tbl_tianlong_role" + "_"
				+ TianLongServer.srvId + " where SNICK = ? and SRACINGMARK=0 )" + ") as count from dual";
		preSql.parameter.add(receiverName);
		preSql.parameter.add(receiverName);
		return preSql;
	}

	public final String getReceiverName() {
		return receiverName;
	}

	public final void setReceiverName(final String receiverName) {
		this.receiverName = receiverName;
	}

	public final int getReceiverId() {
		return receiverId;
	}

	public final void setReceiverId(final int receiverId) {
		this.receiverId = receiverId;
	}

	public final int getReceiverMailBoxNum() {
		return receiverMailBoxNum;
	}

	public final void setReceiverMailBoxNum(final int receiverMailBoxNum) {
		this.receiverMailBoxNum = receiverMailBoxNum;
	}

	public short getZoneid() {
		return zoneid;
	}

	public void setZoneid(short zoneid) {
		this.zoneid = zoneid;
	}

}
