package com.ll.TeamSteam.global.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.web.client.RestTemplate;

import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import jakarta.servlet.DispatcherType;
import lombok.RequiredArgsConstructor;

// @Configuration
// @EnableWebSecurity
// @EnableMethodSecurity(prePostEnabled = true)
// public class SecurityConfig {
//
//     @Bean
//     SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//         http
//             .authorizeRequests(authorize ->
//                 authorize.mvcMatchers("/**").permitAll()
//             )
//             .formLogin(formLogin ->
//                 formLogin
//                     .loginPage("/user/login")
//                     .defaultSuccessUrl("/", true)
//             )
//             .logout(logout ->
//                 logout
//                     .logoutUrl("/user/logout")
//                     .logoutSuccessUrl("/")
//                     .invalidateHttpSession(true)
//             );
//         return http.build();
//     }
// }
// @Configuration
// @EnableWebSecurity
// public class SecurityConfig {
//     @Bean
//     public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//         http
//             .authorizeHttpRequests(authorize -> authorize
//                 .requestMatchers("/**").permitAll()
//                 .anyRequest().authenticated()
//             )
//             .formLogin(formLogin -> formLogin
//                 .loginPage("/user/login")
//                 .permitAll()
//             )
//             .rememberMe(Customizer.withDefaults());
//
//         return http.build();
//     }

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {
    @Bean // @Bean 애노테이션을 이용하여 SecurityFilterChain 객체 생성
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    /* SecurityFilterChain 객체 : Security가 HTTP 요청을 처리할 때, 적용되는 필터 체인을 나타내는 객체
       FilterChain :  HTTP 요청이 처리될 때, 이 요청을 처리하는 필터들의 연속적인 체인을 의미
                      HTTP 요청이 들어오면 체인의 시작점에서부터 필터들이 차례대로 실행되고, 각 필터는 이전 필터의 결과를 전달받아 처리를 수행하고 다음 필터에게 결과 전달
                      체인의 끝까지 실행된 후, 서블릿이 요청을 처리하고, 응답을 반환
       HttpSecurity 객체 : WebSecurityConfigurerAdapter 상속한 구성 클래스에서 configure(HttpSecurity http) 메서드를 오버라이딩하여 생성
                           상속받지 않아도 애너테이션을 사용해 활성화할 수 있음 */

        http.authorizeHttpRequests().requestMatchers(
                        // authorizeRequests() : HTTP 요청에 대한 보안 설정을 시작하는 메서드
                        new AntPathRequestMatcher("/**")).permitAll()
                // 인증되지 않은 요청을 허락한다는 의미 -> 로그인을 하지 않아도 모든 페이지에 접근할 수 있음
                .and()
                // 스프링 시큐리티의 로그인 설정을 담당하는 부분
                .formLogin()
                // 로그인 페이지와 로그인 성공/실패 처리에 대한 설정을 추가하는 메서드
                .loginPage("/user/login")
                // 로그인 페이지의 URL "user/login"으로 설정
                .defaultSuccessUrl("/")
                // 성공 시에 이동하는 default 페이지 URL은 "/" -> redierct로 "question/list"(질문 목록 페이지)로 이동
                .and()
                .logout()
                // 로그아웃 처리에 대한 설정을 추가하는 메서드
                .logoutRequestMatcher(new AntPathRequestMatcher("/user/logout"))
                // 로그아웃 URL을 "user/logout"으로 설정. AntPathRequestMatcher 클래스는 특정 URL과 일치하는지 확인하는 데 사용
                .logoutSuccessUrl("/")
                // 로그 아웃 성공 시에 이동하는 URL은 "/" -> redierct로 "question/list"(질문 목록 페이지)로 이동
                .invalidateHttpSession(true)
        // invalidateHttpSession는 사용자가 로그아웃할 때 사용자의 HTTP 세션을 무효화하는 데 사용 -> logout 메서드와 함께 사용해야 함
        ;
        return http.build();
        /* 스프링 시큐리티의 HTTP 필터 체인을 반환. 이 필터 체인은 요청이 웹 애플리케이션에 들어오면 실행(필터 체인은 요청이 보안 요구 사항을 충족하는지 확인하는 데 사용)
           build : 필터 체인을 구성하는데 사용
           http : 필터 체인을 구성하는 데 사용(http는 스프링 시큐리티의 HTTP 필터 체인을 나타냄) */
    }
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
