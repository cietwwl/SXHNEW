package com.joyveb.tlol.role;

/**
 * 属性变化时的回调
 * @author Sid
 *
 */
public interface PropertyRespond {
	
	/**
	 * @param role 参与角色
	 * @param change 变化数值
	 */
	void respond(final RoleBean role, final double change);
	
}
