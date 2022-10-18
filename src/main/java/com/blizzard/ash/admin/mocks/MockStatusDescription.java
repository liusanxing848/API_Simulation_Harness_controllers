package com.blizzard.ash.admin.mocks;

import com.blizzard.ash.shared.mocks.MockDefinition;

public record MockStatusDescription(
    String source,
    String group,
    String specific,
    MockDefinition mock,
    Integer occurrences
) {
    
}
