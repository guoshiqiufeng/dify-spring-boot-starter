package io.github.guoshiqiufeng.dify.autoconfigure;

import io.github.guoshiqiufeng.dify.server.client.DifyServerClient;
import io.github.guoshiqiufeng.dify.server.client.DifyServerToken;
import io.github.guoshiqiufeng.dify.server.client.DifyServerTokenDefault;
import io.github.guoshiqiufeng.dify.server.client.DifyServerTokenRedis;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * @author yanghq
 * @version 1.0
 * @since 2025/4/17 09:30
 */
@Configuration
@ConditionalOnBean(RedisTemplate.class)
@AutoConfigureAfter(RedisAutoConfiguration.class)
@ConditionalOnClass({DifyServerClient.class})
public class DifyServerRedisTokenAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(DifyServerToken.class)
    public DifyServerToken difyServerToken(ObjectProvider<RedisTemplate<String, String>> redisTemplateProvider) {
        RedisTemplate<String, String> redisTemplate = redisTemplateProvider.getIfAvailable();
        if (redisTemplate != null) {
            return new DifyServerTokenRedis(redisTemplate);
        }
        return new DifyServerTokenDefault();
    }
}
