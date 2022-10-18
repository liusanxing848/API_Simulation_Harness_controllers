package com.blizzard.ash.admin.servicesservice;

import java.time.Instant;

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbImmutable;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;




@DynamoDbImmutable(builder = StoredService.Builder.class)
public record StoredService(
    @DynamoDbPartitionKey
    int id, 
    

    String nickName

) {
    public static final class Builder {
        private int id;
        private String nickName;

        public Builder() {}

        public Builder setId (int id)
        {
            this.id = id;
            return this;
        }

        public Builder setNickName (String nickName)
        {
            this.nickName = nickName;
            return this;
        }

        public StoredService build() {
             if(nickName == null){
                nickName = "null";
             }
            return new StoredService(id, nickName);
        }


    }
}
