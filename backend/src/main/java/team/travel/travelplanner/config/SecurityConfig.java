package team.travel.travelplanner.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.context.DelegatingSecurityContextRepository;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.RequestAttributeSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;
import team.travel.travelplanner.service.UsersDetailService;

import java.util.Objects;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Value("${server.allowed-origin-patterns:#{null}}")
    private String allowedOriginPatterns;

    @Autowired
    private UsersDetailService usersDetailService;

    @Bean
    public MvcRequestMatcher.Builder mvc(HandlerMappingIntrospector introspector) {
        return new MvcRequestMatcher.Builder(introspector);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, MvcRequestMatcher.Builder mvc, AuthenticationManager authenticationManager) throws Exception {
        http.cors(Customizer.withDefaults());
        http.csrf(AbstractHttpConfigurer::disable); // TODO figure out CSRF
        http.authenticationManager(authenticationManager);
        http.authorizeHttpRequests(
                authorize -> authorize
                        .requestMatchers(mvc.pattern("/api/auth/register")).permitAll()
                        .requestMatchers(mvc.pattern("/api/auth/login")).permitAll()
                        .requestMatchers(mvc.pattern("/api/auth/logout")).permitAll()
                        .requestMatchers(mvc.pattern("/actuator/**")).permitAll() // Only accessible through the management port
                        .anyRequest().authenticated()
        );
        http.securityContext(securityContext -> securityContext
                .requireExplicitSave(true)
                .securityContextRepository(securityContextRepository()));
        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.addAllowedOriginPattern(Objects.requireNonNullElse(allowedOriginPatterns, "http://localhost:[*]"));
        corsConfiguration.addAllowedMethod(CorsConfiguration.ALL);
        corsConfiguration.setAllowCredentials(true);
        // Allow all headers and default max age
        corsConfiguration.applyPermitDefaultValues();

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfiguration);
        return source;
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return usersDetailService;
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http, PasswordEncoder encoder, UsersDetailService service) throws Exception {
        AuthenticationManagerBuilder builder = http.getSharedObject(AuthenticationManagerBuilder.class);

        DaoAuthenticationProvider daoProvider = new DaoAuthenticationProvider(encoder);
        daoProvider.setUserDetailsService(service);
        builder.authenticationProvider(daoProvider);

        return builder.build();
    }

    @Bean
    public SecurityContextRepository securityContextRepository() {
        return new DelegatingSecurityContextRepository(
                new RequestAttributeSecurityContextRepository(),
                new HttpSessionSecurityContextRepository()
        );
    }


    @Bean
    public PasswordEncoder passwordEncoder() {
        return Argon2PasswordEncoder.defaultsForSpringSecurity_v5_8();
    }
}
