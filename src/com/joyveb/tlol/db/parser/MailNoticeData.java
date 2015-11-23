package com.joyveb.tlol.db.parser;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

import com.joyveb.tlol.TianLongServer;
import com.joyveb.tlol.db.DataStruct;
import com.joyveb.tlol.db.PreSql;
import com.joyveb.tlol.mailnotice.MailNotice;
import com.joyveb.tlol.util.Log;

public class MailNoticeData extends DataStruct {

	private HashMap<Integer, MailNotice> bulletins = new HashMap<Integer, MailNotice>();

	@Override
	public final boolean readFromRs(final ResultSet resultSet)
			throws SQLException {
		while (resultSet.next()) {
			int id = resultSet.getInt(1);
			Date date = resultSet.getDate(2);
			String title = resultSet.getString(3);
			String content = resultSet.getString(4);

			Log.info(Log.STDOUT, "loadMailNotice", "邮件公告：" + content);
			bulletins.put(id, new MailNotice(id, date, title, content));
		}

		return true;
	}

	@Override
	public final PreSql getPreSql_query() {
		PreSql preSql = new PreSql();
		preSql.sqlstr = "select * from tbl_tianlong_mailnotice" + "_"
				+ TianLongServer.srvId;
		return preSql;
	}

	public HashMap<Integer, MailNotice> getBulletins() {
		return bulletins;
	}

	public void setBulletins(HashMap<Integer, MailNotice> bulletins) {
		this.bulletins = bulletins;
	}

}
