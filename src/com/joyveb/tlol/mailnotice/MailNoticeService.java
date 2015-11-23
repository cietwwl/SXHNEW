package com.joyveb.tlol.mailnotice;

import java.util.HashMap;
import java.util.Set;
import com.joyveb.tlol.db.DataHandler;
import com.joyveb.tlol.db.DataStruct;
import com.joyveb.tlol.db.parser.CommonParser;
import com.joyveb.tlol.db.parser.DbConst;
import com.joyveb.tlol.db.parser.MailNoticeData;
import com.joyveb.tlol.util.Log;

public enum MailNoticeService implements DataHandler {
	INSTANCE;
	
	public void loadMailNotice() {
		Log.info(Log.STDOUT, "loadMailNotice", "加载公告");
		CommonParser.getInstance().postTask(DbConst.GET_MailNotice, this,
				new MailNoticeData());
	}

	@Override
	public void handle(DbConst eventID, boolean flag, DataStruct ds) {
		MailNoticeData mailNoticeData = (MailNoticeData) ds;

		HashMap<Integer, MailNotice> bulletins = mailNoticeData.getBulletins();

		Set<Integer> set = bulletins.keySet();
		if (!set.isEmpty()) {
			for (Integer integer : set) {
				MailNoticeManager.getInstance().sendMailToAllRoles(
						bulletins.get(integer));
			}
		}
		Log.info(Log.STDOUT, "loadMailNotice", "加载公告完成");
	}

}
