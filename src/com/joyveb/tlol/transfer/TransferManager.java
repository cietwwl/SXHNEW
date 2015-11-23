package com.joyveb.tlol.transfer;

import java.util.ArrayList;
import java.util.List;

import com.joyveb.tlol.schedule.MinTickHandler;
import com.joyveb.tlol.util.Cardinality;

public final class TransferManager implements MinTickHandler {

	/** 单例类的常量 */
	private static TransferManager INSTANCE = new TransferManager();
	private  List<Transfer> list = new ArrayList<Transfer>();

	/**
	 * 构造方法
	 */
	private TransferManager() {
	}

	/**
	 * 获取单例类的常量
	 * 
	 * @return INSTANCE
	 */
	public static TransferManager getInstance() {
		return INSTANCE;
	}

	@Override
	public void minTick(int curMin) {
		List<Transfer> temp = new ArrayList<Transfer>();
		for (int i = 0; i < list.size(); i++) {
			Transfer transfer = list.get(i);

			if (transfer.getOutTime() <= curMin) {
				temp.add(transfer);
			} else {
				break;
			}
		}

		list.removeAll(temp);
	}

	public void add(int userId) {
		Transfer transfer = new Transfer(userId, Cardinality.INSTANCE.getMinute() + 5);
		list.add(transfer);
	}

	public List<Transfer> getList() {
		return list;
	}

}