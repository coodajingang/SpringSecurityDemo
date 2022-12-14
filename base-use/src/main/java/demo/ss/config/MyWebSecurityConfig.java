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
                                .loginProcessingUrl("/dologin") // ???????????????form url
                                .loginPage("/login.html")   // ?????????????????????
                                .failureForwardUrl("/login") // ??????????????????url
                                .successForwardUrl("/login") // ??????????????????url
                                .defaultSuccessUrl("/hello") // ?????????????????????URL
                                .failureUrl("/error.html")
                                .successHandler(new SuccessLoginHandler()) // for rest api login
                                .failureHandler(new FailureLoginHandler())
                        /*
                        1?????????????????????????????? ??????defaultSuccessUrl, successForwardUrl, failureForwardUrl;
                            successHandler, failureHandler ???????????????????????????????????????????????????????????????
                        2???????????????????????????
                            ??????successHandler???failureHandler ????????????????????????????????? ??????????????????url
                         */
                )
                .csrf().disable()
                .logout((logout) -> logout
                        .logoutSuccessUrl("/logout.html")
                        .logoutUrl("/doLogout")
                        .addLogoutHandler((req, res, auth) -> log.info("Successfully logged out:   {}", auth)) // LogoutHandler  ????????????????????????????????????????????????
                        .logoutSuccessHandler((req, res, auth) -> log.info("sSuccessfully logged out:   {}", auth))
                        /*
                        1. logoutSuccessUrl logoutSuccessHandler ??????????????? ????????????????????????????????????????????????????????????????????????
                        2. addLogoutHandler ????????????????????????????????????handler????????????logoutSuccessHandler ????????????LogoutFilter??????
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
