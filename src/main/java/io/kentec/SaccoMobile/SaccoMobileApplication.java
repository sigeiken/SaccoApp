package io.kentec.SaccoMobile;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication(scanBasePackages = "io.kentec.SaccoMobile")
@EnableSwagger2
@EnableScheduling
public class SaccoMobileApplication {

	private static final Logger LOGGER = LoggerFactory.getLogger(SaccoMobileApplication.class);

	@Bean
	public RestTemplate getRestTemplate(){
		return new RestTemplate();
	}
	public static void main(String[] args) {

		SpringApplication.run(SaccoMobileApplication.class, args);
		LOGGER.info("Sacco Mobile Application starting...");
	}

}
