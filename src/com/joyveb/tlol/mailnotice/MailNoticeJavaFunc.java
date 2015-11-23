package com.joyveb.tlol.mailnotice;

import org.keplerproject.luajava.LuaException;

import com.joyveb.tlol.DefaultJavaFunc;
import com.joyveb.tlol.TLOLJavaFunction;

public enum MailNoticeJavaFunc implements TLOLJavaFunction {

	RemoveMailNotice(new DefaultJavaFunc("_RemoveMailNotice") {
		@Override
		public int execute() throws LuaException {
			MailNoticeManager.getInstance().removeMail(
					(int) this.getParam(2).getNumber());
			return 0;
		}
	});

	/**
	 * 实现默认的可注册Java函数
	 */
	private final DefaultJavaFunc jf;

	/**
	 * @param jf
	 *            可注册Java函数
	 */
	private MailNoticeJavaFunc(final DefaultJavaFunc jf) {
		this.jf = jf;
	}

	@Override
	public void register() {
		jf.register();
	}

}
