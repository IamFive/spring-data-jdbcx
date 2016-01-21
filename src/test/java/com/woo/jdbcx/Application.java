package com.woo.jdbcx;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application implements CommandLineRunner {

	private static final Logger logger = LoggerFactory.getLogger(Application.class);

	// @Autowired
	// MemberRepository memberRep;

	public static void main(String[] args) {
		SpringApplication.run(new Object[] { Application.class }, args);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.boot.CommandLineRunner#run(java.lang.String[])
	 */
	@Override
	public void run(String... args) throws Exception {
		logger.info("start spring boot application");

		// Member sample = new Member();
		// sample.setName("lol2");
		//
		// List<Member> members = memberRep.queryForListBean("select * from member where name = :name", sample,
		// Member.class);
		// for (Member member : members) {
		// logger.info("{}", member);
		// }

		// KeyHolder keyHolder = new GeneratedKeyHolder();
		// int update = memberRep.update("insert into member (name) values
		// (:name)", param, keyHolder);
		// logger.info("{}", update, keyHolder.getKey());
	}

}
