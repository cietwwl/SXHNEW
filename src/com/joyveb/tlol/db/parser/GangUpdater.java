package com.joyveb.tlol.db.parser;

import java.io.IOException;
import java.util.ArrayList;

import com.joyveb.tlol.TianLongServer;
import com.joyveb.tlol.db.DataStruct;
import com.joyveb.tlol.db.PreSql;
import com.joyveb.tlol.gang.Gang;
import com.joyveb.tlol.util.JackSON;

public class GangUpdater extends DataStruct {
	private PreSql preSql = new PreSql();

	public GangUpdater(final Gang gang) {
		
		switch(gang.getModifyType()){
		case 0://修改公告
			preSql.sqlstr = "update tbl_tianlong_gang_"
				+ TianLongServer.srvId
				+ " t "
				+ "set sname = ?, nlevel = ?, sbulletin = ?, ntribute = ?, "
				+ "nleader = ?, sviceleader = ?, spresbyter = ?, smembers = ?, STRIBUTESTAT = ?"
				+ "where t.nid = ?";
			
			preSql.parameter.add(gang.getName());
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
				memids.addAll(gang.getTributeStat().keySet());

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
			
			break;
		case 1://修改通缉令
			preSql.sqlstr = "update tbl_tianlong_gang_"
				+ TianLongServer.srvId
				+ " t "
				+ "set sname = ?, nlevel = ?, ntribute = ?, "
				+ "nleader = ?, sviceleader = ?, spresbyter = ?, smembers = ?, STRIBUTESTAT = ?,SCATCHORDER = ? "
				+ "where t.nid = ?";
			
			preSql.parameter.add(gang.getName());
			preSql.parameter.add(gang.getLevel());
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
				memids.addAll(gang.getTributeStat().keySet());

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
			
			preSql.parameter.add(gang.getCatchOrder());
			break;
			
		case 2://现把帮派职位改为帮派帮贡的前三名为副帮主，长老
			preSql.sqlstr = "update tbl_tianlong_gang_"
				+ TianLongServer.srvId
				+ " t "
				+ "set sname = ?, nlevel = ?, ntribute = ?, "
				+ "nleader = ?, sviceleader = ?, spresbyter = ?, smembers = ?, STRIBUTESTAT = ?,SCATCHORDER = ? "
				+ "where t.nid = ?";
			
			preSql.parameter.add(gang.getName());
			preSql.parameter.add(gang.getLevel());
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
				memids.addAll(gang.getTributeStat().keySet());

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
			
			preSql.parameter.add(gang.getCatchOrder());
			break;
			
		}
		preSql.parameter.add(gang.getId());
	}

	public final PreSql getPreSql_update() {
		return preSql;
	}
}
