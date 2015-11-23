package com.joyveb.tlol.action;

import com.joyveb.tlol.LuaService;
import com.joyveb.tlol.core.AgentProxy;
import com.joyveb.tlol.net.IncomingMsg;
import com.joyveb.tlol.protocol.MsgID;
import com.joyveb.tlol.protocol.ActionRequestBody;
import com.joyveb.tlol.role.RoleBean;
import com.joyveb.tlol.util.Log;

public class ActionAgent extends AgentProxy {
	
	public ActionAgent(RoleBean player){
		this.player = player;
	}

	@Override
	public void processCommand(final IncomingMsg msg) {
		switch (MsgID.getInstance(msg.getHeader().getMsgID())) {
		case Action_Request:
			ActionRequestBody.INSTANCE.readBody(msg.getBody());
			ActionRequest pr = parseRequest(ActionRequestBody.INSTANCE.getRequest());
			
			LuaService.callLuaFunction("callAction" , player.getRoleid(), pr.pageName, pr.args);
			break;
		default:
			Log.error(Log.STDOUT, "processCommand", "unhandled msgid! : " + msg.getHeader().getMsgID());
			break;
		}

	}
	
	private ActionRequest parseRequest(String request){
		ActionRequest pr = new ActionRequest();
		
		if(request != null){
			
			try{
				String[] tempStr = request.split("\\?");
				pr.pageName = tempStr[0];
				if(tempStr.length == 2){
					pr.args = tempStr[1];
				}
			}catch(Exception e){
				Log.error(Log.ERROR, e);
				return null;
			}
			
		}else{
			return null;
		}
		
		return pr;
	}

}

class ActionRequest{
	String pageName;
	String args;
	
}
