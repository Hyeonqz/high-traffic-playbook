package io.github.hyeonqz.config

import de.codecentric.boot.admin.server.config.AdminServerProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.csrf.CookieCsrfTokenRepository

/**
 * Spring Security 설정
 * - Form 로그인: 브라우저를 통한 Admin UI 접근
 * - HTTP Basic: Admin Client의 프로그래밍 방식 등록
 * - CSRF: Client 등록 엔드포인트만 예외 처리
 */
@Configuration
@EnableWebSecurity
class SecurityConfig(
    private val adminServerProperties: AdminServerProperties
) {

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        val adminContextPath = adminServerProperties.contextPath

        http
            .authorizeHttpRequests { authorize ->
                authorize
                    // 공개 엔드포인트: 로그인 페이지, 정적 자원, 헬스체크, 클라이언트 등록
                    .requestMatchers("$adminContextPath/assets/**").permitAll()
                    .requestMatchers("$adminContextPath/login").permitAll()
                    .requestMatchers("$adminContextPath/actuator/health").permitAll()
                    .requestMatchers("$adminContextPath/actuator/info").permitAll()
                    .requestMatchers("$adminContextPath/instances").permitAll()
                    .requestMatchers("$adminContextPath/instances/**").permitAll()
                    // 그 외 모든 엔드포인트는 인증 필요
                    .anyRequest().authenticated()
            }
            .formLogin { form ->
                form
                    .loginPage("$adminContextPath/login")
                    .defaultSuccessUrl("$adminContextPath/", true)
            }
            .logout { logout ->
                logout
                    .logoutUrl("$adminContextPath/logout")
                    .logoutSuccessUrl("$adminContextPath/login?logout")
            }
            .httpBasic { }
            .csrf { csrf ->
                csrf
                    .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                    .ignoringRequestMatchers(
                        "$adminContextPath/instances",
                        "$adminContextPath/instances/**",
                        "$adminContextPath/actuator/**"
                    )
            }

        return http.build()
    }
}