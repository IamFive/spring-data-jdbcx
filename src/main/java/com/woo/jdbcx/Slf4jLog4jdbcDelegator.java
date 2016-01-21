/**
 * @(#)Slf4jLog4jdbcDelegator.java 2015年5月29日
 *
 * Copyright 2008-2015 by Woo Cupid.
 * All rights reserved.
 * 
 */
package com.woo.jdbcx;

import java.sql.SQLFeatureNotSupportedException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.log4jdbc.Properties;
import net.sf.log4jdbc.log.slf4j.Slf4jSpyLogDelegator;
import net.sf.log4jdbc.sql.Spy;

/**
 * @author Woo Cupid
 * @date 2015年5月29日
 * @version $Revision$
 */
public class Slf4jLog4jdbcDelegator extends Slf4jSpyLogDelegator {

	/**
	 * Logger that shows only the SQL that is occuring
	 */
	final static Logger sqlOnlyLogger = LoggerFactory.getLogger("jdbc.sqlonly");
	final static String nl = System.getProperty("line.separator");

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sf.log4jdbc.log.slf4j.Slf4jSpyLogDelegator#exceptionOccured(net.sf.log4jdbc.sql.Spy, java.lang.String,
	 * java.lang.Exception, java.lang.String, long)
	 */
	@Override
	public void exceptionOccured(Spy spy, String methodCall, Exception e, String sql, long execTime) {
		if (e instanceof SQLFeatureNotSupportedException) {
			// disable not support feature exception
		} else {
			super.exceptionOccured(spy, methodCall, e, sql, execTime);
		}
	}
	
	private boolean shouldSqlBeLogged(String sql) {
		if (sql == null) {
			return false;
		}
		sql = sql.trim();

		if (sql.length() < 6) {
			return false;
		}
		sql = sql.substring(0, 6).toLowerCase();
		return (Properties.isDumpSqlSelect() && "select".equals(sql)) || (Properties.isDumpSqlInsert() && "insert".equals(sql))
				|| (Properties.isDumpSqlUpdate() && "update".equals(sql)) || (Properties.isDumpSqlDelete() && "delete".equals(sql))
				|| (Properties.isDumpSqlCreate() && "create".equals(sql));
	}
	
	private static String getDebugInfo() {
		Throwable t = new Throwable();
		t.fillInStackTrace();

		StackTraceElement[] stackTrace = t.getStackTrace();

		if (stackTrace != null) {
			String className;

			StringBuffer dump = new StringBuffer();

			/**
			 * The DumpFullDebugStackTrace option is useful in some situations when
			 * we want to see the full stack trace in the debug info-  watch out
			 * though as this will make the logs HUGE!
			 */
			if (Properties.isDumpFullDebugStackTrace()) {
				boolean first = true;
				for (int i = 0; i < stackTrace.length; i++) {
					className = stackTrace[i].getClassName();
					if (!className.startsWith("net.sf.log4jdbc")) {
						if (first) {
							first = false;
						} else {
							dump.append("  ");
						}
						dump.append("at ");
						dump.append(stackTrace[i]);
						dump.append(nl);
					}
				}
			} else {
				dump.append(" ");
				int firstLog4jdbcCall = 0;
				int lastApplicationCall = 0;

				for (int i = 0; i < stackTrace.length; i++) {
					className = stackTrace[i].getClassName();
					if (className.startsWith("net.sf.log4jdbc")) {
						firstLog4jdbcCall = i;
					} else if (Properties.isTraceFromApplication() && Pattern.matches(Properties.getDebugStackPrefix(), className)) {
						lastApplicationCall = i;
						break;
					}
				}
				int j = lastApplicationCall;

				if (j == 0)  // if app not found, then use whoever was the last guy that called a log4jdbc class.
				{
					j = 1 + firstLog4jdbcCall;
				}

				dump.append(stackTrace[j].getClassName()).append(".").append(stackTrace[j].getMethodName()).append("(")
						.append(stackTrace[j].getFileName()).append(":").append(stackTrace[j].getLineNumber()).append(")");
			}

			return dump.toString();
		}
		return null;
	}
	
	public void sqlOccurred(Spy spy, String methodCall, String sql) {
		if (!Properties.isDumpSqlFilteringOn() || shouldSqlBeLogged(sql)) {
			if (sqlOnlyLogger.isDebugEnabled()) {
				sqlOnlyLogger.debug(getDebugInfo() + nl + spy.getConnectionNumber() + ". " + processSql(sql));
			} else if (sqlOnlyLogger.isInfoEnabled()) {
				sqlOnlyLogger.info(processSql(sql));
			}
		}
	}
	
	protected String processSql(String sql) {
		return new BasicFormatterImpl().format(sql);
	}

	public static class BasicFormatterImpl {

		public static final String WHITESPACE = " \n\r\f\t";
		private static final Set<String> BEGIN_CLAUSES = new HashSet<String>();
		private static final Set<String> END_CLAUSES = new HashSet<String>();
		private static final Set<String> LOGICAL = new HashSet<String>();
		private static final Set<String> QUANTIFIERS = new HashSet<String>();
		private static final Set<String> DML = new HashSet<String>();
		private static final Set<String> MISC = new HashSet<String>();

		static {
			BEGIN_CLAUSES.add("left");
			BEGIN_CLAUSES.add("right");
			BEGIN_CLAUSES.add("inner");
			BEGIN_CLAUSES.add("outer");
			BEGIN_CLAUSES.add("group");
			BEGIN_CLAUSES.add("order");

			END_CLAUSES.add("where");
			END_CLAUSES.add("set");
			END_CLAUSES.add("having");
			END_CLAUSES.add("join");
			END_CLAUSES.add("from");
			END_CLAUSES.add("by");
			END_CLAUSES.add("join");
			END_CLAUSES.add("into");
			END_CLAUSES.add("union");

			LOGICAL.add("and");
			LOGICAL.add("or");
			LOGICAL.add("when");
			LOGICAL.add("else");
			LOGICAL.add("end");

			QUANTIFIERS.add("in");
			QUANTIFIERS.add("all");
			QUANTIFIERS.add("exists");
			QUANTIFIERS.add("some");
			QUANTIFIERS.add("any");

			DML.add("insert");
			DML.add("update");
			DML.add("delete");

			MISC.add("select");
			MISC.add("on");
		}

		static final String indentString = "    ";
		static final String initial = "\n    ";

		public String format(String source) {
			return new FormatProcess(source).perform();
		}

		private static class FormatProcess {
			boolean beginLine = true;
			boolean afterBeginBeforeEnd = false;
			boolean afterByOrSetOrFromOrSelect = false;
			@SuppressWarnings("unused")
			boolean afterValues = false;
			boolean afterOn = false;
			boolean afterBetween = false;
			boolean afterInsert = false;
			int inFunction = 0;
			int parensSinceSelect = 0;
			private LinkedList<Integer> parenCounts = new LinkedList<Integer>();
			private LinkedList<Boolean> afterByOrFromOrSelects = new LinkedList<Boolean>();

			int indent = 1;

			StringBuilder result = new StringBuilder();
			StringTokenizer tokens;
			String lastToken;
			String token;
			String lcToken;

			public FormatProcess(String sql) {
				tokens = new StringTokenizer(sql, "()+*/-=<>'`\"[]," + WHITESPACE, true);
			}

			public String perform() {

				result.append(initial);

				while (tokens.hasMoreTokens()) {
					token = tokens.nextToken();
					lcToken = token.toLowerCase();

					if ("'".equals(token)) {
						String t;
						do {
							t = tokens.nextToken();
							token += t;
						} while (!"'".equals(t) && tokens.hasMoreTokens()); // cannot
																			// handle
																			// single
																			// quotes
					} else if ("\"".equals(token)) {
						String t;
						do {
							t = tokens.nextToken();
							token += t;
						} while (!"\"".equals(t));
					}

					if (afterByOrSetOrFromOrSelect && ",".equals(token)) {
						commaAfterByOrFromOrSelect();
					} else if (afterOn && ",".equals(token)) {
						commaAfterOn();
					}

					else if ("(".equals(token)) {
						openParen();
					} else if (")".equals(token)) {
						closeParen();
					}

					else if (BEGIN_CLAUSES.contains(lcToken)) {
						beginNewClause();
					}

					else if (END_CLAUSES.contains(lcToken)) {
						endNewClause();
					}

					else if ("select".equals(lcToken)) {
						select();
					}

					else if (DML.contains(lcToken)) {
						updateOrInsertOrDelete();
					}

					else if ("values".equals(lcToken)) {
						values();
					}

					else if ("on".equals(lcToken)) {
						on();
					}

					else if (afterBetween && lcToken.equals("and")) {
						misc();
						afterBetween = false;
					}

					else if (LOGICAL.contains(lcToken)) {
						logical();
					}

					else if (isWhitespace(token)) {
						white();
					}

					else {
						misc();
					}

					if (!isWhitespace(token)) {
						lastToken = lcToken;
					}

				}
				return result.toString();
			}

			private void commaAfterOn() {
				out();
				indent--;
				newline();
				afterOn = false;
				afterByOrSetOrFromOrSelect = true;
			}

			private void commaAfterByOrFromOrSelect() {
				out();
				newline();
			}

			private void logical() {
				if ("end".equals(lcToken)) {
					indent--;
				}
				newline();
				out();
				beginLine = false;
			}

			private void on() {
				indent++;
				afterOn = true;
				newline();
				out();
				beginLine = false;
			}

			private void misc() {
				out();
				if ("between".equals(lcToken)) {
					afterBetween = true;
				}
				if (afterInsert) {
					newline();
					afterInsert = false;
				} else {
					beginLine = false;
					if ("case".equals(lcToken)) {
						indent++;
					}
				}
			}

			private void white() {
				if (!beginLine) {
					result.append(" ");
				}
			}

			private void updateOrInsertOrDelete() {
				out();
				indent++;
				beginLine = false;
				if ("update".equals(lcToken)) {
					newline();
				}
				if ("insert".equals(lcToken)) {
					afterInsert = true;
				}
			}

			private void select() {
				out();
				indent++;
				newline();
				parenCounts.addLast(Integer.valueOf(parensSinceSelect));
				afterByOrFromOrSelects.addLast(Boolean.valueOf(afterByOrSetOrFromOrSelect));
				parensSinceSelect = 0;
				afterByOrSetOrFromOrSelect = true;
			}

			private void out() {
				result.append(token);
			}

			private void endNewClause() {
				if (!afterBeginBeforeEnd) {
					indent--;
					if (afterOn) {
						indent--;
						afterOn = false;
					}
					newline();
				}
				out();
				if (!"union".equals(lcToken)) {
					indent++;
				}
				newline();
				afterBeginBeforeEnd = false;
				afterByOrSetOrFromOrSelect = "by".equals(lcToken) || "set".equals(lcToken) || "from".equals(lcToken);
			}

			private void beginNewClause() {
				if (!afterBeginBeforeEnd) {
					if (afterOn) {
						indent--;
						afterOn = false;
					}
					indent--;
					newline();
				}
				out();
				beginLine = false;
				afterBeginBeforeEnd = true;
			}

			private void values() {
				indent--;
				newline();
				out();
				indent++;
				newline();
				afterValues = true;
			}

			private void closeParen() {
				parensSinceSelect--;
				if (parensSinceSelect < 0) {
					indent--;
					parensSinceSelect = parenCounts.removeLast().intValue();
					afterByOrSetOrFromOrSelect = afterByOrFromOrSelects.removeLast().booleanValue();
				}
				if (inFunction > 0) {
					inFunction--;
					out();
				} else {
					if (!afterByOrSetOrFromOrSelect) {
						indent--;
						newline();
					}
					out();
				}
				beginLine = false;
			}

			private void openParen() {
				if (isFunctionName(lastToken) || inFunction > 0) {
					inFunction++;
				}
				beginLine = false;
				if (inFunction > 0) {
					out();
				} else {
					out();
					if (!afterByOrSetOrFromOrSelect) {
						indent++;
						newline();
						beginLine = true;
					}
				}
				parensSinceSelect++;
			}

			private static boolean isFunctionName(String tok) {
				final char begin = tok.charAt(0);
				final boolean isIdentifier = Character.isJavaIdentifierStart(begin) || '"' == begin;
				return isIdentifier && !LOGICAL.contains(tok) && !END_CLAUSES.contains(tok)
						&& !QUANTIFIERS.contains(tok) && !DML.contains(tok) && !MISC.contains(tok);
			}

			private static boolean isWhitespace(String token) {
				return WHITESPACE.indexOf(token) >= 0;
			}

			private void newline() {
				result.append("\n");
				for (int i = 0; i < indent; i++) {
					result.append(indentString);
				}
				beginLine = true;
			}
		}

	}

}
