package com.blizzard.ash.admin.testaccounts;

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
@RequestMapping("/accountService")
public class AccountController {
    private static final Logger LOGGER = LoggerFactory.getLogger("Account-Controller");
    private AccountService accountServices;

    public AccountController(AccountService accountServices){
        this.accountServices = accountServices;
    }


    @GetMapping("/getNameById/{id}")
    public ResponseEntity<?> GetResponse(@PathVariable("id") int id){
        LOGGER.info("test for GET");

        var res = accountServices.getItemFromId(id);
        return ResponseEntity.ok(res);
    }

    @PostMapping("/post")
    public ResponseEntity<?> PostResponse(@RequestBody SerializedAccount serviceRequestBody){
        LOGGER.info("test for POST");
        var r = new StoredAccount.Builder()
        .setId(serviceRequestBody.id() + 1)
        .setName("reqeustReceived!" + serviceRequestBody.name())
        .build();
        
        accountServices.storeNewItem(r);
        return ResponseEntity.ok(r);
    }

    @PostMapping("/changeNameTo")
    public ResponseEntity<?> changeNameTo(@RequestBody SerializedAccount serviceRequestBody){
        LOGGER.info("test for edit");
        int id = serviceRequestBody.id();
        String name = serviceRequestBody.name();
        var acc = new StoredAccount.Builder()
        .setId(id)
        .setName(name)
        .build();

        accountServices.updateName(acc);
        return ResponseEntity.ok(accountServices.getItemFromId(id));

    }

    @PutMapping("/createAccount")
    public ResponseEntity<?> PutResponse(@RequestBody SerializedAccount serviceRequestBody){
        LOGGER.info("test for PUT");
        String name = serviceRequestBody.name();
        int id = serviceRequestBody.id();
        var acc = new StoredAccount.Builder()
        .setId(id)
        .setName(name)
        .build();

        accountServices.storeNewItem(acc);
        return ResponseEntity.ok(name + " stored!");

    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> DeleteResponse(){
        LOGGER.info("test for DELETE");
        return ResponseEntity.ok("ok delete");
    }

    //needs more work
    @DeleteMapping("/deleteRecordById/{id}")
    public ResponseEntity<?> deleteById(@PathVariable("id") int id){
        LOGGER.info("test for DELETE");
        accountServices.deleteItem(id);
        var res = accountServices.getItemFromId(id);
        return ResponseEntity.ok("deleted");
    }
}
