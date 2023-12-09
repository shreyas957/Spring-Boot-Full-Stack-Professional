package com.shreyas;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PingPongController {

    record PingPong(String name) {
    }

    @GetMapping("/ping")
    public PingPong getPing() {
        return new PingPong("Pong ....updated CD");
    }
}
