package demo.ss.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Slf4j
@EnableWebSecurity(debug = true)
public class MyWebSecurityConfig {

    @Bean
    public UserDetailsService getUserDetailsService() {
        User.UserBuilder users = User.withDefaultPasswordEncoder();
        InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager();
        manager.createUser(users.username("user").password("123").roles("USER").build());
        manager.createUser(users.username("admin").password("password").roles("USER","ADMIN").build());
        return manager;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .authorizeRequests(authorize ->
                        authorize
                                .antMatchers("/error.html").permitAll()
                                .antMatchers("/*.html").permitAll()
                        .anyRequest().authenticated()
                )
                //.httpBasic(Customizer.withDefaults())
                .formLogin(customerize ->
                        customerize.successHandler((req, res, auth) -> {
                                    log.info("{} authenticated", auth.getPrincipal());
                                })
                                .failureHandler((req, res, exp) -> {
                                    log.error("authentication failed: {}", exp.getMessage());
                                })
                                .loginProcessingUrl("/dologin") // 自定义登陆form url
                                .loginPage("/login.html")   // 自定义登陆页面
                                .failureForwardUrl("/login") // 失败时跳转的url
                                .successForwardUrl("/login") // 成功时跳转的url
                                .defaultSuccessUrl("/hello") // 成功时重定向的URL
                                .failureUrl("/error.html")
                                .successHandler(new SuccessLoginHandler()) // for rest api login
                                .failureHandler(new FailureLoginHandler())
                        /*
                        1、非前后端分离项目： 使用defaultSuccessUrl, successForwardUrl, failureForwardUrl;
                            successHandler, failureHandler 不要用来写响应，只用来做写日志等事件处理。
                        2、前后端分离项目：
                            使用successHandler，failureHandler 直接在里面写响应返回， 不要配置跳转url
                         */
                )
                .csrf().disable()
                .logout((logout) -> logout
                        .logoutSuccessUrl("/logout.html")
                        .logoutUrl("/doLogout")
                        .addLogoutHandler((req, res, auth) -> log.info("Successfully logged out:   {}", auth)) // LogoutHandler  用于处理成功注销后的一些清理动作
                        .logoutSuccessHandler((req, res, auth) -> log.info("sSuccessfully logged out:   {}", auth))
                        /*
                        1. logoutSuccessUrl logoutSuccessHandler 是互斥的， 后设置生效，可以认为一个用于不分离，一个用于分离
                        2。addLogoutHandler 可以增加多个注销后调用的handler，但先于logoutSuccessHandler 调用，见LogoutFilter源码
                         */
                )
                .authenticationProvider(new AuthenticationProvider() {
                    @Override
                    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
                        log.info("In customer authenticationProvider");
                        return authentication;
                    }

                    @Override
                    public boolean supports(Class<?> authentication) {
                        return true;
                    }
                })
                .build();
    }

}
