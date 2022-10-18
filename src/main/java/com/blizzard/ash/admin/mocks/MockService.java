package com.blizzard.ash.admin.mocks;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import com.blizzard.ash.admin.definitions.DefinitionService;
import com.blizzard.ash.admin.templates.TemplateService;
import com.blizzard.ash.shared.mocks.MockSource;
import com.blizzard.ash.shared.mocks.MockState;
import com.blizzard.ash.shared.serialization.SerializedMockState;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.lettuce.core.RedisClient;
import io.lettuce.core.api.sync.RedisCommands;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

// TODO: the keying of documents is as shared as the schema - move keying into Shared
@Service
public class MockService {

    private final Logger LOGGER = LoggerFactory.getLogger("mock-service");
    private final RedisCommands<String, String> redisCommands;
    private final ObjectMapper mapper;
    private final DefinitionService definitions;
    private final TemplateService templates;

    public MockService(DefinitionService definitionService, RedisClient redisClient, ObjectMapper mapper, TemplateService templateService) {
        definitions = definitionService;
        templates = templateService;
        redisCommands = redisClient.connect().sync();
        this.mapper = mapper;
    }

    public void loadMock(String clientId, MockSource source, Optional<Integer> occurrences) {
        var possibleDefinition = source.match(
            def -> definitions.definitionBySource(def),
            tem -> templates.render(tem)
        );

        possibleDefinition.ifPresent(
            def -> {
                var newMock = new MockState(def, source, occurrences);
                try {
                    var toRedis = mapper.writeValueAsString(SerializedMockState.from(newMock));
                    redisCommands.set("client-%s".formatted(clientId), toRedis);
                    redisCommands.sadd("active-tests", clientId);
                } catch (JsonProcessingException pe) {

                }
            }
        );
    }

    public void clearMock(String clientId) {
        redisCommands.del("client-%s".formatted(clientId));
        redisCommands.srem("active-tests", clientId);
    }

    public OverallMockStatusDescription status() {
        List<MockStatusDescription> defs = List.of();
        Map<String, List<MockStatusDescription>> map = redisCommands.smembers("active-tests")
        .stream()
        .collect(Collectors.<String, String, List<MockStatusDescription>>toMap(
            entry -> entry, 
            entry -> {
                return Optional.ofNullable(redisCommands.get("client-%s".formatted(entry)))
                    .map(e -> {
                        try {
                            var s = mapper.readValue(e, SerializedMockState.class).toDomain();
                            var d = s.source().match(
                                def -> new MockStatusDescription(
                                    "definition",
                                    def.service(),
                                    def.test(),
                                    s.definition(),
                                    s.occurrences().orElse(null)
                                ),
                                tem -> new MockStatusDescription(
                                    "template",
                                    tem.id(),
                                    tem.url(),
                                    s.definition(),
                                    s.occurrences().orElse(null)
                                )
                            );
                            return List.of(d);
                        } catch (JsonProcessingException pe) {
                            LOGGER.warn("Found entry but error retrieving: %s -> %s".formatted(entry, pe.getMessage()));
                            return defs;
                        }
                    })
                    .orElseGet(() -> {
                        LOGGER.warn("Couldn't find Redis doc for entry %s".formatted(entry));
                        return defs;
                    });
            }
        ));
        return new OverallMockStatusDescription(
            System.getProperty("appEnv"), 
            map
        );
    }
}
