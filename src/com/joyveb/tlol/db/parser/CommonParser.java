package com.joyveb.tlol.db.parser;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.joyveb.tlol.db.DataStruct;
import com.joyveb.tlol.db.DbParser;
import com.joyveb.tlol.db.PreSql;
import com.joyveb.tlol.util.Log;

public final class CommonParser extends DbParser {

	private static DbParser instance = null;

	public static final DbParser getInstance() {
		if (instance == null) {
			instance = new CommonParser();
		}
		return instance;
	}
	/**
	 * 本类构造方法
	 */
	private CommonParser() {

	}

	@Override
	public PreSql struct2db(final DbConst eventID, final DataStruct data) {
		if (data == null)
			return null;

		if (data.type == DataStruct.CommandType.UNKNOW) {
			switch (eventID) {
			case GET_ROLE_LIST:
				return data.getPreSql_query();
			case ROLE_ADD:
				return data.getPreSql_insert();
			case ROLE_DEL:
				return data.getPreSql_delete();
			case ROLE_NAME_EXIST:
				return data.getPreSql_query();
			case REFRESH_BILLBOARD:
				return data.getPreSql_query();
			case ROLE_LOG:
				return data.getPreSql_insert();
			case ROLE_REPORT:
				return data.getPreSql_insert();
			case ROLE_UPDATE:
				return data.getPreSql_update();
			case MAIL_RECEIVER_CHECK:
				return data.getPreSql_query();
			case MAIL_SEND:
				return data.getPreSql_insert();
			case MAIL_DEL:
				return data.getPreSql_delete();
			case MAIL_DEL_ALL://删除所有邮件
				return data.getPreSql_delete();
			case MAIL_LIST:
				return data.getPreSql_query();
			case SEND_MAILL_LIST:
				return data.getPreSql_query();
			case MAIL_UPDATE:
				return data.getPreSql_update();
			case ROLE_UPDATE_MONEY:
				return data.getPreSql_update();
			case ROLE_MALL_RECORD:
				return data.getPreSql_insert();
			case ROLE_NAME_EXIST2:
				return data.getPreSql_query();
			case GET_Bulletin:
				return data.getPreSql_query();
			case GET_LoginBulletin:
				return data.getPreSql_query();
			case ROLE_QUICK_ENTER_INFO:
				return data.getPreSql_query();
			case ROLE_QUICK_ADD:
				return data.getPreSql_insert();
			case NORMAL_REG_GET_BY_NAME:
			case ROLE_GET_BY_NAME:
				return data.getPreSql_query();
			case USER_CHECK_ROLE_INFO:
				return data.getPreSql_query();
			case INIT_UID:
				return data.getPreSql_query();
			case UPDATE_UID:
				return data.getPreSql_update();
			case USER_GET_INFO:
				return data.getPreSql_query();
			case GET_ROLEID_BY_NICK:
				return data.getPreSql_query();
			case Top_TotalKillNum:
				return data.getPreSql_query();
			case Top_Honor:
				return data.getPreSql_query();
			case Top_Levels:
				return data.getPreSql_query();
			case Top_Golds:
				return data.getPreSql_query();
			case Top_Charms:
				return data.getPreSql_query();
			case Top_Marks:
				return data.getPreSql_query();
			case Top_TributeGangs:
				return data.getPreSql_query();
			case Community_Get:
				return data.getPreSql_query();
			case Community_Create:
				return data.getPreSql_insert();
			case Community_Update:
				return data.getPreSql_update();
			case KONGZHONG_CHARGE_CHANNEL:
				return data.getPreSql_query();
			case ShenZhouChargeInfo:
				return data.getPreSql_query();
			case KONGZHONG_CHARGE_CONFIG:
				return data.getPreSql_query();
			case RETRUN_MAIL_ON_ROLE_DEL:
				return data.getPreSql_update();
			case MAIL_DEL_ON_ROLE_DEL:
				return data.getPreSql_delete();
			case Gang_Get:
				return data.getPreSql_query();
			case Gang_Creat:
				return data.getPreSql_insert();
			case Gang_Update:
				return data.getPreSql_update();
			case BatchGetRoleCard:
				return data.getPreSql_query();
			case Gang_Name_Check:
				return data.getPreSql_query();
			case Gang_Delete:
				return data.getPreSql_delete();
			case Role_ChangeName_EXIST:
				return data.getPreSql_query();
			case Auction_Get_All:
				return data.getPreSql_query();
			case Auction_Creat:
				return data.getPreSql_insert();
			case Auction_Update:
				return data.getPreSql_update();
			case Fatwa_Insert:
				return data.getPreSql_insert();
			case Fatwa_Delete:
				return data.getPreSql_delete();
			case Fatwa_Query:
				return data.getPreSql_query();
			case Auction_Delete:
				return data.getPreSql_delete();
			case GET_MailNotice://发送邮件公告
				return data.getPreSql_query();
			case Role_Break_Up://强制离婚
				return data.getPreSql_update();
			case Role_Remove_Master://强制解除师傅
				return data.getPreSql_update();
			case Role_Remove_App://强制解除徒弟
				return data.getPreSql_update();	
			case Get_Fee:
				return data.getPreSql_query();
			default:
				Log.error(Log.STDOUT, "struct2db", "unhandled msgid! : "
						+ eventID);
				break;
			}
		}

		return null;
	}

	@Override
	public void db2struct(final DbConst eventID, final ResultSet rs, final DataStruct ds) {
		/**
		 * 插入删除修改语句没有rs因此当没有记录的时候不需要进行readFromRs 代码可以写成if(rs != null){
		 * readFromRs } 考虑到灵活性这里并没有这么做 如 有记录时但是并不想去读取
		 */
		try {
			switch (eventID) {
			case GET_ROLE_LIST:
				ds.readFromRs(rs);
				break;
			case ROLE_ADD:
				break;
			case ROLE_DEL:
				break;
			case ROLE_NAME_EXIST:
				ds.readFromRs(rs);
				break;
			case REFRESH_BILLBOARD:
				ds.readFromRs(rs);
				break;
			case ROLE_LOG:
				break;
			case ROLE_REPORT:
				break;
			case ROLE_UPDATE:
				break;
			case MAIL_RECEIVER_CHECK:
				ds.readFromRs(rs);
				break;
			case MAIL_LIST:
				ds.readFromRs(rs);
				break;
			case MAIL_DEL_ALL:
				break;
			case SEND_MAILL_LIST:
				ds.readFromRs(rs);
				break;
			case ROLE_NAME_EXIST2:
				ds.readFromRs(rs);
				break;
			case ROLE_QUICK_ENTER_INFO:
				ds.readFromRs(rs);
				break;
			case NORMAL_REG_GET_BY_NAME:
			case ROLE_GET_BY_NAME:
				// !!!特殊处理 改代码时要注意
				if (rs.next())
					ds.readFromRs(rs);
				break;
			
			case USER_CHECK_ROLE_INFO:
				ds.readFromRs(rs);
				break;
			case INIT_UID:
				ds.readFromRs(rs);
				break;
			case USER_GET_INFO:
				ds.readFromRs(rs);
				break;
			case GET_Bulletin:
				ds.readFromRs(rs);
				break;
			case GET_LoginBulletin:
				ds.readFromRs(rs);
				break;
			case GET_ROLEID_BY_NICK:
				ds.readFromRs(rs);
				break;
			case Top_TotalKillNum:
				ds.readFromRs(rs);
				break;
			case Top_Honor:
				ds.readFromRs(rs);
				break;
			case Top_Levels:
				ds.readFromRs(rs);
				break;
			case Top_Golds:
				ds.readFromRs(rs);
				break;
			case Top_Charms:
				ds.readFromRs(rs);
				break;
			case Top_Marks:
				ds.readFromRs(rs);
				break;
			case Top_TributeGangs:
				ds.readFromRs(rs);
				break;
			case Community_Get:
				ds.readFromRs(rs);
				break;
			case Community_Create:
				break;
			case Community_Update:
				break;
			case KONGZHONG_CHARGE_CHANNEL:
				ds.readFromRs(rs);
				break;
			case ShenZhouChargeInfo:
				ds.readFromRs(rs);
				break;
			case KONGZHONG_CHARGE_CONFIG:
				ds.readFromRs(rs);
				break;
			case Gang_Get:
				ds.readFromRs(rs);
				break;
			case BatchGetRoleCard:
				ds.readFromRs(rs);
				break;
			case Gang_Name_Check:
				ds.readFromRs(rs);
				break;
			case Role_ChangeName_EXIST:
				ds.readFromRs(rs);
				break;
			case Auction_Get_All:
				ds.readFromRs(rs);
				break;
			case Auction_Creat:
				break;
			case Auction_Update:
				break;
			case Fatwa_Insert:
				break;
			case Fatwa_Delete:
				break;
			case Fatwa_Query:
				ds.readFromRs(rs);
				break;
			case Auction_Delete:
				break;
			case UPDATE_UID:
			case Gang_Creat:
			case Gang_Delete:
			case ROLE_QUICK_ADD:
				break;
			case GET_MailNotice://发送邮件公告
				ds.readFromRs(rs);
				break;
			case Role_Break_Up://强制离婚
				break;
			case Role_Remove_Master://强制解除师傅
				break;
			case Role_Remove_App://强制解除徒弟
				break;
			case Get_Fee:
				ds.readFromRs(rs);
				break;
			default:
				Log.error(Log.STDOUT, "db2struct", "unhandled msgid! : " + eventID);
				break;
			}

		} catch (SQLException e) {
			Log.error(Log.STDOUT, "db2struct", e);
		}
	}

}
