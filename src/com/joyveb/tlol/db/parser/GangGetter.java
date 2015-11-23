package com.joyveb.tlol.db.parser;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.codehaus.jackson.type.TypeReference;

import com.joyveb.tlol.TianLongServer;
import com.joyveb.tlol.db.DataStruct;
import com.joyveb.tlol.db.PreSql;
import com.joyveb.tlol.gang.Gang;
import com.joyveb.tlol.util.JackSON;

public class GangGetter extends DataStruct {

	private List<Gang> gangs = new ArrayList<Gang>();

	public GangGetter() {
	}

	@Override
	public boolean readFromRs(ResultSet resultSet) throws SQLException {
		while(resultSet.next()) {
			Gang gang = new Gang();

			gang.setId(resultSet.getLong(1)).setName(resultSet.getString(2)).setCreated(resultSet.getTimestamp(3))
					.setLevel(resultSet.getInt(4)).setBulletin(resultSet.getString(5)).setTribute(resultSet.getInt(6))
					.setLeader(resultSet.getInt(7));

			String sviceleader = resultSet.getString(8);
			if(sviceleader != null) {
				try {
					HashSet<Integer> viceLeader = JackSON.INSTANCE.readValue(sviceleader,
							new TypeReference<HashSet<Integer>>() {});
					gang.setViceLeader(viceLeader);
				}catch(IOException e) {
					e.printStackTrace();
				}
			}

			String spresbyter = resultSet.getString(9);
			if(spresbyter != null) {
				try {
					HashSet<Integer> presbyter = JackSON.INSTANCE.readValue(spresbyter,
							new TypeReference<HashSet<Integer>>() {});
					gang.setPresbyter(presbyter);
				}catch(IOException e) {
					e.printStackTrace();
				}
			}

			String smembers = resultSet.getString(10);
			if(smembers != null) {
				try {
					ArrayList<Integer> members = JackSON.INSTANCE.readValue(smembers,
							new TypeReference<ArrayList<Integer>>() {});
					gang.loadCard(members);
				}catch(IOException e) {
					e.printStackTrace();
				}
			}

			String stributestat = resultSet.getString(11);
			if(sviceleader != null) {
				try {
					HashMap<Integer, Integer> tributestat = JackSON.INSTANCE.readValue(stributestat,
							new TypeReference<HashMap<Integer, Integer>>() {});
					gang.setTributeStat(tributestat);
				}catch(IOException e) {
					e.printStackTrace();
				}
			}
			gangs.add(gang);
		}

		return true;
	}

	@Override
	public PreSql getPreSql_query() {
		PreSql preSql = new PreSql();
		preSql.sqlstr = "select nid, sname, dcreated, nlevel, sbulletin, ntribute, nleader, "
				+ "sviceleader, spresbyter, smembers, STRIBUTESTAT " + "from tbl_tianlong_gang_" + TianLongServer.srvId;
				//+ " where nid = ?";
		//preSql.parameter.add(gangid);
		return preSql;
	}

	public List<Gang> getGang() {
		return gangs;
	}

	public void setGang(List<Gang> gang) {
		this.gangs = gang;
	}

}
