package com.blizzard.ash.admin.mocks;

import java.util.List;
import java.util.Map;

public record OverallMockStatusDescription(
    String appEnv,
    Map<String, List<MockStatusDescription>> stubsMap
    // TODO: add Path-Host Mappings
) {
    
}
