package io.github.guoshiqiufeng.dify.boot.base;

import io.github.guoshiqiufeng.dify.boot.application.DifyTestSpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

/**
 * @author yanghq
 * @version 1.0
 * @since 2025/3/31 16:24
 */
@SpringBootTest(
        classes = DifyTestSpringBootApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {}
)
@Testcontainers(disabledWithoutDocker = true)
@ActiveProfiles("test")
public interface RedisContainerTest {

    GenericContainer<?> REDIS_CONTAINER = new GenericContainer<>("redis:7.0.2")
            .withExposedPorts(6379);

    @DynamicPropertySource
    static void redisProperties(DynamicPropertyRegistry registry) {
        REDIS_CONTAINER.start();
        registry.add("spring.data.redis.host", REDIS_CONTAINER::getHost);
        registry.add("spring.data.redis.port", REDIS_CONTAINER::getFirstMappedPort);
    }
}
