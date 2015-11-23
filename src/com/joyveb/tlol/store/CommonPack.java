package com.joyveb.tlol.store;

import java.util.HashMap;
import java.util.Iterator;
import java.util.StringTokenizer;

import com.joyveb.tlol.item.Item;
import com.joyveb.tlol.item.UniqueItem;
import com.joyveb.tlol.item.UbiquitousItem;
import com.joyveb.tlol.role.RoleBean;
import com.joyveb.tlol.util.Cardinality;
import com.joyveb.tlol.validity.Validity;

/**
 * 普通存储位
 * @author Sid
 */
public class CommonPack extends Pack {
	/** 存储上限 */
	public static final byte BAG_SZ_LIMIT = 40;

	/** 容量，单位：格子 */
	private byte capacity = 20;

	/**
	 * @param owner 所有者
	 */
	public CommonPack(final RoleBean owner) {
		super(owner);
	}
	
	@Override
	public final void addItem(final Item item, final boolean firstObtained) {
		if(item == null)
			return;
		
		if (packItems.containsKey(item.getUid())) {
			Item itemsInPack = packItems.get(item.getUid());
			itemsInPack.setStorage((short) (itemsInPack.getStorage() + item.getStorage()));
		} else {
			packItems.put(item.getUid(), item);
			if(firstObtained && owner != null)
				item.onObtain(owner);
			
			if(item instanceof UniqueItem) {
				UniqueItem uniqueItem = (UniqueItem) item;
				if(uniqueItem.monitored())
					this.monitored.add(uniqueItem);
			}
		}
	}

	@Override
	public Item pickItem(final long id, final int num, final boolean lost) {
		if(!packItems.containsKey(id))
			return null;
		
		Item item = packItems.get(id);
		
		if(item instanceof UniqueItem) {
			this.removeMonitor((UniqueItem) item);
			packItems.remove(id);
			return item;
		}else {
			Item itemTemp = ((UbiquitousItem) item).pickup(num);
			
			if(item.isNull())
				packItems.remove(id);
			
			return itemTemp;
		}
	}

	/**
	 * 检查是否包含此物品
	 * @param uid 物品唯一id
	 * @return 是否包含此物品
	 */
	public final boolean checkItem(final long uid) {
		return packItems.containsKey(uid);
	}

	/**
	 * 从此存储位中检索物品
	 * @param require HashMap<Integer, Integer>：Key，tid；Value，数量
	 * @return 满足条件返回
	 */
	public final HashMap<Long, Integer> contains(final HashMap<Integer, Integer> require) {
		HashMap<Long, Integer> result = new HashMap<Long, Integer>();

		Iterator<Item> iterator = packItems.values().iterator();
		while (iterator.hasNext()) {
			Item item = iterator.next();
			if (require.containsKey(item.getTid())) {
				int num = require.get(item.getTid()) - item.getStorage();

				if (item instanceof UniqueItem) { //唯一型物品
					if(num <= 0) { //检索到此模板需要的最后一件
						result.put(item.getUid(), require.get(item.getTid()));
						require.remove(item.getTid());
					}else { //检索到此模板的一件
						result.put(item.getUid(), 1);
						require.put(item.getTid(), num);
					}
				}else if (num > 0) //非唯一型物品数量不足
					return null;
				else { //非唯一型物品数量充足
					result.put(item.getUid(), require.get(item.getTid()));
					require.remove(item.getTid());
				}
			}
		}

		if (require.isEmpty())//还有尚未检索到的
			return result;

		return null;
	}

	/**
	 * 获取此类物品的数量
	 * @param tid 模板id
	 * @return 数量
	 */
	public final int getItemCountByTid(final int tid) {
		if (packItems.containsKey((long) tid)) //如果是存在以此tid为键的键-值对，则必定是非唯一物品，只需要直接取数量即可
			return packItems.get((long) tid).getStorage();

		int count = 0;
		for (Item item : packItems.values())
			if (item.getTid() == tid)
				count += item.getStorage(); //求和是因为统计唯一物品时可能有多个键-值对
		
		return count;
	}

	/**
	 * 容量，单位：格子
	 * @param capacity 容量
	 * @return this
	 */
	public final CommonPack setCapacity(final byte capacity) {
		this.capacity = capacity;
		return this;
	}
	
	@Override
	public final byte getCapacity() {
		return capacity;
	}

	/**
	 * @return 是否可扩充
	 */
	public final boolean canExtBag() {
		return capacity < BAG_SZ_LIMIT;
	}

	/**
	 * 扩充
	 * @param ext 扩充值
	 * @return 实际扩充值
	 */
	public final int extBag(final int ext) {
		int old = capacity;
		if (capacity + ext >= BAG_SZ_LIMIT)
			capacity = BAG_SZ_LIMIT;
		else
			capacity += ext;

		return capacity - old;
	}

	/**
	 * 反序列化
	 * @param tokenizer 数据源
	 * @param owner 所有者
	 * @return CommonPack
	 * @throws Exception 
	 */
	public static CommonPack readPack(final StringTokenizer tokenizer, final RoleBean owner) throws Exception {
		CommonPack commonPack = new CommonPack(owner);
		
		tokenizer.nextToken(); //{
		tokenizer.nextToken(); //capacity
		tokenizer.nextToken(); //=
		commonPack.capacity = Byte.parseByte(tokenizer.nextToken()); //12
		tokenizer.nextToken(); //;
		
		int minute = Cardinality.INSTANCE.getMinute();
		
		while(tokenizer.nextToken().equals("<")) {
			Item item = Item.readItem(tokenizer);
			
			if(item.getState(minute) != Validity.State.Invalid && !item.isNull())
				commonPack.addItem(item, false); 
		}
		
		return commonPack;
	}

}
