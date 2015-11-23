package com.joyveb.tlol.db.parser;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.joyveb.tlol.TianLongServer;
import com.joyveb.tlol.db.DataStruct;
import com.joyveb.tlol.db.PreSql;

public class LoginBulletinData extends DataStruct {
	private String bulletinContent;
	@Override
	public final boolean readFromRs(final ResultSet resultSet) throws SQLException {
		while (resultSet.next()) {
			bulletinContent = resultSet.getString(1);
		}

		return true;
	}
	
	@Override
	public final PreSql getPreSql_query() {
		PreSql preSql = new PreSql();
		preSql.sqlstr = "select loginbulletin from tbl_tianlong_lbulletin"+"_"
		+ TianLongServer.srvId;
		return preSql;
	}

	public String getBulletinContent() {
		return bulletinContent;
	}
}
