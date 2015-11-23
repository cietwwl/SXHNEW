package com.joyveb.tlol.charge;

import com.joyveb.tlol.LuaService;
import com.joyveb.tlol.core.AgentProxy;
import com.joyveb.tlol.mail.MailManager;
import com.joyveb.tlol.net.IncomingMsg;
import com.joyveb.tlol.pay.domain.WapPayState;
import com.joyveb.tlol.pay.util.Constants;
import com.joyveb.tlol.protocol.MsgID;
import com.joyveb.tlol.role.RoleBean;
import com.joyveb.tlol.util.Log;

public class WapPayAgent extends AgentProxy {

	public WapPayAgent() {
		super();
	}

	public WapPayAgent(final RoleBean player) {
		this.player = player;
	}

	@Override
	public void processCommand(final IncomingMsg msg) {
		// TODO Auto-generated method stub

	}

	// 线上发送邮件，并刷新
	public final void wapOnlineHandle(final WapPayState payState) {
		if (payState.getState() == "true") {
			int chargedAmt = payState.getPayamt();
			player.addMoney(chargedAmt);
			String subject = "充值成功"; // 客户端现在显示不了长些的标题
			String content = "用户您好，您本次消费了" + payState.getRealmoney()
					+ "元,我们刚刚为您的账号冲入" + payState.getPayamt() + "元宝,请在商城查收！";

			MailManager.getInstance().sendSysMail(player.getId(), subject,
					content, 0, null);

			// 刷新基本属性
			prepareBody();
			LuaService.callLuaFunction("fillAttributes", player);
			body.putShort((short) 0);
			sendMsg(player, MsgID.MsgID_Special_Train);

			Log.info(Log.STDOUT, "user id: " + player.getUserid() + "role id: "
					+ player.getRoleid() + "role name: " + player.getName()
					+ " 充值成功, 获得" + payState.getPayamt() + "元宝");
		} else {
			String subject = "充值失败";
			String content = null;
			if (Constants.ERRORCODE.get(payState.getWappaycode()) != null) {
				content = Constants.ERRORCODE.get(payState.getWappaycode());
			} else {
				content = Constants.ERRORCODE.get("0203");
			}

			MailManager.getInstance().sendSysMail(player.getId(), subject,
					content, 0, null);
			Log.info(
					Log.STDOUT,
					"user id: " + player.getUserid() + "role id: "
							+ player.getRoleid() + "role name: "
							+ player.getName() + " 充值失败, 错误原因"
							+ Constants.ERRORCODE.get(payState.getWappaycode()));
		}
	}

	// 线下发送邮件
	public final void wapUnderlineHandle(final WapPayState payState) {
		if (payState.getState() == "true") {
			String subject = "充值成功"; // 客户端现在显示不了长些的标题
			String content = "用户您好，您本次消费了" + payState.getRealmoney()
					+ "元,我们刚刚为您的账号冲入" + payState.getPayamt() + "元宝,请在商城查收！";

			MailManager.getInstance().sendSysMail(payState.getRoleid(),
					subject, content, 0, null);
			Log.info(Log.STDOUT, "user id: " + payState.getUserid()
					+ "role id: " + payState.getRoleid() + " 充值成功, 获得"
					+ payState.getPayamt() + "元宝");
		} else {
			String subject = "充值失败";
			String content = null;
			if (Constants.ERRORCODE.get(payState.getWappaycode()) != null) {
				content = Constants.ERRORCODE.get(payState.getWappaycode());
			} else {
				content = Constants.ERRORCODE.get("0203");
			}
			MailManager.getInstance().sendSysMail(payState.getRoleid(),
					subject, content, 0, null);
			Log.info(Log.STDOUT, "user id: " + payState.getUserid()
					+ "role id: " + payState.getRoleid() + " 充值失败, 错误代码"
					+ Constants.ERRORCODE.get(payState.getWappaycode()));
		}
	}
}
