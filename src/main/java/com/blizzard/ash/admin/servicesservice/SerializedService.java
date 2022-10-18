package com.blizzard.ash.admin.servicesservice;

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


@DynamoDbImmutable(builder = SerializedService.Builder.class)
@JsonDeserialize(builder = SerializedService.Builder.class)
public record SerializedService(
    //@DynamoDbConvertedBy(value = SerializedService.RequestConverter.class)
    int id,
    String nickName

    // @DynamoDbConvertedBy(value = SerializedMockResponse.Converter.class)
    // SerializedMockResponse myResponse
) {

    //private static ObjectMapper om = new ObjectMapper();

    // public int getId(){
    //     return this.id;
    // }

    // public String getNickName(){
    //     return this.nickName;
    // }
    

    public static final class Builder {
        // RequestMatch request;
        // SerializedMockResponse response;

        int id;
        String nickName;

        // var b = new StoredService.Builder()
        // .setId(5)
        // .setNickName("sanxing")
        // .build();

        public Builder() {}

        public Builder withId (int id) {
            //this.request = request;
            this.id = id;
            return this;
        }

        

        // public void setId (int id) {
        //     this.id = id;
        // }

        // public void setNickName (String nickName) {
        //     this.nickName = nickName;;
        // }

        public Builder withNickName(String nickName) {
            //this.request = request;
            this.nickName = nickName;
            return this;
        }

        // public Builder setRespid(SerializedMockResponse response) {
        //     this.response = response;
        //     return this;
        // }

        // public Builder setRespnickName(SerializedMockResponse response) {
        //     this.response = response;
        //     return this;
        // }

        public String testString()
        {
            return "tttttt";
        }

        public StoredService genResponse() {
            return new StoredService.Builder()
            .setId(id + 1)
            .setNickName("received, auto response: " + nickName)
            .build();
        }

        public SerializedService build() {
            return new SerializedService(id, nickName);
        }
    }

    // public static final class RequestConverter implements AttributeConverter<RequestMatch> {

    //     @Override
    //     public AttributeValue transformFrom(RequestMatch input) {
    //         try {
    //             return AttributeValue.fromS(om.writeValueAsString(input));
    //         } catch (JsonProcessingException e) {
    //             e.printStackTrace();
    //             throw new RuntimeException(e);
    //         }
    //     }

    //     @Override
    //     public RequestMatch transformTo(AttributeValue input) {
    //         try {
    //             return om.readValue(input.s(), RequestMatch.class);
    //         } catch (JsonProcessingException e) {
    //             e.printStackTrace();
    //             throw new RuntimeException(e);
    //         }
    //     }

    //     @Override
    //     public EnhancedType<RequestMatch> type() {
    //         return EnhancedType.of(RequestMatch.class);
    //     }

    //     @Override
    //     public AttributeValueType attributeValueType() {
    //         return AttributeValueType.S;
    //     }
    // }
}