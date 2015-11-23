package com.joyveb.tlol.store;

import java.util.ArrayList;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.joyveb.tlol.LuaService;
import com.joyveb.tlol.item.Bonus;
import com.joyveb.tlol.item.Equip;
import com.joyveb.tlol.item.EquipQuality;
import com.joyveb.tlol.item.EquipType;
import com.joyveb.tlol.item.Item;
import com.joyveb.tlol.item.UbiquitousItem;
import com.joyveb.tlol.item.Wearable;
import com.joyveb.tlol.role.Property;
import com.joyveb.tlol.role.RoleBean;
import com.joyveb.tlol.schedule.MinTickHandler;
import com.joyveb.tlol.util.CalcMode;
import com.joyveb.tlol.util.Log;

/** 用户所有存储的物品 */
public final class Store implements MinTickHandler {

	/**
	 * 当前对象版本
	 */
	public static final int VERSION = 1;

	/**
	 * 反序列化用的分隔符
	 */
	public static final String DELIM = "#{<[(,=;)]>}";

	/** 存储位上限 */
	public static final byte SHED_SIZE = 7;

	/** 所有者 */
	private RoleBean owner;

	/**
	 * 角色可拥有的仓库数上限
	 */
	public static final byte DEPOT_MAX = 5;

	/**
	 * 角色所有存储位
	 */
	private ArrayList<Pack> rolePacks = new ArrayList<Pack>();

	/**
	 * 私有
	 */
	private Store() { }

	/**
	 * 反序列化
	 * 
	 * @param owner 所有者
	 * @param data 数据源
	 * @return Store
	 * @throws Exception 
	 */
	public static Store deserialize(final RoleBean owner, final String data) throws Exception {
		if(data == null || data.trim().equals(""))
			return defaultStore(owner);

		if(data.charAt(0) == '#') {
			Store store = new Store();
			store.owner = owner;

			StringTokenizer tokenizer = new StringTokenizer(data, DELIM, true);

			tokenizer.nextToken(); // #
			tokenizer.nextToken(); // VERSION
			tokenizer.nextToken(); // ;

			store.rolePacks.add(InusePack.readPack(tokenizer, owner));
			store.rolePacks.add(CommonPack.readPack(tokenizer, owner));
			store.rolePacks.add(CommonPack.readPack(tokenizer, owner));

			while(tokenizer.hasMoreTokens())
				store.rolePacks.add(CommonPack.readPack(tokenizer, owner));

			return store;
		}else
			return oldDataConversion(owner, data);
	}

	/**
	 * 旧数据转换
	 * 
	 * @param owner 所有者
	 * @param data 数据字符串
	 * @return Store
	 */
	private static Store oldDataConversion(final RoleBean owner, final String data) {
		Store store = new Store();
		store.owner = owner;

		store.rolePacks.add(new InusePack(owner));

		Matcher attrMatcher = Pattern.compile("\\{.+?\\}").matcher(data);
		while(attrMatcher.find()) {
			String find = attrMatcher.group();
			Scanner scanner = new Scanner(find.substring(1, find.length() - 1));
			scanner.nextByte();
			store.rolePacks.add(new CommonPack(owner).setCapacity(scanner.nextByte()));
		}

		if(store.rolePacks.size() == 1) {
			store.rolePacks.add(new CommonPack(owner));
			store.rolePacks.add(new CommonPack(owner)); // 默认的仓库
		}

		/*********************** 解析物品 ***********************/
		Matcher matcher = Pattern.compile("<[\\d\\s]+>").matcher(data);
		while(matcher.find()) {
			String find = matcher.group();
			Scanner scanner = new Scanner(find.substring(1, find.length() - 1));

			byte storeAt = scanner.nextByte();

			long uid = scanner.nextLong();

			int tid = scanner.nextInt();

			if(!LuaService.getBool(Item.LUA_CONTAINER, tid))
				continue;

			if(uid == tid) {
				UbiquitousItem ubiquitousItem = new UbiquitousItem(tid, scanner.nextShort());
				if(ubiquitousItem.getStorage() <= 0 || ubiquitousItem.getStorage() > 400) {
					Log.error(Log.ERROR, "角色【" + owner.getRoleid() + "|" + owner.getNick() + "】物品" + tid + "数量为"
							+ ubiquitousItem.getStorage());
					continue;
				}

				LuaService.callOO(2, Item.LUA_CONTAINER, tid, "resetFeature", ubiquitousItem);

				store.rolePacks.get(storeAt).addItem(ubiquitousItem, false);
			}else {
				Equip equip = new Equip();
				equip.setUid(uid);
				equip.setTid(tid);
				equip.setQuality(EquipQuality.values()[scanner.nextInt()]);
				equip.setStar((byte) (scanner.nextByte() - 1));
				equip.setDurability(scanner.nextInt());

				int count = 0;
				while(count < equip.getQuality().ordinal() && scanner.hasNext()) {
					count++;
					byte attrIndex = scanner.nextByte();
					
					if(!LuaService.getBool(Item.LUA_CONTAINER, tid, "ext", attrIndex)) {
						Log.error(Log.ERROR, "物品" + tid + "附加属性数据：" + attrIndex + "不存在！");
						continue;
					}

					Property effectProperty = LuaService.getObject(
							Property.class, Item.LUA_CONTAINER, tid, "ext", attrIndex, "type");
					CalcMode calcMode = LuaService.getObject(
							CalcMode.class, Item.LUA_CONTAINER, tid, "ext", attrIndex, "calc_mode");

					double arg = LuaService.getDouble(Item.LUA_CONTAINER, tid, "ext", attrIndex, "value");
					Bonus bonus = new Bonus(effectProperty, calcMode, arg);
					equip.getAdditiveAttr().add(bonus);
				}

				if(scanner.hasNext()) {
					int startMin = scanner.nextInt();
					if(startMin > 0 && LuaService.getBool(Item.LUA_CONTAINER, tid, "timeLen"))
						equip.setExpire(startMin + LuaService.getInt(Item.LUA_CONTAINER, tid, "timeLen") * 60);
				}

				LuaService.callOO(2, Item.LUA_CONTAINER, tid, "resetBasicAttr", equip);
				equip.setType(LuaService.getObject(EquipType.class, Item.LUA_CONTAINER, tid, "subtype"));
				LuaService.callOO(2, Item.LUA_CONTAINER, tid, "resetFeature", equip);

				store.rolePacks.get(storeAt).addItem(equip, false);
			}
		}

		return store;
	}

	/**
	 * 角色默认的存储
	 * 
	 * @param owner 所有者
	 * @return Store
	 */
	public static Store defaultStore(final RoleBean owner) {
		Store store = new Store();
		store.owner = owner;

		store.rolePacks.add(new InusePack(owner)); // 穿戴部分
		store.rolePacks.add(new CommonPack(owner)); // 背包
		store.rolePacks.add(new CommonPack(owner)); // 默认仓库

		return store;
	}

	/**
	 * 序列化
	 * 
	 * @return String
	 */
	public String serialize() {
		StringBuilder builder = new StringBuilder();

		builder.append("#" + VERSION + ";");
		for(int i = 0; i < rolePacks.size(); i++)
			rolePacks.get(i).serialize(builder);

		return builder.toString();
	}

	/**
	 * @return 角色存储基本信息
	 */
	public String getStoreAttr() {
		StringBuilder builder = new StringBuilder();
		builder.append("【背包容量：");
		builder.append(this.getBag().getCapacity());
		builder.append("】");

		for(int i = 2; i < rolePacks.size(); i++) {
			Pack pack = rolePacks.get(i);
			builder.append("【仓库：");
			builder.append(i - 1);
			builder.append("】：容量：");
			builder.append(pack.getCapacity());
		}

		return builder.toString();
	}

	/**
	 * 添加存储位
	 */
	public void addPack() {
		rolePacks.add(new CommonPack(owner));
	}

	/**
	 * 物品切换存储位
	 * 
	 * @param sPackid 源存储位
	 * @param dPackid 目标存储位
	 * @param uid 物品唯一id
	 * @return 被移动的物品
	 */
	public Item switchPack(final int sPackid, final int dPackid, final long uid) {
		return switchPack(sPackid, dPackid, uid, 1);
	}

	/**
	 * 物品切换存储位
	 * 
	 * @param sPackid 源存储位
	 * @param dPackid 目标存储位
	 * @param uid 物品唯一id
	 * @param num 物品唯一数量
	 * @return 被移动的物品
	 */
	public Item switchPack(final int sPackid, final int dPackid, final long uid, final int num) {
		if(sPackid == dPackid)
			return null;

		if(sPackid < 0 || dPackid < 0 || sPackid >= rolePacks.size() || dPackid >= rolePacks.size())
			return null;

		Item item = rolePacks.get(sPackid).pickItem(uid, num, false);

		if(dPackid > 0)
			rolePacks.get(dPackid).addItem(item, false);
		else if(item instanceof Wearable) {
			Wearable wearable = (Wearable) item;

			Wearable inuse = this.getInuse().getInuseItem(wearable.getWearLoc());
			if(inuse != null)
				this.getBag().addItem(inuse, false);

			this.getInuse().addItem(item, false);
		}else {
			Log.error(Log.ERROR, "无法装备物品【" + item.getName() + "】【" + item.getTid() + "】！");
			return null;
		}

		return item;
	}

	/**
	 * 物品切换存储位
	 * 
	 * @param sPackid 源存储位
	 * @param dPackid 目标存储位
	 * @param item 移动的物品
	 */
	public void switchPack(final int sPackid, final int dPackid, final Item item) {
		switchPack(sPackid, dPackid, item.getUid(), item.getStorage());
	}

	/**
	 * @return 角色所有存储位
	 */
	public ArrayList<Pack> getRolePacks() {
		return rolePacks;
	}

	/**
	 * @param index 存储位索引
	 * @return 存储位
	 */
	public Pack getPack(final int index) {
		return rolePacks.get(index);
	}

	/**
	 * @return 穿戴中
	 */
	public InusePack getInuse() {
		return (InusePack) rolePacks.get(0);
	}

	/**
	 * @return 背包
	 */
	public CommonPack getBag() {
		return (CommonPack) rolePacks.get(1);
	}

	/**
	 * @return 仓库数目
	 */
	public int getDepotCount() {
		return rolePacks.size() - 2;
	}

	/**
	 * @param packid 客户端发送的背包或仓库索引，实际值为packid + 1
	 * @return 此背包或仓库是否存在
	 */
	public boolean isDepotOrBagExist(final byte packid) {
		return packid >= 0 && packid <= rolePacks.size() - 2;
	}

	/**
	 * @param packid 客户端发送的背包或仓库索引，实际值为packid + 1
	 * @return 此仓库是否存在
	 */
	public boolean isDepotExist(final byte packid) {
		return packid > 0 && packid <= rolePacks.size() - 2;
	}

	/**
	 * @return 是否可以扩展仓库
	 */
	public boolean canAddShed() {
		return rolePacks.size() < SHED_SIZE;
	}

	/**
	 * 扩展仓库
	 */
	public void addShed() {
		if(canAddShed())
			rolePacks.add(new CommonPack(owner));
		else
			Log.error(Log.ERROR, "扩展仓库失败，已经达到上限！");
	}

	/**
	 * @return 是否可扩充仓库
	 */
	public boolean canExtShed() {
		return ((CommonPack) rolePacks.get(rolePacks.size() - 1)).canExtBag();
	}

	/**
	 * 扩充仓库
	 * 
	 * @param ext 扩充大小
	 */
	public void extShed(final int ext) {
		int extTemp = ext;
		if(canExtShed()) {
			for(int i = 2; i < rolePacks.size() && extTemp > 0; i++) {
				CommonPack pack = (CommonPack) rolePacks.get(i);
				if(pack.canExtBag())
					extTemp -= pack.extBag(extTemp);
			}
		}else
			Log.error(Log.ERROR, "扩充仓库失败，已经达到上限！");
	}

	@Override
	public void minTick(final int curMin) {
		for(Pack pack : rolePacks)
			pack.minTick(curMin);
	}

}
