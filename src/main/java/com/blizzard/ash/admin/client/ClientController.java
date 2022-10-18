package com.blizzard.ash.admin.client;

import java.util.Optional;

import com.blizzard.ash.admin.definitions.DefinitionService;
import com.blizzard.ash.admin.mocks.MockService;
import com.blizzard.ash.admin.templates.TemplateService;
import com.blizzard.ash.shared.mocks.MockSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// TODO: Handle errors more specifically so the system is easier to operate
@RestController
@RequestMapping("/client")
public class ClientController {
    private final Logger logger;
    private final DefinitionService definitions;
    private final TemplateService templates;
    private final MockService mocks;

    public ClientController(DefinitionService definitionService, TemplateService templateService, MockService mockService) {
        definitions = definitionService;
        templates = templateService;
        mocks = mockService;
        logger = LoggerFactory.getLogger("mocks-controller");
    }

    @PutMapping("/{clientId}/definition")
    public ResponseEntity<?> putTestCase(@PathVariable("clientId") String clientId, @RequestBody DefinitionBasedRequest mockRequest) {
        logger.info("PUT Definition %s/%s against %s".formatted(mockRequest.service(), mockRequest.definition(), clientId));
        var source = MockSource.definition(mockRequest.service(), mockRequest.definition());
        var definition = definitions.definitionBySource(source);
        return definition.map(def -> {
                mocks.loadMock(clientId, source, Optional.ofNullable(mockRequest.occurrences()));
                return ResponseEntity.noContent().build();
            })
            .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{clientId}/template")
    public ResponseEntity<?> putTemplate(@PathVariable("clientId") String clientId, @RequestBody TemplateBasedRequest mockRequest) {
        var template = templates.findById(mockRequest.id());
        return template.map(
            exists -> {
                mocks.loadMock(clientId, MockSource.template(mockRequest.id(), mockRequest.url()), Optional.ofNullable(mockRequest.occurrences()));
                return ResponseEntity.noContent().build();
            })
            .orElse(ResponseEntity.notFound().build());
    }

    // TODO: Add an ad-hoc endpoint

    @DeleteMapping("/{clientId}")
    public ResponseEntity<?> deleteTestCase(@PathVariable("clientId") String clientId) {
        logger.info("DELETE Mocks for %s".formatted(clientId));
        mocks.clearMock(clientId);
        return ResponseEntity.noContent().build();
    }
}
