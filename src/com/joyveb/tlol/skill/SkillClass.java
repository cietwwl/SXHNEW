/**
 * 
 */
package com.joyveb.tlol.skill;

import com.joyveb.tlol.core.IGameCharacter;

/**
 * @author zhongyuan
 * 
 */
public class SkillClass {

	/**
	 * 技能ID 格式为100 而技能ID为10011即100+技能等级
	 */
	private int id;
	/**
	 * 技能名字
	 */
	private String name;
	/**
	 * 技能最大等级
	 */
	private String maxlv;
	/**
	 * 技能图片索引
	 */
	private int pic;
	/**
	 * 技能描述
	 */
	private String description;

	/**
	 * 
	 */
	public SkillClass() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * 判断技能是否可以被使用 这里不仅判断了释放技能者能否释放此技能 而且加入了接受者能否接收此技能的判断
	 * @param caster 
	 * @param reciver 
	 * @return 判断技能是否可以被使用
	 */
	public final boolean canUse(final IGameCharacter caster, final IGameCharacter reciver) {
		return false;
	}

	public final void setId(final int id) {
		this.id = id;
	}

	public final int getId() {
		return id;
	}

	public final void setName(final String name) {
		this.name = name;
	}

	public final String getName() {
		return name;
	}

	public final void setMaxlv(final String maxlv) {
		this.maxlv = maxlv;
	}

	public final String getMaxlv() {
		return maxlv;
	}

	public final void setPic(final int pic) {
		this.pic = pic;
	}

	public int getPic() {
		return pic;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getDescription() {
		return description;
	}

}
