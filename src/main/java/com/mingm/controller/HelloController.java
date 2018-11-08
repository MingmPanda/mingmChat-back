package com.mingm.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author: panmm
 * @date: 2018/11/9 00:52
 * @description:
 */
@RestController
public class HelloController {

    @GetMapping("/hello")
    public String hello() {
        return "hello mingChat~~";
    }
}
