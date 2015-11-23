package com.joyveb.tlol.role;

import java.util.Date;
import java.util.HashSet;

import com.joyveb.tlol.db.DataStruct;
import com.joyveb.tlol.db.parser.RoleData;

public abstract class Role {
	/** 公司所有产品的用户的唯一ID */
	protected String joyid;
	/** 上线角色号 */
	protected int roleid;
	/** 角色id */
	protected int userId;
	/** 上线用户所在大区 */
	protected short zoneid;
	/** 名称 */
	protected String name;
	/** 能否修改名字 */
	protected boolean canChangeName = false;
	/** 等级 */
	protected short level;
	/** 经验 */
	protected int EXP;
	/** 生命 */
	protected int HP;
	/** 魔法 */
	protected int MP;
	/** 动画组id */
	protected short animeGroup;
	/** 动画id */
	protected short anime;
	/** 力量 */
	protected int strength;
	/** 敏捷 */
	protected int agility;
	/** 智力 */
	protected int intellect;
	/** 体质VITALITY */
	protected int vitality;

	/* 以上为人怪物共有属性 但是怪的属性是储存于脚本中的 */

	/** 角色性别 */
	protected byte sex;

	/** 在线时长，单位：秒 */
	protected int onlineSec;

	/** 金币，打怪掉落、出售物品等渠道获得 */
	protected int gold;
	/** 武林大会总积分 */
	protected int mark;
	/** 游戏币，用人民币购买 */
	protected int money;

	/** 称号，昵称上方显示 */
	protected String epithet;

	/** 社区id */
	protected long community;

	protected Date regdate;

	protected Date logoff;
	//--------------------------师傅，徒弟，结婚-开始---------------------------------------
	
	
	
	/**使用结婚戒指的时间 */
	protected Date marryringtime;
	/**解除师傅时间*/
	protected Date removemastertime;
	/**解除徒弟时间*/
	protected Date removeapptime;
	
	//--------------------------师傅，徒弟，结婚-结束---------------------------------------
	
	/** 声望 */
	protected int charm;

	/** 帮派id */
	protected long gangid;

	/** 排行榜使用的总杀人数 */
	protected int totalKillPlayerNum = 0;

	/** 一段时间内总杀人数超过时间清空 */
	protected short killPlayerNum = 0;

	/** 清空killPlayerNum 剩余时间 单位毫秒 */
	protected long resetKillPlayerNumLeftTime = 0;

	/** 赛马积分 */
	protected int racingMarks;

	/** 荣誉值 */
	protected int honor;

	/** 邪恶值 */
	protected int evil;

	/** 每日偷袭次数 */
	protected int sneakAttackNum;

	/** 最后偷袭时间 */
	protected Date lastBattleTime;
	
	public int getRacingMarks() {
		return racingMarks;
	}

	public void setRacingMarks(int racingMarks) {
		this.racingMarks = racingMarks;
	}

	public String getJoyid() {
		return joyid;
	}

	public void setJoyid(String joyid) {
		this.joyid = joyid;
	}

	public int getRoleid() {
		return roleid;
	}

	public void setRoleid(int roleid) {
		this.roleid = roleid;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public short getZoneid() {
		return zoneid;
	}

	public void setZoneid(short zoneid) {
		this.zoneid = zoneid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isCanChangeName() {
		return canChangeName;
	}

	public void setCanChangeName(boolean canChangeName) {
		this.canChangeName = canChangeName;
	}

	public short getLevel() {
		return level;
	}

	public void setLevel(short level) {
		this.level = level;
	}

	public int getEXP() {
		return EXP;
	}

	public void setEXP(int eXP) {
		EXP = eXP;
	}

	public int getHP() {
		return HP;
	}

	public void setHP(int hP) {
		HP = hP;
	}

	public int getMP() {
		return MP;
	}

	public void setMP(int mP) {
		MP = mP;
	}

	public short getAnimeGroup() {
		return animeGroup;
	}

	public void setAnimeGroup(short animeGroup) {
		this.animeGroup = animeGroup;
	}

	public short getAnime() {
		return anime;
	}

	public void setAnime(short anime) {
		this.anime = anime;
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

	public byte getSex() {
		return sex;
	}

	public void setSex(byte sex) {
		this.sex = sex;
	}

	public int getOnlineSec() {
		return onlineSec;
	}

	public void setOnlineSec(int onlineSec) {
		this.onlineSec = onlineSec;
	}

	public int getGold() {
		return gold;
	}

	public void setGold(int gold) {
		this.gold = gold;
	}

	public int getMark() {
		return mark;
	}

	public void setMark(int mark) {
		this.mark = mark;
	}

	public int getMoney() {
		return money;
	}

	public void setMoney(int money) {
		this.money = money;
	}

	public String getEpithet() {
		return epithet;
	}

	public void setEpithet(String epithet) {
		this.epithet = epithet;
	}

	public long getCommunity() {
		return community;
	}

	public void setCommunity(long community) {
		this.community = community;
	}

	public Date getRegdate() {
		return regdate;
	}

	public void setRegdate(Date regdate) {
		this.regdate = regdate;
	}

	public Date getLogoff() {
		return logoff;
	}

	public void setLogoff(Date logoff) {
		this.logoff = logoff;
	}
	
	
	public Date getMarryringtime() {
		return marryringtime;
	}

	public void setMarryringtime(Date marryringtime) {
		this.marryringtime = marryringtime;
	}
	

	public Date getRemovemastertime() {
		return removemastertime;
	}

	public void setRemovemastertime(Date removemastertime) {
		this.removemastertime = removemastertime;
	}

	public Date getRemoveapptime() {
		return removeapptime;
	}

	public void setRemoveapptime(Date removeapptime) {
		this.removeapptime = removeapptime;
	}

	public int getCharm() {
		return charm;
	}

	public void setCharm(int charm) {
		this.charm = charm;
	}

	public long getGangid() {
		return gangid;
	}

	public void setGangid(long gangid) {
		this.gangid = gangid;
	}

	public int getTotalKillPlayerNum() {
		return totalKillPlayerNum;
	}

	public void setTotalKillPlayerNum(int totalKillPlayerNum) {
		this.totalKillPlayerNum = totalKillPlayerNum;
	}

	public short getKillPlayerNum() {
		return killPlayerNum;
	}

	public void setKillPlayerNum(short killPlayerNum) {
		this.killPlayerNum = killPlayerNum;
	}

	public long getResetKillPlayerNumLeftTime() {
		return resetKillPlayerNumLeftTime;
	}

	public void setResetKillPlayerNumLeftTime(long resetKillPlayerNumLeftTime) {
		this.resetKillPlayerNumLeftTime = resetKillPlayerNumLeftTime;
	}

	/**
	 * 获取荣誉值
	 * 
	 * @return
	 */
	public int getHonor() {
		return honor;
	}

	/**
	 * 设置荣誉值
	 * 
	 * @param honor
	 */
	public void setHonor(int honor) {
		this.honor = honor;
	}

	/**
	 * 获取邪恶值
	 * 
	 * @return
	 */
	public int getEvil() {
		return evil;
	}

	/**
	 * 设置邪恶值
	 * 
	 * @param evil
	 */
	public void setEvil(int evil) {
		this.evil = evil;
	}

	/**
	 * 获取每日偷袭次数
	 * 
	 * @return
	 */
	public int getSneakAttackNum() {
		return sneakAttackNum;
	}

	/**
	 * 设置每日偷袭次数
	 * 
	 * @param sneakAttackNum
	 */
	public void setSneakAttackNum(int sneakAttackNum) {
		this.sneakAttackNum = sneakAttackNum;
	}
	
	/**
	 * 增加一次偷袭次数
	 */
	public void addSneakAttackNum(int num){
		this.sneakAttackNum += num;
	}

	/**
	 * 获取最后偷袭时间
	 * 
	 * @return
	 */
	public Date getLastBattleTime() {
		return lastBattleTime;
	}

	/**
	 * 设置最后偷袭时间
	 * 
	 * @param lastBattleTime
	 */
	public void setLastBattleTime(Date lastBattleTime) {
		this.lastBattleTime = lastBattleTime;
	}

	public abstract RoleBean toRole();

	public abstract RoleData toData();

	public abstract byte jobTitle();

	public abstract String getStoreStr();

	public abstract String getSkillStr();

	public abstract DataStruct getRoleDataStruct();

	public abstract byte getVocationCode();

	public static final HashSet<String> LOCK_NAME = new HashSet<String>();

}
