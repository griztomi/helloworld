package com.grieco;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ResourceBanner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.io.DefaultResourceLoader;

@EnableAutoConfiguration
@ComponentScan(basePackages = "com.grieco")
public class Starter
{
    public static void main(String[] args)
    {
        final SpringApplication application = new SpringApplicationBuilder()
                .banner(new ResourceBanner(new DefaultResourceLoader().getResource("banner.txt")))
                .sources(Starter.class)
                .build();
        application.run(Starter.class, args);
    }
}