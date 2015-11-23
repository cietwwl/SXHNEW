package com.joyveb.tlol.charge;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.joyveb.tlol.LuaService;
import com.joyveb.tlol.MessageSend;
import com.joyveb.tlol.SubModules;
import com.joyveb.tlol.TianLongServer;
import com.joyveb.tlol.buff.BuffType;
import com.joyveb.tlol.item.Item;
import com.joyveb.tlol.mail.MailManager;
import com.joyveb.tlol.pay.connect.YuanBaoDataHandler;
import com.joyveb.tlol.pay.domain.PayState;
import com.joyveb.tlol.pay.domain.AffordState;
import com.joyveb.tlol.pay.domain.SelectState;
import com.joyveb.tlol.pay.domain.SubtractState;
import com.joyveb.tlol.protocol.MsgID;
import com.joyveb.tlol.role.RoleBean;
import com.joyveb.tlol.util.Log;

public class YuanBaoOp extends MessageSend implements YuanBaoDataHandler {

	RoleBean player;

	public YuanBaoOp(final RoleBean player) {
		this.player = player;
	}

	@Override
	public final void shenZhouCBHandle(final boolean arg0, final String arg1, final PayState payState) {
		if (payState.getState().equals("true")) {
			int chargedAmt = payState.getAmt();
			player.addMoney(chargedAmt);
			String subject = "充值成功"; // 客户端现在显示不了长些的标题
			String content = "用户您好，刚刚为您的账号冲入" + payState.getAmt() + "元宝,请在商城查收！";

			MailManager.getInstance().sendSysMail(player.getId(), subject, content, 0, null);

			// 刷新基本属性
			prepareBody();
			SubModules.fillAttributes(player);
			putShort((short) 0);
			sendMsg(player, MsgID.MsgID_Special_Train);

			Log.info(Log.STDOUT, "user id: " + player.getUserid() + "role id: " + player.getRoleid() + "role name: " + player.getName() + " 充值成功, 获得" + payState.getAmt() + "元宝");
		} else {
			String subject = "充值失败";
			String content = null;
			if (1 == payState.getCode()) {

				content = "用户您好，本次充值失败. /原因可能为：1.充值卡号和密码错误。请您重试！" + "/充值金额:" + payState.getAmt() / 10 + ";/" + "/你的充值订单号:" + payState.getOrder() + "/";

			} else if (2 == payState.getCode()) {

				content = "用户您好，您输入的充值卡已使用，本次充值失败！/" + "/充值金额:" + payState.getAmt() / 10 + ";/" + "/你的充值订单号:" + payState.getOrder() + "/";

			} else if (3 == payState.getCode()) {

				content = "用户您好，由于网络超时，造成本次充值失败，请您重试！/" + "/充值金额:" + payState.getAmt() / 10 + ";/" + "/你的充值订单号:" + payState.getOrder() + "/";
			}

			MailManager.getInstance().sendSysMail(player.getId(), subject, content, 0, null);
			Log.info(Log.STDOUT, "user id: " + player.getUserid() + "role id: " + player.getRoleid() + "role name: " + player.getName() + " 充值失败, 错误代码" + payState.getCode());
		}

	}

	@Override
	public final void yuanBaoConsumeCBHandle(final boolean arg0, final String arg1, final SubtractState subtractState) {
		if (subtractState.getState().equals("true")) {

			switch (subtractState.getSubConst()) {
			case SubtractState.Buy_ITEM_FROM_MALL:
				player.setMoney(subtractState.getNowamt());

				Item item = LuaService.callOO4Object(2, Item.LUA_CONTAINER, subtractState.getItemid(), "creatJavaItemSingle", subtractState.getItemNum());
				prepareBody();
				putShort((short) 0);
				if (player.hasBuff(BuffType.VIP.getId()))
					LuaService.callLuaFunction("fillNpcRewind", 2, subtractState.getTaskId(), "购买成功，获得：/" + item.getName() + "×" + item.getStorage() + "/您通过VIP节省了"
							+ (subtractState.getOriginalPrice() - subtractState.getCost()) + "元宝");
				else
					LuaService.callLuaFunction("fillNpcRewind", 2, subtractState.getTaskId(), "购买成功，获得：/" + item.getName() + "×" + item.getStorage());

				SubModules.fillAttributes(player);
				LuaService.callLuaFunction("fillBagAdd", item);
				player.getStore().getBag().addItem(item, true);

				// Log.info(Log.ITEM, "[" + player.getNick() + "][" +
				// player.getRoleid() + "][商城购买][" + item.getName() + "][" +
				// item.getStorage() + "]个");

				Log.info(Log.ITEM,
						player.getUserid() + "#$" + player.getRoleid() + "#$13#$" + item.getName() + "#$" + item.getStorage() + "#$" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())
								+ "#$" + TianLongServer.srvId + "#$" + subtractState.getCost() + "#$" + item.getTid() + "#$" + item.getUid());
				putShort((short) 0);

				sendMsg(player, MsgID.MsgID_Talk_To_Npc_Resp);

				player.updateRole();
				player.mallRecord(item, subtractState.getCost());
				break;

			case SubtractState.OPEN_YUANBAO_GOLD_BOX:
				player.setMoney(subtractState.getNowamt());
				LuaService.callOO(2, "Actions", "openYuanbaoGoldBox", "YuanbaoSyntony", player, true);
				break;
			case SubtractState.OPEN_YUANBAO_SILVER_BOX:
				player.setMoney(subtractState.getNowamt());
				LuaService.callOO(2, "Actions", "openYuanbaoSilverBox", "YuanbaoSyntony", player, true);
				break;
			case SubtractState.OPEN_YUANBAO_COPPER_BOX:
				player.setMoney(subtractState.getNowamt());
				LuaService.callOO(2, "Actions", "openYuanbaoCopperBox", "YuanbaoSyntony", player, true);
				break;
			case SubtractState.FASTREFRESH_GOLD_BOX:
				player.setMoney(subtractState.getNowamt());
				LuaService.callOO(2, "Actions", "fastRefreshGoldBox", "YuanbaoSyntony", player, true);
				break;
			case SubtractState.FASTREFRESH_YUANBAO_BOX:
				player.setMoney(subtractState.getNowamt());
				LuaService.callOO(2, "Actions", "fastRefreshYuanbaoBox", "YuanbaoSyntony", player, true);
				break;
			case SubtractState.BET_ACCOUNT:
				player.setMoney(subtractState.getNowamt());
				LuaService.callOO(2, "Actions", "betResult", "YuanbaoSyntony", player, true);
				break;

			case SubtractState.BUY_JINYAOSHI:
				player.setMoney(subtractState.getNowamt());
				LuaService.callOO(2, "Actions", "buyjinyaoshi", "YuanbaoSyntony", player, true);
				break;
			case SubtractState.BUY_BOSS:
				player.setMoney(subtractState.getNowamt());
				LuaService.callOO(2, "Actions", "buyBoss", "YuanbaoSyntony", player, true);
				break;
			}

		} else {
			switch (subtractState.getSubConst()) {
			case SubtractState.Buy_ITEM_FROM_MALL:
				player.setMoney(subtractState.getNowamt());

				prepareBody();

				putShort((short) 0);

				LuaService.callLuaFunction("fillNpcRewind", 2, subtractState.getTaskId(), "购买失败, 元宝不足");

				SubModules.fillAttributes(player);

				putShort((short) 0);

				sendMsg(player, MsgID.MsgID_Talk_To_Npc_Resp);
				break;
			case SubtractState.OPEN_YUANBAO_GOLD_BOX:
				player.setMoney(subtractState.getNowamt());
				LuaService.callOO(2, "Actions", "openYuanbaoGoldBox", "YuanbaoSyntony", player, false);
				break;
			case SubtractState.OPEN_YUANBAO_SILVER_BOX:
				player.setMoney(subtractState.getNowamt());
				LuaService.callOO(2, "Actions", "openYuanbaoSilverBox", "YuanbaoSyntony", player, false);
				break;
			case SubtractState.OPEN_YUANBAO_COPPER_BOX:
				player.setMoney(subtractState.getNowamt());
				LuaService.callOO(2, "Actions", "openYuanbaoCopperBox", "YuanbaoSyntony", player, false);
				break;
			case SubtractState.FASTREFRESH_GOLD_BOX:
				player.setMoney(subtractState.getNowamt());
				LuaService.callOO(2, "Actions", "fastRefreshGoldBox", "YuanbaoSyntony", player, false);
				break;
			case SubtractState.FASTREFRESH_YUANBAO_BOX:
				player.setMoney(subtractState.getNowamt());
				LuaService.callOO(2, "Actions", "fastRefreshYuanbaoBox", "YuanbaoSyntony", player, false);
				break;
			case SubtractState.BET_ACCOUNT:
				player.setMoney(subtractState.getNowamt());
				LuaService.callOO(2, "Actions", "betResult", "YuanbaoSyntony", player, false);
				break;
			case SubtractState.BUY_JINYAOSHI:
				player.setMoney(subtractState.getNowamt());
				LuaService.callOO(2, "Actions", "buyjinyaoshi", "YuanbaoSyntony", player, false);
				break;
			case SubtractState.BUY_BOSS:
				player.setMoney(subtractState.getNowamt());
				LuaService.callOO(2, "Actions", "buyBoss", "YuanbaoSyntony", player, false);
				break;
			}
		}
	}

	@Override
	public final void yuanBaoAffordCBHandle(final boolean arg0, final String arg1, final AffordState affordState) {
		if (affordState.getState().equals("true")) {

			switch (affordState.getPayConst()) {
			case SubtractState.BET_ACCOUNT:
				player.setMoney(affordState.getNowamt());
				// 刷新基本属性
				prepareBody();
				SubModules.fillAttributes(player);
				putShort((short) 0);
				sendMsg(player, MsgID.MsgID_Special_Train);
				break;
			}

		} else {

			switch (affordState.getPayConst()) {
			case SubtractState.BET_ACCOUNT:
				player.setMoney(affordState.getNowamt());
				// 刷新基本属性
				prepareBody();
				SubModules.fillAttributes(player);
				putShort((short) 0);
				sendMsg(player, MsgID.MsgID_Special_Train);
				break;
			}
		}
	}

	@Override
	public void yuanBaoSelectCBHandle(boolean flag, String result, SelectState state) {
		if (state.getState().equals("true")) {

			switch (state.getPayConst()) {
			case SubtractState.SELECT_YUANBAO:
				player.setMoney(state.getNowamt());
				// 刷新基本属性
				prepareBody();
				SubModules.fillAttributes(player);
				putShort((short) 0);
				sendMsg(player, MsgID.MsgID_Special_Train);
				break;
			}
		} else {

			switch (state.getPayConst()) {
			case SubtractState.SELECT_YUANBAO:
				player.setMoney(state.getNowamt());
				// 刷新基本属性
				prepareBody();
				SubModules.fillAttributes(player);
				putShort((short) 0);
				sendMsg(player, MsgID.MsgID_Special_Train);
				break;
			}
		}

	}
}
