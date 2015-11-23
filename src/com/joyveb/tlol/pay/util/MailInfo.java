package com.joyveb.tlol.pay.util;

public class MailInfo {
	private String smtpServer = null;
	private String userName = null;
	private String password = null;
	private String subject = null;
	private String attachment = null;
	private String content = null;
	private String bcc = null;//�ʼ�����
	private String cc = null;//�ʼ�����
	private String to = null;//���͵�ַ
	private String from = null;//����4Դ
	private String[] MutliTo = null;//Ⱥ���û�
	public String[] getMutliTo() {
		return MutliTo;
	}
	public void setMutliTo(String[] mutliTo) {
		MutliTo = mutliTo;
	}
	public String getFrom() {
		return from;
	}
	public void setFrom(String from) {
		this.from = from;
	}
	public String getTo() {
		return to;
	}
	public void setTo(String to) {
		this.to = to;
	}
	public String getBcc() {
		return bcc;
	}
	public void setBcc(String bcc) {
		this.bcc = bcc;
	}
	public String getCc() {
		return cc;
	}
	public void setCc(String cc) {
		this.cc = cc;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getSmtpServer() {
		return smtpServer;
	}
	public void setSmtpServer(String smtpServer) {
		this.smtpServer = smtpServer;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getAttachment() {
		return attachment;
	}
	public void setAttachment(String attachment){		
		this.attachment = attachment;
	}
}
