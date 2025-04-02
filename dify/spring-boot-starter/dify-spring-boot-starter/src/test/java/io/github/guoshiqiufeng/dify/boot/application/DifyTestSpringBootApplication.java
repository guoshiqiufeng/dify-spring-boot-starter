package io.github.guoshiqiufeng.dify.boot.application;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author yanghq
 * @version 1.0
 * @since 2025/3/31 09:42
 */
@SpringBootApplication(excludeName = "io.github.guoshiqiufeng.dify.boot")
public class DifyTestSpringBootApplication {

    public static void main(String[] args) {
        SpringApplication.run(DifyTestSpringBootApplication.class, args);
    }
}
