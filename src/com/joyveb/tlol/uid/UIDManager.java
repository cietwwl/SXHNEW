package com.joyveb.tlol.uid;

import com.joyveb.tlol.db.DataHandler;
import com.joyveb.tlol.db.DataStruct;
import com.joyveb.tlol.db.parser.CommonParser;
import com.joyveb.tlol.db.parser.DbConst;
import com.joyveb.tlol.db.parser.UIDData;
import com.joyveb.tlol.util.Log;

public class UIDManager implements DataHandler {
	private long maxUID = 0;
	private long curUID = 0;
	private boolean isUpdating = false;
	private static final int INCREMENT = 1000;

	private static UIDManager instance = new UIDManager();

	private UIDManager() {

	}

	public void init() {
		if(maxUID == 0) {
			UIDData accountUIDData = new UIDData();
			CommonParser.getInstance().postTask(DbConst.INIT_UID, this, accountUIDData, true);
		}

		int tryTime = 0;
		while(maxUID == 0) {
			try {
				tryTime++;
				Thread.sleep(500);
				if(tryTime > 10)
					Log.error(Log.STDOUT, "can not init UID");
			}catch(InterruptedException e) {
				e.printStackTrace();
			}
		}

		Log.info(Log.STDOUT, "init UID finished!!");
	}

	public long getUID() {

		int tryTime = 0;
		while(curUID == 0 || curUID >= maxUID) {
			try {
				tryTime++;
				Thread.sleep(500);
				if(tryTime > 10)
					Log.error(Log.STDOUT, "can not update UID");
			}catch(InterruptedException e) {
				e.printStackTrace();
			}
		}

		if(maxUID - curUID <= INCREMENT * 0.2 && !isUpdating) {
			updateUID(maxUID + INCREMENT, this);
		}

		// 这么写是为了防止+=不为原子操作
		curUID = curUID + 1;

		return curUID;
	}

	public void updateUID(final long uid, final DataHandler dataHandler) {
		UIDData accountUIDData = new UIDData();
		accountUIDData.setMaxUID(uid);
		CommonParser.getInstance().postTask(DbConst.UPDATE_UID, dataHandler, accountUIDData, true);
		isUpdating = true;
	}

	@Override
	public void handle(final DbConst eventID, final boolean flag, final DataStruct ds) {
		if(flag) {
			UIDData accountUIDData = null;
			switch(eventID) {
			case UPDATE_UID:
				accountUIDData = (UIDData) ds;
				maxUID = accountUIDData.getMaxUID();
				isUpdating = false;
				break;
			case INIT_UID:
				accountUIDData = (UIDData) ds;
				curUID = accountUIDData.getMaxUID();
				updateUID(curUID + INCREMENT, this);
				break;
			default:
				Log.info(Log.STDOUT, "handle", "unhandled db call back! : " + flag);
				break;
			}
		}
	}

	public static UIDManager getInstance() {
		return instance;
	}

}
