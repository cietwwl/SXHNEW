package com.joyveb.tlol.fatwa;
import java.util.Set;
import java.util.TreeMap;
import com.joyveb.tlol.role.RoleBean;

public final class FatwaManager {

	public static FatwaManager fatwaManager = new FatwaManager();
	TreeMap<Integer, Fatwa> fatwas = FatwaTable.INSTANCE.getFatwas();

	public static FatwaManager getInstance() {
		return fatwaManager;
	}


	public TreeMap<Integer, Fatwa> getFatwas() {
		return fatwas;
	}


	private FatwaManager() {
	}

	/**
	 * @function 上线检查
	 * @author LuoSR
	 * @date 2011-12-22
	 */
	public void checkFatwaMap(RoleBean roleBean) {
		//是否被追杀
		if(!roleBean.isNotFatwa){//否
			// 判断是否在Map中
			if(fatwas.containsKey(roleBean.getRoleid())){
				roleBean.setIsNotFatwa(true);
				roleBean.addKillIcon();
				roleBean.sendSystemMsg(fatwas.get(roleBean.getRoleid()).getPromulgatorName());
			}else{
				roleBean.delKillIcon();
			}
		}else{
			if(!fatwas.containsKey(roleBean.getRoleid())){
				roleBean.setIsNotFatwa(false);
				roleBean.delKillIcon();
			}
		}
	}
	
	/**
	 * @function 停服，断电等，进行回写
	 * @author LuoSR
	 * @date 2011-12-22
	 */
	public void delFatwa() {
			FatwaService.INSTANCE.delFatwa();
	}
	
	public void insertFatwa() {
		Set<Integer> fatwaSet = fatwas.keySet();

		for (Integer str : fatwaSet) {
			Fatwa val = fatwas.get(str);
			FatwaService.INSTANCE.insertFatwa(str, val);
		}
	}
}
