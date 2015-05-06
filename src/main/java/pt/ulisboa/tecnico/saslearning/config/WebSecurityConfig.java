package pt.ulisboa.tecnico.saslearning.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.servlet.configuration.EnableWebMvcSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;

import pt.ulisboa.tecnico.saslearning.security.SASLearningUserDetailsService;

@Configuration
@EnableWebMvcSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests().antMatchers("/signup", "/signupTeacher")
				.permitAll().anyRequest().authenticated().and().formLogin()
				.loginPage("/login").permitAll().and().logout()
				.logoutUrl("/logout").permitAll().and().csrf().disable();
	}

	@Override
	protected void configure(AuthenticationManagerBuilder auth)
			throws Exception {
		auth.userDetailsService(userDetailsService());

	}

	@Override
	@Bean
	protected UserDetailsService userDetailsService() {
		return new SASLearningUserDetailsService();
	}
}
