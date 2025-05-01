package com.myblog.configuration;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
//@EnableWebMvc
@ComponentScan(basePackages = {"com.myblog"})
@PropertySource("classpath:test-application.properties")
public class TestServiceConfiguration {
}
