package com.blizzard.ash.admin.templates;

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbImmutable;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

@DynamoDbImmutable(builder = StoredTemplate.Builder.class)
public record StoredTemplate(
    @DynamoDbPartitionKey
    String id,

    DefinitionTemplate template
) {
    public static final class Builder {
        private String id;
        private DefinitionTemplate template;

        public Builder() {

        }

        public Builder setId(String id) {
            this.id = id;
            return this;
        }

        public Builder setTemplate(DefinitionTemplate template) {
            this.template = template;
            return this;
        }

        public StoredTemplate build() {
            return new StoredTemplate(id, template);
        }
    }
}
