package com.kamennova.doggies;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:storage.properties")
public class KeyStorageConfig {
    public String key;
}
