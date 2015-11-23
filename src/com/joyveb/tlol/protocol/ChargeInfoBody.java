package com.joyveb.tlol.protocol;

import java.nio.ByteBuffer;

import com.joyveb.tlol.fee.FeeService;

public final class ChargeInfoBody extends MsgBody {
	public static final ChargeInfoBody INSTANCE = new ChargeInfoBody();

	private ChargeInfoBody() {
	}

	private String cardId;
	private String cardPwd;
	private int cardAmt;
	private String p_MD;
	private String p_RHV;
	private String p_PF;
	private String p_FrpId;
	private short index;
	@Override
	public boolean readBody(final ByteBuffer body) {
		bodyLen = body.getInt();
		cardId = getStrByLen(body, body.getShort());
		cardPwd = getStrByLen(body, body.getShort());
		cardAmt = body.getInt();
		short tempLen = body.getShort();
		p_MD = getStrByLen(body, tempLen);
		p_RHV = getStrByLen(body, body.getShort());
		p_PF = getStrByLen(body, body.getShort());
		//添加多卡种充值参数    更改时间 2012年2月10日 14:37
		index = body.getShort();
		
		return true;
	}

	public String getCardId() {
		return cardId;
	}

	public void setCardId(final String cardId) {
		this.cardId = cardId;
	}

	public String getCardPwd() {
		return cardPwd;
	}

	public void setCardPwd(final String cardPwd) {
		this.cardPwd = cardPwd;
	}

	public int getCardAmt() {
		return cardAmt;
	}

	public void setCardAmt(int cardAmt) {
		this.cardAmt = cardAmt;
	}

	public String getP_MD() {
		return p_MD;
	}

	public void setP_MD(final String p_MD) {
		this.p_MD = p_MD;
	}

	public String getP_RHV() {
		return p_RHV;
	}

	public void setP_RHV(final String p_RHV) {
		this.p_RHV = p_RHV;
	}

	public String getP_PF() {
		return p_PF;
	}

	public void setP_PF(final String p_PF) {
		this.p_PF = p_PF;
	}

	public String getP_FrpId() {
			return p_FrpId;
	}

	public void setP_FrpId(String p_FrpId) {
		this.p_FrpId = p_FrpId;
	}

	public short getIndex() {
		return index;
	}

	public void setIndex(short index) {
		this.index = index;
}
	
}
