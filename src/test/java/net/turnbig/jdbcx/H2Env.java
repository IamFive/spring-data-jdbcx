/**
 * @(#)H2Env.java 2016年11月28日
 *
 * Copyright 2008-2016 by Woo Cupid.
 * All rights reserved.
 * 
 */
package net.turnbig.jdbcx;

import org.flywaydb.core.Flyway;
import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author Woo Cupid
 * @date 2016年11月28日
 * @version $Revision$
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Application.class)
public class H2Env {
	
	@Autowired
	Flyway flyway;
	
	@Before
	public void setupH2() {
		flyway.clean();
		flyway.migrate();
	}

	@After
	public void clean() {
		flyway.clean();
	}

}
