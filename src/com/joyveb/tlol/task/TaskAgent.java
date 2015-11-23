package com.joyveb.tlol.task;

import com.joyveb.tlol.LuaService;
import com.joyveb.tlol.core.AgentProxy;
import com.joyveb.tlol.net.IncomingMsg;
import com.joyveb.tlol.protocol.DelTaskBody;
import com.joyveb.tlol.protocol.ListTaskBody;
import com.joyveb.tlol.protocol.MsgID;
import com.joyveb.tlol.role.RoleBean;
import com.joyveb.tlol.util.Log;

/**
 * 任务模块消息代理
 * @author Sid
 *
 */
public class TaskAgent extends AgentProxy {

	/**
	 * @param player 消息来源
	 */
	public TaskAgent(final RoleBean player) {
		this.player = player;
	}
	
	@Override
	public final void processCommand(final IncomingMsg message) {
		switch (MsgID.getInstance(message.getHeader().getMsgID())) {
		case MsgID_Task_List:
			if (ListTaskBody.INSTANCE.readBody(message.getBody()))
				if (ListTaskBody.INSTANCE.getListType() == 0)
					LuaService.callLuaFunction("getAccepted", player);
				else
					LuaService.callLuaFunction("getAcceptable", player);
			else
				replyMessage(player, 1, MsgID.MsgID_Task_List_Resp, "获取失败！");

			break;
		case MsgID_Task_Del:
			if (DelTaskBody.INSTANCE.readBody(message.getBody())){
				if(DelTaskBody.INSTANCE.getTaskID()==24000){
					replyMessage(player, 1, MsgID.MsgID_Task_Del_Resp, "此任务不可放弃！");
					return;
				}
				LuaService.callLuaFunction("cancelTask", player,
						DelTaskBody.INSTANCE.getTaskID());
			}
			else
				replyMessage(player, 1, MsgID.MsgID_Task_Del_Resp, "删除失败！");

			break;
		case Task_Finish:
			if (MsgID.Task_Finish.readBody(message.getBody())) {
				int taskid = MsgID.Task_Finish.getMsgBody().getInt(1);
				if(LuaService.getBool(Task.LUA_CONTAINER, taskid, "directConsign"))
					LuaService.callOO(2, Task.LUA_CONTAINER, taskid, "directConsign", player);
				else
					replyMessage(player, 2, MsgID.Task_Finish_Resp, "提交失败！");
			}else
				replyMessage(player, 1, MsgID.Task_Finish_Resp, "提交失败！");

			break;
		default:
			Log.error(Log.STDOUT, "processCommand", "unhandled msgid! : " + message.getHeader().getMsgID());
			break;
		}
	}
}
