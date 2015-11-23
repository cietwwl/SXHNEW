package com.joyveb.tlol.buff;

import com.joyveb.tlol.MessageSend;
import com.joyveb.tlol.SubModules;
import com.joyveb.tlol.protocol.MsgID;
import com.joyveb.tlol.role.RoleBean;

public class VIPBuff extends NatureTimeBuff {

	@Override
	public final void initOnline(final RoleBean player) {
		player.setVIP(true);
		player.setVIPLevel(this.buffLevel);
	}

	@Override
	public final void addToRole(final RoleBean player) {
		player.setVIP(true);
		player.setVIPLevel(this.buffLevel);

		MessageSend.prepareBody();
		SubModules.fillVIPInfo(player);
		MessageSend.putShort((short) 0);
		MessageSend.sendMsg(player, MsgID.MsgID_Special_Train);
	}

	@Override
	public final void delFromRole(final RoleBean player) {
		player.setVIP(false);
		player.setVIPLevel((byte) 0);

		MessageSend.prepareBody();
		SubModules.fillVIPInfo(player);
		MessageSend.putShort((short) 0);
		MessageSend.sendMsg(player, MsgID.MsgID_Special_Train);
	}

}
