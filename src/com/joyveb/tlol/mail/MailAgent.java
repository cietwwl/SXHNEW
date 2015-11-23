package com.joyveb.tlol.mail;

import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.joyveb.tlol.LuaService;
import com.joyveb.tlol.OnlineService;
import com.joyveb.tlol.TianLongServer;
import com.joyveb.tlol.core.AgentProxy;
import com.joyveb.tlol.db.DataHandler;
import com.joyveb.tlol.db.DataStruct;
import com.joyveb.tlol.db.parser.CommonParser;
import com.joyveb.tlol.db.parser.DbConst;
import com.joyveb.tlol.db.parser.MailCheckData;
import com.joyveb.tlol.db.parser.MailData;
import com.joyveb.tlol.db.parser.MailDelAllData;
import com.joyveb.tlol.item.Item;
import com.joyveb.tlol.net.IncomingMsg;
import com.joyveb.tlol.protocol.DelAllMailBody;
import com.joyveb.tlol.protocol.DelMailBody;
import com.joyveb.tlol.protocol.MailOperationBody;
import com.joyveb.tlol.protocol.MailReqListBody;
import com.joyveb.tlol.protocol.MailSendBody;
import com.joyveb.tlol.protocol.MsgID;
import com.joyveb.tlol.protocol.ReceiverCheckBody;
import com.joyveb.tlol.role.RoleBean;
import com.joyveb.tlol.store.CommonPack;
import com.joyveb.tlol.util.Log;

public class MailAgent extends AgentProxy implements DataHandler {
	private MailReqListBody mailReqListBody = MailReqListBody.INSTANCE;

	private LinkedList<Mail> mailList = new LinkedList<Mail>();
	private LinkedList<Mail> sendMailList = new LinkedList<Mail>();
	private static final int MAX_MAIL_NUM = 50;
	private boolean isInboxMailNeedCheck = false;
	private boolean isSentMailNeedCheck = false;
	private static long MAIL_OVERDUE_TIME = 5 * 60 * 60 * 1000;

	public MailAgent(final RoleBean player) {
		this.player = player;
	}

	@Override
	public final void processCommand(final IncomingMsg msg) {
		switch (MsgID.getInstance(msg.getHeader().getMsgID())) {
		case MsgID_Mail_ReceiverCheck:
			if (ReceiverCheckBody.INSTANCE.readBody(msg.getBody()))
				checkReceiver(ReceiverCheckBody.INSTANCE.getReceiverName());
			else
				replyMessage(player, 1, MsgID.MsgID_Mail_ReceiverCheck, "收件人检查失败！");

			break;
		case MsgID_Mail_Send:
			if (MailSendBody.INSTANCE.readBody(msg.getBody()))
				sendMail();
			else
				replyMessage(player, 1, MsgID.MsgID_Mail_Send_Resp, "邮件解析失败！");

			break;
		case MsgID_Mail_Del:
			DelMailBody.INSTANCE.readBody(msg.getBody());
			delMail();

			break;
		// 删除所有邮件
		case MsgID_Mail_DelAll:
			DelAllMailBody.INSTANCE.readBody(msg.getBody());
			// delete all mail
			dellAllMail();
			break;
		case MsgID_Mail_GetAttach:
			MailOperationBody.INSTANCE.readBody(msg.getBody());
			getAttach(MailOperationBody.INSTANCE.getMailId());

			break;
		case MsgID_Mail_GetGold:
			MailOperationBody.INSTANCE.readBody(msg.getBody());
			getGold(MailOperationBody.INSTANCE.getMailId());

			break;
		case MsgID_Mail_Pay:
			MailOperationBody.INSTANCE.readBody(msg.getBody());
			payMail(MailOperationBody.INSTANCE.getMailId());

			break;
		case MsgID_Mail_ReqList:
			mailReqListBody.readBody(msg.getBody());
			reqMailList();

			break;
		case MsgID_Mail_Return:
			MailOperationBody.INSTANCE.readBody(msg.getBody());
			returnMail(MailOperationBody.INSTANCE.getMailId());

			break;
		case MsgID_Mail_Read:
			MailOperationBody.INSTANCE.readBody(msg.getBody());
			readMail(MailOperationBody.INSTANCE.getMailId());
			break;
		case MsgID_Mail_Attach_View:
			MailOperationBody.INSTANCE.readBody(msg.getBody());
			getMailAttachInfo(MailOperationBody.INSTANCE.getMailId());
			break;
		default:
			Log.error(Log.STDOUT, "processCommand", "unhandled msgid! : " + msg.getHeader().getMsgID());
			break;
		}
	}

	private void getMailAttachInfo(final long mailId) {
		Mail mail = getMailById(mailId);
		if (mail != null) {
			Item attach = mail.getAttachment();

			if (attach != null) {
				prepareBody();
				putShort((short) 0);
				putString(attach.getDescribe());
				sendMsg(player, MsgID.MsgID_Mail_Attach_View_Resp);
			} else {
				prepareBody();
				putShort((short) 1);
				putString("查看的附件不存在!");
				sendMsg(player, MsgID.MsgID_Mail_Attach_View_Resp);
			}
		} else {
			prepareBody();
			putShort((short) 1);
			putString("查看的邮件不存在!");
			sendMsg(player, MsgID.MsgID_Mail_Attach_View_Resp);
		}
	}

	private void readMail(final long mailId) {
		Mail mail = getMailById(mailId);
		if (mail != null) {
			mail.setRead(true);
			MailData mailData = new MailData(mail);
			CommonParser.getInstance().postTask(DbConst.MAIL_UPDATE, null, mailData);
		}
	}

	private void returnMail(final long mailId) {
		Mail mail = getMailById(mailId);
		if (mail != null) {

			if (mail.getSenderId() == player.getRoleid()) {
				prepareBody();
				body.putShort((short) 1);
				byte[] msg = getUTF8("不能给自己退信");
				body.putShort((short) msg.length);
				body.put(msg);
				body.putLong(mailId);
				sendMsg(player, MsgID.MsgID_Mail_Return_Resp);
				return;
			}

			if (mail.getLevel() == Mail.LV_SYS) {
				prepareBody();
				body.putShort((short) 1);
				byte[] msg = getUTF8("系统邮件不能退信!");
				body.putShort((short) msg.length);
				body.put(msg);
				body.putLong(mailId);
				sendMsg(player, MsgID.MsgID_Mail_Return_Resp);
				return;
			}
			if (mail.getLevel() == Mail.LV_RETURN) {
				prepareBody();
				body.putShort((short) 1);
				byte[] msg = getUTF8("此邮件不能退信!");
				body.putShort((short) msg.length);
				body.put(msg);
				body.putLong(mailId);
				sendMsg(player, MsgID.MsgID_Mail_Return_Resp);
				return;
			}

			mail.setReceiverId(mail.getSenderId());
			mail.setLevel(Mail.LV_RETURN);
			mail.setRead(false);
			mail.setSubject("退信: " + mail.getSubject());

			if (mail.isPayMail()) {
				mail.setPayMail(false);
				mail.setPayGold(0);
			}
			mailList.remove(mail);

			RoleBean role = OnlineService.getOnline(mail.getSenderId());
			if (role != null) {
				role.getMailAgent().addNewMail(mail);
				role.getMailAgent().removeSentMail(mail);
				sendMailNotify(role, Mail.STATE_NEW);
			}

			Log.info(Log.OPERATOR,
					player.getUserid() + "#$" + player.getName() + "#$" + player.getId() + "#$" + mail.getReceiverId() + "#$5#$" + mail.getSubject() + "#$" + mail.getContent()
							+ "#$" + (mail.getAttachment() == null ? "" : mail.getAttachment().getName()) + "#$"
							+ (mail.getAttachment() != null ? mail.getAttachment().getStorage() : "") + "#$" + mail.getGold() + "#$" + mail.getPayGold() + "#$"
							+ new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + "#$" + TianLongServer.srvId + "#$"
							+ (mail.getAttachment() != null ? mail.getAttachment().getUid() : "") + "#$" + (mail.getAttachment() != null ? mail.getAttachment().getTid() : ""));
			prepareBody();
			body.putShort((short) 0);
			byte[] msg = getUTF8("退信成功!");
			body.putShort((short) msg.length);
			body.put(msg);
			body.putLong(mailId);
			sendMsg(player, MsgID.MsgID_Mail_Return_Resp);

			MailData mailData = new MailData(mail);
			CommonParser.getInstance().postTask(DbConst.MAIL_UPDATE, null, mailData);

		} else {
			prepareBody();
			body.putShort((short) 1);
			byte[] msg = getUTF8("未找到此邮件!");
			body.putShort((short) msg.length);
			body.put(msg);
			body.putLong(mailId);
			sendMsg(player, MsgID.MsgID_Mail_Return_Resp);
		}
	}

	private void payMail(final long mailId) {
		Mail mail = getMailById(mailId);
		if (mail != null) {
			if (mail.isPayMail()) {
				if (player.getGold() < mail.getPayGold()) {
					prepareBody();
					body.putShort((short) 1);
					byte[] msg = getUTF8("金币不足!");
					body.putShort((short) msg.length);
					body.put(msg);
					body.putLong(mailId);
					sendMsg(player, MsgID.MsgID_Mail_Pay_Resp);
				} else {
					int paidGold = mail.getPayGold();

					Item attach = mail.getAttachment();
					if (attach != null) {
						if (LuaService.call4Bool("canAddToBag", player, attach.getTid(), attach.getStorage())) {
							player.getStore().getPack(1).addItem(attach, false);
							prepareBody();
							LuaService.callLuaFunction("fillBagAdd", attach);
							body.putShort((short) 0);
							sendMsg(player, MsgID.MsgID_Special_Train);
						} else {
							prepareBody();
							body.putShort((short) 1);
							byte[] msg = getUTF8("背包已满, 请先整理背包");
							body.putShort((short) msg.length);
							body.put(msg);
							body.putLong(mailId);
							sendMsg(player, MsgID.MsgID_Mail_Pay_Resp);
							return;
						}
					}

					Mail feedback = new Mail(player.getRoleid(), mail.getSenderId(), player.getName(), false, "支付 :" + mail.getSubject(), player.getName() + "支付了你的付费邮件", null,
							paidGold, 0, Mail.LV_SYS);

					RoleBean role = OnlineService.getOnline(mail.getSenderId());
					if (role != null) {
						role.getMailAgent().addNewMail(feedback);
						sendMailNotify(role, Mail.STATE_NEW);
						role.getMailAgent().getSendMailById(mailId).setPayMail(false);
					}
					MailData feedbackMailData = new MailData(feedback);
					CommonParser.getInstance().postTask(DbConst.MAIL_SEND, null, feedbackMailData);

					mail.setPayGold(0); // 将支付金额清零
					mail.setAttachment(null);
					mail.setPayMail(false);

					MailData mailData = new MailData(mail);
					CommonParser.getInstance().postTask(DbConst.MAIL_UPDATE, null, mailData);

					prepareBody();
					body.putShort((short) 0);
					byte[] msg = getUTF8("支付成功!");
					body.putShort((short) msg.length);
					body.put(msg);
					body.putLong(mailId);
					sendMsg(player, MsgID.MsgID_Mail_Pay_Resp);

					// 刷新玩家身上的金钱数
					player.decreaseGold(paidGold);

					// Log.info(Log.OPERATOR, player.getUserid()
					// + "#$"
					// + player.getId()
					// + "#$ "
					// + player.getName()
					// + "#$"
					// + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
					// .format(new Date()) + "#$"
					// + TianLongServer.srvId + "#$通过邮件支付:" + paidGold);
					Log.info(Log.OPERATOR, player.getUserid() + "#$" + player.getName() + "#$" + player.getId() + "#$" + mail.getSenderId() + "#$4#$" + mail.getSubject() + "#$"
							+ mail.getContent() + "#$" + (mail.getAttachment() == null ? "" : mail.getAttachment().getName()) + "#$"
							+ (mail.getAttachment() != null ? mail.getAttachment().getStorage() : "") + "#$" + paidGold + "#$" + mail.getPayGold() + "#$"
							+ new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + "#$" + TianLongServer.srvId + "#$"
							+ (mail.getAttachment() != null ? mail.getAttachment().getUid() : "") + "#$" + (mail.getAttachment() != null ? mail.getAttachment().getTid() : ""));
					prepareBody();
					LuaService.callLuaFunction("fillAttributes", player);
					body.putShort((short) 0);
					sendMsg(player, MsgID.MsgID_Special_Train);

				}
			} else {
				prepareBody();
				body.putShort((short) 1);
				byte[] msg = getUTF8("不能支付此邮件");
				body.putShort((short) msg.length);
				body.put(msg);
				body.putLong(mailId);
				sendMsg(player, MsgID.MsgID_Mail_Pay_Resp);
			}
		} else {
			prepareBody();
			putShort((short) 1);
			putString("邮件未找到!");
			body.putLong(mailId);
			sendMsg(player, MsgID.MsgID_Mail_Pay_Resp);
		}
	}

	private void getAttach(final long mailId) {
		Mail mail = getMailById(mailId);
		if (mail != null && !mail.isPayMail()) {

			Item attach = mail.getAttachment();
			
			
			if (attach != null) {
				
				if(attach.getStorage()>20||attach.getStorage()<=0){
					prepareBody();
					body.putShort((short) 1);
					byte[] msg = getUTF8("个数错误!");
					body.putShort((short) msg.length);
					body.put(msg);
					body.putLong(mailId);
					sendMsg(player, MsgID.MsgID_Mail_GetAttach_Resp);
					return;
				}
				
				if (Math.abs(attach.getStorage()) > 0 && Math.abs(attach.getStorage()) <= 20) {
					if (LuaService.call4Bool("canAddToBag", player, attach.getTid(), Math.abs(attach.getStorage()))) {
						mail.setAttachment(null);
						player.getStore().getPack(1).addItem(attach, false);

						Log.info(Log.OPERATOR, player.getUserid() + "#$" + player.getName() + "#$" + player.getId() + "#$" + mail.getReceiverId() + "#$3#$" + mail.getSubject()
								+ "#$" + mail.getContent() + "#$" + (mail.getAttachment() == null ? "" : mail.getAttachment().getName()) + "#$" + Math.abs(attach.getStorage())
								+ "#$" + mail.getGold() + "#$" + mail.getPayGold() + "#$" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + "#$"
								+ TianLongServer.srvId + "#$" + (mail.getAttachment() != null ? mail.getAttachment().getUid() : "") + "#$"
								+ (mail.getAttachment() != null ? mail.getAttachment().getTid() : ""));
						prepareBody();
						body.putShort((short) 0);
						body.putShort((short) 0);
						body.putLong(mailId);
						sendMsg(player, MsgID.MsgID_Mail_GetAttach_Resp);

						MailData mailData = new MailData(mail);
						CommonParser.getInstance().postTask(DbConst.MAIL_UPDATE, null, mailData);

						prepareBody();
						LuaService.callLuaFunction("fillBagAdd", attach);
						body.putShort((short) 0);
						sendMsg(player, MsgID.MsgID_Special_Train);
					} else {
						prepareBody();
						body.putShort((short) 1);
						byte[] msg = getUTF8("背包已满, 请先整理背包");
						body.putShort((short) msg.length);
						body.put(msg);
						body.putLong(mailId);
						sendMsg(player, MsgID.MsgID_Mail_GetAttach_Resp);
					}
				} else {
					mailList.remove(mail);
					prepareBody();
					body.putShort((short) 1);
					byte[] msg = getUTF8("附件存在异常!");
					body.putShort((short) msg.length);
					body.put(msg);
					body.putLong(mailId);
					sendMsg(player, MsgID.MsgID_Mail_GetAttach_Resp);
				}
			} else {
				prepareBody();
				body.putShort((short) 1);
				byte[] msg = getUTF8("附件不存在!");
				body.putShort((short) msg.length);
				body.put(msg);
				body.putLong(mailId);
				sendMsg(player, MsgID.MsgID_Mail_GetAttach_Resp);
			}

		} else {
			prepareBody();
			body.putShort((short) 1);
			byte[] msg = getUTF8("邮件未找到");
			body.putShort((short) msg.length);
			body.put(msg);
			body.putLong(mailId);
			sendMsg(player, MsgID.MsgID_Mail_GetAttach_Resp);
		}
	}

	private void getGold(final long mailId) {
		Mail mail = getMailById(mailId);
		if (mail != null) {
			int gold = mail.getGold();

			if (gold < 0 || gold > 1000000000) {
				prepareBody();
				body.putShort((short) 1);
				byte[] msg = getUTF8("数据异常!");
				body.putShort((short) msg.length);
				body.put(msg);
				body.putLong(mailId);
				sendMsg(player, MsgID.MsgID_Mail_GetGold_Resp);
				return;
			}
			int maxGold = 0;
			int leftGold = 0;
			if (player.getGold() + mail.getGold() > 99999999) {
				maxGold = 99999999 - player.getGold();
				leftGold = mail.getGold() - maxGold;
			} else {
				maxGold = mail.getGold();
				leftGold = 0;
			}
			player.increaseGold(maxGold);

			Log.info(Log.OPERATOR,
					player.getUserid() + "#$" + player.getName() + "#$" + player.getId() + "#$" + mail.getReceiverId() + "#$2#$" + mail.getSubject() + "#$" + mail.getContent()
							+ "#$" + (mail.getAttachment() != null ? mail.getAttachment().getName() : "") + "#$"
							+ (mail.getAttachment() != null ? mail.getAttachment().getStorage() : "") + "#$" + maxGold + "#$" + mail.getPayGold() + "#$"
							+ new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + "#$" + TianLongServer.srvId + "#$"
							+ (mail.getAttachment() != null ? mail.getAttachment().getUid() : "") + "#$" + (mail.getAttachment() != null ? mail.getAttachment().getTid() : ""));

			mail.setGold(leftGold);
			prepareBody();
			body.putShort((short) 0);
			byte[] msg = getUTF8("");
			body.putShort((short) msg.length);
			body.put(msg);
			body.putLong(mailId);
			sendMsg(player, MsgID.MsgID_Mail_GetGold_Resp);

			MailData mailData = new MailData(mail);
			CommonParser.getInstance().postTask(DbConst.MAIL_UPDATE, null, mailData);

			// 刷新玩家身上的金钱数
			prepareBody();
			LuaService.callLuaFunction("fillAttributes", player);
			body.putShort((short) 0);
			sendMsg(player, MsgID.MsgID_Special_Train);
		} else {
			prepareBody();
			body.putShort((short) 1);
			byte[] msg = getUTF8("邮件未找到");
			body.putShort((short) msg.length);
			body.put(msg);
			body.putLong(mailId);
			sendMsg(player, MsgID.MsgID_Mail_GetGold_Resp);
		}
	}

	private void reqMailList() {
		prepareBody();
		if (mailList.isEmpty() || mailReqListBody.getMailIndex() >= mailList.size()) {
			putShort((short) 1);
			putString("无邮件!");
			putByte((byte) 0);
		} else {
			body.putShort((short) 0);
			body.putShort((short) 0);
			if (mailList.size() > 0) {
				body.put((byte) mailList.size());
				byte mailNum = (byte) (mailReqListBody.getMailIndex() + mailReqListBody.getMailNum() > mailList.size() - 1 ? mailList.size() - mailReqListBody.getMailIndex()
						: mailReqListBody.getMailNum());
				body.put(mailNum);

				// int totalMailNum = mailList.size();
				int startIdx = mailReqListBody.getMailIndex();
				// int realIdx = totalMailNum - startIdx - 1;

				for (int i = startIdx; i < startIdx + mailNum; i++) {
					Mail mail = mailList.get(i);
					putMailData(body, mail);
				}
				// for(int i = realIdx; i > realIdx - mailNum; i--){
				// Mail mail = mailList.get(i);
				// putMailData(body, mail);
				// }
			} else {
				body.put((byte) 0);
			}
		}
		sendMsg(player, MsgID.MsgID_Mail_ReqList_Resp);
	}

	private void putMailData(final ByteBuffer body, final Mail mail) {
		body.putLong(mail.getMailId());
		body.put(mail.getLevel());
		body.put((byte) (mail.isRead() ? 1 : 0));
		body.put((byte) (mail.isPayMail() ? 1 : 0));
		body.putInt(mail.getSenderId());
		byte[] senderName = getUTF8(mail.getSenderName());
		body.putShort((short) senderName.length);
		body.put(senderName);
		byte[] subject = getUTF8(mail.getSubject());
		body.putShort((short) subject.length);
		body.put(subject);
		byte[] content = getUTF8(mail.getContent());
		body.putShort((short) content.length);
		body.put(content);
		if (mail.getAttachment() != null) {
			Item attachment = mail.getAttachment();
			body.putLong(attachment.getUid());
			byte[] attachName = getUTF8(attachment.getName());
			body.putShort((short) attachName.length);
			body.put(attachName);
			body.putShort(attachment.getStorage());
		} else {
			body.putLong(0);
			body.putShort((short) 0);
			body.putShort((short) 0);
		}
		body.putInt(mail.getGold());
		body.putInt(mail.getPayGold());
	}

	private Mail getMailById(final long mailId) {
		for (int i = 0; i < mailList.size(); i++) {
			Mail mail = mailList.get(i);
			if (mail.getMailId() == mailId)
				return mail;
		}
		return null;
	}

	private Mail getSendMailById(final long mailId) {
		for (int i = 0; i < sendMailList.size(); i++) {
			Mail mail = sendMailList.get(i);
			if (mail.getMailId() == mailId)
				return mail;
		}
		return null;
	}

	private void delMail() {
		Mail mail = getMailById(DelMailBody.INSTANCE.getMailId());
		if (mail != null) {
			if (mail.isPayMail()) {
				prepareBody();
				body.putShort((short) 1);
				byte[] msg = getUTF8("到付邮件不能删除!");
				body.putShort((short) msg.length);
				body.put(msg);
				body.putLong(DelMailBody.INSTANCE.getMailId());
				sendMsg(player, MsgID.MsgID_Mail_Del_Resp);
			} else if (mail.getAttachment() != null || mail.getGold() != 0) {
				prepareBody();
				body.putShort((short) 1);
				byte[] msg = getUTF8("有物品或金币的邮件不能删除!");
				body.putShort((short) msg.length);
				body.put(msg);
				body.putLong(DelMailBody.INSTANCE.getMailId());
				sendMsg(player, MsgID.MsgID_Mail_Del_Resp);
			} else {
				mailList.remove(mail);
				prepareBody();
				body.putShort((short) 0);
				byte[] msg = getUTF8("邮件删除成功");
				body.putShort((short) msg.length);
				body.put(msg);
				body.putLong(DelMailBody.INSTANCE.getMailId());
				sendMsg(player, MsgID.MsgID_Mail_Del_Resp);
				MailData mailData = new MailData(mail);
				CommonParser.getInstance().postTask(DbConst.MAIL_DEL, null, mailData);
			}

		} else {
			prepareBody();
			body.putShort((short) 1);
			byte[] msg = getUTF8("邮件未找到");
			body.putShort((short) msg.length);
			body.put(msg);
			body.putLong(DelMailBody.INSTANCE.getMailId());
			sendMsg(player, MsgID.MsgID_Mail_Del_Resp);
		}
	}

	private void dellAllMail() {
		DelAllMailBody delAllMailBody = DelAllMailBody.INSTANCE;
		short delMailType = delAllMailBody.getDelMailType();
		switch (delMailType) {
		case 0:// 删除所有己读邮件

			List<Mail> deletedMailList = new ArrayList<Mail>();

			for (Iterator<Mail> it = mailList.iterator(); it.hasNext();) {
				Mail mail = it.next();

				// 邮件己读,邮件没有附件,邮件非到付邮件,邮寄金币数量为0
				if (mail.isRead() && mail.getAttachment() == null && !mail.isPayMail() && mail.getGold() == 0) {
					it.remove();
					deletedMailList.add(mail);
				}
			}

			if (deletedMailList.size() > 0) {
				MailDelAllData mailDelAllData = new MailDelAllData(deletedMailList);
				CommonParser.getInstance().postTask(DbConst.MAIL_DEL_ALL, this, mailDelAllData);
			} else {
				replyMessage(player, 0, MsgID.MsgID_Mail_DelAll_Resp, "删除邮件成功！");
			}

			break;
		case 1:// 删除所有邮件

			List<Mail> deletedAllMailList = new ArrayList<Mail>();

			for (Iterator<Mail> it = mailList.iterator(); it.hasNext();) {
				Mail mail = it.next();

				// 邮件没有附件,邮件非到付邮件,邮寄金币数量为0
				if (mail.getAttachment() == null && !mail.isPayMail() && mail.getGold() == 0) {
					it.remove();
					deletedAllMailList.add(mail);
				}
			}

			if (deletedAllMailList.size() > 0) {
				MailDelAllData mailDelAllData = new MailDelAllData(deletedAllMailList);
				CommonParser.getInstance().postTask(DbConst.MAIL_DEL_ALL, this, mailDelAllData);
			} else {
				replyMessage(player, 0, MsgID.MsgID_Mail_DelAll_Resp, "删除邮件成功！");
			}

			break;
		}
	}

	private void sendMail() {
		MailSendBody mailSendBody = MailSendBody.INSTANCE;
		int fee = 0;
		if (player.getRoleid() == mailSendBody.getReceiverId()) {
			replyMessage(player, 1, MsgID.MsgID_Mail_Send_Resp, "发送失败！不能给自己发送邮件!");
			return;
		}

		if (!LuaService.call4Bool("checkDirtyWord", mailSendBody.getSubject())) {
			replyMessage(player, 1, MsgID.MsgID_Mail_Send_Resp, "发送失败！邮件标题中含有非法词汇！");
			return;
		}

		if (mailSendBody.getGold() < 0 || mailSendBody.getGold() > 99999999) {
			replyMessage(player, 1, MsgID.MsgID_Mail_Send_Resp, "发送失败！数据异常");
			return;
		}
		if (mailSendBody.getPayGold() < 0 || mailSendBody.getPayGold() > 99999999) {
			replyMessage(player, 1, MsgID.MsgID_Mail_Send_Resp, "发送失败！数据异常");
			return;
		}
		if (mailSendBody.getGold() > player.getGold()) {
			replyMessage(player, 1, MsgID.MsgID_Mail_Send_Resp, "发送失败！金币不足");
			return;
		}

		
		
		

		if (mailSendBody.getGold() > 0) {
			fee += mailSendBody.getGold() * 0.01;
		}
		CommonPack bag = (CommonPack) (player.getStore().getPack(1));
		Item attachment = null;
		if (mailSendBody.getAttachmentId() != 0) {
			if (mailSendBody.getAttachmentNum()>20||mailSendBody.getAttachmentNum()<=0) {
				replyMessage(player, 1, MsgID.MsgID_Mail_Send_Resp, "发送失败！个数错误");
				return;
			}
			
			attachment = bag.getItem(mailSendBody.getAttachmentId());
			if (attachment != null) {
				if (attachment.getStorage() < mailSendBody.getAttachmentNum()) {
					replyMessage(player, 1, MsgID.MsgID_Mail_Send_Resp, "数量不足!");
					return;
				}

				// System.out.println("邮寄物品tid：" + attachment.getTid() + "：" +
				// LuaService.getBool("ShenQiList", attachment.getTid()));
				// 神兵不能邮寄
				if (LuaService.getBool("ShenQiList", attachment.getTid())) {
					replyMessage(player, 1, MsgID.MsgID_Mail_Send_Resp, "此物品不能邮寄!");
					return;
				}
				if (attachment.canMailed()) {
					fee += attachment.getMailFee();
				} else {
					replyMessage(player, 1, MsgID.MsgID_Mail_Send_Resp, "此物品不能邮寄!");
					return;
				}
			}
		}
		// if(mailSendBody.getGold() > 0){
		// 刷新玩家身上的金钱数
		// 判断+手续费以后剩余金币是否足够
		if (mailSendBody.getGold() + fee > player.getGold()) {
			replyMessage(player, 1, MsgID.MsgID_Mail_Send_Resp, "发送失败！所剩金币不足以支付手续费");
			return;
		}

		if (mailSendBody.getAttachmentId() != 0) {
			attachment = bag.pickItem(mailSendBody.getAttachmentId(), mailSendBody.getAttachmentNum(), true);

			if (attachment == null) {
				replyMessage(player, 1, MsgID.MsgID_Mail_Send_Resp, "发送失败！背包中不存在此物品或物品数量不足");
				return;
			} else {
				prepareBody();
				LuaService.callLuaFunction("fillBagDel", attachment.getUid(), attachment.getStorage());
				body.putShort((short) 0);
				sendMsg(player, MsgID.MsgID_Special_Train);
			}
		}
		player.decreaseGold(mailSendBody.getGold() + fee);

		Mail mail = new Mail(player.getRoleid(), mailSendBody.getReceiverId(), player.getName(), mailSendBody.isPayMail(), mailSendBody.getSubject(), mailSendBody.getContent(),
				attachment, mailSendBody.getGold(), mailSendBody.getPayGold(), Mail.LV_NORMAL);
		mail.setReceiverName(mailSendBody.getReceiverName());
		RoleBean role = OnlineService.getOnline(mail.getReceiverId());
		if (role != null) {

			role.getMailAgent().addNewMail(mail);
			sendMailNotify(role, Mail.STATE_NEW);
		}
		// 将发送的所有邮件保存下来
		player.getMailAgent().addSendMail(mail);

		Log.info(Log.OPERATOR,
				player.getUserid() + "#$" + player.getName() + "#$" + player.getId() + "#$" + mail.getReceiverId() + "#$1#$" + mail.getSubject() + "#$" + mail.getContent() + "#$"
						+ (mail.getAttachment() == null ? "" : mail.getAttachment().getName()) + "#$" + (mail.getAttachment() != null ? mail.getAttachment().getStorage() : "")
						+ "#$" + mail.getGold() + "#$" + mail.getPayGold() + "#$" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + "#$" + TianLongServer.srvId
						+ "#$" + (mail.getAttachment() != null ? mail.getAttachment().getUid() : "") + "#$" + (mail.getAttachment() != null ? mail.getAttachment().getTid() : ""));

		sendMsg(player, MsgID.MsgID_Mail_Send_Resp, "邮件成功发送, 收取手续费: " + LuaService.call4String("getValueDescribe", fee));

		prepareBody();
		LuaService.callLuaFunction("fillAttributes", player);
		body.putShort((short) 0);
		sendMsg(player, MsgID.MsgID_Special_Train);
		// }

		MailData mailData = new MailData(mail);
		CommonParser.getInstance().postTask(DbConst.MAIL_SEND, null, mailData);
	}

	@Override
	public final void handle(final DbConst eventID, final boolean flag, final DataStruct ds) {
		switch (eventID) {
		case MAIL_SEND:
			// onMailSent((MailData)ds);
			break;
		case MAIL_RECEIVER_CHECK:
			onCheckedResult((MailCheckData) ds);
			break;
		case MAIL_DEL:
			// onMailDeleted();
			break;
		case MAIL_DEL_ALL:// 删除所有邮件
			if (flag) {
				replyMessage(player, 0, MsgID.MsgID_Mail_DelAll_Resp, "删除邮件成功！");
			} else {
				replyMessage(player, 1, MsgID.MsgID_Mail_DelAll_Resp, "删除邮件失败！");
			}
			break;
		default:
			Log.info(Log.STDOUT, "handle", "unhandled db call back! : " + eventID);
			break;
		}

	}

	// private void onMailDeleted(){
	// sendCommonResp(0, MsgIDs.MsgID_Mail_Del_Resp, "邮件删除成功");
	// }

	public final void sendMailNotify(final RoleBean role, final byte mailState) {
		prepareBody();
		body.put(mailState);
		sendMsg(role, MsgID.MsgID_Mail_NewMailNotify);
	}

	// private void onMailSent(MailData mailData){
	// RoleBean role =
	// OnlineService.getOnline(mailData.getMail().getReceiverId());
	// if(role != null){
	// role.getMailAgent().addNewMail(mailData.getMail());
	// sendNewMailNotify(role);
	// }
	// sendCommonResp(0, MsgIDs.MsgID_Mail_Send_Resp, "邮件成功发送");
	// }

	private void checkReceiver(final String receiverName) {
		prepareBody();
		if (player.getName().equals(receiverName)) {
			body.putInt(0);
			body.putShort((short) 1);
			byte[] msg = getUTF8("不能给自己发送邮件!");
			body.putShort((short) msg.length);
			body.put(msg);
			sendMsg(player, MsgID.MsgID_Mail_ReceiverCheck_Resp);
		} else {
			MailCheckData mailCheckData = new MailCheckData(player.getZoneid());
			mailCheckData.setReceiverName(receiverName);
			CommonParser.getInstance().postTask(DbConst.MAIL_RECEIVER_CHECK, this, mailCheckData);
		}
	}

	private void onCheckedResult(final MailCheckData mailCheckData) {
		prepareBody();
		if (mailCheckData.getReceiverId() == 0) {
			body.putInt(mailCheckData.getReceiverId());
			body.putShort((short) 1);
			byte[] msg = getUTF8("收件人不存在！");
			body.putShort((short) msg.length);
			body.put(msg);
			sendMsg(player, MsgID.MsgID_Mail_ReceiverCheck_Resp);
		} else if (mailCheckData.getReceiverMailBoxNum() >= MAX_MAIL_NUM) {
			body.putInt(mailCheckData.getReceiverId());
			body.putShort((short) 2);
			byte[] msg = getUTF8("收件人邮箱已满！");
			body.putShort((short) msg.length);
			body.put(msg);
			sendMsg(player, MsgID.MsgID_Mail_ReceiverCheck_Resp);

			RoleBean role = OnlineService.getOnline(mailCheckData.getReceiverId());
			if (role != null) {
				sendMailNotify(role, Mail.STATE_FULL);
			}
		} else {
			body.putInt(mailCheckData.getReceiverId());
			body.putShort((short) 0);
			body.putShort((short) 0);
			sendMsg(player, MsgID.MsgID_Mail_ReceiverCheck_Resp);
		}
	}

	public final void addNewMail(final Mail mail) {
		for (Mail m : mailList) {
			if (mail.getMailId() == m.getMailId())
				throw new RuntimeException("发现重复邮件");
		}
		mailList.addFirst(mail);
	}

	public final void addSendMail(final Mail mail) {
		for (Mail m : sendMailList) {
			if (mail.getMailId() == m.getMailId())
				throw new RuntimeException("发现重复邮件");
		}
		sendMailList.addFirst(mail);
	}

	public final void initMailList(final List<Mail> mailList) {
		this.mailList.clear();
		for (Mail m : mailList) {
			addInitNewMail(m);
		}
		isInboxMailNeedCheck = true;
	}

	public final void addInitNewMail(final Mail mail) {
		for (Mail m : mailList) {
			if (mail.getMailId() == m.getMailId())
				throw new RuntimeException("发现重复邮件");
		}
		mailList.add(mail);
	}

	public final void initSendMailList(final List<Mail> sendMailList) {
		this.sendMailList.clear();
		for (Mail m : sendMailList) {
			addSendMail(m);
		}
		isSentMailNeedCheck = true;
	}

	public final LinkedList<Mail> getMailList() {
		return mailList;
	}

	public final boolean containsUnreadMail() {
		for (int i = mailList.size() - 1; i >= 0; i--) {
			if (!mailList.get(i).isRead()) {
				return true;
			}
		}
		return false;
	}

	public final int getMailNum() {
		return mailList.size();
	}

	private boolean removeSentMail(final Mail sentMail) {
		Iterator<Mail> it = sendMailList.iterator();
		while (it.hasNext()) {
			Mail mail = it.next();
			if (mail.getMailId() == sentMail.getMailId()) {
				it.remove();
				return true;
			}
		}
		return false;
	}

	// 对方不在线时邮件的超时处理
	public final void sentBoxCheckTimeOut() {
		if (sendMailList.size() != 0 && isSentMailNeedCheck) {
			Iterator<Mail> i = sendMailList.iterator();
			while (i.hasNext()) {
				Mail mail = i.next();
				if (System.currentTimeMillis() - mail.getSendTime() > MAIL_OVERDUE_TIME) { // 设置为五分钟
					RoleBean role = OnlineService.getOnline(mail.getReceiverId());
					if (role == null) {
						if (mail.getLevel() != Mail.LV_RETURN && mail.getLevel() != Mail.LV_SYS && mail.getReceiverId() != player.getRoleid()) {
							if (mail.isPayMail()) {
								checkTimeReturnMail(mail, true);
								i.remove();
							}
						}
					}
				}
			}
		}
	}

	// 邮件超时处理
	public final void inboxCheckTimeOut() {

		if (mailList.size() != 0 && isInboxMailNeedCheck) {
			Iterator<Mail> i = mailList.iterator();

			while (i.hasNext()) {
				Mail mail = i.next();
				if (mail.getLevel() != Mail.LV_RETURN && mail.getLevel() != Mail.LV_SYS && mail.getSenderId() != player.getRoleid()) {
					if (mail.isPayMail()) {

						if (System.currentTimeMillis() - mail.getSendTime() > MAIL_OVERDUE_TIME) { // 设置为五分钟
							checkTimeReturnMail(mail, false);
							i.remove();
						}
					}
				}
			}
		}
	}

	private void checkTimeReturnMail(final Mail mail, final boolean isSentMail) {

		// 为了防止错误先把寄信人ID取出 下面会设置发送者ID
		int senderId = mail.getSenderId();
		RoleBean sender = OnlineService.getOnline(senderId);

		mail.setSenderName("系统");
		mail.setReceiverId(senderId);
		mail.setSenderId(1);
		mail.setPayMail(false);
		mail.setPayGold(0);
		mail.setLevel(Mail.LV_RETURN);
		mail.setSubject("退信: " + mail.getReceiverName() + ": " + mail.getSubject());
		mail.setContent("您发送给 <" + mail.getReceiverName() + " >的信件，因对方在系统规定时限内未接受信件，被系统退回，请注意查收谢谢！/" + mail.getContent() == null ? "无内容" : mail.getContent());
		mail.setRead(false);
		if (sender != null) {
			// TODO: 当发送者在线 发送新邮件通知 将邮件设置到发送者信箱
			sender.getMailAgent().addNewMail(mail);
			sendMailNotify(sender, Mail.STATE_NEW);
			if (!isSentMail)
				sender.getMailAgent().removeSentMail(mail);
		}

		// TODO 退信逻辑 将发件人置为系统
		MailData mailData = new MailData(mail);
		CommonParser.getInstance().postTask(DbConst.MAIL_UPDATE, null, mailData);
	}

}
