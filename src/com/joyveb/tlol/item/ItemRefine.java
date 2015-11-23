package com.joyveb.tlol.item;

import java.util.EnumMap;

import com.joyveb.tlol.LuaService;
import com.joyveb.tlol.MessageSend;
import com.joyveb.tlol.protocol.ItemRefineBody;
import com.joyveb.tlol.protocol.MsgID;
import com.joyveb.tlol.role.RoleBean;
import com.joyveb.tlol.store.CommonPack;
import com.joyveb.tlol.util.Log;

/**
 * 装备精炼数据
 * @author Sid
 *
 */
public enum ItemRefine {
	/**
	 * 单例
	 */
	INSTANCE;
	
	/**
	 * 材料分布
	 */
	private EnumMap<RefineMaterial, Item> refine = new EnumMap<RefineMaterial, Item>(RefineMaterial.class);
	
	/**
	 * 必需品消耗量
	 */
	private int necessaryExpend;
	
	/**
	 * 成功率
	 */
	private double successRate;
	
	/**
	 * 消耗金币
	 */
	private int costGold;
	
	/**
	 * 升星属性增长概率
	 */
	private double growth;
	
	/**
	 * 载入数据
	 * @param role 提交操作的角色
	 * @param reply 载入失败时返回给客户端的协议
	 * @return 数据分析是否通过
	 */
	public boolean loadRefine(final RoleBean role, final MsgID reply) {
		refine.clear();
		
		CommonPack bag = role.getStore().getBag();
		
		for(Long uid : ItemRefineBody.INSTANCE.getUids()) {
			if(uid == 0)
				continue;
			
			Item item = bag.getItem(uid);
			if(item == null) {
				Log.error(Log.ERROR, "客户端发送的uid" + uid + "不存在！");
				MessageSend.replyMessage(role, 2, reply, "升星失败！");
				return false;
			}else if(item instanceof Equip) {
				Equip equip = (Equip) item;
				if(equip.getType().isJewelry()) {
					MessageSend.replyMessage(role, 3, reply, "饰品无法升星！");
					return false;
				}else if(refine.containsKey(RefineMaterial.Equip)) {
					MessageSend.replyMessage(role, 4, reply, "无法升星多件装备！");
					return false;
				}else if(!equip.canRefine()) {
					MessageSend.replyMessage(role, 5, reply, "您的装备已升到最高星级，无法继续升星！");
					return false;
				}else
					refine.put(RefineMaterial.Equip, item);
			}else {
				int tid = item.getTid();
				RefineMaterial itemRefine = null;
				if(tid == Item.getRefineNecessary())
					itemRefine = RefineMaterial.Necessary;
				else
					itemRefine = LuaService.getObject(RefineMaterial.class, Item.LUA_CONTAINER, tid, "refine");
				
				if(itemRefine == null) {
					MessageSend.replyMessage(role, 6, reply, item.getName() + "无法在升星时使用！");
					return false;
				}else if(refine.containsKey(itemRefine)) {
					MessageSend.replyMessage(role, 7, reply, "升星时无法使用不同种的同类道具！");
					return false;
				}else 
					refine.put(itemRefine, item);
			}
		}
		
		if(!refine.containsKey(RefineMaterial.Necessary)) {
			MessageSend.replyMessage(role, 8, reply, "没有足够的"
					+ LuaService.getString(Item.LUA_CONTAINER, Item.getRefineNecessary(), "name") + "，无法升星！");
			return false;
		}
		
		if(refine.containsKey(RefineMaterial.Equip)) {
			Equip equip = (Equip) refine.get(RefineMaterial.Equip);
			
			LuaService.callOO(2, Item.LUA_CONTAINER, equip.getTid(), "calcRefine", equip);
			
			if(refine.get(RefineMaterial.Necessary).getStorage() < necessaryExpend) {
				MessageSend.replyMessage(role, 9, reply, "没有足够的"
						+ LuaService.getString(Item.LUA_CONTAINER, Item.getRefineNecessary(), "name") + "，无法升星！");
				return false;
			}
			
			if(role.getGold() < costGold) {
				MessageSend.replyMessage(role, 10, reply, "您的金钱不足本次升星！");
				return false;
			}
		}else {
			MessageSend.replyMessage(role, 11, reply, "您还没有选择装备，无法升星！");
			return false;
		}
		
		if(refine.containsKey(RefineMaterial.SuccessRate)) {
			Item item = refine.get(RefineMaterial.SuccessRate);
			this.successRate += LuaService.getDouble(Item.LUA_CONTAINER, item.getTid(), "success_rate");
		}
		
		return true;
	}

	/**
	 * @return 材料分布
	 */
	public EnumMap<RefineMaterial, Item> getRefine() {
		return refine;
	}

	/**
	 * @param necessaryExpend 必需品消耗量
	 */
	public void setNecessaryExpend(final int necessaryExpend) {
		this.necessaryExpend = necessaryExpend;
	}

	/**
	 * @return 必需品消耗量
	 */
	public int getNecessaryExpend() {
		return necessaryExpend;
	}

	/**
	 * @param successRate 成功率
	 */
	public void setSuccessRate(final double successRate) {
		if(successRate < 0)
			this.successRate = 0;
		else if(successRate > 1)
			this.successRate = 1;
		else
			this.successRate = successRate;
	}

	/**
	 * @return 成功率
	 */
	public double getSuccessRate() {
		return successRate;
	}

	/**
	 * @param costGold 消耗金币
	 */
	public void setCostGold(final int costGold) {
		this.costGold = costGold;
	}

	/**
	 * @return 消耗金币
	 */
	public int getCostGold() {
		return costGold;
	}

	/**
	 * @param growth 升星属性增长概率
	 */
	public void setGrowth(final double growth) {
		this.growth = growth;
	}

	/**
	 * @return 升星属性增长概率
	 */
	public double getGrowth() {
		return growth;
	}
	
}
