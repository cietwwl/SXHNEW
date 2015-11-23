package com.joyveb.tlol.fee;


import java.util.ArrayList;
import java.util.List;

import com.joyveb.tlol.db.DataHandler;
import com.joyveb.tlol.db.DataStruct;
import com.joyveb.tlol.db.parser.CommonParser;
import com.joyveb.tlol.db.parser.DbConst;
import com.joyveb.tlol.db.parser.FeeData;
import com.joyveb.tlol.util.Log;

public enum FeeService implements DataHandler {
	INSTANCE;
	
	public List<FeeData> feeList = new ArrayList<FeeData>();
	public void loadFee() {
		Log.info(Log.STDOUT, "loadFee", "加载计费");
		CommonParser.getInstance().postTask(DbConst.Get_Fee, this,
				new FeeData());
	}

	@Override
	public final void handle(final DbConst eventID, final boolean flag, final DataStruct ds) {
		FeeData feee = (FeeData) ds;
			if (feee.getFeeList().size() > 0) {
				feeList = feee.getFeeList();
			}
			
		Log.info(Log.STDOUT, "loadFee", "加载计费完成");
	}

	public List<FeeData> getFeeList() {
		return feeList;
	}
}
