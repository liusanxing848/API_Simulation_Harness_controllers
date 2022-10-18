package com.blizzard.ash.admin.security;

import java.util.stream.Stream;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfiguration {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity security, AuthConfig config, Environment environment)
        throws Exception {

        var profile = Stream.of(environment.getActiveProfiles())
            .findFirst()
            .orElse("LOCAL");
        
        // Re-enable Basic authentication. We also supply a new Entry Point
        // so the request to the browser isn't a WWW-Challenge header, because
        // that makes users sad.
        security
            .authorizeHttpRequests()
                // TODO: We need better scopes:
                .mvcMatchers("/status").hasAuthority("SCOPE_ash.status") // ash.inspect for read-only
                .mvcMatchers("/definitions").hasAuthority("SCOPE_ash.status") // ash.modify to edit stored state
                .mvcMatchers("/templates").hasAuthority("SCOPE_ash.status")
                .mvcMatchers("/client").hasAuthority("SCOPE_ash.testcase:modify") // ash.operate for assigning tests to clients
                // ash.admin for any security/admin-level concerns
                .anyRequest().authenticated()
            .and()
            // Add 'logout' just so we can disable it. We can make this fancier in the future
            .logout()
                .logoutRequestMatcher(req -> false)
            .and()
            // TODO: Use Blizzard accounts and the Blizzard OAuth server like we were an OAuth2 Client
            .oauth2ResourceServer()
                .opaqueToken()
                .introspectionClientCredentials(config.oauth().get(profile).clientId(), config.oauth().get(profile).password())
                .introspectionUri("https://%s/v2/check_token".formatted(config.oauthEndpoint()));

        return security.build();
    }
}
