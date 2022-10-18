package com.blizzard.ash.admin.testaccounts;
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
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.GetItemEnhancedRequest;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;
import software.amazon.awssdk.enhanced.dynamodb.model.ScanEnhancedRequest;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.ResourceNotFoundException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.yaml.snakeyaml.events.Event.ID;

// public Optional<Business> getItem(Key key){
//     return Optional.ofNullable(table.getItem(key))
//     .map(storedService -> new buisunessthingCTOR(
//         store.ServiceField1, ....
//     ));
// }

@Service
public class AccountService {
    private final DynamoDbTable<StoredAccount> table;
    private static final Logger logger = LoggerFactory.getLogger("account-service");

    public AccountService(DynamoDbEnhancedClient dbClient) {

        table = dbClient.table("account",
                TableSchema.fromImmutableClass(StoredAccount.class)
        );
        try {
            table.describeTable();
            // logger.info("Table found, no action.");
        } catch (ResourceNotFoundException rnf) {
            table.createTable(); // Need a non-enhanced client for awaiting...
            // logger.info("Table created.");
        }
    }
   
    public void storeNewItem(StoredAccount storedAccount)
    {
        table.putItem(storedAccount);
    }

    public Optional<StoredAccount> getItemFromId(int id){
        Key k = Key.builder().partitionValue(id).build();
         return Optional.ofNullable(table.getItem(k));
    }

    public void deleteItem(int id){
        Key k= Key.builder().partitionValue(id).build();
        table.deleteItem(k);
    }

    public void updateName(StoredAccount storedAccount){
        table.updateItem(storedAccount);
    }
}
