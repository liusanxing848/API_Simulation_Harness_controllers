package com.blizzard.ash.admin.templates;

import com.blizzard.ash.admin.definitions.SerializedMockResponse;

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbConvertedBy;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbImmutable;

@DynamoDbImmutable(builder = DefinitionTemplate.Builder.class)
public record DefinitionTemplate(
    String name,

    @DynamoDbConvertedBy(value = RequestTemplate.Converter.class)
    RequestTemplate request,
    
    // Nothing templated in the responses, can use the one from Definition
    @DynamoDbConvertedBy(value = SerializedMockResponse.Converter.class)
    SerializedMockResponse response

    // TODO: For a more 'templatey' experience, maybe include a keyed list of fields available for this template
    // For now, though, you're just passing in the url
) {
    public static final class Builder {
        private String name;
        private RequestTemplate request;
        private SerializedMockResponse response;

        public Builder() {

        }

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Builder setRequest(RequestTemplate request) {
            this.request = request;
            return this;
        }

        public Builder setResponse(SerializedMockResponse response) {
            this.response = response;
            return this;
        }

        public DefinitionTemplate build() {
            return new DefinitionTemplate(name, request, response);
        }
    }
}
