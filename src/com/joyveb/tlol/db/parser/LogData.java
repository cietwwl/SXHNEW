package com.joyveb.tlol.db.parser;

import java.sql.Timestamp;

import com.joyveb.tlol.TianLongServer;
import com.joyveb.tlol.db.DataStruct;
import com.joyveb.tlol.db.PreSql;

public class LogData extends DataStruct {

	public int roleId;
	public int userId;
	public Timestamp loginTime;
	public Timestamp logoutTime;

	public final PreSql getPreSql_insert() {
		PreSql preSql = new PreSql();
		preSql.sqlstr = "insert into tbl_tianlong_logging"
				+ "_"
				+ TianLongServer.srvId
				+ "(nid, nuid, nrid, dlogon, dlogoff) values(seq_tianlong_logging.nextval, ?, ?, ?, ?)";
		preSql.parameter.add(userId);
		preSql.parameter.add(roleId);
		preSql.parameter.add(loginTime);
		preSql.parameter.add(logoutTime);
		return preSql;
	}
}
