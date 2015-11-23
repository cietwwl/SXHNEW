package com.joyveb.tlol.everydayAward;

import com.joyveb.tlol.item.Item;
import com.joyveb.tlol.mail.MailManager;
import com.joyveb.tlol.schedule.MinTickHandler;
import com.joyveb.tlol.store.Pack;
import com.joyveb.tlol.role.RoleBean;

/**
 * @每日开奖
 * @author SunHL
 * @下午07:22:22
 */
public final class EverydayAwardManager implements MinTickHandler {

	/** 单例类的常量 */
	private static EverydayAwardManager INSTANCE = new EverydayAwardManager();

	/**
	 * 构造方法
	 */
	private EverydayAwardManager() {

	}

	/**
	 * 获取单例类的常量
	 * 
	 * @return INSTANCE
	 */
	public static EverydayAwardManager getInstance() {
		return INSTANCE;
	}

	/**
	 * 通过邮件发送物品
	 * 
	 * @param roleBean
	 * @param item
	 */
	public void getBox(RoleBean roleBean, Item item) {
		MailManager.getInstance().sendSysMail(roleBean.getRoleid(), "每日开奖",
				"恭喜您获得了" + item.getName(), 0, item);
	}

	/**
	 * 查看玩家背包里时候有钥匙
	 * 
	 * @param roleBean
	 * @param boxKeyID
	 * @return
	 */
	public int roleBoxKeyCount(RoleBean roleBean, int boxKeyID) {
		int num = 0;
		Pack bag = roleBean.getStore().getBag();
		Item item = bag.getItem(boxKeyID);
		if (item != null) {
			num = 1;
		}
		return num;
	}

	@Override
	public void minTick(int curMin) {
		// TODO Auto-generated method stub

	}
}
