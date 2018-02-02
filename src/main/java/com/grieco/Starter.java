package com.grieco;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@EnableAutoConfiguration
public class Starter
{
    @RequestMapping("/")
    public String home()
    {
        return "Hello Springboot!";
    }

    public static void main(String[] args) throws Exception
    {
        SpringApplication.run(Starter.class, args);
    }
}
