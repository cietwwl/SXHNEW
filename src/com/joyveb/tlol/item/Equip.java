package com.joyveb.tlol.item;

import java.util.ArrayList;
import java.util.StringTokenizer;

import com.joyveb.tlol.LuaService;
import com.joyveb.tlol.MessageSend;
import com.joyveb.tlol.SubModules;
import com.joyveb.tlol.auction.AutionType;
import com.joyveb.tlol.protocol.MsgID;
import com.joyveb.tlol.role.PropertyMan;
import com.joyveb.tlol.role.RoleBean;
import com.joyveb.tlol.role.Vocation;
import com.joyveb.tlol.store.InusePack;
import com.joyveb.tlol.store.Pack;
import com.joyveb.tlol.store.Store;
import com.joyveb.tlol.util.Cardinality;

/**
 * 装备
 */
public class Equip extends Wearable {
	/**
	 * 序列化自身的属性
	 */
	public static final String[] SERIALIZE = {
		"tid",
		"feature",
		"uid",
		"expire",
		"type",
		"quality",
		"bAttr",
		"aAttr",
		"mounteCount",
		"mountLimit",
		"mounted",
		"producer",
		"endure",
		"durab",
		"star",
	};
	
	
	/** 装备类型 */
	private EquipType type;

	/** 装备品质 */
	private EquipQuality quality;
	
	/** 基本属性 */
	private ArrayList<Bonus> basicAttr = new ArrayList<Bonus>();

	/** 附加属性 */
	private ArrayList<Bonus> additiveAttr = new ArrayList<Bonus>();

	/** 镶嵌上限 */
	private int mountLimit;
	
	/** 镶嵌道具 */
	private ArrayList<Mountable> mounted = new ArrayList<Mountable>();

	/** 制作者 */
	private String producer;

	/** 耐久上限 */
	private int endure = 100;
	
	/** 实际耐久度 */
	private float durability = 100;

	/** 星级 */
	private byte star;
	
	/**
	 * 星级上限
	 */
	public static final byte STAR_MAX = 10;

	/**
	 * @param role 参与事件的角色
	 * @return 通常状态下物品是否可使用
	 */
	public boolean canUse(final RoleBean role) {
		if(type.isExclusive() 
				&& !LuaService.getObject(Vocation.class, LUA_CONTAINER, tid, "vocation").equals(role.getVocation())) {
			MessageSend.replyMessage(role, 1, MsgID.MsgID_Item_Do_Resp, "职业不符，该物品无法使用！");
			return false;
		}
			
		
		if(LuaService.getInt(LUA_CONTAINER, tid, "level") > role.getLevel()) {
			MessageSend.replyMessage(role, 1, MsgID.MsgID_Item_Do_Resp, "级别不够，无法使用！");
			return false;
		}
		
		return true;
	}
	
	@Override
	public State getState(final int curMin) {
		if(super.getState(curMin) == State.Invalid)
			return State.Invalid;

		if(durability == 0)
			return State.MuteValid;

		return State.Valid;
	}

	@Override
	public byte getWearLoc() {
		return type.getWearIndex();
	}

	/**
	 * @param role 穿戴装备的角色
	 */
	@Override
	public void onWear(final RoleBean role) {
		LuaService.callOO(2, LUA_CONTAINER, tid, "wearItem", role, this);

		PropertyMan propertyMan = role.getPropertyMan();
		propertyMan.addBonus(basicAttr);
		propertyMan.addBonus(additiveAttr);
		for(Mountable mountable : mounted) {
			if(mountable != null)
				propertyMan.addBonus(mountable.getBonus());
		}
	}

	@Override
	public void onUnwield(final RoleBean role) {
		PropertyMan propertyMan = role.getPropertyMan();
		propertyMan.removeBonus(basicAttr);
		propertyMan.removeBonus(additiveAttr);
		for(Mountable mountable : mounted) {
			if(mountable != null)
				propertyMan.removeBonus(mountable.getBonus());
		}
	}

	/**
	 * @param role 参与事件的角色
	 * @param pack 存储物品的位置
	 */
	@Override
	public void onUse(final RoleBean role, final Pack pack) {
		InusePack inuse = role.getStore().getInuse();
		Wearable wearable = inuse.getInuseItem(type);
		
		if(wearable != null) {
			if(!wearable.canUnwield(role))
				MessageSend.replyMessage(role, 2, MsgID.MsgID_Item_Do_Resp, "该装备无法卸下！");
			
			wearable.onUnwield(role);
			inuse.getPackItems().remove(wearable.getUid());
		}
		
		
		
		MessageSend.sendMsg(role, MsgID.MsgID_Item_Do_Resp, "装备成功！");
		
		pack.addItem(wearable, false);
		
		pack.pickItem(this, false);
		
		inuse.addItem(this, false);
		
		//判断是否已绑定，并且是否是可以第一次装备后绑定的装备     如果是的话则将装备进行绑定
		
		if((!this.isBind()) && this.hasFeature(ItemFeature.Mountable))
		{
			this.setFeature(ItemFeature.Bind);
		}
		
		MessageSend.prepareBody();
		LuaService.call(0, "fillEquipUse", uid);
		SubModules.fillAttributes(role);
		SubModules.fillAttributesDes(role);
		MessageSend.putShort((short) 0);
		MessageSend.sendMsg(role, MsgID.MsgID_Special_Train);
	}

	/**
	 * @param role 参与事件的角色
	 * @param pack 存储物品的位置
	 */
	@Override
	public void onUnuse(final RoleBean role, final Pack pack) {
		MessageSend.sendMsg(role, MsgID.MsgID_Unwield_Equip_Resp, 
				type.getTypeName() + "【" + this.getName() + "】已卸下～");
		
		Pack bag = role.getStore().getBag();
		InusePack inuse = role.getStore().getInuse();
		bag.addItem(inuse.pickItem(this, false), false);
		
		MessageSend.prepareBody();
		LuaService.call(0, "fillUnwieldEquip", this.type.getWearIndex());
		SubModules.fillAttributes(role);
		SubModules.fillAttributesDes(role);
		MessageSend.putShort((short) 0);
		MessageSend.sendMsg(role, MsgID.MsgID_Special_Train);
	}

	@Override
	public String getName() {
		String rawName = LuaService.getString(LUA_CONTAINER, tid, "name");
		
		if(star > 0 && mountLimit > 0)
			return rawName + "+" + star + "(" + mounted.size() + "|" + mountLimit + ")";
		else if(star > 0)
			return rawName + "+" + star;
		else if(mountLimit > 0)
			return rawName + "(" + mounted.size() + "|" + mountLimit + ")";
		else
			return rawName;
	}

	@Override
	public boolean canWear(final RoleBean role) {
		if(type.isExclusive()
				&& role.getVocation() != LuaService.getObject(Vocation.class, LUA_CONTAINER, tid, "vocation"))
			return false;

		return role.getLevel() >= LuaService.getInt(LUA_CONTAINER, tid, "level");
	}

	@Override
	public boolean canUnwield(final RoleBean role) {
		
		return true;
	}

	@Override
	public String getDescribe() {
		StringBuilder builder = new StringBuilder();
		
		builder.append(this.getName() + "/");

		if(star > 0) {
			for(int i = 0; i < 10; i++)
				builder.append(i < star ? "★" : "☆");
			builder.append("/");
		}
		
		for(ItemFeature itemFeature : ItemFeature.values())
			if(itemFeature.isShow() && ((feature >> itemFeature.ordinal()) & 1) == 1)
				builder.append(itemFeature.getName() + "/");
		builder.append(type.getTypeName() + "/");
		
		if(type.isExclusive())
			builder.append("职业限制    " + LuaService.getObject(Vocation.class, LUA_CONTAINER, tid, "vocation") + "/");
		
		builder.append("等级限制    " + LuaService.getInt(LUA_CONTAINER, tid, "level") + "/");
		builder.append(LuaService.getString(LUA_CONTAINER, tid, "describe") + "/");
		
		builder.append("    /");
//		builder.append("基本属性/");
		for(Bonus bonus : basicAttr)
			builder.append(bonus + "/");
		builder.append("    /");
		
//		builder.append("附加属性/");
		for(Bonus bonus : additiveAttr)
			builder.append(bonus + "/");
		builder.append("    /");
		
		builder.append("价  格    " + LuaService.call4String("getValueDescribe",LuaService.callOO4Int(2, LUA_CONTAINER, tid, "getSellValue")) + "/");
		
//		builder.append("耐久上限    " + ((int) durability) + "    |    " + endure + "/");
		if(expire != Integer.MAX_VALUE) {
			if(expire <= Cardinality.INSTANCE.getMinute())
				
				builder.append("剩余时间    0分钟/");
			else
				builder.append("剩余时间    " + (expire - Cardinality.INSTANCE.getMinute()) + "分钟/");
		}
		
		if(producer != null && !producer.equals(""))
			builder.append("制造者    " + producer + "/");
		
		return builder.toString();
	}
	
	public ArrayList<Bonus> getDescribeBasicAttr() {
		return basicAttr;
	}
	
	public ArrayList<Bonus> getDescribeAdditiveAttr() {
		return additiveAttr;
	}
	
	
	public String getDescribe(final RoleBean player) {
		
		Store store = player.getStore();
		InusePack inusePack = store.getInuse();
		Wearable wearable =inusePack.getInuseItem(type)==null?(new Equip()):inusePack.getInuseItem(type);
		ArrayList<Bonus> basicAttrList = (wearable.getDescribeBasicAttr()==null?(new ArrayList<Bonus>()):wearable.getDescribeBasicAttr());
		ArrayList<Bonus> additiveAttrList = (wearable.getDescribeAdditiveAttr()==null?(new ArrayList<Bonus>()):wearable.getDescribeAdditiveAttr());
		
		StringBuilder builder = new StringBuilder();
		builder.append(this.getName() + "/");

		if(star > 0) {
			for(int i = 0; i < 10; i++)
				builder.append(i < star ? "★" : "☆");
			builder.append("/");
		}
		
		for(ItemFeature itemFeature : ItemFeature.values())
			if(itemFeature.isShow() && ((feature >> itemFeature.ordinal()) & 1) == 1)
				builder.append(itemFeature.getName() + "/");
		builder.append(type.getTypeName() + "/");
		
		if(type.isExclusive())
			builder.append("职业限制    " + LuaService.getObject(Vocation.class, LUA_CONTAINER, tid, "vocation") + "/");
		
		builder.append("等级限制    " + LuaService.getInt(LUA_CONTAINER, tid, "level") + "/");
//		builder.append(LuaService.getString(LUA_CONTAINER, tid, "describe") + "/");
		
		builder.append("    /");
		builder.append("[背包装备]                  [身上装备]/");
		builder.append("基本属性/");
		int basicAttrLength = basicAttr.size();
		int basicAttrListLength = basicAttrList.size();
		int basicAttrMaxLength = basicAttrLength >= basicAttrListLength ? basicAttrLength : basicAttrListLength;
		for(int i=basicAttrLength;i<basicAttrMaxLength;i++){
			basicAttr.add(null);
		}
		for(int i=basicAttrListLength;i<basicAttrMaxLength;i++){
			basicAttrList.add(null);
		}
		for (int i = 0; i < basicAttrMaxLength; i++) {
			builder.append(((basicAttr.get(i)==null?"":basicAttr.get(i).toString())+"~~~~~~~~~~~~~~~~").substring(0,15) + ">" + (basicAttrList.get(i)==null?"":basicAttrList.get(i).toString()) +"/");
		}
		for(int i = basicAttrMaxLength-1;i>basicAttrLength-1;i--){
			basicAttr.remove(i);
		}
		for(int i = basicAttrMaxLength-1;i>basicAttrListLength-1;i--){
			basicAttrList.remove(i);
		}
		builder.append("    /");
		
		builder.append("附加属性/");
		int additiveAttrLength = additiveAttr.size();
		int additiveAttrListLength = additiveAttrList.size();
		int additiveAttrMaxLength = additiveAttrLength > additiveAttrListLength ? additiveAttrLength : additiveAttrListLength;
		for(int i=additiveAttr.size();i<additiveAttrMaxLength;i++){
			additiveAttr.add(null);
		}
		for(int i=additiveAttrList.size();i<additiveAttrMaxLength;i++){
			additiveAttrList.add(null);
		}
		for (int i = 0; i < additiveAttrMaxLength; i++) {
			builder.append(((additiveAttr.get(i)==null?"":additiveAttr.get(i).toString())+"~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~").substring(0,15) + ">" + (additiveAttrList.get(i)==null?"":additiveAttrList.get(i).toString()) +"/");
		}
		for(int i = additiveAttrMaxLength-1;i>additiveAttrLength-1;i--){
			additiveAttr.remove(i);
		}
		for(int i = additiveAttrMaxLength-1;i>additiveAttrListLength-1;i--){
			additiveAttrList.remove(i);
		}
		builder.append("    /");
		
//		builder.append("价  格    " + LuaService.call4String("getValueDescribe",LuaService.callOO4Int(2, LUA_CONTAINER, tid, "getSellValue")) + "/");
		
//		builder.append("耐久上限    " + ((int) durability) + "    |    " + endure + "/");
		if(expire != Integer.MAX_VALUE) {
			if(expire <= Cardinality.INSTANCE.getMinute())
				
				builder.append("剩余时间    0分钟/");
			else
				builder.append("剩余时间    " + (expire - Cardinality.INSTANCE.getMinute()) + "分钟/");
		}
		
		if(producer != null && !producer.equals(""))
			builder.append("制造者    " + producer + "/");
		return builder.toString();
	}
	
	/**
	 * @return 此装备是否可升星
	 */
	public boolean canRefine() {
		return !this.type.isJewelry() && this.star < STAR_MAX;
	}
	
	@Override
	public int getColor() {
		return quality.getColorCode();
	}
	
	/**
	 * 设置镶嵌上限
	 * @param capacity 镶嵌上限
	 */
	public void setMountedCapacity(final int capacity) {
		mountLimit = capacity;
		
		while(mounted.size() > mountLimit) 
			mounted.remove(mounted.size());
	}

	@Override
	public int getMailFee() {
		return LuaService.callOO4Int(2, LUA_CONTAINER, tid, "getMailFee", quality.ordinal());
	}

	/**
	 * @return 装备类型
	 */
	public EquipType getType() {
		if(type == null)
			type = LuaService.getObject(EquipType.class, LUA_CONTAINER, tid, "subtype");
		
		return type;
	}

	/**
	 * @param type 装备类型
	 */
	public void setType(final EquipType type) {
		this.type = type;
	}

	/**
	 * @return 装备品质
	 */
	public EquipQuality getQuality() {
		return quality;
	}

	/**
	 * @param quality 装备品质
	 */
	public void setQuality(final EquipQuality quality) {
		this.quality = quality;
	}

	/**
	 * @return 制作者
	 */
	public String getProducer() {
		return producer;
	}

	/**
	 * @param producer 制作者
	 */
	public void setProducer(final String producer) {
		this.producer = producer;
	}

	/**
	 * @return 实际耐久度
	 */
	public float getDurability() {
		return durability;
	}

	/**
	 * @param durability 实际耐久度
	 */
	public void setDurability(final int durability) {
		this.durability = durability;
	}

	/**
	 * @return 耐久上限
	 */
	public float getEndure() {
		return endure;
	}

	/**
	 * @param endure 耐久上限
	 */
	public void setEndure(final int endure) {
		this.endure = endure;
	}

	/**
	 * @return 基本属性
	 */
	public ArrayList<Bonus> getBasicAttr() {
		return basicAttr;
	}

	/**
	 * @return 附加属性
	 */
	public ArrayList<Bonus> getAdditiveAttr() {
		return additiveAttr;
	}

	/**
	 * @return 镶嵌上限
	 */
	public int getMountLimit() {
		return mountLimit;
	}

	/**
	 * @param mountLimit 镶嵌上限
	 */
	public void setMountLimit(final int mountLimit) {
		this.mountLimit = mountLimit;
	}

	/**
	 * @return 已镶嵌
	 */
	public ArrayList<Mountable> getMounted() {
		return mounted;
	}

	/**
	 * @return 星级
	 */
	public byte getStar() {
		return star;
	}

	/**
	 * @param star 星级
	 */
	public void setStar(final byte star) {
		this.star = star;
	}

	/**
	 * 序列化
	 * @param builder 目标缓冲区
	 */
	@Override
	public void serialize(final StringBuilder builder) {
		builder.append("<");
		builder.append("class=Equip;");
		builder.append("tid=" + tid + ";");
		builder.append("feature=" + feature + ";");
		builder.append("uid=" + Long.toString(uid) + ";");
		if(expire != Integer.MAX_VALUE)
			builder.append("expire=" + expire + ";");
		builder.append("type=" + type.name() + ";");
		builder.append("quality=" + quality.name() + ";");
		
		builder.append("bAttr=[");
		for(Bonus bonus : basicAttr) 
			builder.append("(" + bonus.serialize() + "),");
		builder.append("];");
		
		builder.append("aAttr=[");
		for(Bonus bonus : additiveAttr) 
			builder.append("(" + bonus.serialize() + "),");
		builder.append("];");
		
		builder.append("mMax=" + mountLimit + ";");
		
		builder.append("mounted=[");
		for(Mountable mountable : mounted) 
			mountable.serialize(builder);
		builder.append("];");
		
		if(producer != null && !producer.equals(""))
			builder.append("producer=" + producer + ";");
		builder.append("endure=" + endure + ";");
		builder.append("durab=" + durability + ";");
		builder.append("star=" + star + ";");
		
		builder.append(">");
	}
	
	/**
	 * 反序列化
	 * @param tokenizer 数据源
	 * @return Equip
	 */
	public static Equip readItem(final StringTokenizer tokenizer) {
		Equip equip = new Equip();

		while(true) {
			String token = tokenizer.nextToken(Store.DELIM);
			if(token.equals(">"))//读取了>
				break;
			
			 //key
			tokenizer.nextToken(); //=
			
			if(token.equals("tid")) {
				equip.tid = Integer.parseInt(tokenizer.nextToken()); //value
			}else if(token.equals("feature")) {
				equip.feature = Integer.parseInt(tokenizer.nextToken()); //value
			}else if(token.equals("uid")) {
				equip.uid = Long.parseLong(tokenizer.nextToken()); //value
			}else if(token.equals("expire")) {
				equip.expire = Integer.parseInt(tokenizer.nextToken()); //value
			}else if(token.equals("type")) {
				equip.type = EquipType.valueOf(tokenizer.nextToken()); //value
			}else if(token.equals("quality")) {
				equip.quality = EquipQuality.valueOf(tokenizer.nextToken()); //value
			}else if(token.equals("bAttr")) {
				tokenizer.nextToken(); //[
				while(tokenizer.nextToken().equals("(")) { //读取了]
					equip.basicAttr.add(Bonus.readBonus(tokenizer));
					tokenizer.nextToken(); //,
				}
			}else if(token.equals("aAttr")) {
				tokenizer.nextToken(); //[
				while(tokenizer.nextToken().equals("(")) { //读取了]
					equip.additiveAttr.add(Bonus.readBonus(tokenizer));
					tokenizer.nextToken(); //,
				}
			}else if(token.equals("mMax")) {
				equip.mountLimit = Integer.parseInt(tokenizer.nextToken()); //value
			}else if(token.equals("mounted")) {
				tokenizer.nextToken(); //[
				while(tokenizer.nextToken().equals("<")) { //读取了]
					equip.mounted.add(Mountable.readMounted(tokenizer));
				}
			}else if(token.equals("producer")) {
				String producer = tokenizer.nextToken();
				if(!producer.equals("")) //value
					equip.producer = producer;
			}else if(token.equals("endure")) {
				equip.endure = Integer.parseInt(tokenizer.nextToken()); //value
			}else if(token.equals("durab")) {
				equip.durability = Float.parseFloat(tokenizer.nextToken());
			}else if(token.equals("star")) {
				equip.star = Byte.parseByte(tokenizer.nextToken());
			}else {
				tokenizer.nextToken(";");
			}
			
			tokenizer.nextToken(); //;
		}
		
		return equip;
	}
	
	/**
	 * 重置装备附加属性
	 * @param pack 装备所在的pack
	 * @return 新的附加属性描述
	 */
	public String resetAdditiveAttr(final Pack pack) {
		pack.pickItem(this, false);
		
		LuaService.callOO(2, LUA_CONTAINER, tid, "resetAdditiveAttr", this);
		
		pack.addItem(this, false);
		
		StringBuilder builder = new StringBuilder();
		for(Bonus bonus : additiveAttr)
			builder.append(bonus + "/");
		
		return builder.toString();
	}

	@Override
	public int getAuctionCategory() {
		return (1 << AutionType.EquipTotal.ordinal()) | type.getAuctionCategory();
	}

}
