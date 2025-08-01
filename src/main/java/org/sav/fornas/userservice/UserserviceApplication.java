package org.sav.fornas.userservice;

import lombok.extern.slf4j.Slf4j;
import org.sav.fornas.userservice.property.RsaKeyProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@Slf4j
@SpringBootApplication
public class UserserviceApplication {

	public static void main(String[] args) {
		log.info("------------------------------------Starting--------------------------------------");
		SpringApplication.run(UserserviceApplication.class, args);
		log.info("------------------------------------Started--------------------------------------");
	}

}
