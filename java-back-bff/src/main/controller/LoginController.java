package com.yrnet.spark.sparkbxadmin.controller;

import com.yrnet.spark.sparkbxadmin.service.token.TokenService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.security.Principal;

@RestController
public class LoginController {

    @Resource
    private TokenService tokenService;

    @GetMapping("/home")
    public String home() {
        return tokenService.getAccessToken();
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/principale")
    public Principal homepage(Principal principal) {
        return principal;
    }


}
