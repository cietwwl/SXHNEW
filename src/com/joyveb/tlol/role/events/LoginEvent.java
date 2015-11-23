package com.joyveb.tlol.role.events;

import java.util.EventObject;

import com.joyveb.tlol.role.RoleBean;

public class LoginEvent extends EventObject {

	/**
	 * 自动生成的序列化号
	 */
	private static final long serialVersionUID = 8738069889988467031L;
	

	public LoginEvent(RoleBean roleBean) {
		super(roleBean);
	}
	
	public RoleBean getRoleBean(){
		return (RoleBean) this.getSource();
	}

}
