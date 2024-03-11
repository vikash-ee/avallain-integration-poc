package com.avallaintest.hosting.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import lombok.Data;

@Configuration
@PropertySource("classpath:app-config.properties")
@Data
public class AppConfig {

    @Value("${static.dir}")
    private String staticDir;

    @Value("${lti.stared.dir}")
    private String ltiStaticDir;

    @Value("${static.server.host}")
    private String staticServerHost;

    @Value("${lrs.server.host}")
    private String lrsServerHost;

    @Value("${lrs.key}")
    private String lrsKey;

    @Value("${lrs.secret}")
    private String lrsSecret;

}