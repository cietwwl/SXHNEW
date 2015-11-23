package com.joyveb.tlol.db.parser;

import com.joyveb.tlol.TianLongServer;
import com.joyveb.tlol.db.DataStruct;
import com.joyveb.tlol.db.PreSql;

public class ReturnMailOnRoleDelData extends DataStruct {
	private int deletedRoleId = 0;

	@Override
	public final PreSql getPreSql_update() {
		PreSql preSql = new PreSql();
		preSql.sqlstr = "update TBL_TIANLONG_MAIL_"
				+ TianLongServer.srvId
				+ " set RECEIVER_ID = SENDER_ID, IS_PAYMAIL = 0, IS_READ = 0, SENDER_ID = 1 where RECEIVER_ID = ? and IS_PAYMAIL = 1";
		preSql.parameter.add(deletedRoleId);
		return preSql;
	}

	public final void setDeletedRoleId(final int deletedRoleId) {
		this.deletedRoleId = deletedRoleId;
	}

}
