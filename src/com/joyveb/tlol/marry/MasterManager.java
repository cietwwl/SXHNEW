package com.joyveb.tlol.marry;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.joyveb.tlol.role.RoleBean;

public final class MasterManager {

	public static MasterManager masterManager = new MasterManager();
	public Map<Integer, List<Integer>> masterMap = new HashMap<Integer, List<Integer>>();

	public static MasterManager getInstance() {
		return masterManager;
	}

	public Map<Integer, List<Integer>> getMasterList() {
		return masterMap;
	}

	private MasterManager() {
	}

	public void checkMasterList(RoleBean roleBean) {
		if (masterMap.containsKey(roleBean.getRoleid())) {
			for (Integer integer : masterMap.get(roleBean.getRoleid())) {
				roleBean.removeApprentice(integer);
			}
			masterMap.remove(roleBean.getRoleid());
		}
	}

	public void addMasterList(int masterroleId, int apprenticeroleID) {
		if (masterMap.containsKey(masterroleId)) {
			if (!masterMap.get(masterroleId).contains(apprenticeroleID)) {
				masterMap.get(masterroleId).add(apprenticeroleID);
			}
		} else {
			List<Integer> list = new ArrayList<Integer>();
			list.add(apprenticeroleID);
			masterMap.put(masterroleId, list);
		}
	}
	/**徒弟删离线师傅，把徒弟的ID存到   离线判断字段（APPDELMASTER）里，方便进行对比*/
	public void removeMaster() {
		
		Set<Integer> set = masterMap.keySet();
		for (Integer masterId : set) {
			List<Integer> lists = masterMap.get(masterId);
			String str = "";
			for (Integer appId : lists) {
				str = str + " " + appId;
			}
			RoleRemoveMasterService.INSTANCE.removeMaster(masterId,str.substring(1));
		}
	}
}

