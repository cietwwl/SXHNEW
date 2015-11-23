package com.joyveb.tlol.item;

import java.util.ArrayList;
import java.util.StringTokenizer;

import com.joyveb.tlol.LuaService;
import com.joyveb.tlol.MessageSend;
import com.joyveb.tlol.protocol.MsgID;
import com.joyveb.tlol.role.RoleBean;
import com.joyveb.tlol.store.Pack;
import com.joyveb.tlol.store.Store;

/**
 * 普通物品，如果可使用则为单次消耗品，如：药品
 * @author Sid
 */
public class UbiquitousItem extends Item implements Cloneable {

	/**
	 * 序列化自身的属性
	 */
	public static final String[] SERIALIZE = {
		"tid",
		"feature",
		"storage",
	};
	
	/**
	 * 此物品对象中包含的物品数量
	 */
	private short storage = 1;
	
	/**
	 * 无参构造函数
	 */
	public UbiquitousItem() { }
	
	/**
	 * @param tid 模板id
	 * @param storage 物品数量
	 */
	public UbiquitousItem(final int tid, final short storage) {
		this.tid = tid;
		this.storage = storage;
	}

	@Override
	public long getUid() {
		return tid;
	}

	@Override
	public short getStorage() {
		return storage;
	}

	@Override
	public void setStorage(final short num) {
		this.storage = num;
	}

	@Override
	public boolean canUse(final RoleBean role) {
		if((feature >> ItemFeature.Unavailable.ordinal() & 1) == 1) {
			MessageSend.replyMessage(role, 1, MsgID.MsgID_Item_Do_Resp, "该物品无法使用！");
			return false;
		}
			
		return LuaService.callOO4Bool(2, LUA_CONTAINER, tid, "canUse", role);
	}
	
	@Override
	public void onUse(final RoleBean role, final Pack pack) {
		LuaService.callOO(2, LUA_CONTAINER, tid, "useItem", role, this);
	}

	@Override
	public void onUnuse(final RoleBean role, final Pack pack) { }

	@Override
	public String getName() {
		return LuaService.getString("ItemSet", tid, "name");
	}

	@Override
	public String getDescribe() {
		StringBuilder builder = new StringBuilder();
		
		builder.append(LuaService.callOO4String(2, LUA_CONTAINER, tid, "getDescribe", this) + "/ /");
		
		builder.append("价  格    " + LuaService.call4String("getValueDescribe", 
				LuaService.callOO4Int(2, LUA_CONTAINER, tid, "getSellValue")) + "/");
		
		return builder.toString();
	}
	
	/**
	 * @return 物品标题颜色
	 */
	public int getColor() {
		return LuaService.getBool("ItemSet", tid, "color") ? LuaService.getInt("ItemSet", tid, "color") : 0;
	}

	/**
	 * 从此对象中分割出部分物品
	 * @param num 要分割的数量
	 * @return 新生成的物品
	 */
	public UbiquitousItem pickup(final int num) {
		int realNum = num > storage ? storage : num;
		try {
			UbiquitousItem item = (UbiquitousItem) super.clone();
			this.setStorage((short) (storage - realNum));
			item.setStorage((short) realNum);
			return item;
		}catch(CloneNotSupportedException e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public int getMailFee() {
		return LuaService.callOO4Int(2, LUA_CONTAINER, tid, "getMailFee");
	}

	@Override
	public void serialize(final StringBuilder builder) {
		builder.append("<");
		builder.append("class=Ubiquitous;");
		builder.append("tid=" + tid + ";");
		builder.append("storage=" + storage + ";");
		builder.append("feature=" + feature + ";");
		builder.append(">");
	}
	
	/**
	 * 反序列化
	 * @param tokenizer 数据源
	 * @return UbiquitousItem
	 */
	public static UbiquitousItem readItem(final StringTokenizer tokenizer) {
		UbiquitousItem ubiquitousItem = new UbiquitousItem();
		
		while(true) {
			String token = tokenizer.nextToken(Store.DELIM);
			if(token.equals(">"))//读取了>
				break;
			
			tokenizer.nextToken(); //=
			
			if(token.equals("tid")) {
				ubiquitousItem.tid = Integer.parseInt(tokenizer.nextToken()); //value
			}else if(token.equals("feature")) {
				ubiquitousItem.feature = Integer.parseInt(tokenizer.nextToken()); //value
			}else if(token.equals("storage")) {
				ubiquitousItem.storage = Short.parseShort(tokenizer.nextToken()); //value
			}else if(token.equals("feature")) {
				ubiquitousItem.feature = Integer.parseInt(tokenizer.nextToken()); //value
			}else {
				tokenizer.nextToken(";");
			}
			
			tokenizer.nextToken(); //;
		}
		
		return ubiquitousItem;
	}

	@Override
	public State getState(final int curMin) {
		return LuaService.getBool(LUA_CONTAINER, tid) ? State.Valid : State.Invalid;
	}

	@Override
	public int getAuctionCategory() {
		return LuaService.callOO4Int(2, LUA_CONTAINER, tid, "getAuctionCategory");
	}

	@Override
	public String getDescribe(RoleBean player) {
		StringBuilder builder = new StringBuilder();

		builder.append(LuaService.callOO4String(2, LUA_CONTAINER, tid,
				"getDescribe", this) + "/ /");

		builder.append("价  格    "
				+ LuaService.call4String("getValueDescribe", LuaService
						.callOO4Int(2, LUA_CONTAINER, tid, "getSellValue"))
				+ "/");

		return builder.toString();
	}

	@Override
	public ArrayList<Bonus> getDescribeBasicAttr() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ArrayList<Bonus> getDescribeAdditiveAttr() {
		// TODO Auto-generated method stub
		return null;
	}

}
