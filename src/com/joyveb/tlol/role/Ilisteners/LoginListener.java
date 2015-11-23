package com.joyveb.tlol.role.Ilisteners;

import java.util.EventListener;

import com.joyveb.tlol.role.events.LoginEvent;


public interface LoginListener extends EventListener {
	public void onLogin(LoginEvent loginEvent);
}
