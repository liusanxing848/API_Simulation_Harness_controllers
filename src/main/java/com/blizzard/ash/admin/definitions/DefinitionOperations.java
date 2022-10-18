package com.blizzard.ash.admin.definitions;

import java.util.Optional;

import com.blizzard.ash.shared.mocks.MockDefinition;
import com.blizzard.ash.shared.mocks.MockResponse;
import com.blizzard.ash.shared.mocks.MockSource;

import software.amazon.awssdk.enhanced.dynamodb.Key;

public final class DefinitionOperations {
    
    public static MockResponse serializedToShared(SerializedMockResponse serialized) {
        return new MockResponse(
            serialized.status(), 
            Optional.ofNullable(serialized.body())
                .filter(b -> ! b.isBlank()), 
            serialized.headers(), 
            serialized.delayDurationMs(), 
            serialized.proxied()
        );  
    }

    public static MockDefinition serializedToShared(SerializedDefinition serialized) {
        return new MockDefinition(serialized.requestConfig(), serializedToShared(serialized.responseConfig()));
    }

    public static SerializedDefinition sharedToSerialized(MockDefinition shared) {
        return new SerializedDefinition(
            shared.request(), 
            new SerializedMockResponse(
                shared.response().status(), 
                shared.response().body().orElse(""), 
                shared.response().headers(), 
                shared.response().delayDurationMs(),
                shared.response().proxied()
            )
        );
    }

    public static String activeSortKey() {
        return "active";
    }

    public static String coordinateFor(MockSource.ForDefinition source) {
        return source.service() + "/" + source.test();
    }

    public static MockSource.ForDefinition sourceFor(StoredDefinition stored) {
        var parts = stored.coordinate().split("/");
        return MockSource.definition(parts[0], parts[1]);
    }

    public static Key latestFrom(MockSource.ForDefinition source) {
        return Key.builder()
            .partitionValue(coordinateFor(source))
            .sortValue(activeSortKey())
            .build();
    }

    public static Key partitionFrom(MockSource.ForDefinition source) {
        return Key.builder()
            .partitionValue(coordinateFor(source))
            .build();
    }
}
