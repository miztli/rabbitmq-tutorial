package com.consumersample.demo.controllers;

import com.consumersample.demo.services.ConsumerService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping(value = "/messages")
public class ConsumerController {

    @Resource
    private ConsumerService consumerService;

    @GetMapping
    public List<String> fetchMessages() {
        return consumerService.fetchMessages();
    }
}
