package io.github.guoshiqiufeng.dify.boot.base;

import io.github.guoshiqiufeng.dify.server.DifyServer;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;

/**
 * @author yanghq
 * @version 1.0
 * @since 2025/4/1 13:27
 */
@Slf4j
public abstract class BaseChatContainerTest implements RedisContainerTest {

    @Resource
    DifyServer difyServer;


}
