package com.example.jeffrey.springcloudtask.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SpringCloudTaskController {

    @RequestMapping("/")
    public @ResponseBody
    String test() {
        return "test okay";
    }

}
