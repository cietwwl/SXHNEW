package com.joyveb.tlol.core;

import com.joyveb.tlol.MessageSend;
import com.joyveb.tlol.role.RoleBean;

public abstract class AgentProxy extends MessageSend implements PlayerAgent {
	protected RoleBean player;
	
}
