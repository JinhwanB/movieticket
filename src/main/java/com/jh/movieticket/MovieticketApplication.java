package com.jh.movieticket;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement // transactionManagement 활성화
@EnableCaching // 캐싱 기능 활성화
public class MovieticketApplication {

	public static void main(String[] args) {
		SpringApplication.run(MovieticketApplication.class, args);
	}

}
