package com.joyveb.tlol.protocol;

import java.nio.ByteBuffer;
import java.util.HashMap;

import com.joyveb.tlol.util.Log;

public enum MsgID {
	/** 服务器 (验证、登录、注册、快速)相关 */
	MsgID_Manage_Login(1001),

	/** 服务器返回相关 */
	MsgID_Manage_Login_Resp(2001),

	/** 找回密码 */
	MsgID_Get_Pass(1002),

	/** 找回密码返回 */
	MsgID_Get_Pass_Resp(2002),

	/** 刚登陆后显示角色列表 */
	MsgID_Role_List(1003),

	/** 刚登陆后显示角色列表返回 */
	MsgID_Role_List_Resp(2003),

	/** 添加一个新的角色 */
	MsgID_Role_Add(1004),

	/** 添加一个新的角色返回 */
	MsgID_Role_Add_Resp(2004),

	/** 删除已有的角色 */
	MsgID_Role_Del(1005),

	/** 删除已有的角色返回 */
	MsgID_Role_Del_Resp(2005),

	/** 获取某个角色的详细信息 */
	MsgID_Role_Get_Info(1006),

	/** 获取某个角色的详细信息返回 */
	MsgID_Role_Get_Info_Resp(2006),

	/** 角色更新事件 */
	MsgID_NPC_Event_Resp(1008),

	/** 角色更新事件返回 */
	MsgID_NPC_Event(2008),

	/** 偷袭 */
	MsgID_Fight_Sneak_Attack(10501),

	/** 用户请求与某人切磋 */
	MsgID_Hero_Request_Duel(1009),

	/** 用户请求与某人组队，或切磋返回 */
	MsgID_Hero_Request_Duel_Resp(2009),

	/** 玩家被某人邀请什么（切磋、最对） */
	MsgID_Hero_BeRequest_Duel(2010),

	/** 玩家被某人邀请什么返回 */
	MsgID_Hero_BeRequest_Duel_Resp(1010),

	/** 强制用户到某个场景 */
	MsgID_Order_Hero_Map(2011),

	/** 强制用户到某个场景返回 */
	MsgID_Order_Hero_Map_Resp(1011),

	/** 请求和某人或某怪物战斗 */
	MsgID_Fight_With(1014),

	/** 请求返回 */
	MsgID_Fight_With_Resp(2014),

	/** 用户选择攻击类型 */
	MsgID_Fight_Choose(1015),

	/** 用户选择攻击类型返回 */
	MsgID_Fight_Choose_Resp(2015),

	/** 某人已经准备好攻击了 */
	MsgID_Fight_OReady(2016),

	/** 某人已经准备好攻击了返回 */
	MsgID_Fight_OReady_Resp(1016),

	/** 客户端可以开始播放攻击动画了 */
	MsgID_Fight_Start(2017),

	/** 客户端收到了 */
	MsgID_Fight_Start_Resp(1017),

	/** 打斗结算 */
	MsgID_Fight_Closing(2018),

	/** 打斗结算返回 */
	MsgID_Fight_Closing_Resp(1018),

	/** 玩家被强制战斗(偷袭) */
	MsgID_Order_Hero_Fight(2020),

	/** 玩家被强制战斗返回 */
	MsgID_Order_Hero_Fight_Resp(1020),

	/** 玩家属性的加点操作 */
	MsgID_Manage_Skill_Points(1021),

	/** 服务器返回 */
	MsgID_Manage_Skill_Points_Resp(2021),

	/** 客户端请求和某个NPC的对话 */
	MsgID_Talk_To_Npc(1022),

	/** 服务器返回 */
	MsgID_Talk_To_Npc_Resp(2022),

	/**
	 * 计时奖励时间
	 */
	Task_TimeAward(2025),
	
	/**
	 * 计时奖励结束
	 */
	Task_TimeAward_End(2026),
	
	/** 客户端获取任务列表 */
	MsgID_Task_List(1023),

	/** 服务器返回 */
	MsgID_Task_List_Resp(2023),

	/** 客户端放弃任务 */
	MsgID_Task_Del(1024),

	/** 服务器返回数据 */
	MsgID_Task_Del_Resp(2024),
	
	/**
	 * 提交完成任务
	 */
	Task_Finish(10210, "44"),
	
	/**
	 * 完成任务返回
	 */
	Task_Finish_Resp(20210),


	/** 获取某个角色的菜单 */
	MsgID_Role_Menu(1036),

	/** 服务器返回 */
	MsgID_Role_Menu_Resp(2036),

	/** 玩家说话 */
	MsgID_Message_Send(1033),

	/** 说话返回 */
	MsgID_Message_Send_Resp(2033),

	/** 服务器告诉你有消息过来 */
	MsgID_Message_Receive(2034),

	/** 收到,客户端回复 */
	MsgID_Message_Receive_Resp(1034),

	/** 客户端请求名单列表（黑、白） */
	MsgID_NameList_List(1030),

	/** 服务器返回 */
	MsgID_NameList_List_Resp(2030),

	/** 客户端请求添加名单 */
	MsgID_NameList_Add(1031),
	
	/** 服务器返回 */
	MsgID_NameList_Add_Resp(2031),

	/** 客户端请求删除名单 */
	MsgID_NameList_Del(1032),

	/** 服务器返回 */
	MsgID_NameList_Del_Resp(2032),
	
//-------------------------------------------------------------

	/** 客户端请求查看仇人列表 */
	MsgID_NameList_Enemy_Info(1145),

	/** 服务器返回查看仇人列表 */
	MsgID_NameList_Enemy_Info_Resp(2145),
	
	/** 客户端请求仇人列表 */
	MsgID_NameList_EnemyList(1146),

	/** 服务器返回仇人列表 */
	MsgID_NameList_EnemyList_Resp(2146),

	/** 客户端请求添加仇人列表 */
	MsgID_NameList_EnemyAdd(1147),
	
	/** 服务器返回仇人添加仇人列表*/
	MsgID_NameList_EnemyAdd_Resp(2147),

	/** 客户端请求删除仇人列表 */
	MsgID_NameList_EnemyDel(1148),

	/** 服务器返回删除仇人列表 */
	MsgID_NameList_EnemyDel_Resp(2148),
	
//------------------------------------------------------------------	
	

	MsgID_Team_Create(1038),
	
	MsgID_Team_Create_Resp(2038),
	
	MsgID_Team_Delete(1039),
	
	MsgID_Team_Delete_Resp(2039),
	
	MsgID_Team_Request_Enter(1040),
	
	MsgID_Team_Request_Enter_Resp(2040),
	
	MsgID_Team_Request_Quit(1041),
	
	MsgID_Team_Request_Quit_Resp(2041),
	
	MsgID_Team_Give_Invite(1042),
	
	MsgID_Team_Give_Invite_Resp(2042),
	
	MsgID_Team_Ask(2043),

	MsgID_Team_Ask_Resp(1043),

	MsgID_Team_In(2044),
	
	MsgID_Team_In_Resp(1044),

	MsgID_Team_Event(2045),

	MsgID_Team_Event_Resp(1045),

	/** 获取包裹中的物品的详细信息 */
	MsgID_Item_Get_Info(1027),

	/** 服务器返回数据 */
	MsgID_Item_Get_Info_Resp(2027),

	/** 使用、丢弃、穿戴物品 */
	MsgID_Item_Do(1028, "4181"),

	/** 服务器返回数据 */
	MsgID_Item_Do_Resp(2028),

	/** 服务器命令某人离开，携带描述 */
	MsgID_Order_Exit(2400),

	/** 客户端告诉服务器，收到消息了 */
	MsgID_Order_Exit_Resp(1400),

	/** 心跳 */
	MsgID_Hello(1500),

	/** 心跳应答 */
	MsgID_Hello_Resp(2500),

	MsgID_Top_Get_Info(1050),

	MsgID_Top_Get_Detail(1051),

	MsgID_Top_Get_Info_Resp(2050),

	MsgID_Top_Get_Detail_Resp(2051),

	/***********************************************************/

	MsgID_Special_Train(2029),

	MsgID_Special_Train_Resp(1029),

	MsgID_Mail_Send(1060),

	MsgID_Mail_Send_Resp(2060),

	MsgID_Mail_Del(1061),

	MsgID_Mail_Del_Resp(2061),
	
	//删除所有邮件
	MsgID_Mail_DelAll(10610),
	
	MsgID_Mail_DelAll_Resp(20610),

	MsgID_Mail_GetAttach(1062),
	
	MsgID_Mail_GetAttach_Resp(2062),
	
	MsgID_Mail_GetGold(1063),
	
	MsgID_Mail_GetGold_Resp(2063),

	MsgID_Mail_Pay(1064),

	MsgID_Mail_Pay_Resp(2064),

	MsgID_Mail_ReqList(1065),

	MsgID_Mail_ReqList_Resp(2065),

	MsgID_Mail_NewMailNotify(2066),

	MsgID_Mail_ReceiverCheck(1067),

	MsgID_Mail_ReceiverCheck_Resp(2067),

	MsgID_Mail_Return(1068),

	MsgID_Mail_Return_Resp(2068),

	MsgID_Mail_Read(1069),
	
	MsgID_Mail_Attach_View(1070),
	
	MsgID_Mail_Attach_View_Resp(2070),

	MsgID_Shop_GetItem(1080),

	MsgID_Shop_GetItem_Resp(2080),

	MsgID_Charge_ChargeInfo(1090),

	MsgID_Charge_ChargeInfo_Resp(2090),

	MsgID_Unwield_Equip(1100, "41"),

	MsgID_Unwield_Equip_Resp(2100),

	MsgID_Inuse_View(1101, "41"),

	MsgID_Inuse_View_Resp(2101),

	MsgID_Nick_Check(1205),

	MsgID_Nick_Check_Resp(2205),

	MsgID_Role_Quick_Enter(10101),

	MsgID_Role_Quick_Enter_Resp(20101),

	/** map模块 */
	MsgID_Enter_Map(10301),

	MsgID_Enter_Map_Resp(20301),

	MsgID_Enter_Map_Start(10302),
	
	MsgID_Manage_Move(10303),

	MsgID_Manage_Move_Resp(20303),

	MsgID_Find_Point_Address(10304),

	MsgID_Find_Point_Address_Resp(20304),

	MsgID_Role_Other_Init(20305),

	MsgID_Role_Other_Init_Resp(10305),

	MsgID_Role_Arround(10306),

	MsgID_Role_Arround_Resp(20306),

	MsgID_Get_MapInfo(10307),

	MsgID_Get_MapInfo_Resp(20307),
	
	MsgID_Get_TransToMap(10308),
	MsgID_Get_TransToMap_Resp(20308),
	
	MsgID_Get_TransMaps(10309),
	MsgID_Get_TransMaps_Resp(20309),

	/** item模块 */
	MsgID_Get_Pack_Data(10801, "41"),

	MsgID_Get_Pack_Data_Resp(20801),
	
	MsgID_Pack_Put(10802, "4182"),

	MsgID_Pack_Put_Resp(20802),

	MsgID_Pack_Get(10803, "4182"),

	MsgID_Pack_Get_Resp(20803),
	
	MsgID_Pack_Item_Info(10804, "418"),

	MsgID_Pack_Item_Info_Resp(20804),

	MsgID_Fashion_Info_Resp(20810),

	MsgID_Gang_Creat(11801, "4S"),

	MsgID_Gang_Creat_Resp(21801),

	MsgID_Gang_Quit(11802),

	MsgID_Gang_Quit_Resp(21802),
	/** 玩家发送邀请 */
	MsgID_Gang_Invite(11803, "4S"),
	/** 邀请消息响应 */
	MsgID_Gang_Invite_Resp(21803),
	/** 转发邀请 */
	Gang_Invite_Transmit(21804),
	/** 转发邀请响应 */
	Gang_Invite_Transmit_Resp(11804),
	/** 对方同意或拒绝邀请 */
	Gang_Invite_Echo(11805),
	/** 同意或拒绝邀请时服务器响应 */
	Gang_Invite_Echo_Resp(21805),

	Gang_View(11806), Gang_View_Resp(21806),

	Gang_View_Page(11807, "44"),

	Gang_View_Page_Resp(21807),

	Gang_Expel(11808, "44"),

	Gang_Expel_Resp(21808),

	Gang_Change_Bulletin(11809, "4S"),

	Gang_Change_Bulletin_Resp(21809),
	
	Action_Request(11901),
	
	Action_Request_Resp(21901),

	/**
	 * 资源下载协议号
	 * */
	MsgID_Request_Res(10901), 
	
	MsgID_Request_Res_Resp(20901), 
	
	MsgID_Role_Logout(10204),
	
	MsgID_Role_ChangeName(10201),
	
	MsgID_Role_ChangeName_Resp(20201),
	
	/**
	 * 装备升星初始化
	 */
	Item_Refine_Init(10811),
	/**
	 * 装备升星初始化响应
	 */
	Item_Refine_Init_Resp(20811),
	/**
	 * 装备升星确认
	 */
	Item_Refine_Confirm(10812),
	/**
	 * 装备升星确认响应
	 */
	Item_Refine_Confirm_Resp(20812),
	/**
	 * 装备升星
	 */
	Item_Refine(10813),
	/**
	 * 装备升星响应
	 */
	Item_Refine_Resp(20813),
	
	/**
	 * 合成确认
	 */
	Item_Compose_Confirm(10814, "48"),
	
	/**
	 * 合成确认响应
	 */
	Item_Compose_Confirm_Resp(20814),
	
	/**
	 * 物品合成
	 */
	Item_Compose(10815, "48"),
	
	/**
	 * 物品合成响应
	 */
	Item_Compose_Resp(20815),
	
	/**
	 * 分解确认
	 */
	Item_Decompose_Confirm(10816, "48"),
	
	/**
	 * 分解确认响应
	 */
	Item_Decompose_Confirm_Resp(20816),
	
	/**
	 * 物品分解
	 */
	Item_Decompose(10817, "48"),
	
	/**
	 * 物品分解响应
	 */
	Item_Decompose_Resp(20817),
	
	/**
	 * 服务器通知客户端进入拍卖行
	 */
	Auction_Enter(22000),

	/**
	 * 客户端请求物品列表
	 */
	Auction_Req_List(12001, "41"),
	
	/**
	 * 服务器返回物品列表
	 */
	Auction_Req_List_Resp(22001),
	
	/**
	 * 客户端请求物品列表下一页
	 */
	Auction_Req_NextPage(12002, "4121"),
	
	/**
	 * 服务器返回物品列表下一页
	 */
	Auction_Req_NextPage_Resp(22002),
	
	/**
	 * 竞标物品 确认
	 */
	Auction_Bidding_Confirm(12003, "48"),
	
	/**
	 * 竞标物品确认返回
	 */
	Auction_Bidding_Confirm_Resp(22003),
	
	/**
	 * 竞标物品
	 */
	Auction_Bidding(12004, "484"),

	/**
	 * 竞标物品返回
	 */
	Auction_Bidding_Resp(22004),
	
	/**
	 * 拍卖物品确认
	 */
	Auction_Sale_Confirm(12005, "481441"),
	
	/**
	 * 拍卖物品确认返回
	 */
	Auction_Sale_Confirm_Resp(22005),
	
	/**
	 * 拍卖物品
	 */
	Auction_Item_Sale(12006, "481441"),
	
	/**
	 * 拍卖物品返回
	 */
	Auction_Item_Sale_Resp(22006),

	/** 查看信息 */
	MsgID_Role_View(10202),

	/** 查看信息返回 */
	MsgID_Role_View_Resp(20202),
	
	/** 查看信息 */
	MsgID_Notice_Resp(20107),
	/**查看结婚*/
	MsgID_MarryList_List_view(10521),
	/**查看结婚返回*/
	MsgID_MarryList_List_Resp(20521)
	,
	/**查看充值*/
	MsgID_Fee_Info(12301),
	/**查看充值返回*/
	MsgID_Fee_Info_Resp(22301)
	;
	
	
	private final short msgid;
	private final DefaultMsgBody msgBody;

	private MsgID(final int msgid) {
		this.msgid = (short) msgid;
		msgBody = null;
	}

	private MsgID(final int msgid, final String protocol) {
		this.msgid = (short) msgid;
		this.msgBody = new DefaultMsgBody(protocol);
	}

	private static HashMap<Short, MsgID> mapping = new HashMap<Short, MsgID>();

	public static MsgID getInstance(final short msgid) {
		if(mapping.isEmpty())
			for (MsgID instance : MsgID.values())
				mapping.put(instance.msgid, instance);
		
		return mapping.get(msgid);
	}

	public short getMsgid() {
		return msgid;
	}

	public DefaultMsgBody getMsgBody() {
		return msgBody;
	}

	public boolean readBody(final ByteBuffer body) {
		if (msgBody == null) {
			Log.error(Log.ERROR, "没有为该协议定制相应的处理对象！");
			return false;
		}

		return msgBody.readBody(body);
	}
	

}
