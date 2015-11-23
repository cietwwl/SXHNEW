package com.joyveb.tlol.heartbeat;

import com.joyveb.tlol.core.AgentProxy;
import com.joyveb.tlol.net.IncomingMsg;
import com.joyveb.tlol.net.NetHandler;
import com.joyveb.tlol.protocol.MsgID;
import com.joyveb.tlol.role.RoleBean;
import com.joyveb.tlol.util.Log;

public class HeartBeatAgent extends AgentProxy {

	public HeartBeatAgent(final RoleBean player) {
		this.player = player;
	}

	@Override
	public final void processCommand(final IncomingMsg msg) {
		switch (MsgID.getInstance(msg.getHeader().getMsgID())) {
		case MsgID_Hello:
			respHello(player);
			break;
		case MsgID_Hello_Resp:
			// 在此做心跳判断，太快就踢掉 心跳踢掉 ！！！
			// 每五次心跳时间小于1分钟，就踢掉
			// System.out.println(player.getLogOnTime() + "~~~~");
			// System.out.println(System.currentTimeMillis());
			// if (player.getHeartNum() == 5) {
			// if (System.currentTimeMillis() - player.getLogOnTime() < 3 * 60 *
			// 1000) {
			// System.out.println("玩家开加速踢下线：---------------------------------" +
			// player.getNick());
			// player.getNetHandler().close(NetHandler.STATE_CLOSED_BY_APP_LEVEL);
			// } else {
			// player.setHeartNum(0);
			// player.setLogOnTime(System.currentTimeMillis());
			// }
			// } else {
			// if (player.getHeartNum() < 5) {
			// player.setHeartNum(player.getHeartNum() + 1);
			// } else {
			// player.setHeartNum(0);
			// player.setLogOnTime(System.currentTimeMillis());
			// }
			// }

			break;
		default:
			Log.error(Log.STDOUT, "HeartBeatAgent.processCommand", "unhandled msgid! : " + msg.getHeader().getMsgID());
			break;
		}
	}

	/**
	 * 回复客户端探测
	 * 
	 * @param online
	 */
	private void respHello(final RoleBean online) {

		body.clear();
		body.putInt(0);
		sendMsg(online, MsgID.MsgID_Hello_Resp);

	}
}
