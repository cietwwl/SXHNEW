package com.joyveb.tlol.db.parser;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

import com.joyveb.tlol.TianLongServer;
import com.joyveb.tlol.db.DataStruct;
import com.joyveb.tlol.db.PreSql;
import com.joyveb.tlol.schedule.ScheduleBulletin;
import com.joyveb.tlol.util.Log;

public class BulletinData extends DataStruct {
	private HashMap<Integer, ScheduleBulletin> bulletins = new HashMap<Integer, ScheduleBulletin>();

	@Override
	public final boolean readFromRs(final ResultSet resultSet) throws SQLException {
		while (resultSet.next()) {
			int id = resultSet.getInt(1);
			long start = resultSet.getTimestamp(2).getTime();
			long end = resultSet.getTimestamp(3).getTime();
			int interval = resultSet.getInt(4);
			String bulletin = resultSet.getString(5);

			long curTime = System.currentTimeMillis();
			if (curTime > end)
				continue;

			Log.info(Log.STDOUT, "loadBulletin", "公告：" + bulletin);
			bulletins.put(id, new ScheduleBulletin(id, start, end, interval,
					bulletin));
		}

		return true;
	}

	@Override
	public final PreSql getPreSql_query() {
		PreSql preSql = new PreSql();
		preSql.sqlstr = "select * from tbl_tianlong_notice" + "_"
				+ TianLongServer.srvId;
		return preSql;
	}

	public final void setBulletins(final HashMap<Integer, ScheduleBulletin> bulletins) {
		this.bulletins = bulletins;
	}

	public final HashMap<Integer, ScheduleBulletin> getBulletins() {
		return bulletins;
	}
}
