package org.tyto.square.util;

import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Profile("test")
@RestController
@RequestMapping("/test")
public class TestController {

    @GetMapping("/success")
    public ResponseEntity<?> returnOk() {
        return ResponseEntity.ok().build();
    }

    @GetMapping("/failure")
    public ResponseEntity<?> returnBadRequest() {
        return ResponseEntity.badRequest().build();
    }
}
