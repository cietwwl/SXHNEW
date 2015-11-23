package com.joyveb.tlol.db.parser;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;

import com.joyveb.tlol.TianLongServer;
import com.joyveb.tlol.db.DataStruct;
import com.joyveb.tlol.db.PreSql;
import com.joyveb.tlol.gang.Gang;
import com.joyveb.tlol.role.RoleCard;
import com.joyveb.tlol.util.JackSON;

public class GangCreator extends DataStruct {
	private String gangName;

	private PreSql preSql = new PreSql();

	public GangCreator(final Gang gang) {
		preSql.sqlstr = "insert into tbl_tianlong_gang_" + TianLongServer.srvId
				+ " (nid, sname, dcreated, nlevel, sbulletin, ntribute, "
				+ "	nleader, sviceleader, spresbyter, smembers, STRIBUTESTAT) "
				+ "values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
		preSql.parameter.add(gang.getId());
		preSql.parameter.add(gang.getName());
		preSql.parameter.add(new Timestamp(gang.getCreated().getTime()));
		preSql.parameter.add(gang.getLevel());
		preSql.parameter.add(gang.getBulletin());
		preSql.parameter.add(gang.getTribute());
		preSql.parameter.add(gang.getLeader());
		try {
			preSql.parameter.add(JackSON.INSTANCE.writeValueAsString(gang
					.getViceLeader()));
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			preSql.parameter.add(JackSON.INSTANCE.writeValueAsString(gang
					.getPresbyter()));
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			ArrayList<Integer> memids = new ArrayList<Integer>();
			for (RoleCard card : gang.getMembers())
				memids.add(card.getRoleid());

			preSql.parameter.add(JackSON.INSTANCE.writeValueAsString(memids));
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			preSql.parameter.add(JackSON.INSTANCE.writeValueAsString(gang
					.getTributeStat()));
		} catch (IOException e) {
			e.printStackTrace();
		}

		this.gangName = gang.getName();
	}
	/**
	 * @return 返回insert语句
	 */
	public final PreSql getPreSql_insert() {
		return preSql;
	}

	public final void setGangName(final String gangName) {
		this.gangName = gangName;
	}

	public final String getGangName() {
		return gangName;
	}

}
