package ke.driveestate.config;

import ke.driveestate.service.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.session.ConcurrentSessionControlAuthenticationStrategy;
import org.springframework.security.web.authentication.session.CompositeSessionAuthenticationStrategy;
import org.springframework.security.web.authentication.session.SessionFixationProtectionStrategy;
import org.springframework.security.web.authentication.session.RegisterSessionAuthenticationStrategy;
import org.springframework.security.web.session.HttpSessionEventPublisher;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final LoginSuccessHandler loginSuccessHandler;

    public SecurityConfig(UserService userService,
                          PasswordEncoder passwordEncoder,
                          LoginSuccessHandler loginSuccessHandler) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.loginSuccessHandler = loginSuccessHandler;
    }

    /**
     * SessionRegistry tracks all active sessions across all users.
     * Required for concurrent session control.
     */
    @Bean
    public SessionRegistry sessionRegistry() {
        return new SessionRegistryImpl();
    }

    /**
     * Required so Spring knows when HTTP sessions are created/destroyed,
     * keeping the SessionRegistry in sync.
     */
    @Bean
    public HttpSessionEventPublisher httpSessionEventPublisher() {
        return new HttpSessionEventPublisher();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userService);
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authenticationProvider(authenticationProvider())

            // ── URL access rules ─────────────────────────────────────────────
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    "/", "/listings/**", "/listing/**", "/seller/**",
                    "/auth/**", "/api/**", "/css/**", "/js/**",
                    "/images/**", "/h2-console/**", "/error"
                ).permitAll()
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .anyRequest().authenticated()
            )

            // ── Login ────────────────────────────────────────────────────────
            .formLogin(form -> form
                .loginPage("/auth/login")
                .loginProcessingUrl("/auth/login")
                .usernameParameter("email")
                .passwordParameter("password")
                .successHandler(loginSuccessHandler)   // routes admin→/admin, client→/dashboard
                .failureUrl("/auth/login?error=true")
                .permitAll()
            )

            // ── Logout ───────────────────────────────────────────────────────
            .logout(logout -> logout
                .logoutRequestMatcher(new AntPathRequestMatcher("/auth/logout"))
                .logoutSuccessUrl("/auth/login?logout=true")
                .deleteCookies("JSESSIONID")
                .invalidateHttpSession(true)
                .clearAuthentication(true)
                .permitAll()
            )

            // ── Remember-me ──────────────────────────────────────────────────
            .rememberMe(rem -> rem
                .key("driveestate-remember-2024")
                .tokenValiditySeconds(30 * 24 * 3600)   // 30 days
                .rememberMeParameter("remember")
            )

            // ── Concurrent session control ───────────────────────────────────
            // maximumSessions(-1) = UNLIMITED simultaneous logins per user account
            // Different browsers / incognito windows / devices all work independently
            .sessionManagement(session -> session
                .maximumSessions(-1)                    // -1 = no limit
                .sessionRegistry(sessionRegistry())
                .expiredUrl("/auth/login?expired=true")
            )

            // ── CSRF exemptions (H2 console + REST API) ──────────────────────
            .csrf(csrf -> csrf.ignoringRequestMatchers("/h2-console/**", "/api/**"))
            .headers(h -> h.frameOptions(f -> f.sameOrigin()));   // H2 console iframes

        return http.build();
    }
}
