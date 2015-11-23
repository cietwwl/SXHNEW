package com.joyveb.tlol.item;

import java.util.ArrayList;
import java.util.StringTokenizer;

import com.joyveb.tlol.LuaService;
import com.joyveb.tlol.MessageSend;
import com.joyveb.tlol.auction.AutionType;
import com.joyveb.tlol.protocol.MsgID;
import com.joyveb.tlol.role.RoleBean;
import com.joyveb.tlol.store.Pack;
import com.joyveb.tlol.store.Store;

/**
 * 可镶嵌类物品
 * @author Sid
 *
 */
public class Mountable extends UniqueItem {

	/**
	 * 序列化自身的属性
	 */
	public static final String[] SERIALIZE = {
		"tid",
		"feature",
		"uid",
		"expire",
		"bonus",
	};
	
	/**
	 * 可镶嵌物品所带有的属性
	 */
	private Bonus bonus;
	
	@Override
	public void onObtain(final RoleBean role) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean canUse(final RoleBean role) {
		MessageSend.replyMessage(role, 1, MsgID.MsgID_Item_Do_Resp, "该物品无法使用！");
		return false;
	}
	
	@Override
	public void onUse(final RoleBean role, final Pack pack) { }

	@Override
	public void onUnuse(final RoleBean role, final Pack pack) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * @param bonus 可镶嵌物品所带有的属性
	 */
	public void setBonus(final Bonus bonus) {
		this.bonus = bonus;
	}

	/**
	 * @return 可镶嵌物品所带有的属性
	 */
	public Bonus getBonus() {
		return bonus;
	}

	@Override
	public int getColor() {
		return LuaService.getBool("ItemSet", tid, "color") ? LuaService.getInt("ItemSet", tid, "color") : 0;
	}

	@Override
	public int getMailFee() {
		return LuaService.callOO4Int(2, LUA_CONTAINER, tid, "getMailFee");
	}

	/**
	 * 序列化
	 * @param builder 目标容器
	 */
	public void serialize(final StringBuilder builder) {
		builder.append("<");
		builder.append("class=Mountable;");
		builder.append("tid=" + tid + ";");
		builder.append("feature=" + feature + ";");
		builder.append("uid=" + Long.toString(uid) + ";");
		builder.append("expire=" + expire + ";");
		builder.append("bonus=(" + bonus.serialize() + ");");
		builder.append(">");
	}
	
	/**
	 * 未镶嵌时反序列化物品
	 * @param tokenizer 数据源
	 * @return Mountable
	 */
	public static Mountable readItem(final StringTokenizer tokenizer) {
		Mountable mountable = new Mountable();
		
		while(true) {
			String token = tokenizer.nextToken(Store.DELIM); //key & >
			if(token.equals(">"))
				break;
			
			tokenizer.nextToken(); //=
			
			if(token.equals("tid")) {
				mountable.tid = Integer.parseInt(tokenizer.nextToken()); //value
			}else if(token.equals("feature")) {
				mountable.feature = Integer.parseInt(tokenizer.nextToken()); //value
			}else if(token.equals("uid")) {
				mountable.uid = Long.parseLong(tokenizer.nextToken()); //value
			}else if(token.equals("expire")) {
				mountable.expire = Integer.parseInt(tokenizer.nextToken()); //value
			}else if(token.equals("bonus")) {
				tokenizer.nextToken(); //)
				mountable.bonus = Bonus.readBonus(tokenizer); //value+)
			}else {
				tokenizer.nextToken(";");
			}
			
			tokenizer.nextToken(); //;
		}
		
		return mountable;
	}
	
	/**
	 * 镶嵌后反序列化物品
	 * @param tokenizer 数据源
	 * @return Mountable
	 */
	public static Mountable readMounted(final StringTokenizer tokenizer) {
		Mountable mountable = new Mountable();
		
		tokenizer.nextToken(); //class
		tokenizer.nextToken(); //=
		tokenizer.nextToken(); //Mountable
		tokenizer.nextToken(); //;
		
		while(true) {
			String token = tokenizer.nextToken(Store.DELIM); //key & >
			if(token.equals(">"))
				break;
			
			tokenizer.nextToken(); //=
			
			if(token.equals("tid")) {
				mountable.tid = Integer.parseInt(tokenizer.nextToken()); //value
			}else if(token.equals("feature")) {
				mountable.feature = Integer.parseInt(tokenizer.nextToken()); //value
			}else if(token.equals("uid")) {
				mountable.uid = Long.parseLong(tokenizer.nextToken()); //value
			}else if(token.equals("expire")) {
				mountable.expire = Integer.parseInt(tokenizer.nextToken()); //value
			}else if(token.equals("bonus")) {
				tokenizer.nextToken(); //)
				mountable.bonus = Bonus.readBonus(tokenizer); //value+)
			}
			
			tokenizer.nextToken(); //;
		}
		
		return mountable;
	}

	@Override
	public String getDescribe() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getAuctionCategory() {
		return (1 << AutionType.Grocery.ordinal()) | (1 << AutionType.GroceryOther.ordinal());
	}

	@Override
	public String getDescribe(RoleBean player) {
		// TODO Auto-generated method stub
		return null;
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
