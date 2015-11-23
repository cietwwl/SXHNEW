package com.joyveb.tlol.exception;

/**
 * 错误的请求
 * @author Sid
 */
public class BadRequestException extends Exception {
	/** serialVersionUID */
	private static final long serialVersionUID = 500398319388760216L;

	/**
	 * @param error 错误消息
	 */
	public BadRequestException(final String error) {
		super(error);
	}
	
	@Override
	public String getMessage() {
		return toString();
	}
	
	@Override
	public String toString() {
		return "错误的请求" + super.getMessage();
	}
	
}
