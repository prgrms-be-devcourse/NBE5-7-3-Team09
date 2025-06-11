package ninegle.Readio;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
@ConfigurationPropertiesScan
@EnableFeignClients(basePackages = "ninegle.Readio.tosspay") // TossApiClient 있는 패키지
public class ReadioApplication {

	public static void main(String[] args) {
		SpringApplication.run(ReadioApplication.class, args);
	}

}
