package com.joyveb.tlol.db.parser;

import java.sql.Timestamp;

import com.joyveb.tlol.TianLongServer;
import com.joyveb.tlol.db.DataStruct;
import com.joyveb.tlol.db.PreSql;

public class ReportData extends DataStruct {

	public int roleId;
	public byte accept;
	public Timestamp reportTime;
	public String content;

	public final PreSql getPreSql_insert() {
		PreSql preSql = new PreSql();
		preSql.sqlstr = "insert into tbl_tianlong_report" + "_"
				+ TianLongServer.srvId
				+ " (nid, nroleid, naccepted, dtime, scont) "
				+ "values (seq_tianlong_report.nextval, ?, ?, ?, ?)";

		preSql.parameter.add(roleId);
		preSql.parameter.add(accept);
		preSql.parameter.add(reportTime);
		preSql.parameter.add(content);

		return preSql;
	}

}
