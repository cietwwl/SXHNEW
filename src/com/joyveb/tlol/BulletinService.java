package com.joyveb.tlol;

import java.util.HashMap;
import java.util.Iterator;

import com.joyveb.tlol.db.DataHandler;
import com.joyveb.tlol.db.DataStruct;
import com.joyveb.tlol.db.parser.BulletinData;
import com.joyveb.tlol.db.parser.CommonParser;
import com.joyveb.tlol.db.parser.DbConst;
import com.joyveb.tlol.schedule.ScheduleBulletin;
import com.joyveb.tlol.schedule.ScheduleManager;
import com.joyveb.tlol.schedule.ScheduleTask;
import com.joyveb.tlol.util.Log;

public enum BulletinService implements DataHandler {
	INSTANCE;

	public void loadBulletin() {
		Log.info(Log.STDOUT, "loadBulletin", "加载公告");
		CommonParser.getInstance().postTask(DbConst.GET_Bulletin, this,
				new BulletinData());
	}

	@Override
	public final void handle(final DbConst eventID, final boolean flag, final DataStruct ds) {
		BulletinData bulletinData = (BulletinData) ds;

		HashMap<Integer, ScheduleBulletin> bulletins = bulletinData
				.getBulletins();

		Iterator<ScheduleTask> iterator = ScheduleManager.INSTANCE
				.getScheduleQueue().iterator();
		while (iterator.hasNext()) {
			ScheduleTask executable = iterator.next();
			if (!(executable instanceof ScheduleBulletin))
				continue;

			ScheduleBulletin schedule = (ScheduleBulletin) executable;
			int id = schedule.getId();

			if (bulletins.containsKey(id))
				bulletins.get(id).setLast(schedule.getLast());

			iterator.remove();
		}

		ScheduleManager.INSTANCE.getScheduleQueue().addAll(bulletins.values());

		Log.info(Log.STDOUT, "loadBulletin", "加载公告完成");
	}
}
