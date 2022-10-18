package com.blizzard.ash.admin.testaccounts;

import com.blizzard.ash.shared.mocks.RequestMatch;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import software.amazon.awssdk.enhanced.dynamodb.AttributeConverter;
import software.amazon.awssdk.enhanced.dynamodb.AttributeValueType;
import software.amazon.awssdk.enhanced.dynamodb.EnhancedType;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbConvertedBy;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbImmutable;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;


@DynamoDbImmutable(builder = SerializedAccount.Builder.class)
@JsonDeserialize(builder = SerializedAccount.Builder.class)
public record SerializedAccount(
    int id,
    String name

) {

    public static final class Builder {
        int id;
        String name;

        public Builder() {}

        public Builder withId (int id) {
            //this.request = request;
            this.id = id;
            return this;
        }

        public Builder withName(String name) {
            //this.request = request;
            this.name = name;
            return this;
        }

        public String testString()
        {
            return "testResult";
        }

        public StoredAccount genResponse() {
            return new StoredAccount.Builder()
            .setId(id + 1)
            .setName("received, auto response: " + name)
            .build();
        }

        public SerializedAccount build() {
            return new SerializedAccount(id, name);
        }
    }

    
}