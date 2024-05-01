package com.ps.config;

import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SpringSecurityConfig {

    @Bean
    public UserDetailsService userDetailsService(){

        UserDetails prabhul = User.builder()
                .username("prabhul")
                .password(passwordEncoder().encode("password"))
                .roles("USER")
                .build();

        UserDetails admin = User.builder()
                .username("admin")
                .password(passwordEncoder().encode("admin"))
                .roles("ADMIN")
                .build();

        return new InMemoryUserDetailsManager(prabhul, admin);
    }
    @Bean
    WebServerFactoryCustomizer<ConfigurableServletWebServerFactory> enableDefaultServlet() {
        return (factory) -> factory.setRegisterDefaultServlet(true);
    }
    @Bean
    public static PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(
                        auth -> auth.requestMatchers("/","/login","/WEB-INF/jsp/**").permitAll()
                                .requestMatchers("/welcome","/home").hasAnyRole("ADMIN","USER")
                                .anyRequest().authenticated()
                )
                .formLogin(
                        login -> login.loginPage("/login")
                                .usernameParameter("name").passwordParameter("password").permitAll()
                                .loginProcessingUrl("/welcome")
                                .defaultSuccessUrl("/home")
                )
                .logout(logout -> logout.permitAll())
                .exceptionHandling(eh -> eh.accessDeniedPage("/403"));

        return http.build();
/*        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth ->
                        auth.requestMatchers( "/",""/login","/WEB-INF/jsp/**).permitAll()
                        .anyRequest().authenticated())
                .formLogin(login -> login.loginPage("/login")
                        //.usernameParameter("u").passwordParameter("p")
                        .loginProcessingUrl("/welcome")
                        .defaultSuccessUrl("/welcome")
                        .permitAll())
                .logout(logout -> logout.permitAll());

        return http.build();*/
    }

}
