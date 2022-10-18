package com.blizzard.ash.admin.definitions;

import java.net.URI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.blizzard.ash.shared.mocks.MockSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@RestController
@RequestMapping("/definitions")
public class DefinitionController {
    private static final Logger LOGGER = LoggerFactory.getLogger("definition-controller");
    private DefinitionService definitions;

    public DefinitionController(DefinitionService definitionService) {
        definitions = definitionService;
    }

    @GetMapping("/services")
    public ResponseEntity<?> serviceList() {
        LOGGER.info("GET services");
        return ResponseEntity.ok(definitions.listServices());
    }

    @GetMapping("/{service}/tests")
    public ResponseEntity<?> testList(@PathVariable("service") String service) {
        LOGGER.info("GET tests for " + service);
        return ResponseEntity.ok(definitions.latestForService(service));
    }

    @GetMapping("/{service}/{test}")
    public ResponseEntity<?> whatIs(@PathVariable("service") String service, @PathVariable("test") String test) {
        LOGGER.info("GET " + service + "/" + test);
        return ResponseEntity.of(definitions.definitionBySource(MockSource.definition(service, test)));
    }

    @GetMapping("/{service}/{test}/history")
    public ResponseEntity<?> historyFor(@PathVariable("service") String service, @PathVariable("test") String test) {
        LOGGER.info("GET history for " + service + "/" + test);
        return ResponseEntity.ok(definitions.historyBySource(MockSource.definition(service, test)));
    }

    @PostMapping("/{service}/{test}")
    public ResponseEntity<?> upsertDefinition(@PathVariable("service") String service, @PathVariable("test") String test, @RequestBody SerializedDefinition definition) {
        var toSave = DefinitionOperations.serializedToShared(definition);
        var newDef = definitions.saveDefinition(MockSource.definition(service, test), toSave);
        LOGGER.info("UPDATE " + service + "/" + test + " and ARCHIVE version " + newDef.version());
        return ResponseEntity.created(URI.create("/definitions/" + service + "/" + test)).body(newDef);
    }

    @DeleteMapping("/{service}/{test}")
    public ResponseEntity<?> deactivateDefinition(@PathVariable("service") String service, @PathVariable("test") String test) {
        definitions.deleteDefinition(MockSource.definition(service, test));
        LOGGER.info("ARCHIVE " + service + "/" + test);
        return ResponseEntity.noContent().build();
    }
}
