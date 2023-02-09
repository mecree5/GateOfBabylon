package org.move.fast.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * 只能读取application.yml
 */
@Component
public class ReadConf {

    private static Environment environment;

    @Autowired
    Environment initEnvironment;

    public static String getConfValue(String key) {
        return environment.getProperty(key);
    }

    @PostConstruct
    public void setEnvironment() {
        environment = initEnvironment;
    }
}