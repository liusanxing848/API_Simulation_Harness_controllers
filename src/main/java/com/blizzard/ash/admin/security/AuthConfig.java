package com.blizzard.ash.admin.security;

import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("auth")
public record AuthConfig(
    String oauthEndpoint,
    Map<String, Client> oauth,
    String pathToHostMappingFilename
) {
    
    public static record Client(
        String clientId,
        String password
    ) {

    }
}
