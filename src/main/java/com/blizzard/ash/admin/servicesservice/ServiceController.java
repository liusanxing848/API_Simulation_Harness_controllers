package com.blizzard.ash.admin.servicesservice;

import java.net.URI;
import java.util.Arrays;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/service")
public class ServiceController {
    private static final Logger LOGGER = LoggerFactory.getLogger("Service-Controller");
    private Services services;

    public ServiceController(Services services){
        this.services = services;
    }


    @GetMapping("/get")
    public ResponseEntity<?> GetResponse(){
        // var b = new StoredService.Builder()
        // .setId(5)
        // .setNickName("sanxing")
        // .build();

        LOGGER.info("test for GET");
        String[] a = new String[] {"A",  "B", "C", "D"};

        List<String> list = Arrays.asList(a);
        return ResponseEntity.ok(list);
    }

    @PostMapping("/post")
    public ResponseEntity<?> PostResponse(@RequestBody SerializedService serviceRequestBody){
        LOGGER.info("test for POST");
        var r = new StoredService.Builder()
        .setId(serviceRequestBody.id() + 1)
        .setNickName("reqeustReceived!" + serviceRequestBody.nickName())
        .build();
        
        services.storeNewItem(r);
        return ResponseEntity.ok(r);
    }

    @PutMapping("/put")
    public ResponseEntity<?> PutResponse(){
        LOGGER.info("test for PUT");
        return ResponseEntity.ok("okabc");
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> DeleteResponse(){
        LOGGER.info("test for DELETE");
        return ResponseEntity.ok("ok delete");
    }
}
