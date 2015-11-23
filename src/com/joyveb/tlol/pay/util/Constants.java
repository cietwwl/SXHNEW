package com.joyveb.tlol.pay.util;

import java.util.HashMap;
import java.util.Map;

public class Constants {
	public static Integer BUSINESS_ID = 1;//�̻���ʶ
	public static String BUSINESS_PWD = "f92f0a02b30ed4acc532026ee3651c";//�̻�����
	public static String GAMEPAY_PWD = "ba6d91dd3324da5d7a966402487b1b01";//��Ϸ��ֵ����   www.joyveb.com
	public static String PAY_URL = "http://pay.sxmobi.com/pmc/charge.aspx?";//�����ַ
//	public static String GAMEPAY_URL = "http://127.0.0.1:8080/GamePayServer/services/PayManager";//���ζ�websercice�����ַ �������
	public static String GAMEPAY_URL = "http://192.168.0.202/gamepayserver/services/PayManager";//���ζ�websercice�����ַ 202
	public static String HTTPGAMEPAY_URL = "http://218.206.85.106/gamepayserver/gamepay.do?method=pay";//���ζ�websercice�����ַ 202
	public static String HTTPGAMESUB_URL = "http://10.0.0.106/gamepayserver/gamepay.do?method=subtract";//���ζ�websercice�����ַ 202
	public static String MYGAMESUB_URL = "http://127.0.0.1:8080/GamePayServer/gamepay.do?method=subtract";//���ζ�websercice�����ַ 202
	public static final String[] DATAMAIL={"sunhonglei@iyunu.com"};
	public static String MYHTTPGAMESUB_URL = "http://124.127.125.117/gamepayserver/gamepay.do?method=subtract";//���ζ�websercice�����
	
	public static Map<String,String> ERRORCODE=new HashMap<String,String>();
	public static void dataEmail(String title,String content,String[] usersMail){
		try{
			//String[] usersMail = {"daiyu@joyveb.com","niefengge@joyveb.com"};
			//String[] usersMail = {"daiyu@joyveb.com","cuile@joyveb.com","niefengge@joyveb.com"};
			//String[] usersMail = {"niefengge@joyveb.com"};
             //����mail
             MailInfo mInfo = new MailInfo();
             mInfo.setSubject(title);
             mInfo.setFrom("gameall@iyunu.com");
             mInfo.setMutliTo(usersMail);
             String s_notify_context = content;
             mInfo.setContent(new String(s_notify_context.getBytes("gb2312"), "iso-8859-1"));
             // ����
             //mInfo.setAttachment(getFileOutPath() + der.getFileName() + der.getFileSuffix());
             mInfo.setSmtpServer("mail.iyunu.com");
             mInfo.setUserName("sunhonglei@iyunu.com");
             mInfo.setPassword("jaiye8879699");
             MailHandle.sendMutliUser(mInfo);
		}catch(Exception ex){
			System.out.println("�����ʼ�ʧ��! �ʼ�����"+content);
			ex.printStackTrace();
		}
	}
	
	public static void initErrorCode(){
		String errorcontent1="充值失败。您的话费金额不足，建议您购买充值卡进行充值，谢谢。/如遇有问题，请您联系我们的客服人员。/客服电话：400-650-8380";
		ERRORCODE.put("0102", errorcontent1);
		ERRORCODE.put("0103", errorcontent1);
		ERRORCODE.put("0202", errorcontent1);
		String errorcontent2="充值失败。未扣除任何费用，请您重试或购买充值卡进行充值，谢谢。/如遇有问题，请您联系我们的客服人员。/客服电话：400-650-8380";
		ERRORCODE.put("0203", errorcontent2);
		ERRORCODE.put("01500", errorcontent2);
		String errorcontent3="充值失败。您充值金额已达上限，建议您购买充值卡进行充值，谢谢。/如遇有问题，请您联系我们的客服人员。/客服电话：400-650-8380";
		ERRORCODE.put("0204", errorcontent3);
		ERRORCODE.put("0205", errorcontent3);
		ERRORCODE.put("0206", errorcontent3);
		ERRORCODE.put("0207", errorcontent3);
		ERRORCODE.put("0208", errorcontent3);
		String errorcontent4="充值失败。未扣除任何费用，请您重试或购买充值卡进行充值，谢谢。/如遇有问题，请您联系我们的客服人员。/客服电话：400-650-8380";
		ERRORCODE.put("09015", errorcontent4);
		ERRORCODE.put("09017", errorcontent4);
		String errorcontent5="充值失败。您的游戏客户端可能已损坏，建议您重现下载安装，谢谢。/如遇有问题，请您联系我们的客服人员。/客服电话：400-650-8380";
		ERRORCODE.put("09598", errorcontent5);
		String errorcontent6="充值失败。网络超时，请您重试或购买充值卡进行充值，谢谢。/如遇有问题，请您联系我们的客服人员。/客服电话：400-650-8380";
		ERRORCODE.put("09599", errorcontent6);
		ERRORCODE.put("09601", errorcontent6);
		ERRORCODE.put("0-1", errorcontent2);
		ERRORCODE.put("011", errorcontent2);
		ERRORCODE.put("012", errorcontent2);
		ERRORCODE.put("013", errorcontent2);
		ERRORCODE.put("014", errorcontent2);
		ERRORCODE.put("015", errorcontent2);
		ERRORCODE.put("016", errorcontent2);
		ERRORCODE.put("017", errorcontent2);
		ERRORCODE.put("02",  errorcontent2);
		ERRORCODE.put("0-2", errorcontent2);
		ERRORCODE.put("0-0", errorcontent2);
		ERRORCODE.put("0-9", errorcontent2);
		ERRORCODE.put("014", errorcontent2);
		ERRORCODE.put("0142",errorcontent2);
		ERRORCODE.put("0143",errorcontent2);
		@SuppressWarnings("unused")
		String errorcontent7="充值超时,你的上次支付失败，可以尝试更换支付通道,谢谢。/如遇有问题，请您联系我们的客服人员。/客服电话：400-650-8380";
		ERRORCODE.put("timeout", "errorcontent7");
	}

}
