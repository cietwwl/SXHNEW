package com.joyveb.tlol.team;

import java.util.ArrayList;

import com.joyveb.tlol.core.IGameCharacter;
import com.joyveb.tlol.role.RoleBean;

public class Team {
	private static int autoincreaseid;
	static final byte MAX_MEMBER = 3;
	// **********************************
	// * 小队数据结构 *
	// **********************************
	final int id = autoincreaseid++;

	/**
	 * 小队成员ID列表
	 */
	ArrayList<IGameCharacter> member;

	/**
	 * Constructer
	 * 
	 */
	public Team() {
		member = new ArrayList<IGameCharacter>(MAX_MEMBER); // 小队中最多有3个成员
	}

	/**
	 * 
	 * @param player
	 */
	public final void addMember(final RoleBean player) {
		if(member.size()<3){
			member.add(player);
		}
		
	}

	/**
	 * 
	 * @param player
	 * @return
	 */
	public boolean removeMember(RoleBean player) {
		return member.remove(player);
	}

	/**
	 * 小队队长，第一个人是队长
	 * 
	 * @return
	 */
	public IGameCharacter getLeader() {
		if (member.size() > 0)
			return member.get(0);
		return null;
	}

	/**
	 * 
	 * @return
	 */
	public int size() {
		return member.size();
	}

	/**
	 * 解散小队
	 */
	public void dismiss() {
		member.clear();
	}

	/**
	 * 将player设置为小队队长
	 * 
	 * @param buddyId
	 * @return
	 */
	public void setLeader(RoleBean player) {
		int index = member.indexOf(player);
		IGameCharacter old = member.get(0);
		member.set(0, player);
		member.set(index, old);
	}

	/**
	 * 获得小队的member
	 * 
	 * @param product
	 * @return
	 */
	public ArrayList<IGameCharacter> getMember() {
		return member;
	}
	/**
	 * 得到结婚队伍中，第二人的RoleBean
	 * @return the id
	 */
	public final RoleBean getMarryTeamRoleBean(){
		return ((RoleBean)this.member.get(1)).getRole();
	}

	/**
	 * 得到队伍中，第三人的RoleBean
	 * @return the id
	 */
	public final RoleBean getTeamThreeRoleBean(){
		return ((RoleBean)this.member.get(2)).getRole();
	}
	
	/**
	 * @return the id
	 */
	public final int getId() {
		return id;
	}

	public boolean isTeammate(RoleBean player) {

		return member.contains(player);
	}

	/**
	 * 判断小队其他成员是否达到每日偷袭次数上限
	 * 
	 * @return
	 */
	public boolean isOtherMembersSneakAttackNum() {
		boolean isSneakAttack = false;
		for (int index = 1; index < this.member.size(); index++) {
			if (((RoleBean) this.member.get(index)).getSneakAttackNum() >= 10) {
				isSneakAttack = true;
				break;
			}
		}
		return isSneakAttack;
	}

	/**
	 * 判断小队其他成员是否没有达到偷袭等级限制
	 * 
	 * @param allowPVPLevel
	 *            偷袭等级限制
	 * @return
	 */
	public boolean isAllowPVPLevel(int allowPVPLevel) {
		boolean isAllowPVPLevel = false;
		for (int index = 1; index < this.member.size(); index++) {
			if (((RoleBean) this.member.get(index)).getLevel() < allowPVPLevel) {
				isAllowPVPLevel = true;
				break;
			}
		}
		return isAllowPVPLevel;
	}
	
	/**
	 * 增加小队所有人员偷袭次数
	 */
	public void addSneakAttackNum(int num){
		for (int index = 0; index < this.member.size(); index++) {
			((RoleBean) this.member.get(index)).addSneakAttackNum(num);
		}
	}
}
