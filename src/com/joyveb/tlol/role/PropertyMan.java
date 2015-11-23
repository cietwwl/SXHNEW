package com.joyveb.tlol.role;

import java.util.ArrayList;
import java.util.EnumMap;

import com.joyveb.tlol.item.Bonus;

/**
 * 角色动态属性管理
 * @author Sid
 */
public class PropertyMan {

	/** 所有者 */
	private RoleBean owner;

	/** 属性map */
	private EnumMap<Property, Double> poperties = new EnumMap<Property, Double>(Property.class);

	/**
	 * @param owner 所有者
	 */
	public PropertyMan(final RoleBean owner) {
		this.owner = owner;
	}

	/**
	 * @param bonus 添加属性加成
	 */
	public final void addBonus(final Bonus bonus) {
		bonus.getEffectProperty().addBonus(owner, bonus);
	}

	/**
	 * @param bonus 移除属性加成
	 */
	public final void removeBonus(final Bonus bonus) {
		bonus.getEffectProperty().removeBonus(owner, bonus);
	}

	/**
	 * 获取属性值
	 * @param property 属性
	 * @return 值
	 */
	public final double getDynamicProperty(final Property property) {
		if(!poperties.containsKey(property))
			poperties.put(property, Double.valueOf(0));
		
		return poperties.get(property);
	}

	/**
	 * @param bonuses 添加属性加成
	 */
	public final void addBonus(final ArrayList<Bonus> bonuses) {
		for(Bonus bonus : bonuses)
			addBonus(bonus);
	}

	/**
	 * @param bonuses 移除属性加成
	 */
	public final void removeBonus(final ArrayList<Bonus> bonuses) {
		for(Bonus bonus : bonuses)
			removeBonus(bonus);
	}

	/**
	 * 直接改变属性值而不涉及触发其他属性
	 * @param property 属性
	 * @param change 增量值
	 */
	public final void changeProperty(final Property property, final double change) {
		
		double rawValue = poperties.containsKey(property) ? poperties.get(property) : 0;
		poperties.put(property, rawValue + change > 0 ? rawValue + change : 0);
		
		property.respond(owner, change);
	}

}
