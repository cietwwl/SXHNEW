package com.joyveb.tlol.role.listeners;

import com.joyveb.tlol.pay.connect.ConnectCommonParser;
import com.joyveb.tlol.pay.domain.SelectYuanbao;
import com.joyveb.tlol.role.RoleBean;
import com.joyveb.tlol.role.Ilisteners.LoginListener;
import com.joyveb.tlol.role.events.LoginEvent;

public class LoginResponder implements LoginListener {

	@Override
	public void onLogin(LoginEvent loginEvent) {
		RoleBean roleBean = loginEvent.getRoleBean();

		SelectYuanbao selectYuanbao = new SelectYuanbao(roleBean,
				roleBean.getUserid(), 8);
		ConnectCommonParser.getInstance().postSelectTask(
				roleBean.getYuanBaoOp(), selectYuanbao);

	}

}
