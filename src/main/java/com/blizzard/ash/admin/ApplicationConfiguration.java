package com.blizzard.ash.admin;

import java.net.URI;
import java.net.URISyntaxException;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;

import io.lettuce.core.RedisClient;

import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.regions.Region;

// TODO: Get an OpenAPI-generating endpoint going - Spring may already have done this for us...
@Configuration
public class ApplicationConfiguration {
    @Bean
    public ObjectMapper objectMapper() {
        var om = new ObjectMapper()
            .registerModules(
                new ParameterNamesModule(),
                new Jdk8Module(),
                new JavaTimeModule()
            );
        return om;
    }

    @Bean
    public RedisClient redisClient() {
        return RedisClient.create("redis://cache:6379/0");
    }

    @Bean
    public DynamoDbEnhancedClient dynamoClient() throws URISyntaxException {
        var db = DynamoDbClient.builder()
            .region(Region.US_WEST_2)
            // TODO: When we're getting ready for DevOps, figure out the best ways to inject credentials
            .credentialsProvider(() -> new AwsCredentials() {
                @Override
                public String accessKeyId() {
                    return "Doesn't Matter For Local";
                }

                @Override
                public String secretAccessKey() {
                    return "Doesn't Matter For Local";
                }
            })
            // TODO: Only use this in Dev builds...
            .endpointOverride(new URI("http://db:8000"))
            .build();
        
        return DynamoDbEnhancedClient.builder().dynamoDbClient(db).build();
    }
}
