package com.blizzard.ash.admin.definitions;

import java.util.HashMap;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import software.amazon.awssdk.enhanced.dynamodb.AttributeConverter;
import software.amazon.awssdk.enhanced.dynamodb.AttributeValueType;
import software.amazon.awssdk.enhanced.dynamodb.EnhancedType;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

public record SerializedMockResponse(
    int status,
    String body,
    HashMap<String, String> headers,
    int delayDurationMs,
    // TODO: Actual intent - a response is either delayed and proxied, or delayed and returned.
    boolean proxied
) {
    // TODO: This works but it makes me uncomfortable...
    private static ObjectMapper om = new ObjectMapper();
    
    public static final class Converter implements AttributeConverter<SerializedMockResponse> {

        @Override
        public AttributeValue transformFrom(SerializedMockResponse input) {
            try {
                return AttributeValue.fromS(om.writeValueAsString(input));
            } catch (JsonProcessingException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }

        @Override
        public SerializedMockResponse transformTo(AttributeValue input) {
            try {
                return om.readValue(input.s(), SerializedMockResponse.class);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }

        @Override
        public EnhancedType<SerializedMockResponse> type() {
            return EnhancedType.of(SerializedMockResponse.class);
        }

        @Override
        public AttributeValueType attributeValueType() {
            return AttributeValueType.S;
        }
    }
}
