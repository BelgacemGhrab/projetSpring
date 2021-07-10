package tn.esprit.spring.security;
import java.util.Arrays;
import java.util.List;

import javax.sql.DataSource;

/*
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

import tn.esprit.spring.service.ParentService;
@Configuration
@EnableWebSecurity
 
public class WebSecurityConfig extends WebSecurityConfigurerAdapter
{

	@Autowired
	ParentService parentservice ;
	
	
	protected void configure(AuthenticationManagerBuilder auth,DataSource dataSource) throws Exception{
	auth.jdbcAuthentication()
		.dataSource(dataSource)
		.usersByUsernameQuery("select email as principal , password as credentials,true from users where email= ?")
		.authoritiesByUsernameQuery("select email as principal,role from users where email =? ")
		.rolePrefix("ROle");
	}
	public void configure(HttpSecurity http) throws Exception{
		http
		.authorizeRequests()  
			.anyRequest()
				.authenticated()
				.and()
				.httpBasic();
					
						
					
	}

}
*/
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;


	@Autowired
	private JwtRequestFilter jwtRequestFilter;
	
	private static final String[] AUTH_WHITELIST = {
			"/api/parent/auth/{email}/{password}",
			"/api/kindergarten/add",
			"/api/kindergarten/all",
			"/api/image/{id}",
			"/authenticate",
			"/api/test",
			"/api/parent/add",
			"/css/**",
			"/js/**",
			"/images/**"
	};
	
	@Autowired
	protected void configure(AuthenticationManagerBuilder auth,DataSource dataSource) throws Exception{
		auth.jdbcAuthentication()
			.dataSource(dataSource)
			.usersByUsernameQuery("select email as principal , password as credentials ,true from users where email= ?")
			.authoritiesByUsernameQuery("select email as principal,role as role from users u where email =?")
			.rolePrefix("ROLE_");
		}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	@Override
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}

	@Override
	protected void configure(HttpSecurity httpSecurity) throws Exception {
		  
        
		// We don't need CSRF for this example
		httpSecurity.httpBasic().disable()
		.csrf().disable()
		.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
		.and()
		.cors() 
		.and()
		.authorizeRequests()
		.antMatchers(AUTH_WHITELIST).permitAll()
		.antMatchers("/files", "/availability/**").permitAll()

		.anyRequest().authenticated();

		// Add a filter to validate the tokens with every request
		httpSecurity.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);
	}
}