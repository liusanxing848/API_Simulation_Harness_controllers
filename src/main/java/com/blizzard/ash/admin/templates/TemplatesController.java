package com.blizzard.ash.admin.templates;

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

@RestController
@RequestMapping("/templates")
public class TemplatesController {
    private static final Logger LOGGER = LoggerFactory.getLogger("templates-controller");
    private final TemplateService templates;

    public TemplatesController(TemplateService templates) {
        this.templates = templates;
    }

    @GetMapping("")
    public ResponseEntity<?> listTemplates() {
        LOGGER.info("LIST ALL templates");
        var allTemplates = templates.listAll();
        return ResponseEntity.ok(allTemplates.stream().map(st -> st.id()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> describeTemplate(@PathVariable("id") String id) {
        LOGGER.info("DESCRIBE %s".formatted(id));
        return templates.findById(id)
            .map(found -> ResponseEntity.ok(found))
            .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/preview")
    public ResponseEntity<?> previewTemplate(@PathVariable("id") String id, String url) {
        LOGGER.info("PREVIEW %s for %s".formatted(id, url));
        return templates.render(MockSource.template(id, url))
            .map(rendered -> ResponseEntity.ok(rendered))
            .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{id}")
    public ResponseEntity<?> upsertTemplate(@PathVariable("id") String id, @RequestBody DefinitionTemplate definition) {
        LOGGER.info("SET %s".formatted(id));
        templates.save(id, definition);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> removeTemplate(@PathVariable("id") String id) {
        LOGGER.info("DELETE %s".formatted(id));
        templates.remove(id);
        return ResponseEntity.noContent().build();
    }
}
