/**
 * @(#)NotImplementDialectException.java 2016年1月30日
 *
 * Copyright 2008-2016 by Woo Cupid.
 * All rights reserved.
 * 
 */
package net.turnbig.jdbcx.dialect.exception;

/**
 * @author Woo Cupid
 * @date 2016年1月30日
 * @version $Revision$
 */
public class NotImplementDialectException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1426790398723382540L;

	/**
	 * 
	 */
	public NotImplementDialectException() {
		super();
	}


	/**
	 * @param message
	 * @param cause
	 */
	public NotImplementDialectException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param message
	 */
	public NotImplementDialectException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public NotImplementDialectException(Throwable cause) {
		super(cause);
	}
	
}
