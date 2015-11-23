package com.joyveb.tlol.db.parser;

import com.joyveb.tlol.TianLongServer;
import com.joyveb.tlol.db.DataStruct;
import com.joyveb.tlol.db.PreSql;

public class MailDelOnRoleDel extends DataStruct {
	private int deletedRoleId = 0;

	@Override
	public final PreSql getPreSql_delete() {
		PreSql preSql = new PreSql();
		preSql.sqlstr = "delete TBL_TIANLONG_MAIL_" + TianLongServer.srvId
				+ " where RECEIVER_ID = ? and IS_PAYMAIL = 0";
		preSql.parameter.add(deletedRoleId);
		return preSql;
	}

	public final void setDeletedRoleId(final int deletedRoleId) {
		this.deletedRoleId = deletedRoleId;
	}

}
