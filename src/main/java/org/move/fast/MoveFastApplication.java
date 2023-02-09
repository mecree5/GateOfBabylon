package org.move.fast;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author sgw
 */
@SpringBootApplication
@MapperScan("org.move.fast.module.mapper")
@EnableScheduling
@EnableAsync
public class MoveFastApplication {

    public static void main(String[] args) {
        //设置本地代理
        System.setProperty("socksProxyHost", "127.0.0.1");
        System.setProperty("socksProxyPort", "10808");
        SpringApplication.run(MoveFastApplication.class, args);
    }

}
