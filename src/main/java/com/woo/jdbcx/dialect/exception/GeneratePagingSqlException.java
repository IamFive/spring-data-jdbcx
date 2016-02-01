/**
 * @(#)JdbcxPagingException.java 2016年1月28日
 *
 * Copyright 2008-2016 by Woo Cupid.
 * All rights reserved.
 * 
 */
package com.woo.jdbcx.dialect.exception;

import java.text.MessageFormat;

/**
 * @author Woo Cupid
 * @date 2016年1月28日
 * @version $Revision$
 */
public class GeneratePagingSqlException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1104003198665241560L;

	private String originalSql;

	/**
	 * @return the originalSql
	 */
	public String getOriginalSql() {
		return originalSql;
	}

	/**
	 * @param originalSql the originalSql to set
	 */
	public void setOriginalSql(String originalSql) {
		this.originalSql = originalSql;
	}

	/**
	 * 
	 */
	public GeneratePagingSqlException() {
		super();
	}


	public GeneratePagingSqlException(String message, Throwable cause) {
		super(message, cause);
	}

	public GeneratePagingSqlException(String message) {
		super(message);
	}

	public GeneratePagingSqlException(Throwable cause) {
		super(cause);
	}


	public GeneratePagingSqlException(String originalSql, String message, Throwable cause) {
		super(msgWithOriginalSql(originalSql, message), cause);
	}

	public GeneratePagingSqlException(String originalSql, String message) {
		super(msgWithOriginalSql(originalSql, message));
	}


	/**
	 * @param originalSql2
	 * @param message
	 * @return
	 */
	private static String msgWithOriginalSql(String originalSql, String message) {
		return MessageFormat.format("message : {1}, original sql is : {0}", originalSql, message);
	}

}
