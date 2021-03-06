package com.arman.armaNote.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.session.SessionManagementFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;

	@Autowired
	private DataSource dataSource;

	@Autowired
	private JwtAuthenticationEntryPoint unauthorizedHandler;

	@Bean
	CorsFilter corsFilter() {
		CorsFilter filter = new CorsFilter();
		return filter;
	}

	@Bean
	public JwtAuthenticationFilter authenticationTokenFilterBean() throws Exception {
		return new JwtAuthenticationFilter();
	}

	@Bean
	public BCryptPasswordEncoder bCryptPasswordEncoder() {
		BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
		return bCryptPasswordEncoder;
	}

	@Bean
	public AuthenticationManager customAuthenticationManager() throws Exception {
		// https://stackoverflow.com/questions/37970709/could-not-autowire-field-private-org-springframework-security-authentication-au
		return authenticationManager();
	}

	@Value("${spring.queries.users-query}")
	private String userQuery;

	@Value("${spring.queries.users-by-username-query}")
	private String usernameQuery;

	@Value("${spring.queries.roles-query}")
	private String rolesQuery;

	@Override
	public void configure(AuthenticationManagerBuilder auth) throws Exception {
		// Check this portions requirement now (after adding jwt part, is it still
		// required the same
		// way or we can comment it)
		// No!!! Actually we can't comment as its giving error, so understand thoroughly
		// how and where
		// (exactly) this code is in use
		auth.jdbcAuthentication().usersByUsernameQuery(userQuery).authoritiesByUsernameQuery(rolesQuery)
				.dataSource(dataSource).passwordEncoder(bCryptPasswordEncoder);

		// This is just a trick to login trough username. But not the correct way.
		// (Change it later)
		// Better to customize userDetailsService's loadUserByUsername(String username)
		// method.
		// means, create own class, extend with userDetailsServiceImpl, then override
		// loadUserByUsername
		// method to get user by using both email and username
		// https://stackoverflow.com/questions/14122756/spring-security-using-both-username-or-email
		auth.jdbcAuthentication().usersByUsernameQuery(usernameQuery).authoritiesByUsernameQuery(rolesQuery)
				.dataSource(dataSource).passwordEncoder(bCryptPasswordEncoder);
	}

	public void configure(HttpSecurity http) throws Exception {
		http.httpBasic().and().addFilterBefore(corsFilter(), SessionManagementFilter.class) // adds your custom
																							// CorsFilter
				.authorizeRequests().antMatchers("/", "/v2/api-docs", // swagger
						"/webjars/**", // swagger-ui webjars
						"/swagger-resources/**", // swagger-ui resources
						"/configuration/**", // swagger configuration
						"/*.html", // For swagger
						"/favicon.ico", // For swagger
						"/**/*.html", // For swagger
						"/**/*.css", // For swagger
						"/**/*.js" // For swagger
				).permitAll().antMatchers("/token/*", "/login").permitAll().antMatchers("/registration").permitAll()
				// .antMatchers("/api/**").permitAll()
				.antMatchers("/api/**")
				.hasAnyAuthority("ADMIN", "WEBSERVICE", "USER") // gave rights to user also so
																// that we can make webservice
																// calls from ajax
				.antMatchers("/admin/**").hasAuthority("ADMIN").antMatchers("/user/**").hasAnyAuthority("ADMIN", "USER")
				.anyRequest().authenticated().and().exceptionHandling().authenticationEntryPoint(unauthorizedHandler)
				.and().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and().csrf().disable()
				 // csrf disabling is important especially when we have jwt added in browser cookies (cookies are prone to csrf attacks)
				.formLogin().loginPage("/login").failureUrl("/login?error=true").defaultSuccessUrl("/user/home")
				.usernameParameter("email").passwordParameter("password")
				// probably this logout part is not required for our only-ws application now,
				// but keeping it unchanged for now.
				.and().logout().logoutRequestMatcher(new AntPathRequestMatcher("/logout")).logoutSuccessUrl("/").and()
				.exceptionHandling().accessDeniedPage("/access-denied").and().headers().frameOptions().sameOrigin();																														
				// last line is added bcz otherwise I was getting "X-frame-option set to deny" error in iframe. So I followed:
				// https://docs.spring.io/spring-security/site/docs/current/reference/html/headers.html
		http.addFilterBefore(authenticationTokenFilterBean(), UsernamePasswordAuthenticationFilter.class);
	}

	@Override
	public void configure(WebSecurity web) throws Exception {
		// this portion is added after reading
		// https://stackoverflow.com/questions/24726218/spring-security-refused-to-execute-script-from
		// and
		// https://stackoverflow.com/questions/24916894/serving-static-web-resources-in-spring-boot-spring-security-application/24920752#24920752
		web.ignoring().antMatchers("/js/**").antMatchers("/css/**"); // can add more directories later.
	}

}
