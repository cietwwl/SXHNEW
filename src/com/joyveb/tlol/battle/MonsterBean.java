package com.joyveb.tlol.battle;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import com.joyveb.tlol.LuaService;
import com.joyveb.tlol.core.IGameCharacter;
import com.joyveb.tlol.protocol.MsgID;
import com.joyveb.tlol.role.Vocation;

/** 打斗中的怪物，根据其id可以获取属性，所有这里只包含当前状态 */
public class MonsterBean implements IGameCharacter {

	/** 怪物id */
	private int id;

	/** 剩余生命 */
	private int HP;

	/** 剩余魔法 */
	private int MP;
	/** 最大生命*/
	private int maxHP;
	/** 最大魔法*/
	private int maxMP;

	private String nick;

	private int color;

	private byte seatId;

	/** 动画组id */
	private short animiGroupid;
	/** 动画id */
	private short animationId;

	private int speed;

	private Vocation vocation;

	public MonsterBean(final int id, final String name, final int color, final int HP, final int MP,
			final short animiGroupId, final short anmationId, final int speed, final Vocation vocation) {
		this.id = id;
		this.nick = name;
		this.color = color;
		this.HP = HP;
		this.MP = MP;
		this.maxHP = HP;
		this.maxMP = MP;
		this.animiGroupid = animiGroupId;
		this.animationId = anmationId;
		this.speed = speed;
		this.vocation = vocation;
	}

	/**
	 * @return 玩家名字颜色
	 */
	public final int getColor() {
		return color;
	}

	public final void setColor(final int color) {
		this.color = color;
	}

	public final void setId(final int monsterid) {
		this.id = monsterid;
	}

	/**
	 * @return 玩家ID
	 */
	public final int getId() {
		return id;
	}

	public final void setHP(final int HP) {
		this.HP = HP;
	}

	/**
	 * @return 玩家现有血量
	 */
	public final int getHP() {
		return HP;
	}

	public final void setMP(final int MP) {
		this.MP = MP;
	}

	/**
	 * @return 玩家现有蓝量
	 */
	public final int getMP() {
		return MP;
	}

	/**
	 * 给玩家减少血量操作
	 * 
	 * @param var
	 *            减少血量值
	 * @return 是否减少操作成功
	 */
	public final boolean decreaseHP(final int var) {
		if(this.HP < var)
			this.HP = 0;
		else
			this.HP -= var;
		return true;
	}

	/**
	 * 给玩家减少魔法操作
	 * 
	 * @param MP
	 *            减少魔法值
	 * @return 是否减少操作成功
	 */
	public final boolean decreaseMP(final int MP) {
		if(this.MP < MP)
			this.MP = 0;
		else
			this.MP -= MP;
		return true;
	}
	/**
	 * @return 玩家等级
	 */
	public final short getLevel() {
		return (short) LuaService.call4Int("getMonsterLv", id);
	}
	@Override
	public final byte getType() {
		return 1;
	}
	/**
	 * @param targetList
	 * @return 玩家物理伤害值
	 */
	public final List<FightOne> physicalAttack(final List<IGameCharacter> targetList) {
		ArrayList<FightOne> fightList = new ArrayList<FightOne>();

		LuaService.callLuaFunction("monsterAtk", this, targetList, fightList);

		return fightList;
	}
	@Override
	public final short getAnime() {
		return animationId;
	}
	@Override
	public final short getAnimeGroup() {

		return animiGroupid;
	}
	/**
	 * @return 玩家最大生命
	 */
	public final int getMaxHP() {
		return maxHP;
	}
	/**
	 * @return 玩家最大魔法值
	 */
	public final int getMaxMP() {
		return maxMP;
	}
	/**
	 * @param maxHP 最大生命
	 */
	public final void setMaxHP(final int maxHP) {
		this.maxHP = maxHP;
	}
	/**
	 * @param maxMP 最大魔法
	 */
	public final void setMaxMP(final int maxMP) {
		this.maxMP = maxMP;
	}
	/**
	 * @param animationId 动画ID
	 */
	public final void setAnime(final short animationId) {
		this.animationId = animationId;

	}
	/**
	 * @param animiGroupid 动画组ID
	 */
	public final void setAnimeGroup(final short animiGroupid) {
		this.animiGroupid = animiGroupid;

	}
	/**
	 * @param battle
	 */
	public final void setBattle(final Battle battle) {
		// this.battle = battle;
	}

	public final int compare(final IGameCharacter o1, final IGameCharacter o2) {
		if(o1.getSpeed() > o2.getSpeed())
			return 1;
		else if(o1.getSpeed() < o2.getSpeed())
			return -1;
		else
			return 0;
	}
	@Override
	public final int getSpeed() {
		return speed;
	}

	public final void setSpeed(final int speed) {
		this.speed = speed;
	}
	@Override
	public final String getNick() {
		return nick;
	}
	@Override
	public final void setNick(final String nick) {
		this.nick = nick;
	}
	@Override
	public final byte getSeatId() {
		// if(this.battle != null)
		return seatId;
		// return -1;
	}
	@Override
	public final void setSeatId(final byte seatId) {
		this.seatId = seatId;
	}
	@Override
	public final boolean isConnected() {
		return false;
	}

	public final void sendMsg(final ByteBuffer body, final MsgID msgid) {
		// it is a monster do nothing
	}
	@Override
	public final int getSkillLv(final long skillId) {
		// TODO 因为怪物还不会释放技能所以这里留空
		return 1;
	}
	@Override
	public final List<FightOne> fightItemUse(final long itemId, final List<IGameCharacter> targetList) {
		// 怪物还不会使用物品 这里留空 返回null肯定会出错
		// 修改怪物ai时记得补充此方法
		return null;
	}
	@Override
	public final Vocation getVocation() {
		return vocation;
	}

	@Override
	public final int fixValueAfterBuff(final byte buffType, final int value) {
		return 0;
	}

	@Override
	public final boolean hasBuff(final byte buffType) {
		// TODO 怪物还没有BUFF
		return false;
	}
	@Override
	public final int getEvade() {
		return 0;
	}
	@Override
	public final int getHit() {
		return 0;
	}
	
	@Override
	public final int getCrit() {
		return 0;
	}
	@Override
	public final int getpDef() {
		return LuaService.call4Int("getMonsterPDef", id);
	}
	@Override
	public final int getmDef() {
		return LuaService.call4Int("getMonsterMDef", id);
	}

	@Override
	public final void setLevel(final short level) {
		// TODO Auto-generated method stub

	}

	@Override
	public final void increaseGold(final int var) {
		// TODO Auto-generated method stub

	}

	@Override
	public final void increaseExp(final int exp) {
		// TODO Auto-generated method stub

	}

	@Override
	public final boolean increaseHP(final int var) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public final boolean increaseMP(final int MP) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public final List<FightOne> skillAttack(final long skillId, final List<IGameCharacter> targetList) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public final void relive(final byte type) {
		// TODO Auto-generated method stub

	}

	@Override
	public final void tick() {
		// TODO Auto-generated method stub

	}

	@Override
	public final int getMaxPAtk() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public final int getMinPAtk() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public final int getMaxMAtk() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public final int getMinMAtk() {
		// TODO Auto-generated method stub
		return 0;
	}
}
