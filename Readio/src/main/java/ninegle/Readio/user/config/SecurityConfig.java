package ninegle.Readio.user.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

import lombok.RequiredArgsConstructor;
import ninegle.Readio.user.repository.BlackListRepository;
import ninegle.Readio.user.service.JwtTokenProvider;
import ninegle.Readio.user.service.UserService;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

	@Bean
	public SecurityFilterChain securityFilterChain(
		HttpSecurity http,
		JwtTokenProvider jwtTokenProvider,
		UserService userService,
		BlackListRepository blackListRepository
	) throws Exception {
		JwtAuthFilter jwtAuthFilter = new JwtAuthFilter(jwtTokenProvider, userService, blackListRepository);

		return http
			.formLogin(form -> form.disable())
			.csrf(csrf -> csrf.disable())

			.cors(cors -> cors.configurationSource(request -> {
				CorsConfiguration config = new CorsConfiguration();
				config.setAllowedOrigins(List.of("http://localhost:5173")); // ✅ 프론트엔드 도메인
				config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
				config.setAllowedHeaders(List.of("*"));
				config.setExposedHeaders(List.of("Authorization", "Refresh")); //
				config.setAllowCredentials(true);
				return config;
			}))

			.httpBasic(httpBasic -> httpBasic.disable())
			.authorizeHttpRequests(auth -> {
				auth
					.requestMatchers("/user/login", "/user/signup", "/user/logout").permitAll()
					.requestMatchers("/admin/**").hasRole("ADMIN")
					.requestMatchers(HttpMethod.GET, "/books/**").permitAll()
					.requestMatchers(HttpMethod.GET, "/category").permitAll()
					.requestMatchers("/viewer/**").permitAll().anyRequest()
					.hasAnyRole("USER", "ADMIN"); //나머지 요청은 USER나 ADMiN 권한을 가져야 접근 가능
			})

			.sessionManagement(session -> session
				.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
			.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
			.build();
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return PasswordEncoderFactories.createDelegatingPasswordEncoder();
	}

}
