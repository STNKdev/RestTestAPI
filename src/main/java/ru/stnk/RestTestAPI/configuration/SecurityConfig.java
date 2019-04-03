package ru.stnk.RestTestAPI.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.sql.DataSource;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    DataSource dataSource;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {

        auth.jdbcAuthentication().dataSource(dataSource)
                //проверка существования пользователя и его состояние enable_user
                .usersByUsernameQuery("select email,password,enable_user from users where email=?")
                //запрос роли пользователя
                .authoritiesByUsernameQuery("select user_email,role_name from user_roles where user_email=?")
                //нужно для проверки пароля
                .passwordEncoder(new BCryptPasswordEncoder());

    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http
                //.sessionManagement()
                //.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                //.and()
                //HTTP Basic authentication
                    .httpBasic()
                .and()
                    .rememberMe()
                .and()
                    .authorizeRequests()
                    .antMatchers("/reg-start").permitAll()
                    .antMatchers(HttpMethod.GET, "/add-role").permitAll()
                    .antMatchers(HttpMethod.GET, "/hello").authenticated()
                    .antMatchers("/logout").authenticated()
                    .anyRequest().authenticated()
                .and()
                    .csrf()
                    .disable()
                    .formLogin()
                    .disable()
                .logout();

    }

    /*
     * Взято отсюда
     * https://alexkosarev.name/2016/05/19/spring-security-token-authentication-part-1/
     */
    /*@Bean
    public RequestHeaderAuthenticationFilter requestHeaderAuthenticationFilter() throws Exception {
        RequestHeaderAuthenticationFilter requestHeaderAuthenticationFilter = new RequestHeaderAuthenticationFilter();
        requestHeaderAuthenticationFilter.setPrincipalRequestHeader("X-AUTH-TOKEN");
        requestHeaderAuthenticationFilter.setAuthenticationManager(authenticationManager());
        //requestHeaderAuthenticationFilter.setExceptionIfHeaderMissing(false);

        return requestHeaderAuthenticationFilter;
    }

    @Bean
    public PreAuthenticatedAuthenticationProvider preAuthenticatedAuthenticationProvider() {
        PreAuthenticatedAuthenticationProvider preAuthenticatedAuthenticationProvider = new PreAuthenticatedAuthenticationProvider();
        preAuthenticatedAuthenticationProvider.setPreAuthenticatedUserDetailsService(new UserDetailsByNameServiceWrapper<>(userDetailsService()));

        return preAuthenticatedAuthenticationProvider;
    }*/



}