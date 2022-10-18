package com.blizzard.ash.admin.client;

public record DefinitionBasedRequest(
    String service,
    String definition,
    Integer occurrences
) {
    
}
