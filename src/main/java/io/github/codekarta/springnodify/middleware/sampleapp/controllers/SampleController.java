package io.github.codekarta.springnodify.middleware.sampleapp.controllers;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SampleController {

    @GetMapping("/")
    public String getUsers(){
        return "Manish Bansal";
    }

    @GetMapping("/api/private/data")
    public String privateMethodSample(){
        return "you will not see this output but 401 error";
    }
}
