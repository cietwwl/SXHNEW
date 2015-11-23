package com.joyveb.tlol.gang;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Set;

import com.joyveb.tlol.LuaService;
import com.joyveb.tlol.OnlineService;
import com.joyveb.tlol.SubModules;
import com.joyveb.tlol.core.AgentProxy;
import com.joyveb.tlol.db.DataHandler;
import com.joyveb.tlol.db.DataStruct;
import com.joyveb.tlol.db.parser.CommonParser;
import com.joyveb.tlol.db.parser.DbConst;
import com.joyveb.tlol.db.parser.GangNameCheckData;
import com.joyveb.tlol.net.IncomingMsg;
import com.joyveb.tlol.protocol.GangInviteEchoBody;
import com.joyveb.tlol.protocol.MsgID;
import com.joyveb.tlol.role.RoleBean;
import com.joyveb.tlol.role.RoleCard;
import com.joyveb.tlol.role.RoleCardService;
import com.joyveb.tlol.schedule.OneOffSchedule;
import com.joyveb.tlol.schedule.ScheduleManager;
import com.joyveb.tlol.server.ServerMessage;
import com.joyveb.tlol.util.Log;

public class GangAgent extends AgentProxy implements DataHandler {

	public GangAgent(RoleBean player) {
		this.player = player;
	}

	@Override
	public void processCommand(IncomingMsg message) {
		switch (MsgID.getInstance(message.getHeader().getMsgID())) {
		case MsgID_Gang_Creat:
			if (MsgID.MsgID_Gang_Creat.readBody(message.getBody()))
				gangCreat();
			else
				replyMessage(player, 1, MsgID.MsgID_Gang_Creat_Resp, "查看失败！");

			break;
		case MsgID_Gang_Quit:
			gangQuit();
			break;
		case MsgID_Gang_Invite:
			if (MsgID.MsgID_Gang_Invite.readBody(message.getBody()))
				gangInvite();
			else
				replyMessage(player, 1, MsgID.MsgID_Gang_Invite_Resp, "邀请失败！");
			break;
		case Gang_Invite_Echo:
			if (GangInviteEchoBody.INSTANCE.readBody(message.getBody()))
				gangInviteEcho();
			else
				replyMessage(player, 1, MsgID.Gang_Invite_Echo_Resp, "处理失败！");
			break;
		case Gang_View:
			viewGang();
			break;
		case Gang_View_Page:
			if (MsgID.Gang_View_Page.readBody(message.getBody()))
				viewPage();
			else
				replyMessage(player, 1, MsgID.Gang_View_Page_Resp, "查看失败！");
			break;
		case Gang_Expel:
			if (MsgID.Gang_Expel.readBody(message.getBody()))
				expel();
			else
				replyMessage(player, 1, MsgID.Gang_View_Page_Resp, "查看失败！");
			break;
		case Gang_Change_Bulletin:
			// if(MsgID.Gang_Change_Bulletin.readBody(message.getBody()))
			// changeBulletin();
			// else
			// replyMessage(player, 1, MsgID.Gang_Change_Bulletin_Resp,
			// "修改失败！");

			changeBulletin(message.getBody());

			break;
		}
	}

	/**
	 * 创建帮派
	 */
	public void gangCreat() {
		if (player.getGangid() > 0) {
			replyMessage(player, 2, MsgID.MsgID_Gang_Creat_Resp, "创建失败，已经加入帮派！");
			return;
		}

		if (!LuaService.call4Bool("chkCreatGangConditons", player))
			return;

		final String gangName = MsgID.MsgID_Gang_Creat.getMsgBody().getString(1);

		if (gangName.replaceAll(" ", "").toUpperCase().contains("GM") || gangName.replaceAll(" ", "").toUpperCase().contains("VIP")) {
			replyMessage(player, 4, MsgID.MsgID_Gang_Creat_Resp, "此名称已存在！");
			return;
		}

		if (GangService.INSTANCE.isNameExist(gangName)) {
			replyMessage(player, 5, MsgID.MsgID_Gang_Creat_Resp, "此名称已存在！");
			return;
		}

		if (gangName.length() < 2) {
			replyMessage(player, 6, MsgID.MsgID_Gang_Creat_Resp, "名称过短！");
			return;
		}

		if (gangName.length() > 6) {
			replyMessage(player, 7, MsgID.MsgID_Gang_Creat_Resp, "名称过长！");
			return;
		}

		if (!LuaService.call4Bool("checkDirtyWord", gangName)) {
			replyMessage(player, 8, MsgID.MsgID_Gang_Creat_Resp, "非法的名称！");
			return;
		}

		CommonParser.getInstance().postTask(DbConst.Gang_Name_Check, this, new GangNameCheckData(gangName));
	}

	private void gangCreat(boolean flag, DataStruct ds) {
		GangNameCheckData gangNameCheckData = (GangNameCheckData) ds;
		if (!flag || gangNameCheckData.isNameExist() || GangService.INSTANCE.isNameExist(gangNameCheckData.getName())) {
			replyMessage(player, 8, MsgID.MsgID_Gang_Creat_Resp, "名称已存在！");
			return;
		}

		if (!LuaService.call4Bool("chkCreatGangConditons", player))
			return;

		Gang gang = GangService.INSTANCE.creatGang(player, gangNameCheckData.getName());

		// 移除任务状态
		player.getTasks().delTaskState(LuaService.call4Int("getCreatGangTaskid"));

		prepareBody();
		putShort((short) 0);

		LuaService.callLuaFunction("creatGangChargeback", player);

		LuaService.callLuaFunction("fillPrompt", "帮派【" + gang.getName() + "】创建成功～");

		SubModules.fillGangJobTitle(GangJobTitle.Leader, gang.getName() + GangJobTitle.Leader.getDesAtTitle());

		LuaService.callLuaFunction("fillFreshNPCState", player);

		putShort((short) 0);

		sendMsg(player, MsgID.MsgID_Gang_Creat_Resp);
	}

	@Override
	public void handle(DbConst eventID, boolean flag, DataStruct ds) {
		switch (eventID) {
		case Gang_Name_Check:
			gangCreat(flag, ds);
			break;
		case Gang_Delete:
			replyMessage(player, 0, MsgID.MsgID_Gang_Quit_Resp, "删除成功！");
			break;
		}
	}

	private void gangQuit() {
		if (player.getGangid() == 0) {
			replyMessage(player, 1, MsgID.MsgID_Gang_Quit_Resp, "尚未加入帮派！");
			return;
		}

		if (player.getJobTitle().highRigths()) {
			replyMessage(player, 2, MsgID.MsgID_Gang_Quit_Resp, "帮主无法退出帮派，如需解散帮派请前往帮派管理员处！");
			return;
		}

		Gang gang = GangService.INSTANCE.getGang(player.getGangid());
		if (gang == null) {
			replyMessage(player, 0, MsgID.MsgID_Gang_Quit_Resp, "退出成功！");
			return;
		}

		gang.removeMember(RoleCardService.INSTANCE.getCard(player.getRoleid()));
		player.setGangid(0);
		player.setJobTitle(GangJobTitle.NULL);
		replyMessage(player, 0, MsgID.MsgID_Gang_Quit_Resp, "退出成功！");

		prepareBody();
		SubModules.fillGangJobTitle(GangJobTitle.NULL, null);
		putShort((short) 0);
		sendMsg(player, MsgID.MsgID_Special_Train);
	}

	private void gangInvite() {
		if (!GangService.INSTANCE.isGangLoaded(player.getGangid())) {
			replyMessage(player, 2, MsgID.MsgID_Gang_Invite_Resp, "邀请失败，您尚未加入帮派！");
			return;
		}

		if (!player.getJobTitle().hasRight(GangRight.INVITE)) {
			replyMessage(player, 3, MsgID.MsgID_Gang_Invite_Resp, "邀请失败，没有相关权限！");
			return;
		}

		Gang gang = GangService.INSTANCE.getGang(player.getGangid());

		if (gang.fullStrength()) {
			replyMessage(player, 4, MsgID.MsgID_Gang_Invite_Resp, "帮派人数已达上限！");
			return;
		}

		String oppositeName = MsgID.MsgID_Gang_Invite.getMsgBody().getString(1);
		if (oppositeName.equals(player.getName())) {
			replyMessage(player, 2, MsgID.MsgID_Gang_Invite_Resp, "邀请失败，不能邀请自己！");
			return;
		}
		RoleBean opposite = null;

		RoleCard card = RoleCardService.INSTANCE.getCard(oppositeName);
		if (card != null)
			opposite = OnlineService.getOnline(card.getRoleid());

		if (opposite == null)
			replyMessage(player, 5, MsgID.MsgID_Gang_Invite_Resp, "邀请失败，对方不在线！");
		else if (opposite.getJobTitle() != GangJobTitle.NULL)
			replyMessage(player, 6, MsgID.MsgID_Gang_Invite_Resp, "邀请失败，对方已有帮派！");
		else {
			prepareBody();

			putLong(gang.getId());
			putString("玩家【" + player.getName() + "】邀请您加入他所在的帮派【" + gang.getName() + "】，是否同意？");

			sendMsg(opposite, MsgID.Gang_Invite_Transmit);

			replyMessage(player, 0, MsgID.MsgID_Gang_Invite_Resp, "邀请已发送！");
		}
	}

	private void gangInviteEcho() {
		if (!GangInviteEchoBody.INSTANCE.isAgree())
			return;

		if (player.getGangid() > 0) {
			replyMessage(player, 2, MsgID.Gang_Invite_Echo_Resp, "您已加入帮派！");
			return;
		}

		long gangid = GangInviteEchoBody.INSTANCE.getGangid();
		if (!GangService.INSTANCE.isGangLoaded(gangid)) {
			replyMessage(player, 3, MsgID.Gang_Invite_Echo_Resp, "处理失败，该帮派已解散！");
			return;
		}

		Gang gang = GangService.INSTANCE.getGang(gangid);
		if (gang.fullStrength()) {
			replyMessage(player, 4, MsgID.MsgID_Gang_Invite_Resp, "加入失败！该帮派人数已达上限！");
			return;
		}

		gang.addMember(RoleCardService.INSTANCE.getCard(player.getRoleid()), true);

		player.setGangid(gangid);
		player.setJobTitle(GangJobTitle.Member);

		prepareBody();
		putShort((short) 0);
		LuaService.callLuaFunction("fillPrompt", "您已加入帮派【" + gang.getName() + "】！");
		SubModules.fillGangJobTitle(GangJobTitle.Member, gang.getName() + GangJobTitle.Member.getDesAtTitle());
		putShort((short) 0);
		sendMsg(player, MsgID.Gang_Invite_Echo_Resp);
	}

	private void viewGang() {
		if (player.getJobTitle() == GangJobTitle.NULL) {
			replyMessage(player, 2, MsgID.Gang_View_Resp, "尚未加入帮派！");
			return;
		}

		if (GangService.INSTANCE.isGangDiscarded(player.getGangid())) {
			replyMessage(player, 3, MsgID.Gang_View_Resp, "您所在的帮派已解散！");
			player.setGangid(0);
			player.setJobTitle(GangJobTitle.NULL);
			return;
		}

		if (!GangService.INSTANCE.isGangLoaded(player.getGangid())) {
			replyMessage(player, 4, MsgID.Gang_View_Resp, "您的帮派正在加载中，请稍候！");
			return;
		}

		Gang gang = GangService.INSTANCE.getGang(player.getGangid());
		gang.sortMembers();

		prepareBody();
		putShort((short) 0);

		putString(gang.getDescribe());
		putString(gang.getBulletin());

		// 帮会通缉令
		putString(gang.getCatchOrder());

		ArrayList<RoleCard> cards = gang.getMembers();

		putByte((byte) 1);// 当前页码
		putByte((byte) (Math.ceil((double) cards.size() / Gang.Per_Page)));// 总页数
		putByte(Gang.Per_Page);// 单页显示数量
		putByte((byte) Math.min(Gang.Per_Page, cards.size()));// 本页数量

		for (int i = 0; i < Math.min(Gang.Per_Page, cards.size()); i++) {
			RoleCard card = cards.get(i);
			putInt(card.getRoleid());

			GangJobTitle jobTitle = gang.getJobTitle(card);
			putByte(jobTitle.getJobTitleVaule());

			putString(jobTitle.getDes() + "：" + card.getName() + "  " + card.getLevel() + "级 " + gang.getTributeStat().get(card.getRoleid()));
			putInt(card.isRoleOnline() ? 0 : 0xAAAAAA);
		}

		sendMsg(player, MsgID.Gang_View_Resp);
	}

	private void viewPage() {
		int page = MsgID.Gang_View_Page.getMsgBody().getInt(1);
		int index = page - 1;

		if (player.getJobTitle() == GangJobTitle.NULL) {
			replyMessage(player, 2, MsgID.Gang_View_Resp, "尚未加入帮派！");
			return;
		}

		if (GangService.INSTANCE.isGangDiscarded(player.getGangid())) {
			replyMessage(player, 3, MsgID.Gang_View_Resp, "您所在的帮派已解散！");
			player.setGangid(0);
			player.setJobTitle(GangJobTitle.NULL);
			return;
		}

		if (!GangService.INSTANCE.isGangLoaded(player.getGangid())) {
			replyMessage(player, 4, MsgID.Gang_View_Resp, "您的帮派正在加载中，请稍候！");
			return;
		}

		Gang gang = GangService.INSTANCE.getGang(player.getGangid());

		ArrayList<RoleCard> cards = gang.getMembers();

		if (index * Gang.Per_Page >= cards.size())
			index = 0;
		else if (index < 0)
			index = (int) Math.ceil((double) cards.size() / Gang.Per_Page) - 1;

		page = index + 1;
		int pageStart = index * Gang.Per_Page;
		int pageEnd = (index + 1) * Gang.Per_Page;
		if (pageEnd > cards.size())
			pageEnd = cards.size();

		prepareBody();
		putShort((short) 0);

		putByte((byte) page);// 当前页码
		putByte((byte) (Math.ceil((double) cards.size() / Gang.Per_Page)));// 总页数
		putByte(Gang.Per_Page);// 单页显示数量
		putByte((byte) (pageEnd - pageStart));// 本页数量

		for (int i = pageStart; i < pageEnd; i++) {
			RoleCard card = cards.get(i);
			putInt(card.getRoleid());

			GangJobTitle jobTitle = gang.getJobTitle(card);
			putByte(jobTitle.getJobTitleVaule());

			putString(jobTitle.getDes() + "：" + card.getName() + "  " + card.getLevel() + "级 " + gang.getTributeStat().get(card.getRoleid()));
			putInt(card.isRoleOnline() ? 0 : 0xAAAAAA);
		}

		sendMsg(player, MsgID.Gang_View_Page_Resp);
	}

	private void expel() {
		Gang gang = GangService.INSTANCE.getGang(player.getGangid());

		if (gang == null || !gang.getJobTitle(player).hasRight(GangRight.EXPEL)) {
			replyMessage(player, 2, MsgID.Gang_Expel_Resp, "权限不足！");
			return;
		}

		int roleid = MsgID.Gang_Expel.getMsgBody().getInt(1);

		if (roleid == player.getRoleid()) {
			replyMessage(player, 3, MsgID.Gang_Expel_Resp, "不能踢出自己！");
			return;
		}

		Set set1 = gang.getPresbyter();// 长老权限
		Set set2 = gang.getViceLeader();// 副帮主权限
		if (roleid == gang.getLeader() || (set2.contains(roleid) && player.getRoleid() != gang.getLeader()) 
				|| (set1.contains(roleid) && (player.getRoleid() != gang.getLeader()))) {
			replyMessage(player, 4, MsgID.Gang_Expel_Resp, "权限不足！");
			return;
		}

		RoleCard card = RoleCardService.INSTANCE.getCard(roleid);
		final ArrayList<RoleCard> members = gang.getMembers();

		final String message = card.getName() + "被" + player.getNick() + "踢出帮派～";
		ScheduleManager.INSTANCE.offerTask(new OneOffSchedule() {
			public void execute() {
				ServerMessage.batchSendSysPrompt(members, message);
			}
		});
		gang.removeMember(card);
		if (card.isRoleOnline()) {
			RoleBean role = card.getRole();
			role.setGangid(0);
			role.setJobTitle(GangJobTitle.NULL);

			prepareBody();
			SubModules.fillGangJobTitle(GangJobTitle.NULL, null);
			LuaService.call(0, "fillPrompt", "您已被管理员踢出帮派！");
			putShort((short) 0);
			sendMsg(role, MsgID.MsgID_Special_Train);
		}

		replyMessage(player, 0, MsgID.Gang_Expel_Resp, card.getName() + "已被踢出帮派！");
	}

	private void changeBulletin(ByteBuffer body) {
		Gang gang = GangService.INSTANCE.getGang(player.getGangid());

		if (gang == null || !gang.getJobTitle(player).hasRight(GangRight.CHANGE_BULLETIN) || player.getRoleid() != gang.getLeader()) {
			replyMessage(player, 2, MsgID.Gang_Change_Bulletin_Resp, "权限不足！");
			return;
		}

		body.position(0);

		body.getInt();

		byte type = body.get();// 修改类型

		short bulletinLen = body.getShort();// 公告长度

		String bulletin = null;
		if (bulletinLen > 0)
			bulletin = getUTFString(body, bulletinLen);
		else
			bulletin = "";

		short catchOrderLen = body.getShort();
		String catchOrder = null;
		if (catchOrderLen > 0)
			catchOrder = getUTFString(body, catchOrderLen);
		else
			catchOrder = "";

		if (bulletinLen == 0 && catchOrderLen == 0) {
			replyMessage(player, 1, MsgID.Gang_Change_Bulletin_Resp, "修改失败！");
			return;
		}

		gang.setModifyType(type);

		switch (type) {
		case 0:// 修改公告
			if (bulletin != null && bulletin.length() > 0)
				gang.resetBulletin(bulletin);
			break;
		case 1:// 修改通缉令
				// 获取帮会通缉令
			if (catchOrder != null && catchOrder.length() > 0) {
				gang.resetCatchOrder(catchOrder);
			}
			break;
		}

		replyMessage(player, 0, MsgID.Gang_Change_Bulletin_Resp, null);
	}

	public static String getUTFString(final ByteBuffer body, final short len) {
		byte[] temp = new byte[len];
		body.get(temp);
		try {
			return new String(temp, "utf-8");
		} catch (UnsupportedEncodingException e) {
			Log.error(Log.STDOUT, "getStrByLen", e);
			return "";
		}
	}

}