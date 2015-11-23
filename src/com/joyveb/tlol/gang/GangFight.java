package com.joyveb.tlol.gang;

import com.joyveb.tlol.Watchable;
import com.joyveb.tlol.db.DataHandler;
import com.joyveb.tlol.db.DataStruct;
import com.joyveb.tlol.db.parser.DbConst;
import com.joyveb.tlol.role.Vocation;
import com.joyveb.tlol.schedule.MinTickHandler;

public class GangFight implements DataHandler, MinTickHandler, Watchable {
	/**角色ID*/
	private int roleid;//角色ID
	/**角色名*/
	private String name;
	/**小组ID*/
	private int groupId;//小组ID
	/**帮主ID*/
	private int leaderId;//帮主ID
	/**帮派ID*/
	private int gangId;//帮派ID
	/**职业*/
	private Vocation vocation;//职业
	/**性别*/
	private int sex;//性别
	/**等级*/
	private int level;// 等级
	/** 力量 */
	private int strength;// 力 量
	/** 敏捷 */
	private int agility;// 敏 捷
	/** 智力 */
	private int intellect;// 智 力
	/** 体质 */
	private int vitality;// 体 质
	/** 最小物攻 */
	private int minPAtk;// 最小物攻
	/** 最大物攻 */
	private int maxPAtk;// 最大物攻
	/** 最小法攻 */
	private int minMAtk;// 最小法攻
	/** 最大法攻 */
	private int maxMAtk;// 最大法攻
	/** 物理防御 */
	private int pDef;// 物理防御
	/** 法术防御 */
	private int mDef;// 法术防御
	/** 命中 */
	private int hit;// 命 中
	/** 躲闪 */
	private int evade;// 躲 闪
	/** 致命 */
	private int crit;// 致 命
	/** 攻速度 */
	private int atkSpd;// 攻 速
	/** 生命上限 */
	private int maxHp;// 生命上限
	/** 魔法上限 */
	private int maxMp;// 魔法上限
	/**生命值*/
	private int Hp;
	/**实际最大生命值*/
	private int newMaxHp;
	/**战斗所获积分*/
	private int score= 0;
	
	public int getRoleid() {
		return roleid;
	}

	public void setRoleid(int roleid) {
		this.roleid = roleid;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getGroupId() {
		return groupId;
	}

	public void setGroupId(int groupId) {
		this.groupId = groupId;
	}
	
	public int getLeaderId() {
		return leaderId;
	}

	public void setLeaderId(int leaderId) {
		this.leaderId = leaderId;
	}

	public int getGangId() {
		return gangId;
	}

	public void setGangId(int gangId) {
		this.gangId = gangId;
	}

	public Vocation getVocation() {
		return vocation;
	}

	public void setVocation(Vocation vocation) {
		this.vocation = vocation;
	}

	public int getSex() {
		return sex;
	}

	public void setSex(int sex) {
		this.sex = sex;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public int getStrength() {
		return strength;
	}

	public void setStrength(int strength) {
		this.strength = strength;
	}

	public int getAgility() {
		return agility;
	}

	public void setAgility(int agility) {
		this.agility = agility;
	}

	public int getIntellect() {
		return intellect;
	}

	public void setIntellect(int intellect) {
		this.intellect = intellect;
	}

	public int getVitality() {
		return vitality;
	}

	public void setVitality(int vitality) {
		this.vitality = vitality;
	}

	public int getMinPAtk() {
		return minPAtk;
	}

	public void setMinPAtk(int minPAtk) {
		this.minPAtk = minPAtk;
	}

	public int getMaxPAtk() {
		return maxPAtk;
	}

	public void setMaxPAtk(int maxPAtk) {
		this.maxPAtk = maxPAtk;
	}

	public int getMinMAtk() {
		return minMAtk;
	}

	public void setMinMAtk(int minMAtk) {
		this.minMAtk = minMAtk;
	}

	public int getMaxMAtk() {
		return maxMAtk;
	}

	public void setMaxMAtk(int maxMAtk) {
		this.maxMAtk = maxMAtk;
	}

	public int getpDef() {
		return pDef;
	}

	public void setpDef(int pDef) {
		this.pDef = pDef;
	}

	public int getmDef() {
		return mDef;
	}

	public void setmDef(int mDef) {
		this.mDef = mDef;
	}

	public int getHit() {
		return hit;
	}

	public void setHit(int hit) {
		this.hit = hit;
	}

	public int getEvade() {
		return evade;
	}

	public void setEvade(int evade) {
		this.evade = evade;
	}

	public int getCrit() {
		return crit;
	}

	public void setCrit(int crit) {
		this.crit = crit;
	}

	public int getAtkSpd() {
		return atkSpd;
	}

	public void setAtkSpd(int atkSpd) {
		this.atkSpd = atkSpd;
	}

	public int getMaxHp() {
		return maxHp;
	}

	public void setMaxHp(int maxHp) {
		this.maxHp = maxHp;
	}

	public int getMaxMp() {
		return maxMp;
	}

	public void setMaxMp(int maxMp) {
		this.maxMp = maxMp;
	}

	public int getHp() {
		return Hp;
	}

	public void setHp(int hp) {
		Hp = hp;
	}
	
	public int getNewMaxHp() {
		return newMaxHp;
	}

	public void setNewMaxHp(int newMaxHp) {
		this.newMaxHp = newMaxHp;
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}

	@Override
	public void watch() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void minTick(int curMin) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void handle(DbConst eventID, boolean flag, DataStruct ds) {
		// TODO Auto-generated method stub
		
	}
	
	

}