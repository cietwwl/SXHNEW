package com.joyveb.tlol.trigger;

import com.joyveb.tlol.MessageSend;
import com.joyveb.tlol.SubModules;
import com.joyveb.tlol.gang.Gang;
import com.joyveb.tlol.gang.GangJobTitle;
import com.joyveb.tlol.gang.GangService;
import com.joyveb.tlol.protocol.MsgID;
import com.joyveb.tlol.role.RoleBean;

public enum GangOnlineCheck implements RoleEventHandler {
	INSTANCE;

	@Override
	public void handleEvent(RoleBean source, Object... args) {
		long gangid = source.getGangid();
		
		if(gangid == 0)
			return;
		
		if(GangService.INSTANCE.isGangDiscarded(gangid)) { //帮派已解散
			source.setGangid(0);
			source.setJobTitle(GangJobTitle.NULL);
		}else if(GangService.INSTANCE.isGangLoaded(gangid)) { //帮派已加载
			Gang gang = GangService.INSTANCE.getGang(gangid);
			if(gang.contains(source)) { //此人还在帮派中
				source.setJobTitle(gang.getJobTitle(source));//可能被设置职位
				
				MessageSend.prepareBody();
				SubModules.fillGangJobTitle(source.getJobTitle(), gang.getName() + source.getJobTitle().getDesAtTitle());
				MessageSend.putShort((short) 0);
				MessageSend.sendMsg(source, MsgID.MsgID_Special_Train);
			}else { //被T
				source.setGangid(0);
				source.setJobTitle(GangJobTitle.NULL);
			}
		}//else //未加载
			//GangService.INSTANCE.loadGang(gangid);
	}

	@Override
	public void removeEvent(RoleBean source) {
		// TODO Auto-generated method stub
		
	}

}