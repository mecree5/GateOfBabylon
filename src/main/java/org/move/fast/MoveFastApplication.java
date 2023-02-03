package org.move.fast;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author sgw
 */
@SpringBootApplication
@MapperScan("org.move.fast.module.mapper")
@EnableScheduling
public class MoveFastApplication {

    public static void main(String[] args) {
        SpringApplication.run(MoveFastApplication.class, args);
    }

}
