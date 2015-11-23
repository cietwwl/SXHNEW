package com.joyveb.tlol.role;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.joyveb.tlol.NameCard;
import com.joyveb.tlol.OnlineService;
import com.joyveb.tlol.TianLongServer;

public class RoleCard extends NameCard<RoleCard> implements RoleAccessible {

	private int roleid;

	private String name;

	private int level;

	private int gold;

	private int charm;

	private int exp;

	private int mark;

	/** 罪恶值 */
	private int evil;

	/** 荣誉值 */
	private int honor;

	public static final String SQL = " select nid, snick, nlevel, nexp, ngold, ncharm, nmark, nevil, nhonor "
			+ " from tbl_tianlong_role" + "_" + TianLongServer.srvId + " ";

	public static RoleCard readFromDB(final ResultSet resultSet)
			throws SQLException {
		return new RoleCard().setRoleid(resultSet.getInt(1))
				.setName(resultSet.getString(2))
				.setLevel(resultSet.getShort(3)).setExp(resultSet.getInt(4))
				.setGold(resultSet.getInt(5)).setCharm(resultSet.getInt(6))
				.setMark(resultSet.getInt(7)).setEvil(resultSet.getInt(8))
				.setHonor(resultSet.getInt(9));
	}

	@Override
	public final boolean isRoleOnline() {
		return OnlineService.getOnline(roleid) != null;
	}

	@Override
	public final RoleBean getRole() {
		return OnlineService.getOnline(roleid);
	}

	public final RoleCard setName(final String name) {
		this.name = name;
		return this;
	}

	@Override
	public final String toString() {
		return "角色【" + roleid + "】昵称【" + name + "】等级【" + level + "】金币【" + gold
				+ "】声望【" + charm + "】经验【" + exp + "】积分【" + mark + "】罪恶值【"
				+ evil + "】荣誉值【" + honor + "】";
	}

	public final RoleCard setRoleid(final int roleid) {
		this.roleid = roleid;
		return this;
	}

	public final int getRoleid() {
		return roleid;
	}

	@Override
	public final String getName() {
		return name;
	}

	public final RoleCard setLevel(final int level) {
		this.level = level;
		return this;
	}

	public final int getLevel() {
		return level;
	}

	public final RoleCard setGold(final int gold) {
		this.gold = gold;
		return this;
	}

	public final int getGold() {
		return gold;
	}

	public final RoleCard setCharm(final int charm) {
		this.charm = charm;
		return this;
	}

	public final int getCharm() {
		return charm;
	}

	public final RoleCard setExp(final int exp) {
		this.exp = exp;
		return this;
	}

	public final int getExp() {
		return exp;
	}

	public final RoleCard setMark(final int mark) {
		this.mark = mark;
		return this;
	}

	public final int getMark() {
		return mark;
	}

	public final RoleCard setEvil(final int evil) {
		this.evil = evil;
		return this;
	}

	public final int getEvil() {
		return evil;
	}

	public final RoleCard setHonor(final int honor) {
		this.honor = honor;
		return this;
	}

	public final int getHonor() {
		return honor;
	}
}
