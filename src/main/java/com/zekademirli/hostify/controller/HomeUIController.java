package com.zekademirli.hostify.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
//this controller contain
public class HomeUIController {

    @GetMapping("/success")
    public String success(){
        return "success";
    }
}
