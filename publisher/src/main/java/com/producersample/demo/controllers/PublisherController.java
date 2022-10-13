package com.producersample.demo.controllers;

import com.producersample.demo.dto.Message;
import com.producersample.demo.services.PublisherService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.io.IOException;

@RestController
@RequestMapping(value = "/publish")
public class PublisherController {

    @Resource
    private PublisherService publisherService;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    void publishMessage(@RequestBody final Message message) throws IOException {
        publisherService.publishMessage(message.getMessage());
    }
}
