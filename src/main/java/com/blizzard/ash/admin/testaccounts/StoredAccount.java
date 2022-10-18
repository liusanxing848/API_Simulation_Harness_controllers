package com.blizzard.ash.admin.testaccounts;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbImmutable;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;





@DynamoDbImmutable(builder = StoredAccount.Builder.class)
public record StoredAccount(
    @DynamoDbPartitionKey
    int id, 

    String name


) {
    public static final class Builder {
        private int id;
        private String name;

        public Builder() {}

        public Builder setId (int id)
        {
            this.id = id;
            return this;
        }

        public Builder setName (String name)
        {
            this.name = name;
            return this;
        }

        public StoredAccount build() {
             if(name == null){
                name = "null";
             }
            return new StoredAccount(id, name);
        }
    }
}
