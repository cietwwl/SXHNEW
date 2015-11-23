package com.joyveb.tlol.pay.util;



import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.omg.CORBA.SystemException;

/**
 * ϵͳ�ʼ�����
 * <p>
 * Title: ϵͳ�ʼ�����
 * </p>
 * <p>
 * Copyright: Copyright (c) 2005
 * </p>
 * 
 * @version 1.0
 */

public class MailHandle {

	/*
    ���������ʼ����ͳ�ȥ,�ʼ�����Ϊ�ı�����,�����и���
  */
  public static void send(MailInfo mail) throws Exception
  {
    try
    {
      Properties props = new Properties();

      String smtpServer = mail.getSmtpServer();
      String SmtpUser = mail.getUserName();
      String SmtpPwd = mail.getPassword();
      String subject = "";
      if(mail.getSubject()!=null) subject = mail.getSubject();
      if(smtpServer==null)
      {
//        smtpServer = Config.getInstance().getProperties("defaultSmtpServer");
//        SmtpUser = Config.getInstance().getProperties("defaultSmtpUser");
//        SmtpPwd = Config.getInstance().getProperties("defaultSmtpPwd");
//        smtpServer = "mail.luckto.com";
//        SmtpUser = "baozhijie";
//		SmtpPwd = "123";
    	  smtpServer = "smtp.sina.com.cn";
    	  SmtpUser = "lzcc2003";
    	  SmtpPwd = "2166165";
      }

      props.put("mail.smtp.host", smtpServer);               //�����ʼ�smtp�������ַ
      props.put("mail.smtp.auth","true");                    //���÷�����smtp��Ҫ��֤
      Session sendMailSession = Session.getInstance(props, null);
      MimeMessage newMessage = new MimeMessage(sendMailSession);
      newMessage.setFrom(new InternetAddress(mail.getFrom()));
      InternetAddress[] address = {new InternetAddress(mail.getTo())};
      newMessage.setRecipients(Message.RecipientType.TO, address);

      if( mail.getCc()!=null )      //�ж��Ƿ�����ʼ�����
      {
        InternetAddress[] addresscc = {new InternetAddress(mail.getCc())};
        newMessage.setRecipients(Message.RecipientType.CC, addresscc);
      }

      if( mail.getBcc()!=null )     //�ж��Ƿ�����ʼ�����
      {
        InternetAddress[] addressbcc = {new InternetAddress(mail.getBcc())};
        newMessage.setRecipients(Message.RecipientType.CC, addressbcc);
      }

      newMessage.setSubject(subject);               //�趨����
      newMessage.setSentDate(new Date());           //�趨����ʱ��

      //���ʼ����������зָ�����д���
      if(mail.getAttachment()==null)
      {
        newMessage.setDataHandler(new DataHandler(mail.getContent(),"text/plain"));
      }
      else
      {
          MimeBodyPart messageBodyPart = new MimeBodyPart();
          messageBodyPart.setDataHandler(new DataHandler(mail.getContent(),"text/plain"));
          Multipart multipart = new MimeMultipart();
          multipart.addBodyPart(messageBodyPart);
          messageBodyPart = new MimeBodyPart();
          DataSource source = new FileDataSource(mail.getAttachment());
          messageBodyPart.setDataHandler(new DataHandler(source));
          File fn = new File(mail.getAttachment());
          String fileName = fn.getName();
          if(fileName!=null&&!"".equals(fileName)){
        	  fileName = new String(fileName.getBytes("gb2312"),"iso-8859-1");
  		  }
          messageBodyPart.setFileName(fileName);
          multipart.addBodyPart(messageBodyPart);
          newMessage.setContent(multipart);
      }

      //��ʼl��smtp�����������Ƿ�ͨ��smtp������֤��
      Transport transport = sendMailSession.getTransport("smtp");
      transport.connect(smtpServer,SmtpUser,SmtpPwd);
      transport.sendMessage(newMessage,newMessage.getAllRecipients());
      transport.close();
    }
    catch(MessagingException e)
    {
    	System.out.println("�ʼ�����ʧ��");
		e.printStackTrace();
    }
  }

  /**
   * Ⱥ���ʼ�.���
   * @param mail
   * @param to
   * @throws Exception
   */
  public static void sendMutliUser(MailInfo mail) throws Exception
  {
    try
    {
      Properties props = new Properties();

      String smtpServer = mail.getSmtpServer();
      String SmtpUser = mail.getUserName();
      String SmtpPwd = mail.getPassword();
      String subject = "";
      if(mail.getSubject()!=null) subject = mail.getSubject();
      if(smtpServer==null)
      {
//        smtpServer = Config.getInstance().getProperties("defaultSmtpServer");
//        SmtpUser = Config.getInstance().getProperties("defaultSmtpUser");
//        SmtpPwd = Config.getInstance().getProperties("defaultSmtpPwd");
 //       smtpServer = "mail.luckto.com";
 //       SmtpUser = "baozhijie";
//		SmtpPwd = "123";
    	  smtpServer = "mail.iyunu.com";
    	  SmtpUser = "sunhonglei";
    	  SmtpPwd = "jaiye8879699";
      }

      props.put("mail.smtp.host", smtpServer);               //�����ʼ�smtp�������ַ
      props.put("mail.smtp.auth","true");                    //���÷�����smtp��Ҫ��֤
      Session sendMailSession = Session.getInstance(props, null);
      MimeMessage newMessage = new MimeMessage(sendMailSession);
      newMessage.setFrom(new InternetAddress(mail.getFrom()));
//      InternetAddress[] address = {new InternetAddress(mail.getTo())};
      
//    ��ʼ����Ⱥ���ʼ���ַ
	  InternetAddress[] address = new InternetAddress[mail.getMutliTo().length];
	  
	  for (int i = 0; i < mail.getMutliTo().length; i++) {
			address[i] = new InternetAddress((mail.getMutliTo())[i]);
		}

	  newMessage.setRecipients(Message.RecipientType.TO, address);


      if( mail.getCc()!=null )      //�ж��Ƿ�����ʼ�����
      {
        InternetAddress[] addresscc = {new InternetAddress(mail.getCc())};
        newMessage.setRecipients(Message.RecipientType.CC, addresscc);
      }

      if( mail.getBcc()!=null )     //�ж��Ƿ�����ʼ�����
      {
        InternetAddress[] addressbcc = {new InternetAddress(mail.getBcc())};
        newMessage.setRecipients(Message.RecipientType.CC, addressbcc);
      }

      newMessage.setSubject(subject);               //�趨����
      newMessage.setSentDate(new Date());           //�趨����ʱ��

      //���ʼ����������зָ�����д���
      if(mail.getAttachment()==null)
      {
        newMessage.setDataHandler(new DataHandler(mail.getContent(),"text/plain"));
      }
      else
      {
          MimeBodyPart messageBodyPart = new MimeBodyPart();
          messageBodyPart.setDataHandler(new DataHandler(mail.getContent(),"text/plain"));
          Multipart multipart = new MimeMultipart();
          multipart.addBodyPart(messageBodyPart);
          messageBodyPart = new MimeBodyPart();
          DataSource source = new FileDataSource(mail.getAttachment());
          messageBodyPart.setDataHandler(new DataHandler(source));
          File fn = new File(mail.getAttachment());
          String fileName = fn.getName();
          if(fileName!=null&&!"".equals(fileName)){
        	  fileName = new String(fileName.getBytes("gb2312"),"iso-8859-1");
  		  }
          messageBodyPart.setFileName(fileName);
          multipart.addBodyPart(messageBodyPart);
          newMessage.setContent(multipart);
      }

      //��ʼl��smtp�����������Ƿ�ͨ��smtp������֤��
      Transport transport = sendMailSession.getTransport("smtp");
      transport.connect(smtpServer,SmtpUser,SmtpPwd);
      transport.sendMessage(newMessage,newMessage.getAllRecipients());
      transport.close();
    }
    catch(MessagingException e)
    {
    	System.out.println("�ʼ�����ʧ��");
		e.printStackTrace();
    }
  }
  
  
  /**
   * ��������html��ʽ���ʼ����ݷ��ͳ�ȥ
   */
  public static void sendHtml(MailInfo mail) throws SystemException
  {
    try
    {
      Properties props = new Properties();

      String smtpServer = mail.getSmtpServer();
      String SmtpUser = mail.getUserName();
      String SmtpPwd = mail.getPassword();
      String subject = "";
      if(mail.getSubject()!=null) subject = mail.getSubject();
      if(smtpServer==null)
      {
    	  //smtpServer = "mail.luckto.com";
         // SmtpUser = "baozhijie";
  		 // SmtpPwd = "123";
    	  smtpServer = "mail.iyunu.com";
    	  SmtpUser = "sunhonglei";
    	  SmtpPwd = "jaiye8879699";
      }

      props.put("mail.smtp.host", smtpServer);               //�����ʼ�smtp�������ַ
      props.put("mail.smtp.auth","true");                    //���÷�����smtp��Ҫ��֤
      Session sendMailSession = Session.getInstance(props, null);
      MimeMessage newMessage = new MimeMessage(sendMailSession);
      newMessage.setFrom(new InternetAddress(mail.getFrom()));
      InternetAddress[] address = {new InternetAddress(mail.getTo())};
      newMessage.setRecipients(Message.RecipientType.TO, address);

      if( mail.getCc()!=null )      //�ж��Ƿ�����ʼ�����
      {
        InternetAddress[] addresscc = {new InternetAddress(mail.getCc())};
        newMessage.setRecipients(Message.RecipientType.CC, addresscc);
      }

      if( mail.getBcc()!=null )     //�ж��Ƿ�����ʼ�����
      {
        InternetAddress[] addressbcc = {new InternetAddress(mail.getBcc())};
        newMessage.setRecipients(Message.RecipientType.CC, addressbcc);
      }

      newMessage.setSubject(subject);               //�趨����
      newMessage.setSentDate(new Date());           //�趨����ʱ��

      //���ʼ����������зָ�����д���
      if(mail.getAttachment()==null)
      {
        newMessage.setDataHandler(new DataHandler(mail.getContent(),"text/html"));
      }
      else
      {
          MimeBodyPart messageBodyPart = new MimeBodyPart();
          messageBodyPart.setDataHandler(new DataHandler(mail.getContent(),"text/html"));
          Multipart multipart = new MimeMultipart();
          multipart.addBodyPart(messageBodyPart);
          messageBodyPart = new MimeBodyPart();
          DataSource source = new FileDataSource(mail.getAttachment());
          messageBodyPart.setDataHandler(new DataHandler(source));
          File fn = new File(mail.getAttachment());
          messageBodyPart.setFileName(fn.getName());
          multipart.addBodyPart(messageBodyPart);
          newMessage.setContent(multipart);
      }

      //��ʼl��smtp�����������Ƿ�ͨ��smtp������֤��
      Transport transport = sendMailSession.getTransport("smtp");
      transport.connect(smtpServer,SmtpUser,SmtpPwd);
      transport.sendMessage(newMessage,newMessage.getAllRecipients());
      transport.close();
    }
    catch(MessagingException e)
    {
    	System.out.println("�ʼ�����ʧ��");
		e.printStackTrace();      
    }
  }


	/**
	 * ��������html��ʽ���ʼ����ݷ��͸�һ���û�
	 * 
	 * @param to
	 *            String
	 * @param from
	 *            String
	 * @param subject
	 *            String
	 * @param content
	 *            String
	 * @throws SystemException
	 */
	public static void send(String to, String from, String subject,
			String content) throws SystemException {
		try {
			Properties props = new Properties();

			//String smtpServer = "mail.luckto.com";
			//String SmtpUser = "baozhijie";
			//String SmtpPwd = "123";
			String  smtpServer = "mail.iyunu.com";
			String  SmtpUser = "sunhonglei";
			String  SmtpPwd = "jaiye8879699";
	    	  
			if (from == null || from.equals("")) {
				from = "baozhijie@luckto.com";
			}

			props.put("mail.iyunu.com", smtpServer); // �����ʼ�smtp�������ַ
			props.put("mail.iyunu.com", "true"); // ���÷�����smtp��Ҫ��֤
			Session sendMailSession = Session.getInstance(props, null);
			MimeMessage newMessage = new MimeMessage(sendMailSession);
			newMessage.setFrom(new InternetAddress(from));
			InternetAddress[] address = { new InternetAddress(to) };

			newMessage.setRecipients(Message.RecipientType.TO, address);

			newMessage.setSubject(subject); // �趨����
			newMessage.setSentDate(new Date()); // �趨����ʱ��

			// ���ʼ����������н��б��봦�?֧������
			content = new String(content.getBytes("GB2312"), "ISO8859-1");
			newMessage.setDataHandler(new DataHandler(content, "text/html"));

			// ��ʼl��smtp�����������Ƿ�ͨ��smtp������֤��
			Transport transport = sendMailSession.getTransport("smtp");
			transport.connect(smtpServer, SmtpUser, SmtpPwd);
			transport.sendMessage(newMessage, newMessage.getAllRecipients());
			transport.close();
		} catch (MessagingException ex) {
			System.out.println("�ʼ�����ʧ��");
			ex.printStackTrace();
		} catch (UnsupportedEncodingException ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * ��������html��ʽ���ʼ����ݷ��͸����û�
	 * 
	 * @param to
	 *            ArrayList
	 * @param from
	 *            String
	 * @param subject
	 *            String
	 * @param content
	 *            String
	 * @throws SystemException
	 */
	@SuppressWarnings("rawtypes")
	public static void send(ArrayList to, String from, String subject,
			String content) throws SystemException {
		try {
			Properties props = new Properties();

			//String smtpServer = "192.168.0.8";
			//String SmtpUser = "webmaster";
			//String SmtpPwd = "tlx2s9rk7dfk76";
			String  smtpServer = "mail.iyunu.com";
			String  SmtpUser = "sunhonglei";
			String  SmtpPwd = "jaiye8879699";

			if (from == null || from.equals("")) {
				from = "webmaster@yahoo.com";
			}

			props.put("mail.smtp.host", smtpServer); // �����ʼ�smtp�������ַ
			props.put("mail.smtp.auth", "true"); // ���÷�����smtp��Ҫ��֤
			Session sendMailSession = Session.getInstance(props, null);
			MimeMessage newMessage = new MimeMessage(sendMailSession);
			newMessage.setFrom(new InternetAddress(from));
			// ��ʼ����Ⱥ���ʼ���ַ
			InternetAddress[] address = new InternetAddress[to.size()];
			for (int i = 0; i < to.size(); i++) {
				address[i] = new InternetAddress((String) to.get(i));
			}

			newMessage.setRecipients(Message.RecipientType.TO, address);

			newMessage.setSubject(subject); // �趨����
			newMessage.setSentDate(new Date()); // �趨����ʱ��

			// ���ʼ����������н��б��봦�?֧������
			content = new String(content.getBytes("GB2312"), "ISO8859-1");
			newMessage.setDataHandler(new DataHandler(content, "text/html"));

			// ��ʼl��smtp�����������Ƿ�ͨ��smtp������֤��
			Transport transport = sendMailSession.getTransport("smtp");
			transport.connect(smtpServer, SmtpUser, SmtpPwd);
			transport.sendMessage(newMessage, newMessage.getAllRecipients());
			transport.close();
		} catch (MessagingException ex) {
			System.out.println("�ʼ�����ʧ��");
			ex.printStackTrace();
		} catch (UnsupportedEncodingException ex) {
			ex.printStackTrace();
		}
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try{
		MailInfo mInfo = new MailInfo();
		mInfo.setSubject("sunhonglei");
		//mInfo.setFrom("baozhijie@luckto.com");
		mInfo.setFrom("lzcc2003@sina.com");
		//mInfo.setTo("shigong@luckto.com");
		mInfo.setTo("sunhonglei@iyunu.com");
		mInfo.setContent("test mail");
		//mInfo.setAttachment("c://�����޸�����.txt");
		mInfo.setSmtpServer("mail.iyunu.com");
		mInfo.setUserName("sunhonglei@iyunu.com");
		mInfo.setPassword("jaiye8879699");
		//mInfo.setCc("liuxiangbin@joyveb.com");
//		mail.send("shigong@luckto.com","baozhijie@luckto.com","testMail","testMail");
		MailHandle.send(mInfo);
		}catch (Exception e){
			e.printStackTrace();
		}
	}

}

