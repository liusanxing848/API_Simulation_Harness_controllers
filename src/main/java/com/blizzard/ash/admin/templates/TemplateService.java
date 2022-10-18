package com.blizzard.ash.admin.templates;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.blizzard.ash.admin.definitions.DefinitionOperations;
import com.blizzard.ash.shared.mocks.MockDefinition;
import com.blizzard.ash.shared.mocks.RequestMatch;
import com.blizzard.ash.shared.mocks.MockSource.ForTemplate;

import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.ScanEnhancedRequest;
import software.amazon.awssdk.services.dynamodb.model.ResourceNotFoundException;

@Service
public class TemplateService {
    private final DynamoDbTable<StoredTemplate> table;

    public TemplateService(DynamoDbEnhancedClient dbClient) {
        table = dbClient.table("templates",
            TableSchema.fromImmutableClass(StoredTemplate.class)
        );

        try {
            table.describeTable();
        } catch (ResourceNotFoundException rnf) {
            table.createTable();
        }
    }

    public Optional<MockDefinition> render(ForTemplate source) {
        return findById(source.id())
            .map(
                stored -> {
                    return new MockDefinition(
                        new RequestMatch(
                            stored.template().request().method(),
                            source.url()
                        ),
                        DefinitionOperations.serializedToShared(stored.template().response())
                    );
                }
            );
    }

    public List<StoredTemplate> listAll() {
        // TODO: Store this in a reference doc on changes instead - the List or Set types are ideal here
        return table.scan(
            ScanEnhancedRequest.builder().build()
        )
        .items()
        .stream()
        .toList();
    }

    public Optional<StoredTemplate> findById(String id) {
        return Optional.ofNullable(table.getItem(Key.builder().partitionValue(id).build()));
    }

    public void save(String id, DefinitionTemplate definition) {
        table.putItem(new StoredTemplate(id, definition));
    }

    public void remove(String id) {
        table.deleteItem(Key.builder().partitionValue(id).build());
    }
}
