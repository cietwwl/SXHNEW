package com.joyveb.tlol.db.parser;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.joyveb.tlol.TianLongServer;
import com.joyveb.tlol.db.DataStruct;
import com.joyveb.tlol.db.PreSql;

public class QuickEnterRoleData extends DataStruct {

	private int roleId;
	private int userId;
	private RoleData roleData;

	@Override
	public final PreSql getPreSql_query() {
		PreSql preSql = new PreSql();
		preSql.sqlstr = "select t1.NID, t1.NZONEID, t1.NUSERID, t1.SNICK, t1.NSEX, t1.NVOCATION, t1.NGROUP, t1.NAMINI, t1.NLEVEL, t1.NGOLD, t1.NMARK, t1.NMARK, t1.NLASTMAP, t1.NLASTMAPX, t1.NLASTMAPY, t1.NRED, t1.NBLUE, t1.NSTRENGTH, t1.NAGILITY, t1.NINTELLECT, t1.NSTAMINA, t1.NONLINESEC, t1.NEXP, t1.CTASK, t1.SFRIENDS, t1.SFOES, t1.SKILLS, t1.citems, t1.SBUFFS, t1.NUSERID "
				+ "from tbl_tianlong_role"
				+ "_"
				+ TianLongServer.srvId
				+ " t1 "
				+ "where t1.NID = ? and t1.NUSERID = ?  and  sracingmark=0 ";
		preSql.parameter.add(roleId);
		preSql.parameter.add(userId);
		return preSql;
	}

	@Override
	public final boolean readFromRs(final ResultSet rs) throws SQLException {
		if (rs.next()) {
			roleData = new RoleData();
			roleData.roleDataStruct.readFromRs(rs);
		}
		return true;
	}

	@Override
	public final PreSql getPreSql_insert() {
		PreSql preSql = new PreSql();
		preSql.sqlstr = "insert into tbl_tianlong_role"
				+ "_"
				+ TianLongServer.srvId
				+ "(nid, nzoneid, nuserid, snick, nsex, nvocation, ngroup, namini, nlevel, ngold, "
				+ "nmark, nmoney, nlastmap, nlastmapx, nlastmapy, nred, nblue, nstrength, nagility, nintellect, "
				+ "nstamina, nonlinesec, nexp) " + "values (seq_tianlong_role_"
				+ TianLongServer.srvId
				+ ".nextval, ?, ?, ?, ?, ?, ?, ?, ?, ?, "
				+ "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, " + "?, ?, ?, ?, ?, ?, ?, ?)";
		preSql.parameter.add(roleData.getZoneid());
		preSql.parameter.add(roleData.getUserId());
		preSql.parameter.add(roleData.getName());
		preSql.parameter.add(roleData.getSex());
		preSql.parameter.add(roleData.getVocation());
		preSql.parameter.add(roleData.getAnimeGroup());
		preSql.parameter.add(roleData.getAnime());
		preSql.parameter.add(roleData.getLevel());
		preSql.parameter.add(roleData.getGold());
		preSql.parameter.add(roleData.getMark());
		preSql.parameter.add(roleData.getMoney());
		preSql.parameter.add(roleData.getLastmap());
		preSql.parameter.add(roleData.getLastmapx());
		preSql.parameter.add(roleData.getLastmapy());
		preSql.parameter.add(roleData.getHP());
		preSql.parameter.add(roleData.getMP());
		preSql.parameter.add(roleData.getStrength());
		preSql.parameter.add(roleData.getAgility());
		preSql.parameter.add(roleData.getIntellect());
		preSql.parameter.add(roleData.getVitality());
		preSql.parameter.add(roleData.getOnlineSec());
		preSql.parameter.add(roleData.getEXP());
		// preSql.parameter.add(roleData.getTasks());
		// preSql.parameter.add(roleData.getFriends());
		// preSql.parameter.add(roleData.getEnemies());
		// preSql.parameter.add(roleData.getSkills());
		// preSql.parameter.add(roleData.getStore());
		return preSql;
	}

	public final int getRoleId() {
		return roleId;
	}

	public final void setRoleId(final int roleId) {
		this.roleId = roleId;
	}

	public final RoleData getRoleData() {
		return roleData;
	}

	public final int getUserId() {
		return userId;
	}

	public final void setUserId(final int userId) {
		this.userId = userId;
	}

}
