package com.joyveb.tlol.store;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import com.joyveb.tlol.LuaService;
import com.joyveb.tlol.MessageSend;
import com.joyveb.tlol.SubModules;
import com.joyveb.tlol.TianLongServer;
import com.joyveb.tlol.item.Item;
import com.joyveb.tlol.item.UniqueItem;
import com.joyveb.tlol.protocol.MsgID;
import com.joyveb.tlol.role.RoleBean;
import com.joyveb.tlol.schedule.MinTickHandler;
import com.joyveb.tlol.util.Log;
import com.joyveb.tlol.validity.Validity.State;

/** 穿戴、背包及仓库 */
public abstract class Pack implements MinTickHandler {
	/** 所有者 */
	protected final RoleBean owner;

	/**
	 * @param owner 所有者
	 */
	public Pack(final RoleBean owner) {
		this.owner = owner;
	}

	/**
	 * 所存储的物品
	 */
	protected final HashMap<Long, Item> packItems = new HashMap<Long, Item>();

	/**
	 * 正在被监听的物品
	 */
	protected final HashSet<UniqueItem> monitored = new HashSet<UniqueItem>();

	/**
	 * 添加物品
	 * 
	 * @param item 待添加物品
	 * @param firstObtained 是否初次获得
	 */
	public abstract void addItem(Item item, boolean firstObtained);

	/**
	 * 获取物品
	 * 
	 * @param uid 物品唯一id
	 * @return Item 相应的物品
	 */
	public Item getItem(final long uid) {
		return packItems.get(uid);
	}

	/**
	 * @return 容量，格子数目
	 */
	public abstract byte getCapacity();

	/**
	 * 从此存储位取出物品
	 * 
	 * @param id 物品id
	 * @param num 数量
	 * @param lost 是否完全失去物品
	 * @return 取出的物品
	 */
	public abstract Item pickItem(final long id, final int num, boolean lost);

	/**
	 * 从存储位置中取出全部此物品
	 * 
	 * @param item 物品对象
	 * @param lost 是否完全失去物品
	 * @return 全部此物品
	 */
	public Item pickItem(final Item item, final boolean lost) {
		return pickItem(item.getUid(), item.getStorage(), lost);
	}

	/**
	 * @return 此存储位中所有物品数据
	 */
	public final String getDescribe() {
		StringBuilder builder = new StringBuilder();
		for(Item item : packItems.values()) {
			builder.append("id：");
			builder.append(String.valueOf(item.getUid()));
			builder.append("tid：");
			builder.append(item.getTid());
			builder.append("  ");
			builder.append(item.getName());
			builder.append("×");
			builder.append(item.getStorage());
			builder.append("\n");
		}
		return builder.toString();
	}

	/**
	 * @return 此存储位中所有的物品
	 */
	public final HashMap<Long, Item> getPackItems() {
		return packItems;
	}

	/**
	 * 检测并处理过期物品
	 * 
	 * @param curMin 当前时间
	 */
	public void minTick(final int curMin) {
		Iterator<UniqueItem> iterator = monitored.iterator();
		while(iterator.hasNext()) {
			UniqueItem uniqueItem = iterator.next();
			if(uniqueItem.getState(curMin) == State.Invalid) {
				iterator.remove();
				pickItem(uniqueItem, true);
				
				Log.info(
						Log.ITEM,
						owner.getUserid() + "#$" + owner.getRoleid() + "#$34#$" + uniqueItem.getName() + "#$"
								+ uniqueItem.getStorage() + "#$"
								+ new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + "#$"
								+ TianLongServer.srvId + "#$" + "0" + "#$" + uniqueItem.getTid() + "#$"
								+ uniqueItem.getUid());
				
				MessageSend.prepareBody();
				if(this instanceof InusePack) {
					LuaService.call(0, "fillInuseDel", uniqueItem);
					SubModules.fillAttributes(owner);
					SubModules.fillAttributesDes(owner);
				}else
					LuaService.call(0, "fillBagDel", uniqueItem.getUid());
				LuaService.call(0, "fillSystemPrompt", "物品【" + uniqueItem.getName() + "】由于过期而消失～");

				MessageSend.putShort((short) 0);

				MessageSend.sendMsg(owner, MsgID.MsgID_Special_Train);
			}
		}
	}

	/**
	 * 移除对此物品的监听
	 * 
	 * @param uniqueItem 唯一物品
	 */
	public void removeMonitor(final UniqueItem uniqueItem) {
		monitored.remove(uniqueItem);
	}

	/**
	 * @return 拥有者
	 */
	public RoleBean getOwner() {
		return owner;
	}

	/**
	 * 序列化
	 * 
	 * @param builder 容器
	 */
	public void serialize(final StringBuilder builder) {
		builder.append("{");
		builder.append("capacity=" + getCapacity() + ";");
		for(Item item : packItems.values())
			item.serialize(builder);
		builder.append("}");
	}
}
