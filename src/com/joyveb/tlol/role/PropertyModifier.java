package com.joyveb.tlol.role;

import com.joyveb.tlol.item.Bonus;

/**
 * 修改角色动态属性
 * @author dell
 */
public interface PropertyModifier {
	
	/**
	 * 修改属性的值
	 * @param role 角色
	 * @param change 属性变化值
	 */
	void changeValue(RoleBean role, double change);
	
	/**
	 * 对属性进行加成
	 * @param role 角色
	 * @param bonus 加成
	 */
	void addBonus(RoleBean role, Bonus bonus);
	
	/**
	 * 移除属性加成
	 * @param role 角色
	 * @param bonus 加成
	 */
	void removeBonus(RoleBean role, Bonus bonus);
}
