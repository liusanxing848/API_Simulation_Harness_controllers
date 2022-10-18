package com.blizzard.ash.admin.definitions;

import java.time.Instant;

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbImmutable;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;

@DynamoDbImmutable(builder = StoredDefinition.Builder.class)
public record StoredDefinition(
    @DynamoDbPartitionKey
    String coordinate,

    @DynamoDbSortKey
    String version,

    SerializedDefinition definition,

    // TODO: When this goes out to JSON it's just a number...
    Instant savedAt

    // TODO: When we have users, also mark the user who saved this
) {
    public static final class Builder {
        String coordinate;
        String version;
        Instant savedAt;
        SerializedDefinition definition;

        public Builder() { }

        public Builder setCoordinate(String folder) {
            this.coordinate = folder;
            return this;
        }

        public Builder setVersion(String version) {
            this.version = version;
            return this;
        }

        public Builder setSavedAt(Instant date) {
            this.savedAt = date;
            return this;
        }

        public Builder setDefinition(SerializedDefinition definition) {
            this.definition = definition;
            return this;
        }

        public StoredDefinition build() {
            return new StoredDefinition(coordinate, version, definition, savedAt);
        }
    }
}
