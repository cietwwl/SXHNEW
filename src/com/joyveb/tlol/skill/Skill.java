/**
 * 
 */
package com.joyveb.tlol.skill;

import com.joyveb.tlol.LuaService;
import com.joyveb.tlol.core.IGameCharacter;

/**
 * @author zhongyuan
 * 
 */
public class Skill {

	/** 技能等级 */
	private int level;

	/**
	 * 技能组ID 即skillclass的ID 例:此技能为火球术(等级1)他所隶属的技能组为火球术(ID=100)
	 */
	private int classId;

	/**
	 * 学习此技能需要的玩家等级
	 */
	private int lvRequire;

	public Skill() {

	}

	/**
	 * 判断技能是否能够被学习
	 * @param character 
	 * @return 技能是否能够被学习
	 */
	public final boolean canLearn(final IGameCharacter character) {
		return false;
	}

	/**
	 * 技能产生的效果
	 * @param caster 
	 * @param receiver 
	 * @return 技能是否产生效果
	 */
	public final boolean toEffect(final IGameCharacter caster, final IGameCharacter receiver) {
		return false;
	}

	/**
	 * 技能移除以后产生的效果
	 * @param caster 
	 * @param receiver 
	 * @return 技能移除以后产生的效果
	 */
	public final boolean unEffect(final IGameCharacter caster, final IGameCharacter receiver) {
		return false;
	}

	/**
	 * 获得技能名字
	 */
	public final String getName() {
		return null;
	}

	public final String getLiteTitle() {
		return LuaService.call4String("getLiteTitle", classId, level);
	}

	public final String getFullTitle() {
		return LuaService.call4String("getFullTitle", classId, level);
	}

	public final short getSkillIcon() {
		return (short) LuaService.call4Int("getSkillIcon", classId);
	}

	public final byte isPassive() {
		return (byte) LuaService.call4Int("isPassiveSkill", classId);
	}

	public final byte toEffectNum() {
		return (byte) LuaService.call4Int("toEffectNum", classId, level);
	}

	public final void setLevel(final int level) {
		this.level = level;
	}

	public final int getLevel() {
		return level;
	}

	public final void setClassId(final int classId) {
		this.classId = classId;
	}

	public final int getClassId() {
		return classId;
	}

	public final void setLvRequire(final int lvRequire) {
		this.lvRequire = lvRequire;
	}

	public final int getLvRequire() {
		return lvRequire;
	}

}
