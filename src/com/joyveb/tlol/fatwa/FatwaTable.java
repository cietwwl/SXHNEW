package com.joyveb.tlol.fatwa;

import java.util.Comparator;
import java.util.TreeMap;
import com.joyveb.tlol.Watchable;
import com.joyveb.tlol.db.DataHandler;
import com.joyveb.tlol.db.DataStruct;
import com.joyveb.tlol.db.parser.DbConst;
import com.joyveb.tlol.schedule.MinTickHandler;


/**
 * @function 追杀列表
 * @author LuoSR
 * @date 2011-12-21
 */
public enum FatwaTable implements MinTickHandler, DataHandler, Watchable {
	/** 单例 */
	INSTANCE;

	/**
	 * 追杀列表中的所有追杀令
	 */
	TreeMap<Integer, Fatwa> fatwas = new TreeMap<Integer, Fatwa>();

	sortByFatwaTimeout timeOutListSorter = new sortByFatwaTimeout();
	
	/**
	 * @function 内部类 追杀令到时列表
	 * @author LuoSR
	 * @date 2011-12-21
	 */
	class sortByFatwaTimeout implements Comparator<Integer> {

		@Override
		public int compare(Integer roleIdByFatwa1, Integer roleIdByFatwa2) {
			return (int)(fatwas.get(roleIdByFatwa1).timeOut - fatwas.get(roleIdByFatwa2).timeOut);
		}	
	}
	
	@Override
	public void watch() {
		// TODO Auto-generated method stub
	}

	@Override
	public void handle(DbConst eventID, boolean flag, DataStruct ds) {
	}

	@Override
	public void minTick(int curMin) {
		// TODO Auto-generated method stub
		
	}

	public TreeMap<Integer, Fatwa> getFatwas() {
		return fatwas;
	}
}


