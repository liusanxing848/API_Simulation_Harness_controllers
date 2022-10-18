package com.blizzard.ash.admin.templates;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import software.amazon.awssdk.enhanced.dynamodb.AttributeConverter;
import software.amazon.awssdk.enhanced.dynamodb.AttributeValueType;
import software.amazon.awssdk.enhanced.dynamodb.EnhancedType;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

public record RequestTemplate(
    String method
    // The missing piece is the route/url, which will be supplied to create the actual Mock
) {
    private static final ObjectMapper om = new ObjectMapper();

    public static final class Converter implements AttributeConverter<RequestTemplate>{

        @Override
        public AttributeValue transformFrom(RequestTemplate input) {
            try {
                return AttributeValue.fromS(om.writeValueAsString(input));
            } catch (JsonProcessingException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }

        @Override
        public RequestTemplate transformTo(AttributeValue input) {
            try {
                return om.readValue(input.s(), RequestTemplate.class);
            } catch (JsonProcessingException jpe) {
                jpe.printStackTrace();
                throw new RuntimeException(jpe);
            }
        }

        @Override
        public EnhancedType<RequestTemplate> type() {
            return EnhancedType.of(RequestTemplate.class);
        }

        @Override
        public AttributeValueType attributeValueType() {
            return AttributeValueType.S;
        }

    }
}
