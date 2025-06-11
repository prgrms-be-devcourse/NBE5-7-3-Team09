package ninegle.Readio.global.config;

import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

import ninegle.Readio.ReadioApplication;

@Configuration
@EnableAsync // 비동기 처리를 활성화
public class ApplicationConfig {
	public static void main(String[] args) {
		SpringApplication.run(ReadioApplication.class, args);
	}
}
