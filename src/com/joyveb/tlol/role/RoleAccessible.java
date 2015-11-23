package com.joyveb.tlol.role;

/**
 * 可访问到角色对象的接口
 * @author Sid
 *
 */
public interface RoleAccessible {
	/**
	 * @return 角色对象
	 */
	RoleBean getRole();

	/**
	 * @return 角色是否在线
	 */
	boolean isRoleOnline();
}
