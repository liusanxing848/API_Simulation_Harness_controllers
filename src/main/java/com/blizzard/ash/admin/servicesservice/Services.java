package com.blizzard.ash.admin.servicesservice;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

import com.blizzard.ash.shared.mocks.MockDefinition;
import com.blizzard.ash.shared.mocks.MockSource;
//import static com.blizzard.ash.admin.definitions.DefinitionOperations.*;

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

// public Optional<Business> getItem(Key key){
//     return Optional.ofNullable(table.getItem(key))
//     .map(storedService -> new buisunessthingCTOR(
//         store.ServiceField1, ....
//     ));
// }

@Service
public class Services {
    private final DynamoDbTable<StoredService> table;
    private static final Logger logger = LoggerFactory.getLogger("service-service");

    public Services(DynamoDbEnhancedClient dbClient) {

        table = dbClient.table("service",
                TableSchema.fromImmutableClass(StoredService.class)
        );
        try {
            table.describeTable();
            // logger.info("Table found, no action.");
        } catch (ResourceNotFoundException rnf) {
            table.createTable(); // Need a non-enhanced client for awaiting...
            // logger.info("Table created.");
        }
    }
   
    public void storeNewItem(StoredService ss)
    {
        table.putItem(ss);
    }
    
    

}
