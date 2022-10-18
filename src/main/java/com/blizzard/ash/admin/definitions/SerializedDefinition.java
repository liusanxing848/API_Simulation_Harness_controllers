package com.blizzard.ash.admin.definitions;

import com.blizzard.ash.shared.mocks.RequestMatch;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import software.amazon.awssdk.enhanced.dynamodb.AttributeConverter;
import software.amazon.awssdk.enhanced.dynamodb.AttributeValueType;
import software.amazon.awssdk.enhanced.dynamodb.EnhancedType;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbConvertedBy;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbImmutable;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

@DynamoDbImmutable(builder = SerializedDefinition.Builder.class)
public record SerializedDefinition(
    @DynamoDbConvertedBy(value = SerializedDefinition.RequestConverter.class)
    RequestMatch requestConfig,

    @DynamoDbConvertedBy(value = SerializedMockResponse.Converter.class)
    SerializedMockResponse responseConfig
) {
    // TODO: this works but it makes me uncomfortable...
    private static ObjectMapper om = new ObjectMapper();

    public static final class Builder {
        RequestMatch request;
        SerializedMockResponse response;

        public Builder() { }

        public Builder setRequestConfig(RequestMatch request) {
            this.request = request;
            return this;
        }

        public Builder setResponseConfig(SerializedMockResponse response) {
            this.response = response;
            return this;
        }

        public SerializedDefinition build() {
            return new SerializedDefinition(request, response);
        }
    }

    public static final class RequestConverter implements AttributeConverter<RequestMatch> {

        @Override
        public AttributeValue transformFrom(RequestMatch input) {
            try {
                return AttributeValue.fromS(om.writeValueAsString(input));
            } catch (JsonProcessingException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }

        @Override
        public RequestMatch transformTo(AttributeValue input) {
            try {
                return om.readValue(input.s(), RequestMatch.class);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }

        @Override
        public EnhancedType<RequestMatch> type() {
            return EnhancedType.of(RequestMatch.class);
        }

        @Override
        public AttributeValueType attributeValueType() {
            return AttributeValueType.S;
        }
    }
}
