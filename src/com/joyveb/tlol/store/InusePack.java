package com.joyveb.tlol.store;

import java.util.Map.Entry;
import java.util.StringTokenizer;

import com.joyveb.tlol.exception.BadRequestException;
import com.joyveb.tlol.item.EquipType;
import com.joyveb.tlol.item.Item;
import com.joyveb.tlol.item.Wearable;
import com.joyveb.tlol.role.RoleBean;
import com.joyveb.tlol.util.Cardinality;
import com.joyveb.tlol.util.Log;
import com.joyveb.tlol.validity.Validity;

/** 穿戴中的物品 */
public class InusePack extends Pack {
	/** 容量 */
	public static final byte CAPACITY = 12;
	
	/**
	 * @param owner 所有者
	 */
	public InusePack(final RoleBean owner) {
		super(owner);
	}

	@Override
	public final void addItem(final Item item, final boolean firstObtained) {
		if(item == null)
			return;
		
		if(item instanceof Wearable) {
			Wearable wearable = (Wearable) item;
			packItems.put(wearable.getUid(), wearable);
			
			if(firstObtained)
				wearable.onObtain(owner);
			
			if(owner != null)
				wearable.onWear(owner);
			
			if(wearable.monitored())
				this.monitored.add(wearable);
		}else
			throw new IllegalArgumentException("非可穿戴物品！");
	}

	@Override
	public final Item getItem(final long uid) {
		return packItems.get(uid);
	}

	/**
	 * 根据穿戴位置查找可穿戴物品
	 * @param wearLoc 穿戴位置
	 * @return 对应的可穿戴物品
	 */
	public final Wearable getInuseItem(final byte wearLoc) {
		for(Item item : packItems.values()) 
			if(((Wearable) item).getWearLoc() == wearLoc)
				return (Wearable) item;
		
		return null;
	}
	
	/**
	 * 根据穿戴位置查找可穿戴物品
	 * @param equipType 装备类型
	 * @return 对应的可穿戴物品
	 */
	public final Wearable getInuseItem(final EquipType equipType) {
		return getInuseItem(equipType.getWearIndex());
	}

	@Override
	public final Item pickItem(final long uid, final int num, final boolean lost) {
		if(!packItems.containsKey(uid)) 
			return null;
		
		Wearable item = (Wearable) packItems.get(uid);
		if(!item.canUnwield(owner)) {
			Log.error(Log.ERROR, item, new BadRequestException("该装备无法卸下"));
			return null;
		}

		item.onUnwield(owner);
		
		this.removeMonitor(item);
		
		packItems.remove(uid);
		
		return item;
	}

	/**
	 * 从身上拿下此某种类型的物品
	 * @param wearLoc 穿戴位置
	 * @param lost 是否完全失去
	 * @return 拿下的物品
	 */
	public final Wearable pickItem(final byte wearLoc, final boolean lost) {
		Wearable item = null;
		for(Entry<Long, Item> entry : packItems.entrySet()) {
			Wearable wearable = (Wearable) entry.getValue();
			if(wearable.getWearLoc() == wearLoc) {
				item = wearable;
				break;
			}
		}
		
		if(item == null)
			return null;
		
		if(!item.canUnwield(owner)) {
			Log.error(Log.ERROR, item, new BadRequestException("该装备无法卸下"));
			return null;
		}

		item.onUnwield(owner);
		this.removeMonitor(item);
		
		packItems.remove(item.getUid());
		
		return item;
	}
	
	@Override
	public final byte getCapacity() {
		return CAPACITY;
	}

	/**
	 * 反序列化
	 * @param tokenizer 数据源
	 * @param owner 所有者
	 * @return InusePack
	 * @throws Exception 
	 */
	public static InusePack readPack(final StringTokenizer tokenizer, final RoleBean owner) throws Exception {
		InusePack inusePack = new InusePack(owner);
		
		tokenizer.nextToken(); //{
		tokenizer.nextToken(); //capacity
		tokenizer.nextToken(); //=
		tokenizer.nextToken(); //12
		tokenizer.nextToken(); //;
		
		int minute = Cardinality.INSTANCE.getMinute();
		
		while(!tokenizer.nextToken().equals("}")) {
			Item item = Item.readItem(tokenizer);
			
			if(item.getState(minute) != Validity.State.Invalid)
				inusePack.addItem(item, false); 
		}
		
		return inusePack;
	}

}
