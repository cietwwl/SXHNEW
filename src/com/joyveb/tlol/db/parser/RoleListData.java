package com.joyveb.tlol.db.parser;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.joyveb.tlol.TianLongServer;
import com.joyveb.tlol.db.DataStruct;
import com.joyveb.tlol.db.PreSql;
import com.joyveb.tlol.role.Role;

public class RoleListData extends DataStruct {

	private int userId;
	private short zoneId;
	private int roleid;

	private List<Role> roleList = new ArrayList<Role>();

	public RoleListData(final int userId, final short zoneId, final Role role) {
		this.userId = userId;
		this.zoneId = zoneId;
		if(role != null)
			roleList.add(role);
	}

	@Override
	public final boolean readFromRs(final ResultSet rs) throws SQLException {
		while (rs.next()) {
			RoleData roleData = new RoleData();
			roleData.getRoleDataStruct().readFromRs(rs);
			roleList.add(roleData);
		}
		return true;
	}

	@Override
	public final PreSql getPreSql_query() {
		PreSql preSql = new PreSql();

		preSql.sqlstr = "select t1.NID, t1.NZONEID, t1.NUSERID, t1.SNICK, t1.NSEX, t1.NVOCATION, t1.NGROUP, t1.NAMINI, "
				+ "t1.NLEVEL, t1.NGOLD, t1.NMARK, t1.NMARK, t1.NLASTMAP, t1.NLASTMAPX, t1.NLASTMAPY, t1.NRED, t1.NBLUE, "
				+ "t1.NSTRENGTH, t1.NAGILITY, t1.NINTELLECT, t1.NSTAMINA, t1.NONLINESEC, t1.NEXP, t1.CTASK, t1.SFRIENDS, "
				+ "t1.SFOES, t1.SKILLS, t1.citems, t1.SBUFFS, t1.NUSERID, t1.SEPITHET, t1.NCOMMUNITY, t1.dregdate, t1.dlogoff, "
				+ "t1.ncharm, t1.ngangid, t1.njobtitle, t1.NTOTALKILLNUM, t1.NKILLNUM, t1.NRESETKILLNUMTIME, t1.SRACINGMARK, "
				+ "t1.NHONOR, t1.NEVIL, t1.NSNEAKATTACKNUM, t1.NLASTBATTLETIME, t1.MARRY, t1.MASTER, t1.APPRENTICE, t1.APPDELMASTER,t1.MARRYRINGTIME, "
				+ "t1.REMOVEMASTERTIME, t1.REMOVEAPPTIME,t1.SENEMIES, "
				+ "t1.MARRY, t1.MASTER, t1.APPRENTICE, t1.APPDELMASTER,t1.MARRYRINGTIME,t1.isNotFatwa "
				+ "from tbl_tianlong_role"
				+ "_"
				+ TianLongServer.srvId
				+ " t1 "
				+ "where t1.nzoneid = ? and t1.nuserid = ? ";
		if(!roleList.isEmpty())
			preSql.sqlstr += "and t1.nid != " + roleList.get(0).getRoleid();
		
		preSql.sqlstr +="and (t1.SRACINGMARK=0)";  
		
		preSql.parameter.add(zoneId);
		preSql.parameter.add(userId);
		return preSql;
	}

	public final List<Role> getRoleList() {
		return roleList;
	}

	public final RoleListData setRoleid(final int roleid) {
		this.roleid = roleid;
		return this;
	}

	public final int getRoleid() {
		return roleid;
	}

}
