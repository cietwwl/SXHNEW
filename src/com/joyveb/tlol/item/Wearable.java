package com.joyveb.tlol.item;

import com.joyveb.tlol.role.RoleBean;

/**
 * 可穿戴物品
 */
public abstract class Wearable extends UniqueItem {
	
	/**
	 * @return 穿戴位置
	 */
	public abstract byte getWearLoc();

	/**
	 * @param role 参与角色
	 * @return 是否可穿戴
	 */
	public abstract boolean canWear(RoleBean role);
	
	/**
	 * 穿戴物品触发
	 * @param role 穿戴物品的角色
	 */
	public abstract void onWear(RoleBean role);

	/**
	 * @param role 穿戴此物品的角色
	 * @return 是否可卸下
	 */
	public abstract boolean canUnwield(RoleBean role);
	
	/**
	 * 卸下物品触发
	 * @param role 卸下物品的橘色
	 */
	public abstract void onUnwield(RoleBean role);
}
