package com.joyveb.tlol.db.parser;

import java.util.List;

import com.joyveb.tlol.TianLongServer;
import com.joyveb.tlol.db.DataStruct;
import com.joyveb.tlol.db.PreSql;
import com.joyveb.tlol.mail.Mail;
import com.joyveb.tlol.util.Log;

public class MailDelAllData extends DataStruct {

	private List<Mail> deletedMailList ;
	

	public MailDelAllData(final List<Mail> deletedMailList) {
		this.deletedMailList = deletedMailList;
	}


	/**
	 * @return 返回delete语句
	 */
	@Override
	public final PreSql getPreSql_delete() {
		
		
		PreSql preSql = new PreSql();
		
		StringBuffer strBuffer = new StringBuffer();
		
		strBuffer.append("delete from Tbl_Tianlong_Mail" + "_" + TianLongServer.srvId + " t where ");
		
		for(int i=0;i<deletedMailList.size();i++){
			Mail mail = deletedMailList.get(i);
			
			strBuffer.append("t.id = ?");
			preSql.parameter.add(mail.getMailId());
			//strBuffer.append(mail.getMailId());
			
			if(i != deletedMailList.size() -1){
				strBuffer.append(" or ");
			}
			
		}
		
		Log.info(Log.OPERATOR, "dell all mail", strBuffer.toString());
		
		preSql.sqlstr = strBuffer.toString();
		
		
		return preSql;
	}


}
