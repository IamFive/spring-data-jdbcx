/**
 * @(#)PageableTest.java 2016年1月28日
 *
 * Copyright 2008-2016 by Woo Cupid.
 * All rights reserved.
 * 
 */
package net.turnbig.jdbcx.test;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;

/**
 * @author Woo Cupid
 * @date 2016年1月28日
 * @version $Revision$
 */
public class PageableTest {
	
	private static final Logger logger = LoggerFactory.getLogger(PageableTest.class);

	
	@Test
	public void offsetTest() {
		PageRequest pr = new PageRequest(1, 10);
		int offset = pr.getOffset();
		int pageNumber = pr.getPageNumber();
		int limit = pr.getPageSize();
		logger.info("offset : {}, page number : {}", offset, pageNumber);
		Assert.assertEquals(offset, 10);
		Assert.assertEquals(limit, 10);
	}

}
