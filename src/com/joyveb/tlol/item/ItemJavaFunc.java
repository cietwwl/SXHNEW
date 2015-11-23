package com.joyveb.tlol.item;

import org.keplerproject.luajava.LuaException;
import org.keplerproject.luajava.LuaObject;

import com.joyveb.tlol.DefaultJavaFunc;
import com.joyveb.tlol.LuaService;
import com.joyveb.tlol.TLOLJavaFunction;
import com.joyveb.tlol.role.RoleBean;
import com.joyveb.tlol.util.CalcMode;
import com.joyveb.tlol.MessageSend;

/**
 * 物品操作注册函数
 */
public enum ItemJavaFunc implements TLOLJavaFunction {
	/**
	 * Item.getStorage()
	 * 
	 * @param 参数1：Item
	 */
	ItemGetStorage(new DefaultJavaFunc("_ItemGetStorage") {
		@Override
		public int execute() throws LuaException {
			LuaService.push(((Item) this.getParam(2).getObject()).getStorage());
			return 1;
		}
	}),

	/**
	 * Item instanceof UniqueItem
	 * 
	 * @param 参数1：Item
	 */
	ItemIsUnique(new DefaultJavaFunc("_ItemIsUnique") {
		@Override
		public int execute() throws LuaException {
			this.L.pushBoolean(((Item) this.getParam(2).getObject()) instanceof UniqueItem);
			return 1;
		}
	}),

	/**
	 * Item.getTid()
	 * 
	 * @param 参数1：Item
	 */
	ItemGetTid(new DefaultJavaFunc("_ItemGetTid") {
		@Override
		public int execute() throws LuaException {
			LuaService.push(((Item) this.getParam(2).getObject()).getTid());
			return 1;
		}
	}),

	/**
	 * Item.getUid()
	 * 
	 * @param 参数1：Item
	 */
	ItemGetUid(new DefaultJavaFunc("_ItemGetUid") {
		@Override
		public int execute() throws LuaException {
			LuaService.push(((Item) this.getParam(2).getObject()).getUid());
			return 1;
		}
	}),

	/**
	 * Item.setUid()
	 * 
	 * @param 参数1：UniqueItem
	 */
	UniqueItemSetUid(new DefaultJavaFunc("_UniqueItemSetUid") {
		@Override
		public int execute() throws LuaException {
			((UniqueItem) this.getParam(2).getObject()).setUid((long) this.getParam(3).getNumber());
			return 0;
		}
	}),
	
	/**
	 * Item.setQuality()
	 * 
	 * @param 参数1：Item
	 */
	EquipSetQuality(new DefaultJavaFunc("_EquipSetQuality") {
		@Override
		public int execute() throws LuaException {
			((Equip) this.getParam(2).getObject()).setQuality((EquipQuality) this.getParam(3).getObject());
			return 0;
		}
	}),
	
	/**
	 * Item.setQuality()
	 * 
	 * @param 参数1：Item
	 */
	EquipGetQuality(new DefaultJavaFunc("_EquipGetQuality") {
		@Override
		public int execute() throws LuaException {
			LuaService.push(((Equip) this.getParam(2).getObject()).getQuality());
			return 1;
		}
	}),
	
	/**
	 * Item.getTid()
	 * 
	 * @param 参数1：Item
	 */
	ItemSetTid(new DefaultJavaFunc("_ItemSetTid") {
		@Override
		public int execute() throws LuaException {
			((Item) this.getParam(2).getObject()).setTid((int) this.getParam(3).getNumber());
			return 0;
		}
	}),
	
	/**
	 * Item.feature
	 */
	ItemGetFeatures(new DefaultJavaFunc("_ItemGetFeatures") {
		@Override
		public int execute() throws LuaException {
			LuaService.push(((Item) this.getParam(2).getObject()).feature);
			return 1;
		}
	}),
	
	/**
	 * Item.feature
	 */
	ItemHasFeature(new DefaultJavaFunc("_ItemHasFeature") {
		@Override
		public int execute() throws LuaException {
			LuaService.push(((Item) this.getParam(2).getObject()).hasFeature((ItemFeature) this.getParam(3).getObject()));
			return 1;
		}
	}),
	
	/**
	 * Item.setFeature()
	 */
	ItemSetFeatures(new DefaultJavaFunc("_ItemSetFeatures") {
		@Override
		public int execute() throws LuaException {
			Item item = (Item) this.getParam(2).getObject();
			
			LuaObject lobj = null;
			int index = 3;
			while(true) {
				lobj = this.getParam(index);
				if(lobj.isJavaObject()) {
					item.setFeature((ItemFeature) lobj.getObject());
					index++;
				}else
					break;
			}
			
			return 0;
		}
	}),
	
	/**
	 * Equip.getBasicAttr()
	 */
	EquipGetBasicAttr(new DefaultJavaFunc("_EquipGetBasicAttr") {
		@Override
		public int execute() throws LuaException {
			LuaService.push(((Equip) this.getParam(2).getObject()).getBasicAttr());
			return 1;
		}
	}),
	
	
	/**
	 * Equip.getAdditiveAttr()
	 */
	EquipGetAdditiveAttr(new DefaultJavaFunc("_EquipGetAdditiveAttr") {
		@Override
		public int execute() throws LuaException {
			LuaService.push(((Equip) this.getParam(2).getObject()).getAdditiveAttr());
			return 1;
		}
	}),
	
	/**
	 * Equip.setProducer()
	 */
	EquipSetProducer(new DefaultJavaFunc("_EquipSetProducer") {
		@Override
		public int execute() throws LuaException {
			((Equip) this.getParam(2).getObject()).setProducer(((RoleBean) this.getParam(3).getObject()).getName());
			return 0;
		}
	}),
	
	/**
	 * Equip.setEndure()
	 */
	EquipSetEndure(new DefaultJavaFunc("_EquipSetEndure") {
		@Override
		public int execute() throws LuaException {
			((Equip) this.getParam(2).getObject()).setEndure((int) this.getParam(3).getNumber());
			return 0;
		}
	}),
	
	/**
	 * Equip.setDurability()
	 */
	EquipSetDurability(new DefaultJavaFunc("_EquipSetDurability") {
		@Override
		public int execute() throws LuaException {
			((Equip) this.getParam(2).getObject()).setDurability((int) this.getParam(3).getNumber());
			return 0;
		}
	}),
	
	/**
	 * Equip.setMountedCapacity()
	 */
	EquipSetMountLimit(new DefaultJavaFunc("_EquipSetMountLimit") {
		@Override
		public int execute() throws LuaException {
			((Equip) this.getParam(2).getObject()).setMountedCapacity((int) this.getParam(3).getNumber());
			return 0;
		}
	}),
	
	/**
	 * UniqueItem.setExpire()
	 */
	UniqueItemSetExpire(new DefaultJavaFunc("_UniqueItemSetExpire") {
		@Override
		public int execute() throws LuaException {
			((UniqueItem) this.getParam(2).getObject()).setExpire((int) this.getParam(3).getNumber());
			return 0;
		}
	}),
	
	/**
	 * Equip.setStar()
	 */
	EquipSetStar(new DefaultJavaFunc("_EquipSetStar") {
		@Override
		public int execute() throws LuaException {
			((Equip) this.getParam(2).getObject()).setStar((byte) this.getParam(3).getNumber());
			return 0;
		}
	}),
	
	/**
	 * Equip.getStar()
	 */
	EquipGetStar(new DefaultJavaFunc("_EquipGetStar") {
		@Override
		public int execute() throws LuaException {
			LuaService.push(((Equip) this.getParam(2).getObject()).getStar());
			return 1;
		}
	}),
	
	/**
	 * EquipType.canPunch()
	 */
	EquipTypeCanPunch(new DefaultJavaFunc("_EquipTypeCanPunch") {
		@Override
		public int execute() throws LuaException {
			LuaService.push(((EquipType) this.getParam(2).getObject()).canPunch());
			return 1;
		}
	}),
	
	/**
	 * EquipType.isExclusive()
	 */
	EquipTypeIsExclusive(new DefaultJavaFunc("_EquipTypeIsExclusive") {
		@Override
		public int execute() throws LuaException {
			LuaService.push(((EquipType) this.getParam(2).getObject()).isExclusive());
			return 1;
		}
	}),
	
	/**
	 * Equip.setType()
	 */
	EquipSetType(new DefaultJavaFunc("_EquipSetType") {
		@Override
		public int execute() throws LuaException {
			((Equip) this.getParam(2).getObject()).setType((EquipType) this.getParam(3).getObject());
			return 1;
		}
	}),
	
	/**
	 * MessageSend.putString(Item.getName())
	 */
	PutItemNameAddColor(new DefaultJavaFunc("_PutItemNameAddColor") {
		@Override
		public int execute() throws LuaException {
			MessageSend.putString(((Item) this.getParam(2).getObject()).getName());
			MessageSend.putInt(((Item) this.getParam(2).getObject()).getColor());
			return 0;
		}
	}),
	
	/**
	 * EquipQuality.getColorCode()
	 */
	EquipQualityGetColor(new DefaultJavaFunc("_EquipQualityGetColor") {
		@Override
		public int execute() throws LuaException {
			LuaService.push(((EquipQuality) this.getParam(2).getObject()).getColorCode());
			return 1;
		}
	}),
	
	/**
	 * Item.getDescribe()
	 */
	ItemGetDescribe(new DefaultJavaFunc("_ItemGetDescribe") {
		@Override
		public int execute() throws LuaException {
			LuaService.push(((Item) this.getParam(2).getObject()).getDescribe());
			return 1;
		}
	}),
	
	/**
	 * EquipType.getTypeName()
	 */
	EquipTypeGetName(new DefaultJavaFunc("_EquipTypeGetName") {
		@Override
		public int execute() throws LuaException {
			LuaService.push(((EquipType) this.getParam(2).getObject()).getTypeName());
			return 1;
		}
	}),
	
	/**
	 * 获取Bonus显示
	 */
	GetBonusString(new DefaultJavaFunc("_GetBonusString") {
		@Override
		public int execute() throws LuaException {
			LuaService.push(this.getParam(2).getObject().toString()
					+ ((CalcMode) this.getParam(3).getObject()).fixValueString(this.getParam(4).getNumber()));
			return 1;
		}
	}),
	
	/**
	 * 物品特性是否显示
	 */
	ItemFeatureIsShow(new DefaultJavaFunc("_ItemFeatureIsShow") {
		@Override
		public int execute() throws LuaException {
			LuaService.push(((ItemFeature)this.getParam(2).getObject()).isShow());
			return 1;
		}
	}),
	
	/**
	 * 物品特性显示
	 */
	ItemFeatureGetName(new DefaultJavaFunc("_ItemFeatureGetName") {
		@Override
		public int execute() throws LuaException {
			LuaService.push(((ItemFeature)this.getParam(2).getObject()).getName());
			return 1;
		}
	}),
	
	/**
	 * 装备精炼设置属性
	 * @param 参数1：消耗必需品数量
	 * @param 参数2：成功率
	 * @param 参数3：消耗金币数量
	 * @param 参数4：属性增长率
	 */
	RefineSetProperty(new DefaultJavaFunc("_RefineSetProperty") {
		@Override
		public int execute() throws LuaException {
			ItemRefine.INSTANCE.setNecessaryExpend((int) this.getParam(2).getNumber());
			ItemRefine.INSTANCE.setSuccessRate(this.getParam(3).getNumber());
			ItemRefine.INSTANCE.setCostGold((int) this.getParam(4).getNumber());
			ItemRefine.INSTANCE.setGrowth(this.getParam(5).getNumber());
			
			return 0;
		}
	}),
	
	/**
	 * 物品名称
	 */
	ItemGetName(new DefaultJavaFunc("_ItemGetName") {
		@Override
		public int execute() throws LuaException {
			LuaService.push(((Item)this.getParam(2).getObject()).getName());
			return 1;
		}
	});
	
	/**
	 * 实现默认的可注册Java函数
	 */
	private final DefaultJavaFunc jf;

	/**
	 * @param jf 可注册Java函数
	 */
	private ItemJavaFunc(final DefaultJavaFunc jf) {
		this.jf = jf;
	}

	@Override
	public void register() {
		jf.register();
	}

}
