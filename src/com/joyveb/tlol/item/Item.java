package com.joyveb.tlol.item;

import java.util.ArrayList;
import java.util.Scanner;
import java.util.StringTokenizer;

import com.joyveb.tlol.LuaService;
import com.joyveb.tlol.MessageSend;
import com.joyveb.tlol.protocol.MsgID;
import com.joyveb.tlol.role.Property;
import com.joyveb.tlol.role.RoleBean;
import com.joyveb.tlol.store.Pack;
import com.joyveb.tlol.store.Store;
import com.joyveb.tlol.util.CalcMode;
import com.joyveb.tlol.util.Log;
import com.joyveb.tlol.validity.Validity;

/**
 * 物品抽象类
 * @author Sid
 */
public abstract class Item implements Validity {

	/**
	 * @return 防偷袭道具id
	 */
	public static int getAgainstSneakAttack() {
		return LuaService.getInt("Temp", "Item", "AgainstSneakAttack");
	}
	
	/**
	 * @return 防偷袭道具id
	 */
	public static long getRefineNecessary() {
		return LuaService.getLong("Temp", "Item", "RefineNecessary");
	}
	
	/**
	 * Lua中物品模板容器table的名称
	 */
	public static final String LUA_CONTAINER = "ItemSet";
	
	/**
	 * 合成分解table
	 */
	public static final String COMPOSE = "Compose";
	
	/** 物品模板id */
	protected int tid;

	/** 物品特征码 */
	protected int feature;
	/**
	 * @return 物品唯一id
	 */
	public abstract long getUid();
	
	/**
	 * @return 物品名称
	 */
	public abstract String getName();
	
	/**
	 * @param role 参与事件的角色
	 * @return 通常状态下物品是否可使用
	 */
	public abstract boolean canUse(final RoleBean role);
	
	/**
	 * @param role 参与事件的角色
	 * @return 战斗中物品是否可使用
	 */
	public boolean canUseInFight(final RoleBean role) {
		if((feature >> ItemFeature.FightUse.ordinal() & 1) != 1)
			return false;
		
		if(LuaService.getBool(LUA_CONTAINER, tid, "level"))
			return role.getLevel() >= LuaService.getInt(LUA_CONTAINER, tid, "level"); 
		
		return true;
	}
	
	/**
	 * 获得物品时触发操作
	 * @param role 参与事件的角色
	 */
	public void onObtain(final RoleBean role) {
		LuaService.callOO(2, LUA_CONTAINER, tid, "onObtain", role, this);
	}
	
	/**
	 * 使用物品时触发操作
	 * @param role 参与事件的角色
	 * @param pack 存储物品的位置
	 */
	public abstract void onUse(final RoleBean role, final Pack pack);
	
	/**
	 * 取消使用物品时触发操作
	 * @param role 参与事件的角色
	 * @param pack 存储物品的位置
	 */
	public abstract void onUnuse(final RoleBean role, final Pack pack);
	
	/**
	 * @return 此物品对象中包含的物品数量
	 */
	public abstract short getStorage();

	/**
	 * @param num 此物品对象中包含的物品数量
	 */
	public abstract void setStorage(final short num);
	
	/**
	 * @return 物品描述
	 */
	public abstract String getDescribe();
	public abstract String getDescribe(final RoleBean player);
	public abstract ArrayList<Bonus> getDescribeAdditiveAttr();
	public abstract ArrayList<Bonus> getDescribeBasicAttr();
	
	/**
	 * @return 物品是否可掉落
	 */
	public final boolean isDropable() {
		return (feature >> ItemFeature.NoDrop.ordinal() & 1) != 1;
	}
	
	/** 客户端查看此物品 
	 * @param player 查看此物品的角色
	 */
	
	
	public void viewItem(final RoleBean player) {
		MessageSend.prepareBody();
		
		MessageSend.putShort((short) 0);
		
		MessageSend.putString(this.getDescribe(player));
		
		MessageSend.putInt(this.getColor());
		
		MessageSend.putShort(this.getStorage());
		
		MessageSend.sendMsg(player, MsgID.MsgID_Item_Get_Info_Resp);
	}
	
	public void viewEquip(final RoleBean player) {
		MessageSend.prepareBody();
		
		MessageSend.putShort((short) 0);
		
		MessageSend.putString(this.getDescribe());
		
		MessageSend.putInt(this.getColor());
		
		MessageSend.putShort(this.getStorage());
		
		MessageSend.sendMsg(player, MsgID.MsgID_Item_Get_Info_Resp);
	}
	
	/**
	 * @return 物品标题颜色
	 */
	public abstract int getColor();
	
	/**
	 * @return 是否可以被邮寄
	 */
	public boolean canMailed() {
		return (feature >> ItemFeature.NoMail.ordinal() & 1) != 1;
	}
	
	/**
	 * @return 获取邮寄费用
	 */
	public abstract int getMailFee();
	
	/**
	 * @param tid 物品模板id
	 */	
	public final void setTid(final int tid) {
		this.tid = tid;
	}

	/**
	 * @return 物品模板id
	 */
	public final int getTid() {
		return tid;
	}

	/**
	 * @return 是否绑定
	 */
	public final boolean isBind() {
		return (feature >> ItemFeature.Bind.ordinal() & 1) == 1;
	}
	
	
	/**
	 * @return 是否装备后绑定
	 */
	public final boolean isBindAfterUse() {
		
		return (feature >> ItemFeature.Mountable.ordinal() & 1) == 1;
	}

	/**
	 * @return 物品特征码
	 */
	public int getFeature() {
		return feature;
	}

	/**
	 * @param itemFeature 物品特征
	 * @return 是否拥有此项特征
	 */
	public boolean hasFeature(final ItemFeature itemFeature) {
		return (feature >> itemFeature.ordinal() & 1) == 1;
	}
	/**
	 * @param itemFeatures 物品特征码
	 */
	public void setFeature(final ItemFeature ... itemFeatures) {
		for(ItemFeature itemFeature : itemFeatures)
			this.feature |= (1 << itemFeature.ordinal());
	}

	/**
	 * 序列化物品到StringBuilder中
	 * @param builder 字符序列
	 */
	public abstract void serialize(StringBuilder builder);
	
	/**
	 * 序列化物品为String
	 * @return 序列化字符串
	 */
	public String serialize() {
		StringBuilder builder = new StringBuilder();
		serialize(builder);
		return builder.toString();
	}
	
	/**
	 * 反序列化物品
	 * @param tokenizer 数据源
	 * @return 物品
	 * @throws Exception 
	 */
	public static Item readItem(final StringTokenizer tokenizer) throws Exception {
		if(!tokenizer.hasMoreTokens())
			return null;
		
		String firstToken = tokenizer.nextToken();
		if(firstToken.equals("<"))
			tokenizer.nextToken(); //class
		
		tokenizer.nextToken(); //=
		String token = tokenizer.nextToken(); //类型
		tokenizer.nextToken(); //;
		if(token.equals("Equip")) {
			return Equip.readItem(tokenizer);
		}else if(token.equals("Ubiquitous")) {
			return UbiquitousItem.readItem(tokenizer);
		}else if(token.equals("Mountable")) {
			return Mountable.readItem(tokenizer);
		}else {
			throw new Exception("无法反序列化类型为" + token + "的物品");
		}
	}
	
	/**
	 * 反序列化物品
	 * @param itemStr 数据源
	 * @return 物品
	 * @throws Exception 
	 */
	public static Item readItem(final String itemStr) throws Exception {
		if(itemStr == null || itemStr.length() == 0)
			return null;
		else if(itemStr.charAt(0) == '#') {
			StringTokenizer tokenizer = new StringTokenizer(itemStr, Store.DELIM, true);
			tokenizer.nextToken(); //#
			tokenizer.nextToken(); //version
			return readItem(tokenizer);
		}else
			return conversionOldItemData(itemStr);
	}
	
	/**
	 * 转换旧的物品数据
	 * @param itemStr 旧的序列化字符串
	 * @return Item
	 */
	private static Item conversionOldItemData(final String itemStr) {
		if(itemStr == null)
			return null;
		
		Scanner scanner = new Scanner(itemStr.substring(1, itemStr.length() - 1));

		scanner.nextByte();

		long uid = scanner.nextLong();

		int tid = scanner.nextInt();

		if(!LuaService.getBool(Item.LUA_CONTAINER, tid))
			return null;
		
		if(uid == tid) {
			UbiquitousItem ubiquitousItem = new UbiquitousItem(tid, scanner.nextShort());
			LuaService.callOO(2, Item.LUA_CONTAINER, tid, "resetFeature", ubiquitousItem);
			return ubiquitousItem;
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
				
				Property effectProperty = LuaService.getObject(Property.class, 
						Item.LUA_CONTAINER, tid, "ext", attrIndex, "type");
				CalcMode calcMode = LuaService.getObject(CalcMode.class, 
						Item.LUA_CONTAINER, tid, "ext", attrIndex, "calc_mode");
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

			return equip;
		}
	}
	
	/**
	 * @param halfDays 时间长度，单位：半天
	 * @return 拍卖手续费
	 */
	public int getAuctionFee(final byte halfDays) {
		return LuaService.callOO4Int(2, LUA_CONTAINER, tid, "getAuctionFee", halfDays);
	}
	
	/**
	 * @return 拍卖行归类码
	 */
	public abstract int getAuctionCategory();
	
	/**
	 * @return 物品为空
	 */
	public boolean isNull() {
		return this.getStorage() == 0;
	}
	
}
