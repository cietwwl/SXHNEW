package com.joyveb.tlol.server;

import java.util.ArrayList;

import com.joyveb.tlol.MessageSend;
import com.joyveb.tlol.OnlineService;
import com.joyveb.tlol.gang.Gang;
import com.joyveb.tlol.gang.GangService;
import com.joyveb.tlol.net.HoldNetHandler;
import com.joyveb.tlol.protocol.MessageSendBody;
import com.joyveb.tlol.protocol.MsgID;
import com.joyveb.tlol.role.RoleAccessible;
import com.joyveb.tlol.role.RoleBean;
import com.joyveb.tlol.role.RoleCard;
import com.joyveb.tlol.util.Log;

/**
 * 系统消息
 */
public final class ServerMessage extends MessageSend {
	/**
	 * 系统消息颜色
	 */
	public static final int SYS_COLOR = 0xff8400;

	/**
	 * 私有
	 */
	private ServerMessage() {
	}

	/**
	 * 填充系统提示
	 * 
	 * @param prompt
	 *            系统提示
	 */
	private static void fillSysPrompt(final String prompt) {
		prepareBody();

		putByte((byte) 0);
		putByte((byte) 0);
		putInt(0);
		putShort((short) 0);

		putString("系统提示：" + prompt);
		putInt(SYS_COLOR);
		putShort((short) 0);
	}

	/**
	 * 发送系统提示
	 * 
	 * @param role
	 *            接收方
	 * @param prompt
	 *            提示内容
	 */
	public static void sendSysPrompt(final RoleBean role, final String prompt) {
		if (role == null)
			return;
		fillSysPrompt(prompt);
		sendMsg(role, MsgID.MsgID_Message_Receive);
	}

	/**
	 * 批量发送系统提示
	 * 
	 * @param accessors
	 *            可访问到角色的对象接口
	 * @param prompt
	 *            提示内容
	 */
	public static void batchSendSysPrompt(final ArrayList<? extends RoleAccessible> accessors, final String prompt) {
		fillSysPrompt(prompt);

		for (RoleAccessible accessor : accessors)
			if (accessor.isRoleOnline())
				sendMsg(accessor.getRole(), MsgID.MsgID_Message_Receive);
	}

	/**
	 * 发送系统公告，全服广播
	 * 
	 * @param bulletin
	 *            公告内容
	 */
	public static void sendBulletin(final String bulletin) {
		if (bulletin == null)
			return;

		Log.info(Log.STDOUT, "发送系统公告");
		Log.info(Log.STDOUT, bulletin);

		ArrayList<Integer> allOnline = OnlineService.getAllOnlines();
		int index = 0;
		if (!allOnline.isEmpty()) {
			prepareBody();

			putByte((byte) 0);
			putByte((byte) 0);
			putInt(0);
			putShort((short) 0);

			putString("系统公告：" + bulletin);
			putInt(SYS_COLOR);
			putShort((short) 0);

			for (int roleid : allOnline) {
				RoleBean online = OnlineService.getOnline(roleid);
				if (online != null) {
					sendMsg(online, MsgID.MsgID_Message_Receive);
					index++;
				}
			}
		}

		Log.info(Log.STDOUT, "发送系统公告完成，发往" + index + "人");
	}

	/**
	 * 发送帮派公告，用于帮战公告
	 */
	public static void sendGangTalk(int gangId, final String contentStr) {
		if (gangId == 0)
			return;

		Gang gang = GangService.INSTANCE.getGang(gangId);

		ArrayList<Integer> allOnline = OnlineService.getAllOnlines();
		if (!allOnline.isEmpty()) {

			prepareBody();
			putByte((byte) 3);
			putByte((byte) 2);// 显示级别（0运营、公告1世界聊天2杂，公会队伍，私聊）
			putInt(0);
			putString("系统");
			putString("【公会】：" + contentStr);
			putInt(0x30ff00);
			putShort((short) 0);

			for (RoleCard card : gang.getMembers()) {
				RoleBean role = OnlineService.getOnline(card.getRoleid());
				if (role != null)
					sendMsg(role, MsgID.MsgID_Message_Receive);// 转发
			}
		}
	}

	/**
	 * 发送帮派公告，用于帮战公告
	 */
	public static void sendGangWorldTalk(final String contentStr) {
		if (contentStr == "" || contentStr == null) {
			return;
		}
		ArrayList<Integer> allOnline = OnlineService.getAllOnlines();
		if (!allOnline.isEmpty()) {

			prepareBody();
			putByte((byte) 0);
			putByte((byte) 1);// 显示级别（0运营、公告1世界聊天2杂，公会队伍，私聊）
			putInt(0);
			putString("帮战");
			putString("【世】" + contentStr);
			putInt(0xff0000);
			putShort((short) 0);

			for (int roleid : allOnline) {
				RoleBean online = OnlineService.getOnline(roleid);
				if (online != null) {
					sendMsg(online, MsgID.MsgID_Message_Receive);
				}
			}
		}
	}

	/**
	 * 发送心跳
	 * 
	 * @param holder
	 *            心跳接收方
	 */
	public static void sendHeartBeat(final HoldNetHandler holder) {
		prepareBody();
		sendMsg(holder, MsgID.MsgID_Hello);
	}

	/**
	 * 命令客户端断开连接，已废弃
	 * 
	 * @param holder
	 *            连接拥有者
	 */
	public static void orderExit(final HoldNetHandler holder) {
		orderExit(holder, "与服务器断开连接!");
	}

	/**
	 * 命令客户端断开连接，已废弃
	 * 
	 * @param holder
	 *            连接拥有者
	 * @param prompt
	 *            断开提示
	 */
	public static void orderExit(final HoldNetHandler holder, final String prompt) {
		prepareBody();
		putByte((byte) 2);
		putString(prompt);
		putShort((short) 0);
		sendMsg(holder, MsgID.MsgID_Order_Exit);
	}
}
