import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;

@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
			.csrf().disable()
			.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
			.and()
			.addFilterBefore(steamOpenIdFilter(), SteamOpenIdFilter.class)
			.authorizeRequests()
			.antMatchers("/steam/login").permitAll()
			.antMatchers("/steam/verify").permitAll()
			.anyRequest().authenticated();
	}

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.authenticationProvider(steamOpenIdAuthProvider());
	}

	@Bean
	public SteamOpenIdFilter steamOpenIdFilter() throws Exception {
		SteamOpenIdFilter filter = new SteamOpenIdFilter("/steam/verify");
		filter.setAuthenticationManager(authenticationManager());
		return filter;
	}

	@Bean
	public SteamOpenIdAuthProvider steamOpenIdAuthProvider() {
		return new SteamOpenIdAuthProvider();
	}

}