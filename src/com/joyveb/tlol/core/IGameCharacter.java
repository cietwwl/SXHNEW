package com.joyveb.tlol.core;

import java.util.List;

import com.joyveb.tlol.battle.Battle;
import com.joyveb.tlol.battle.FightOne;
import com.joyveb.tlol.role.Vocation;

public interface IGameCharacter {

	/**
	 * @return Object Type
	 */
	byte getType();
	

	/**
	 * @return Object Id
	 */
	int getId();

	/**
	 * @return 名称
	 */
	String getNick();

	/**
	 * set name of the object
	 * 
	 * @param nick 
	 */
	void setNick(String nick);

	/**
	 * @return 当前等级
	 */
	short getLevel();

	/**
	 * 设置等级
	 * 
	 * @param level 
	 */
	void setLevel(short level);

	Vocation getVocation();

	/**
	 * 增加金钱
	 * 
	 * @param var 
	 */
	void increaseGold(int var);

	/**
	 * @param exp 增加经验值
	 */
	void increaseExp(int exp);

	/**
	 * @return 当前生命值
	 */
	int getHP();

	int getMaxHP();

	/**
	 * 增加生命值
	 * 
	 * @param var 
	 * @return true if hp is full
	 */
	boolean increaseHP(int var);

	/**
	 * 减少生命值
	 * 
	 * @param var 
	 * @return true if object is dead
	 */
	boolean decreaseHP(int var);

	/**
	 * @return 当前魔法值
	 */
	int getMP();

	int getMaxMP();

	/**
	 * 增加魔法值
	 * 
	 * @param MP 
	 * @return true if mp is full
	 */
	boolean increaseMP(int MP);

	/**
	 * 减少魔法值
	 * 
	 * @param MP 
	 * @return true if mp is zero
	 */
	boolean decreaseMP(int MP);

	/**
	 * 
	 * @param itemId 
	 * @param targetList 
	 * @return list
	 */
	List<FightOne> fightItemUse(long itemId, List<IGameCharacter> targetList);

	/**
	 * 
	 * @param skillId 
	 * @param targetList 
	 * @return list 
	 */
	List<FightOne> skillAttack(long skillId, List<IGameCharacter> targetList);

	/**
	 * 物理数值伤害,不加任何魔法元素伤害
	 * @param targetList 
	 * @return list
	 */
	List<FightOne> physicalAttack(List<IGameCharacter> targetList);

	/**
	 * 复活
	 * 
	 * @param type
	 *            type: PVE PVP DUEL
	 * 
	 */
	void relive(byte type);

	void setAnimeGroup(short groupid);

	short getAnimeGroup();

	void setAnime(short aminiid);

	short getAnime();

	/**
	 * 进入战斗时设置battle对象
	 * @param battle 
	 * */
	void setBattle(Battle battle);

	int getColor();

	boolean isConnected();

	/**
	 * 获得出手速度
	 * */
	int getSpeed();

	/**
	 * 获得技能等级
	 * */
	int getSkillLv(long skillId);

	/**
	 * 更新
	 */
	void tick();

	/**
	 * 修正buff增益/减益后的值
	 */
	int fixValueAfterBuff(byte buffType, int value);

	boolean hasBuff(byte buffType);

	String toString();

	byte getSeatId();

	void setSeatId(byte seatId);

	int getMaxPAtk();

	int getMinPAtk();

	int getMaxMAtk();

	int getMinMAtk();

	int getEvade();

	int getHit();
    int getCrit();

	int getpDef();

	int getmDef();
}
