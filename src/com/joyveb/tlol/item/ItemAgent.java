package com.joyveb.tlol.item;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import com.joyveb.tlol.LuaService;
import com.joyveb.tlol.SubModules;
import com.joyveb.tlol.TianLongServer;
import com.joyveb.tlol.billboard.TopRated;
import com.joyveb.tlol.core.AgentProxy;
import com.joyveb.tlol.net.IncomingMsg;
import com.joyveb.tlol.protocol.DefaultMsgBody;
import com.joyveb.tlol.protocol.GetItemInfoBody;
import com.joyveb.tlol.protocol.ItemRefineBody;
import com.joyveb.tlol.protocol.MsgID;
import com.joyveb.tlol.role.RoleBean;
import com.joyveb.tlol.schedule.Broadcast;
import com.joyveb.tlol.store.CommonPack;
import com.joyveb.tlol.store.InusePack;
import com.joyveb.tlol.store.Pack;
import com.joyveb.tlol.util.Log;

/**
 * 物品代理
 * 
 * @author Sid
 */
public class ItemAgent extends AgentProxy {

	/**
	 * @param player 角色
	 */
	public ItemAgent(final RoleBean player) {
		this.player = player;
	}

	@Override
	public void processCommand(final IncomingMsg message) {
		switch(MsgID.getInstance(message.getHeader().getMsgID())) {
		case MsgID_Item_Get_Info:
			if(GetItemInfoBody.INSTANCE.readBody(message.getBody())) {
				Item item = player.getStore().getBag().getItem(GetItemInfoBody.INSTANCE.getItemid());
				if(item == null)
					replyMessage(player, 2, MsgID.MsgID_Item_Get_Info_Resp, "查看失败！");
				else
					item.viewItem(player);
			}else
				replyMessage(player, 1, MsgID.MsgID_Item_Get_Info_Resp, "查看失败！");
			break;
		case MsgID_Item_Do:
			if(MsgID.MsgID_Item_Do.readBody(message.getBody())) {
				DefaultMsgBody msgBody = MsgID.MsgID_Item_Do.getMsgBody();

				Pack bag = player.getStore().getBag();

				Item item = bag.getItem(msgBody.getLong(2));

				if(item == null) {
					Log.error(Log.ERROR, "客户端发送的物品id" + GetItemInfoBody.INSTANCE.getItemid() + "不存在！");
					replyMessage(player, 1, MsgID.MsgID_Item_Do_Resp, "操作失败！该物品不存在");
					return;
				}

				if(msgBody.getByte(1) == 0) {
					if(item.canUse(player))
						item.onUse(player, bag);
				}else
					drop(msgBody.getLong(2), msgBody.getByte(3));
			}else
				replyMessage(player, 1, MsgID.MsgID_Item_Do_Resp, "操作失败！");
			break;
		case MsgID_Unwield_Equip:
			if(MsgID.MsgID_Unwield_Equip.readBody(message.getBody())) {
				byte type = MsgID.MsgID_Unwield_Equip.getMsgBody().getByte(1);
				InusePack pack = player.getStore().getInuse();
				Item item = pack.getInuseItem(type);
				if(item == null) {
					replyMessage(player, 1, MsgID.MsgID_Unwield_Equip_Resp, "卸下失败！");
					return;
				}
				Equip equip = (Equip) item;
				if(equip.canUnwield(player))
					equip.onUnuse(player, pack);
				else
					replyMessage(player, 1, MsgID.MsgID_Unwield_Equip_Resp, "卸下失败！");
			}else
				replyMessage(player, 1, MsgID.MsgID_Unwield_Equip_Resp, "卸下失败！");
			break;
		case MsgID_Inuse_View:
			if(MsgID.MsgID_Inuse_View.readBody(message.getBody())) {
				byte type = MsgID.MsgID_Inuse_View.getMsgBody().getByte(1);
				Item item = player.getStore().getInuse().getInuseItem(type);
				if(item == null)
					replyMessage(player, 1, MsgID.MsgID_Inuse_View_Resp, "查看失败！");
				else
					item.viewEquip(player);
			}else {
				replyMessage(player, 1, MsgID.MsgID_Inuse_View_Resp, "查看失败！");
			}
			break;
		case MsgID_Get_Pack_Data:
			if(MsgID.MsgID_Get_Pack_Data.readBody(message.getBody())) {
				byte packid = MsgID.MsgID_Get_Pack_Data.getMsgBody().getByte(1);
				getPackData(packid);
			}else
				replyMessage(player, 1, MsgID.MsgID_Get_Pack_Data_Resp, "查看失败！");

			break;
		case MsgID_Pack_Put:
			if(MsgID.MsgID_Pack_Put.readBody(message.getBody())) {
				DefaultMsgBody msgBody = MsgID.MsgID_Pack_Put.getMsgBody();
				byte packid = msgBody.getByte(1);
				long uid = msgBody.getLong(2);
				short num = msgBody.getShort(3);

				packPut(packid, uid, num);
			}else
				replyMessage(player, 1, MsgID.MsgID_Pack_Put_Resp, "操作失败！");
			break;
		case MsgID_Pack_Get:
			if(MsgID.MsgID_Pack_Get.readBody(message.getBody())) {
				DefaultMsgBody msgBody = MsgID.MsgID_Pack_Get.getMsgBody();
				byte packid = msgBody.getByte(1);
				long uid = msgBody.getLong(2);
				short num = msgBody.getShort(3);

				packGet(packid, uid, num);
			}else
				replyMessage(player, 1, MsgID.MsgID_Pack_Put_Resp, "操作失败！");
			break;
		case MsgID_Pack_Item_Info:
			if(MsgID.MsgID_Pack_Item_Info.readBody(message.getBody())) {
				DefaultMsgBody msgBody = MsgID.MsgID_Pack_Item_Info.getMsgBody();
				byte packid = msgBody.getByte(1);
				long uid = msgBody.getLong(2);

				packItemInfo(packid, uid);
			}else
				replyMessage(player, 1, MsgID.MsgID_Pack_Put_Resp, "操作失败！");
			break;
		case Item_Refine_Init:
			prepareBody();
			putLong(Item.getRefineNecessary());
			sendMsg(player, MsgID.Item_Refine_Init_Resp);
			break;
		case Item_Refine_Confirm:
			if(ItemRefineBody.INSTANCE.readBody(message.getBody()))
				confirmRefine();
			else
				replyMessage(player, 1, MsgID.Item_Refine_Confirm_Resp, "操作失败！");

			break;
		case Item_Refine:
			if(ItemRefineBody.INSTANCE.readBody(message.getBody()))
				refineItem();
			else
				replyMessage(player, 1, MsgID.Item_Refine_Resp, "操作失败！");

			break;
		case Item_Compose_Confirm:
			if(MsgID.Item_Compose_Confirm.readBody(message.getBody()))
				confirmCompose();
			else
				replyMessage(player, 1, MsgID.Item_Compose_Confirm_Resp, "操作失败！");

			break;
		case Item_Compose:
			if(MsgID.Item_Compose.readBody(message.getBody()))
				composeItem();
			else
				replyMessage(player, 1, MsgID.Item_Compose_Resp, "操作失败！");

			break;
		case Item_Decompose_Confirm:
			if(MsgID.Item_Decompose_Confirm.readBody(message.getBody()))
				confirmDecompose();
			else
				replyMessage(player, 1, MsgID.Item_Decompose_Confirm_Resp, "操作失败！");

			break;
		case Item_Decompose:
			if(MsgID.Item_Compose.readBody(message.getBody()))
				decomposeItem();
			else
				replyMessage(player, 1, MsgID.Item_Decompose_Resp, "操作失败！");

			break;
		default:
			Log.error(Log.ERROR, "此模块下不存在的协议！" + MsgID.getInstance(message.getHeader().getMsgID()));
			break;
		}
	}

	/**
	 * 丢弃背包中物品
	 * 
	 * @param uid 物品id
	 * @param num 数量
	 */
	private void drop(final long uid, final byte num) {
		Pack bag = player.getStore().getBag();

		Item item = bag.getItem(uid);
		
		if(!item.isDropable()) {
			replyMessage(player, 2, MsgID.MsgID_Item_Do_Resp, "该物品不可丢弃！");
			return;
		}

		short drop = item.getStorage() >= num ? num : item.getStorage();

		bag.pickItem(uid, drop, true);

		Log.info(Log.ITEM, player.getUserid() + "#$" + player.getRoleid() + "#$1#$" + item.getName() + "#$" + drop
				+ "#$" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + "#$" + TianLongServer.srvId
				+ "#$" + "0" + "#$" + item.getTid() + "#$" + uid);
		sendMsg(player, MsgID.MsgID_Item_Do_Resp, "丢弃成功～");

		prepareBody();

		LuaService.call(0, "fillBagDel", uid, drop);

		putShort((short) 0);

		sendMsg(player, MsgID.MsgID_Special_Train);
	}

	/**
	 * 获取仓库中的物品数据
	 * 
	 * @param packid 客户端发送的仓库索引，实际值为packid + 1
	 */
	private void getPackData(final byte packid) {
		CommonPack pack = (CommonPack) player.getStore().getPack(packid + 1);
		int checkNum = 0;
		HashMap<Long, Item> packItems = pack.getPackItems();
		
		
		for(Item item: packItems.values()){
			if(item.getStorage()>20){
				if(item.getStorage()%20==0){
					checkNum =item.getStorage()/20+checkNum;
				}else{
					checkNum =item.getStorage()/20+checkNum+1;
				}
				
			} else{
				checkNum =checkNum+1;
			}
		}
		if((int)pack.getCapacity()<checkNum){
			replyMessage(player, 0, MsgID.MsgID_Get_Pack_Data_Resp, "数据异常，无法查看！");
			return;
		}else{
		
		
		
		
		
		prepareBody();
		putShort((short) 0);

		putString("仓库" + packid);
		putByte(packid);
		putShort(pack.getCapacity());

		

		putByte((byte) packItems.size());

		for(Item item : packItems.values()) {
			putLong(item.getUid());
			putString(item.getName());
			putShort(item.getStorage());
			putInt(item.getColor());

			if(LuaService.getBool(Item.LUA_CONTAINER, item.getTid(), "overlay"))
				putShort(LuaService.getShort(Item.LUA_CONTAINER, item.getTid(), "overlay"));
			else
				putShort((short) 20);
		}

		sendMsg(player, MsgID.MsgID_Get_Pack_Data_Resp);
		}
	}

	/**
	 * 存入
	 * 
	 * @param packid 存储位id
	 * @param uid 物品id
	 * @param num 数量
	 */
	private void packPut(final byte packid, final long uid, final short num) {
		if(!player.getStore().isDepotExist(packid)) {
			replyMessage(player, 2, MsgID.MsgID_Get_Pack_Data_Resp, "查看失败！");
			return;
		}

		Item item = player.getStore().getBag().getItem(uid);
		if(item == null) {
			replyMessage(player, 3, MsgID.MsgID_Pack_Put_Resp, "存入失败！");
			return;
		}else if(num < 0 || item.getStorage() < num) {
			replyMessage(player, 4, MsgID.MsgID_Pack_Put_Resp, "存入失败！");
			return;
		}

		if(!LuaService.call4Bool("canAddToPack", player, packid, item.getTid(), num)) {
			replyMessage(player, 5, MsgID.MsgID_Pack_Put_Resp, "存入失败！仓库空间不足！");
			return;
		}

		prepareBody();
		putShort((short) 0);

		Item switchItem = player.getStore().switchPack(1, packid + 1, uid, num);
		SubModules.fillDepotAdd(player, packid, switchItem);
		LuaService.callLuaFunction("fillBagDel", item.getUid(), num);

		sendMsg(player, MsgID.MsgID_Pack_Put_Resp);
	}

	/**
	 * 取出
	 * 
	 * @param packid 存储位id
	 * @param uid 物品id
	 * @param num 数量
	 */
	private void packGet(final byte packid, final long uid, final short num) {
		if(!player.getStore().isDepotExist(packid)) {
			replyMessage(player, 2, MsgID.MsgID_Get_Pack_Data_Resp, "查看失败！");
			return;
		}

		Pack pack = player.getStore().getPack(packid + 1);
		Item item = pack.getItem(uid);
		if(item == null) {
			replyMessage(player, 3, MsgID.MsgID_Pack_Get_Resp, "取出失败！");
			return;
		}else if(item.getStorage() < num) {
			replyMessage(player, 4, MsgID.MsgID_Pack_Get_Resp, "取出失败！");
			return;
		}

		if(!LuaService.call4Bool("canAddToBag", player, item.getTid(), num)) {
			replyMessage(player, 5, MsgID.MsgID_Pack_Get_Resp, "取出失败！包裹空间不足！");
			return;
		}

		prepareBody();
		putShort((short) 0);

		Item switchItem = player.getStore().switchPack(packid + 1, 1, uid, num);
		SubModules.fillDepotDel(player, packid, switchItem);
		LuaService.callLuaFunction("fillBagAdd", switchItem);

		sendMsg(player, MsgID.MsgID_Pack_Get_Resp);
	}

	/**
	 * 查看某存储位中的物品
	 * 
	 * @param packid 存储位id
	 * @param uid 物品id
	 */
	private void packItemInfo(final byte packid, final long uid) {
		if(!player.getStore().isDepotOrBagExist(packid)) {
			replyMessage(player, 2, MsgID.MsgID_Get_Pack_Data_Resp, "查看失败！");
			return;
		}

		Pack pack = player.getStore().getPack(packid + 1);
		Item item = pack.getItem(uid);
		if(item == null) {
			replyMessage(player, 3, MsgID.MsgID_Pack_Item_Info_Resp, "查看失败！");
			return;
		}

		item.viewItem(player);
	}

	/**
	 * 装备升星确认
	 */
	private void confirmRefine() {
		if(!ItemRefine.INSTANCE.loadRefine(player, MsgID.Item_Refine_Confirm_Resp))
			return;

		Equip equip = (Equip) ItemRefine.INSTANCE.getRefine().get(RefineMaterial.Equip);

		StringBuilder builder = new StringBuilder();

		builder.append("升星装备");
		builder.append(equip.getName());
		builder.append("/");
		builder.append("当前成功率：");
		builder.append((int) (ItemRefine.INSTANCE.getSuccessRate() * 100));
		builder.append("%/");
		builder.append("消耗");
		builder.append(LuaService.getString(Item.LUA_CONTAINER, Item.getRefineNecessary(), "name"));
		builder.append("：");
		builder.append(ItemRefine.INSTANCE.getNecessaryExpend());
		builder.append("/");
		builder.append("消耗金币：");
		builder.append(LuaService.call4String("getValueDescribe", ItemRefine.INSTANCE.getCostGold()));
		replyMessage(player, 0, MsgID.Item_Refine_Confirm_Resp, builder.toString());
	}

	/**
	 * 装备升星
	 */
	private void refineItem() {
		if(!ItemRefine.INSTANCE.loadRefine(player, MsgID.Item_Refine_Resp))
			return;

		prepareBody();

		CommonPack bag = player.getStore().getBag();

		boolean success = Math.random() < ItemRefine.INSTANCE.getSuccessRate();

		if(success) {
			putShort((short) 0);
			putString("恭喜您升星成功！");
		}else {
			putShort((short) 11);
			putString("哎呀~很遗憾您升星失败了！");
		}

		for(Item item : ItemRefine.INSTANCE.getRefine().values()) {
			if(item instanceof Equip) {
				Equip equip = (Equip) item;
				int oldstar = equip.getStar(); // 进行升级操作前的星数

				if(success) {
					equip.setStar((byte) (equip.getStar() + 1));
					SubModules.fillFlushEquip(equip);

					if(equip.getStar() >= 6)
						Broadcast.send("恭喜" + player.getName() + "的武器升级到了"
								+ TopRated.CHNUM.charAt(equip.getStar() - 1) + "星");
				}else if(ItemRefine.INSTANCE.getRefine().containsKey(RefineMaterial.EquipProtect))
					continue;
				else if(equip.getStar() <= 3)
					continue;
				else {
					if(equip.getStar() < 6)
						equip.setStar((byte) 3);
					else if(equip.getStar() < 8)
						equip.setStar((byte) 6);
					else
						equip.setStar((byte) 8);

					SubModules.fillFlushEquip(equip);
				}

				int nowstar = equip.getStar();// 进行升级操作后的星数
				Log.info(Log.CHATDETAIL, player.getUserid() + "#$" + player.getName() + "#$" + player.getId() + "#$"
						+ equip.getName() + "#$" + equip.getTid() + "#$" + equip.getUid() + "#$" + oldstar + "#$"
						+ nowstar + "#$" + (oldstar < nowstar ? "success" : "false") + "#$" + (nowstar - oldstar)
						+ "#$" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + "#$"
						+ TianLongServer.srvId + "#$" + ItemRefine.INSTANCE.getCostGold());

				bag.pickItem(equip, false);

				for(int i = 1; i <= equip.getBasicAttr().size(); i++) {
					int rawValue = LuaService.getInt(Item.LUA_CONTAINER, equip.getTid(), "attr", i, "value");
					equip.getBasicAttr().get(i - 1).setArg(rawValue * (1 + ItemRefine.INSTANCE.getGrowth()));
				}

				bag.addItem(equip, false);
			}else if(item.getTid() == Item.getRefineNecessary()) {
				if(!success && ItemRefine.INSTANCE.getRefine().containsKey(RefineMaterial.NecessaryProtect))
					continue;

				bag.pickItem(item.getUid(), ItemRefine.INSTANCE.getNecessaryExpend(), true);
				LuaService.call(0, "fillBagDel", item.getUid(), ItemRefine.INSTANCE.getNecessaryExpend());
			}else {
				bag.pickItem(item.getUid(), 1, true);
				LuaService.call(0, "fillBagDel", item.getUid(), 1);
			}
		}

		player.setGold(player.getGold() - ItemRefine.INSTANCE.getCostGold());
		SubModules.fillAttributes(player);
		
		putShort((short) 0);
		sendMsg(player, MsgID.Item_Refine_Resp);
	}

	/**
	 * 合成确认
	 */
	private void confirmCompose() {
		long uid = MsgID.Item_Compose_Confirm.getMsgBody().getLong(1);

		CommonPack bag = player.getStore().getBag();
		Item item = bag.getItem(uid);
		if(item == null) {
			replyMessage(player, 2, MsgID.Item_Compose_Confirm_Resp, "操作失败！");
			return;
		}

		if(!LuaService.getBool(Item.COMPOSE, item.getTid())) {
			replyMessage(player, 3, MsgID.Item_Compose_Confirm_Resp, "操作失败，无法对该物品进行合成！");
			return;
		}

		int sourceNum = LuaService.getInt(Item.COMPOSE, item.getTid(), "sourceNum");
		if(item.getStorage() < sourceNum) {
			replyMessage(player, 4, MsgID.Item_Compose_Confirm_Resp, "该物品数量不足，无法进行合成！");
			return;
		}

		int costGold = LuaService.getInt(Item.COMPOSE, item.getTid(), "costGold");
		if(player.getGold() < costGold) {
			replyMessage(player, 5, MsgID.Item_Compose_Confirm_Resp, "资金不足，无法进行合成！");
			return;
		}

		int destItemId = LuaService.getInt(Item.COMPOSE, item.getTid(), "destItemId");

		StringBuilder builder = new StringBuilder();
		builder.append("合成物品：");
		builder.append(LuaService.getString(Item.LUA_CONTAINER, destItemId, "name"));
		builder.append("/需要消耗：/");
		builder.append(item.getName());
		builder.append("×");
		builder.append(sourceNum);
		builder.append("/金币：");
		builder.append(LuaService.call4String("getValueDescribe", costGold));
		builder.append("/当前成功率：");
		builder.append((int) (LuaService.getDouble(Item.COMPOSE, item.getTid(), "successRate") * 100));
		builder.append("%");

		replyMessage(player, 0, MsgID.Item_Compose_Confirm_Resp, builder.toString());
	}

	/**
	 * 合成
	 */
	private void composeItem() {
		long uid = MsgID.Item_Compose.getMsgBody().getLong(1);

		CommonPack bag = player.getStore().getBag();
		Item item = bag.getItem(uid);
		if(item == null) {
			replyMessage(player, 2, MsgID.Item_Compose_Resp, "操作失败！");
			return;
		}

		if(!LuaService.getBool(Item.COMPOSE, item.getTid())) {
			replyMessage(player, 3, MsgID.Item_Compose_Resp, "操作失败，无法对该物品进行合成！");
			return;
		}

		int sourceNum = LuaService.getInt(Item.COMPOSE, item.getTid(), "sourceNum");
		if(item.getStorage() < sourceNum) {
			replyMessage(player, 4, MsgID.Item_Compose_Resp, "该物品数量不足，无法进行合成！");
			return;
		}

		int costGold = LuaService.getInt(Item.COMPOSE, item.getTid(), "costGold");
		if(player.getGold() < costGold) {
			replyMessage(player, 5, MsgID.Item_Compose_Resp, "资金不足，无法进行合成！");
			return;
		}

		int destItemId = LuaService.getInt(Item.COMPOSE, item.getTid(), "destItemId");

		prepareBody();

		boolean success = Math.random() < LuaService.getDouble(Item.COMPOSE, item.getTid(), "successRate");

		if(success) {
			putShort((short) 0);
			putString("合成成功！");
		}else {
			putShort((short) 6);
			putString("合成失败！");
		}

		bag.pickItem(uid, sourceNum, true);
		LuaService.call(0, "fillBagDel", uid, sourceNum);

		player.setGold(player.getGold() - costGold);
		SubModules.fillAttributes(player);

		if(success) {
			Item compose = LuaService.callOO4Object(2, Item.LUA_CONTAINER, destItemId, "creatJavaItemSingle");
			LuaService.call(0, "fillBagAdd", compose);
			bag.addItem(compose, true);
		}

		sendMsg(player, MsgID.Item_Compose_Resp);
	}

	/**
	 * 分解确认
	 */
	private void confirmDecompose() {
		long uid = MsgID.Item_Decompose_Confirm.getMsgBody().getLong(1);

		CommonPack bag = player.getStore().getBag();
		Item item = bag.getItem(uid);
		if(item == null || !(item instanceof Equip)) {
			replyMessage(player, 2, MsgID.Item_Decompose_Confirm_Resp, "操作失败！");
			return;
		}

		int fragment = LuaService.callOO4Int(2, Item.LUA_CONTAINER, item.getTid(), "calcDecomposeFragment", item);
		int necessary = LuaService.callOO4Int(2, Item.LUA_CONTAINER, item.getTid(), "calcDecomposeNecessary", item);

		StringBuilder builder = new StringBuilder();
		builder.append("分解装备：");
		builder.append(item.getName());

		if(fragment == 0 && necessary == 0)
			builder.append("/不能获得任何物品。");
		else {
			builder.append("/可获得:/");
			if(fragment > 0) {
				int decompose = LuaService.getInt(Item.COMPOSE, "decompose");

				builder.append(LuaService.getString(Item.LUA_CONTAINER, decompose, "name"));
				builder.append("*");
				builder.append(fragment);
				builder.append("/");
			}

			if(necessary > 0) {
				builder.append(LuaService.getString(Item.LUA_CONTAINER, Item.getRefineNecessary(), "name"));
				builder.append("*");
				builder.append(necessary);
				builder.append("/");
			}
		}

		replyMessage(player, 0, MsgID.Item_Decompose_Confirm_Resp, builder.toString());
	}

	/**
	 * 分解
	 */
	private void decomposeItem() {
		long uid = MsgID.Item_Compose.getMsgBody().getLong(1);

		CommonPack bag = player.getStore().getBag();
		Item item = bag.getItem(uid);
		if(item == null || !(item instanceof Equip)) {
			replyMessage(player, 2, MsgID.Item_Decompose_Resp, "操作失败！");
			return;
		}

		int fragment = LuaService.callOO4Int(2, Item.LUA_CONTAINER, item.getTid(), "calcDecomposeFragment", item);
		int necessary = LuaService.callOO4Int(2, Item.LUA_CONTAINER, item.getTid(), "calcDecomposeNecessary", item);

		if(fragment == 0 && necessary == 0) {
			replyMessage(player, 3, MsgID.Item_Decompose_Resp, "操作失败！");
			return;
		}

		int decompose = LuaService.getInt(Item.COMPOSE, "decompose");
		if(!LuaService.call4Bool("canAddToBag", player, decompose, fragment, Item.getRefineNecessary(), necessary)) {
			replyMessage(player, 4, MsgID.Item_Decompose_Resp, "包裹空间不足！");
			return;
		}

		prepareBody();
		putShort((short) 0);

		StringBuilder builder = new StringBuilder();
		builder.append("分解装备：");
		builder.append(item.getName());

		if(fragment == 0 && necessary == 0)
			builder.append("没有获得任何物品。");
		else {
			builder.append("获得:/");

			if(fragment > 0) {
				builder.append(LuaService.getString(Item.LUA_CONTAINER, decompose, "name"));
				builder.append("*");
				builder.append(fragment);
				builder.append("/");
			}

			if(necessary > 0) {
				builder.append(LuaService.getString(Item.LUA_CONTAINER, Item.getRefineNecessary(), "name"));
				builder.append("*");
				builder.append(necessary);
				builder.append("/");
			}
		}

		putString(builder.toString());

		bag.pickItem(uid, 1, true);
		LuaService.call(0, "fillBagDel", uid, 1);

		if(fragment > 0) {
			Item decomposeItem = LuaService.callOO4Object(2, Item.LUA_CONTAINER, decompose, "creatJavaItemSingle", fragment);
			LuaService.call(0, "fillBagAdd", decomposeItem);
			bag.addItem(decomposeItem, true);
		}

		if(necessary > 0) {
			Item necessaryItem = LuaService.callOO4Object(2, Item.LUA_CONTAINER, Item.getRefineNecessary(),
					"creatJavaItemSingle", necessary);
			LuaService.call(0, "fillBagAdd", necessaryItem);
			bag.addItem(necessaryItem, true);
		}

		sendMsg(player, MsgID.Item_Decompose_Resp);
	}

}
