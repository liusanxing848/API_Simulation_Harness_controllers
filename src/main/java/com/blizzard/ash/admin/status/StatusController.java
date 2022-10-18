package com.blizzard.ash.admin.status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.blizzard.ash.admin.mocks.MockService;
import com.blizzard.ash.admin.mocks.OverallMockStatusDescription;

@RestController
public class StatusController {
    private final Logger logger;
    private MockService mocks;

    public StatusController(MockService mocks) {
        this.mocks = mocks;
        this.logger = LoggerFactory.getLogger("status-controller");
    }

    @GetMapping("/status")
    public OverallMockStatusDescription getStatus() {
        var status = mocks.status();
        logger.info("GET status");
        return status;
    }
}
