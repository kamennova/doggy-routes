package com.kamennova.doggies;

import com.kamennova.doggies.user.User;
import com.kamennova.doggies.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.cors.CorsConfiguration;

@Configuration
@EnableWebSecurity
@ComponentScan(basePackageClasses = User.class)
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Autowired
    private UserService userService;

    @Bean(name = "passwordEncoder")
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.cors().configurationSource(request -> new CorsConfiguration().applyPermitDefaultValues());
        http.csrf().disable();

        http
                .formLogin()
                .loginPage("/signIn")
                .defaultSuccessUrl("/my-routes")
                .permitAll()
                .and()
                .authorizeRequests()
                .antMatchers("/", "/signUp")
                .permitAll()
                .and()
                .logout()
                .logoutSuccessUrl("/login?logout") // append a query string value
                .and()
                .authorizeRequests()
                .antMatchers("/my-dogs", "/my-routes", "/my-dogs")
                .authenticated();
    }
}
