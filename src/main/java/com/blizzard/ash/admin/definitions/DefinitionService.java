package com.blizzard.ash.admin.definitions;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

import com.blizzard.ash.shared.mocks.MockDefinition;
import com.blizzard.ash.shared.mocks.MockSource;
import static com.blizzard.ash.admin.definitions.DefinitionOperations.*;

import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Expression;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;
import software.amazon.awssdk.enhanced.dynamodb.model.ScanEnhancedRequest;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.ResourceNotFoundException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class DefinitionService {
    private final DynamoDbTable<StoredDefinition> table;
    private static final Logger logger = LoggerFactory.getLogger("definition-service");

    public DefinitionService(DynamoDbEnhancedClient dbClient) {
        table = dbClient.table("definitions", 
            TableSchema.fromImmutableClass(StoredDefinition.class)
        );

        try {
            table.describeTable();
            // logger.info("Table found, no action.");
        } catch (ResourceNotFoundException rnf) {
            table.createTable(); // Need a non-enhanced client for awaiting...
            // logger.info("Table created.");
        }
    }

    public Optional<MockDefinition> definitionBySource(MockSource.ForDefinition source) {
        return Optional.ofNullable(table.getItem(latestFrom(source)))
            .map(stored -> new MockDefinition(
                stored.definition().requestConfig(), 
                serializedToShared(stored.definition().responseConfig())
            ));
    }

    
    
    

    public List<StoredDefinition> historyBySource(MockSource.ForDefinition source) {
        return historyFor(source)
            .toList();
    }

    public StoredDefinition saveDefinition(MockSource.ForDefinition source, MockDefinition definition) {
        return archiveActiveAnd(source, 
            present -> {
                var newDef = new StoredDefinition(
                    coordinateFor(source), 
                    activeSortKey(),
                    sharedToSerialized(definition),
                    Instant.now()
                );
                table.putItem(newDef);
                return newDef;
            },
            () -> {
                var newDef = new StoredDefinition(
                    coordinateFor(source), 
                    activeSortKey(),
                    sharedToSerialized(definition),
                    Instant.now()
                );
                table.putItem(newDef);
                return newDef;
            });
    }

    // TODO: Put in a dummy/deleted value instead?
    // Idea is, the way it's working here a history query will look like a definition was
    // active at this source after the delete, if any newer versions were saved. Making a
    // deletion marker in the history will let us be more specific.
    public void deleteDefinition(MockSource.ForDefinition source) {
        // Policy: to maintain the audit trail, we simply move the active one to the audit stack
        // This means another create for this coordinate will just write an active, and further creates
        // will push that new active to the audit...but searches for the active one will fail/empty.
        archiveActiveAnd(source, 
            present -> {
                table.deleteItem(present);
            },
            () -> {
                // This is a specific no-op, on the off chance we want to handle a deletion
                // of an inactive item in the future.
            });
    }

    public Stream<StoredDefinition> historyFor(MockSource.ForDefinition source) {
        return table
            .query(
                QueryEnhancedRequest.builder()
                .queryConditional(QueryConditional.keyEqualTo(partitionFrom(source)))
                // .filterExpression(Expression.builder()
                //     .expression("version <> :ver")
                //     .putExpressionValue(":ver", AttributeValue.fromS(activeSortKey()))
                //     .build()
                // )
                .build()
            )
            .items()
            .stream()
            .filter(sd -> !sd.version().equalsIgnoreCase(activeSortKey()));
    }

    public Stream<String> listServices() {
        // TODO: Store this in a reference doc on changes instead - the List or Set types are ideal here
        return table
            .scan(ScanEnhancedRequest.builder()
            .build())
            .items()
            .stream()
            .map(DefinitionOperations::sourceFor)
            .map(source -> source.service())
            .distinct();
    }

    public Stream<String> latestForService(String service) {
        // TODO: Store this in a reference doc on changes instead - the List or Set types are ideal here
        return table
            .scan(
                ScanEnhancedRequest.builder()
                .filterExpression(Expression.builder()
                    .expression("begins_with(coordinate, :service) AND version = :version")
                    .putExpressionValue(":service", AttributeValue.fromS(service))
                    .putExpressionValue(":version", AttributeValue.fromS(DefinitionOperations.activeSortKey()))
                    .build()
                )
                .build()
            )
            .items()
            .stream()
            .map(sd -> sourceFor(sd).test());
    }

    public void archiveActiveAnd(MockSource.ForDefinition source, Consumer<StoredDefinition> ifPresent, Runnable ifAbsent) {
        var active = latestFrom(source);
        
        Optional.ofNullable(table.getItem(active))
        .ifPresentOrElse(
            present -> {
                    var nextVersion = historyFor(source)
                        .max((defA, defB) -> defA.version().compareTo(defB.version()))
                        .map(lastSaved -> Integer.parseInt(lastSaved.version().substring(2)) + 1)
                        .orElse(0);

                    table.putItem(new StoredDefinition(
                        present.coordinate(), 
                        "v_" + nextVersion.toString(),
                        present.definition(),
                        present.savedAt()
                    ));
                    
                    logger.info("Current active found - archiving as version " + nextVersion.toString());
                    ifPresent.accept(present);
                },
                () -> {
                    logger.info("No current active found.");
                    ifAbsent.run();
                }
            );
    }

    public StoredDefinition archiveActiveAnd(MockSource.ForDefinition source, Function<StoredDefinition, StoredDefinition> ifPresent, Supplier<StoredDefinition> ifAbsent) {
        var active = latestFrom(source);
        return Optional.ofNullable(table.getItem(active))
            .map(present -> {
                var nextVersion = historyFor(source)
                    .max((defA, defB) -> defA.version().compareTo(defB.version()))
                    .map(lastSaved -> Integer.parseInt(lastSaved.version().substring(2)) + 1)
                    .orElse(0);

                table.putItem(new StoredDefinition(
                    present.coordinate(), 
                    "v_" + nextVersion.toString(),
                    present.definition(),
                    present.savedAt()
                ));
                
                logger.info("Current active found - archiving as version " + nextVersion.toString());
                return ifPresent.apply(present);
            })
            .orElseGet(() -> {
                logger.info("No current active found.");
                return ifAbsent.get();
            });
    }
}
