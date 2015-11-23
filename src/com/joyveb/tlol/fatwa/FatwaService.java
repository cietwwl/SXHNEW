package com.joyveb.tlol.fatwa;

import com.joyveb.tlol.db.DataHandler;
import com.joyveb.tlol.db.DataStruct;
import com.joyveb.tlol.db.parser.CommonParser;
import com.joyveb.tlol.db.parser.DbConst;
import com.joyveb.tlol.db.parser.FatwaData;

public enum FatwaService implements DataHandler {
	INSTANCE;

	public void delFatwa() {
		CommonParser.getInstance().postTask(DbConst.Fatwa_Delete, this, new FatwaData());
	}

	public void insertFatwa(int roleIdByFatwa, Fatwa fatwa) {
		CommonParser.getInstance().postTask(DbConst.Fatwa_Insert, this, new FatwaData(fatwa.getPromulgatorId(), fatwa.getPromulgatorName(), roleIdByFatwa, fatwa.getTimeOut()));
	}

	@Override
	public void handle(DbConst eventID, boolean flag, DataStruct ds) {

	}

	public void loadFatwa() {
		CommonParser.getInstance().postTask(DbConst.Fatwa_Query, this, new FatwaData());

	}

}
