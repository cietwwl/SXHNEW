package com.joyveb.tlol;


import com.joyveb.tlol.db.DataHandler;
import com.joyveb.tlol.db.DataStruct;
import com.joyveb.tlol.db.parser.CommonParser;
import com.joyveb.tlol.db.parser.DbConst;
import com.joyveb.tlol.db.parser.LoginBulletinData;
import com.joyveb.tlol.util.Log;

public enum LoginBulletinService implements DataHandler {
	INSTANCE;
	public String bulletinContent = "";
	
	public void loadLoginBulletin() {
		Log.info(Log.STDOUT, "loadLoginBulletin", "加载登录新公告");
		CommonParser.getInstance().postTask(DbConst.GET_LoginBulletin, this,
				new LoginBulletinData());
	}

	@Override
	public final void handle(final DbConst eventID, final boolean flag, final DataStruct ds) {
		LoginBulletinData loginbulletinData = (LoginBulletinData) ds;
		this.bulletinContent = loginbulletinData.getBulletinContent();
		Log.info(Log.STDOUT, "loadLoginBulletin", "加载登录新公告完成");
	}

	public String getBulletinContent() {
		return bulletinContent;
	}

	public void setBulletinContent(String bulletinContent) {
		this.bulletinContent = bulletinContent;
	}
	
}
