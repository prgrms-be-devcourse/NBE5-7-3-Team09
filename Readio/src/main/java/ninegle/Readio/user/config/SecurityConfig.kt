package ninegle.Readio.user.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configurers.*
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.factory.PasswordEncoderFactories
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.web.cors.CorsConfiguration

@Configuration
open class SecurityConfig (
//    private val jwtTokenProvider: JwtTokenProvider,
//    private val userService: UserService,
//    private val blackListRepository: BlackListRepository
){

    @Bean
    open fun securityFilterChain(
        jwtAuthFilter: JwtAuthFilter,
        http: HttpSecurity,
    ): SecurityFilterChain {
//        val jwtAuthFilter = JwtAuthFilter(jwtTokenProvider, userService, blackListRepository)

        return http
            .formLogin { it.disable() }
            .csrf { it.disable() }

            .cors { cors: CorsConfigurer<HttpSecurity?> ->
                cors.configurationSource { request ->
                    CorsConfiguration().apply {
                        allowedOrigins = listOf("http://localhost:5173") // 프론트엔드 도메인
                        allowedMethods = listOf("GET", "POST", "PUT", "DELETE", "OPTIONS")
                        allowedHeaders = listOf("*")
                        exposedHeaders = listOf("Authorization", "Refresh")
                        allowCredentials = true
                    }
                }
            }

            .httpBasic { it.disable() }
            .authorizeHttpRequests { auth ->
                auth
                    .requestMatchers("/user/login", "/user/signup", "/user/logout").permitAll()
                    .requestMatchers("/admin/**").hasRole("ADMIN")
                    .requestMatchers(HttpMethod.GET, "/books/**").permitAll()
                    .requestMatchers(HttpMethod.GET, "/category").permitAll()
                    .requestMatchers("/viewer/**").permitAll()
                    .anyRequest().hasAnyRole("USER", "ADMIN")
            }
            .sessionManagement { session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            }
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter::class.java)
            .build()
    }

    @Bean
    open fun passwordEncoder(): PasswordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder()
}
