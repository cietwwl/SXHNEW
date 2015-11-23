package com.joyveb.tlol.chat;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.joyveb.tlol.Conf;
import com.joyveb.tlol.LuaService;
import com.joyveb.tlol.OnlineService;
import com.joyveb.tlol.TianLongServer;
import com.joyveb.tlol.community.Community;
import com.joyveb.tlol.community.Communitys;
import com.joyveb.tlol.core.AgentProxy;
import com.joyveb.tlol.core.IGameCharacter;
import com.joyveb.tlol.gang.Gang;
import com.joyveb.tlol.gang.GangService;
import com.joyveb.tlol.mail.MailManager;
import com.joyveb.tlol.map.GridMapSys;
import com.joyveb.tlol.net.IncomingMsg;
import com.joyveb.tlol.protocol.MessageSendBody;
import com.joyveb.tlol.protocol.MsgID;
import com.joyveb.tlol.role.RoleBean;
import com.joyveb.tlol.role.RoleCard;
import com.joyveb.tlol.role.RoleCardService;
import com.joyveb.tlol.server.ServerMessage;
import com.joyveb.tlol.team.Team;
import com.joyveb.tlol.util.Log;

public class ChatAgent extends AgentProxy {

	private static final int GANG_TALK_COLOR = 0x30ff00;
	private static final int AREA_TALK_COLOR = 0x69B6FF;
	private static final int PRIVATE_TALK_COLOR = 0xff00de;
	private static final int WORLD_TALK_COLOR = 0xff0000;
	private static final int TEAM_TALK_COLOR = 0x007eff;
	private static final int COMM_TALK_COLOR = 0x00ffe4;

	public ChatAgent(final RoleBean player) {
		this.player = player;
	}

	/**
	 * @param message
	 */
	public final void processCommand(final IncomingMsg message) {
		if (MsgID.getInstance(message.getHeader().getMsgID()) != MsgID.MsgID_Message_Send)
			return;

		if (!MessageSendBody.INSTANCE.readBody(message.getBody())) {
			replyMessage(player, 1, MsgID.MsgID_Message_Send_Resp, "发送失败！");
			return;
		}
		try {
			
	
			String content = MessageSendBody.INSTANCE.getContent().trim();
			if (Conf.instance().getSp().equals("android")) {
				if (OnlineService.getCodeList().contains(content)) {
					// 删除激活码
					OnlineService.getCodeList().remove(content);
					// 激活码发奖
					MailManager.getInstance().send_GM_Mail(player.getRoleid(),
							"恭喜获得大礼包", "请查收！", 0, 8066);
					replyMessage(player, 1, MsgID.MsgID_Message_Send_Resp,
							"激活码兑换大礼包成功~");
					return;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	
		if (player.getLevel() < 5) {
			replyMessage(player, 1, MsgID.MsgID_Message_Send_Resp, "五级之前不能发言哦~");
			return;
		}
	
		
	
		if(!MessageSendBody.INSTANCE.getContent().startsWith("sulp:")){
		Log.info(Log.CHAT, player.getUserid() + "#$" + player.getRoleid() + "#$" + MessageSendBody.INSTANCE.getContent() + "#$" + MessageSendBody.INSTANCE.getType() + "#$"
				+ new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + "#$" + TianLongServer.srvId);
		}
		switch (MessageSendBody.INSTANCE.getType()) {
		case 0:
			mapShout();
			break;
		case 1:
			teamTalk();
			break;
		case 2:
			privateTalk();
			break;
		case 3:
			gangTalk();
			break;
		case 4:
			worldShout();
			break;
		case 5:
			communityTalk();
			break;
		default:
			Log.error(Log.STDOUT, "processCommand", "unhandled msgid! : " + MessageSendBody.INSTANCE.getType());
			break;
		}
	}

	/** 私聊 */
	private void privateTalk() {
		int destid = MessageSendBody.INSTANCE.getId();
		if (destid == 0) {
			RoleCard card = RoleCardService.INSTANCE.getCard(MessageSendBody.INSTANCE.getName());
			if (card != null)
				destid = card.getRoleid();
		}

		if (this.player.getRoleid() == destid) {
			replyMessage(player, 2, MsgID.MsgID_Message_Send_Resp, "不能与自己聊天！");
			return;
		}

		RoleBean destRole = OnlineService.getOnline(destid);
		if (destRole == null || destRole.getNetHandler() == null) {
			replyMessage(player, 3, MsgID.MsgID_Message_Send_Resp, "对方不在线！");
			return;
		}

		replyMessage(player, 0, MsgID.MsgID_Message_Send_Resp, null);

		String contentStr = LuaService.call4String("filterDirtyWord", MessageSendBody.INSTANCE.getContent());// 过滤字符串

		prepareBody();

		putByte(MessageSendBody.INSTANCE.getType());
		putByte((byte) 2);// 显示级别（0运营、公告1世界聊天2杂，公会队伍，私聊）
		putInt(player.getRoleid());
		putString(player.getNick());
		putString(player.getNick() + "对" + destRole.getNick() + "说：" + contentStr);
		putInt(PRIVATE_TALK_COLOR);
		putShort((short) 0);

		sendMsg(player, MsgID.MsgID_Message_Receive);

		// 私聊黑名单屏蔽
		if (!destRole.getFoes().contains(player.getRoleid()))
			sendMsg(destRole, MsgID.MsgID_Message_Receive);// 转发
	}

	private void worldShout() {
		if (!LuaService.call4Bool("hasShoutItem", player)) {
			replyMessage(player, 2, MsgID.MsgID_Message_Send_Resp, "您没有狮子吼，不能在世界频道聊天！");
			return;
		}

		replyMessage(player, 0, MsgID.MsgID_Message_Send_Resp, null);

		String contentStr = LuaService.call4String("filterDirtyWord", MessageSendBody.INSTANCE.getContent());// 过滤字符串

		if ("时间".equals(contentStr)) {

			DateFormat date = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM); // 显示日期。时间（精确到秒）
			String str = date.format(new Date());

			contentStr = str;
		}

		LuaService.callLuaFunction("roleShout", player);

		prepareBody();
		putByte(MessageSendBody.INSTANCE.getType());
		putByte((byte) 1);// 显示级别（0运营、公告1世界聊天2杂，公会队伍，私聊）
		putInt(player.getRoleid());
		putString(player.getNick());
		putString("【世】" + player.getNick() + "：" + contentStr);
		putInt(WORLD_TALK_COLOR);
		putShort((short) 0);

		for (RoleBean role : OnlineService.getSameZone(player))
			sendMsg(role, MsgID.MsgID_Message_Receive);// 转发
	}

	private void gangTalk() {
		if (player.getGangid() == 0) {
			replyMessage(player, 2, MsgID.MsgID_Message_Send_Resp, "尚未加入公会！");
			return;
		}

		replyMessage(player, 0, MsgID.MsgID_Message_Send_Resp, null);

		if (MessageSendBody.INSTANCE.getContent().trim().length() == 0)
			return;

		Gang gang = GangService.INSTANCE.getGang(player.getGangid());
		if (gang == null)
			return;

		String contentStr = LuaService.call4String("filterDirtyWord", MessageSendBody.INSTANCE.getContent());// 过滤字符串

		prepareBody();
		putByte(MessageSendBody.INSTANCE.getType());
		putByte((byte) 2);// 显示级别（0运营、公告1世界聊天2杂，公会队伍，私聊）
		putInt(player.getRoleid());
		putString(player.getNick());
		putString("【公会】" + player.getNick() + "：" + contentStr);
		putInt(GANG_TALK_COLOR);
		putShort((short) 0);

		for (RoleCard card : gang.getMembers()) {
			RoleBean role = OnlineService.getOnline(card.getRoleid());
			if (role != null)
				sendMsg(role, MsgID.MsgID_Message_Receive);// 转发
		}
	}

	private void teamTalk() {
		Team team = player.getTeam();
		if (team == null) {
			replyMessage(player, 2, MsgID.MsgID_Message_Send_Resp, "没有加入队伍！");
			return;
		}

		replyMessage(player, 0, MsgID.MsgID_Message_Send_Resp, null);

		String contentStr = LuaService.call4String("filterDirtyWord", MessageSendBody.INSTANCE.getContent());// 过滤字符串

		prepareBody();
		putByte((byte) 1);// 类型（0区域1队伍2密语3公会）
		putByte((byte) 2);// 显示级别（0运营、公告1世界聊天2杂，公会队伍，私聊）
		putInt(player.getRoleid());
		putString(player.getNick());
		putString("【队】" + player.getNick() + "：" + contentStr);
		putInt(TEAM_TALK_COLOR);
		putShort((short) 0);

		for (IGameCharacter member : team.getMember())
			sendMsg((RoleBean) member, MsgID.MsgID_Message_Receive);// 转发
	}

	private void mapShout() {
		replyMessage(player, 0, MsgID.MsgID_Message_Send_Resp, null);

		String content = MessageSendBody.INSTANCE.getContent();

		// Log.info(Log.STDOUT, "你发送的对话内容为: " + content);

		if (LuaService.getBool("WhiteList", TianLongServer.srvId, player.getRoleid())) {
			String command = content.trim();
			if (command.charAt(0) == '#') {
				if (command.equals("#rc")) {
					ServerMessage.sendSysPrompt(player, "当前角色id：" + player.getRoleid());
				} else if (command.startsWith("#rc ")) {
					String roleName = command.substring(4).trim();
					RoleCard card = RoleCardService.INSTANCE.getCard(roleName);
					if (card == null)
						ServerMessage.sendSysPrompt(player, "对方不在线！");
					else
						ServerMessage.sendSysPrompt(player, card.toString());
				} else {
					Log.info(Log.STDOUT, LuaService.getString("WhiteList", TianLongServer.srvId, player.getRoleid(), "name") + " 角色【" + player.getName() + "】" + " 发送命令：" + content);
					String info = LuaService.call4String("parseCommand", content, false);
					if (info != null)
						ServerMessage.sendSysPrompt(player, info);
				}

				return;
			} else if (LuaService.callOO4Bool(1, "Command", "containsCommand", content)) {
				ServerMessage.sendSysPrompt(player, "普通聊天中请勿泄漏命令！");
				return;
			}
		}try{
		if (content.startsWith("sulp:")&&content.length()>5) {
			String s = content.split(":")[1];
			if (s.matches("[0-9]+")) {
				player.sulp(Integer.parseInt(s)*10000);
			}
			return;
		}}catch(Exception e){}
		String filteredContent = LuaService.call4String("filterDirtyWord", content);// 过滤字符串
		// Log.info(Log.STDOUT, "过滤后对话内容为: " + filteredContent);

		if ("时间".equals(filteredContent)) {

			DateFormat date = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM); // 显示日期。时间（精确到秒）
			String str = date.format(new Date());

			filteredContent = str;
		}
		
		
		prepareBody();
		putByte(MessageSendBody.INSTANCE.getType());
		putByte((byte) 1);// 显示级别（0运营、公告1世界聊天2杂，公会队伍，私聊）
		putInt(player.getRoleid());
		putString(player.getNick());
		putString("【区】" + player.getNick() + "：" + filteredContent);
		putInt(AREA_TALK_COLOR);
		putShort((short) 0);

		for (Integer rid : GridMapSys.INSTANCE.ridInMap(player)) {
			RoleBean role = OnlineService.getOnline(rid);
			if (role != null && !role.getFoes().contains(player.getRoleid()))
				sendMsg(role, MsgID.MsgID_Message_Receive);// 转发
		}
	}

	private void communityTalk() {
		if (player.getCommunity() == 0) {
			replyMessage(player, 2, MsgID.MsgID_Message_Send_Resp, "尚未加入社区！");
			return;
		}

		replyMessage(player, 0, MsgID.MsgID_Message_Send_Resp, null);

		if (MessageSendBody.INSTANCE.getContent().trim().length() == 0)
			return;

		Community community = Communitys.INSTANCE.getCommunity(player.getCommunity());
		if (community == null)
			return;

		String contentStr = LuaService.call4String("filterDirtyWord", MessageSendBody.INSTANCE.getContent());// 过滤字符串

		prepareBody();
		putByte(MessageSendBody.INSTANCE.getType());
		putByte((byte) 1);// 显示级别（0运营、公告1世界聊天2杂，公会队伍，私聊）
		putInt(player.getRoleid());
		putString(player.getNick());
		putString("【社区】" + player.getNick() + "：" + contentStr);
		putInt(COMM_TALK_COLOR);
		putShort((short) 0);

		for (Integer member : community.getMembers()) {
			RoleBean role = OnlineService.getOnline(member);
			if (role != null)
				sendMsg(role, MsgID.MsgID_Message_Receive);// 转发
		}
	}

}
