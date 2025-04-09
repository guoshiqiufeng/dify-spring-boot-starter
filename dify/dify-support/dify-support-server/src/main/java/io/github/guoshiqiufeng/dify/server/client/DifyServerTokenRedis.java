package io.github.guoshiqiufeng.dify.server.client;

import cn.hutool.core.util.StrUtil;
import io.github.guoshiqiufeng.dify.server.cache.DifyRedisKey;
import io.github.guoshiqiufeng.dify.server.dto.response.LoginResponseVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;

import java.util.concurrent.TimeUnit;

/**
 * @author yanghq
 * @version 0.8.0
 * @since 2025/4/9 13:28
 */
@Slf4j
public class DifyServerTokenRedis extends DifyServerToken {

    private static final long TOKEN_EXPIRE_MINUTES = 60;

    private final RedisTemplate<String, String> redisTemplate;

    public DifyServerTokenRedis(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    void addAuthorizationHeader(HttpHeaders headers, DifyServerClient difyServerClient) {
        String accessToken = redisTemplate.opsForValue().get(DifyRedisKey.ACCESS_TOKEN);
        if (StrUtil.isEmpty(accessToken)) {
            accessToken = obtainToken(difyServerClient);
        }
        headers.setBearerAuth(accessToken);
    }

    public String obtainToken(DifyServerClient difyServerClient) {
        LoginResponseVO loginResponse = difyServerClient.login();
        if (loginResponse != null) {
            String accessToken = loginResponse.getAccessToken();
            redisTemplate.opsForValue().set(DifyRedisKey.ACCESS_TOKEN, accessToken);
            redisTemplate.expire(DifyRedisKey.ACCESS_TOKEN, TOKEN_EXPIRE_MINUTES, TimeUnit.MINUTES);
            redisTemplate.opsForValue().set(DifyRedisKey.REFRESH_TOKEN, loginResponse.getRefreshToken());
            return accessToken;
        }
        return null;
    }

    /**
     * refresh or obtain new token
     */
    @Override
    public void refreshOrObtainNewToken(DifyServerClient difyServerClient) {
        String refreshToken = redisTemplate.opsForValue().get(DifyRedisKey.REFRESH_TOKEN);
        if (refreshToken != null) {
            LoginResponseVO response = difyServerClient.refreshToken(refreshToken);
            if (response != null) {
                String accessToken = response.getAccessToken();
                redisTemplate.opsForValue().set(DifyRedisKey.ACCESS_TOKEN, accessToken);
                redisTemplate.expire(DifyRedisKey.ACCESS_TOKEN, TOKEN_EXPIRE_MINUTES, TimeUnit.MINUTES);
                redisTemplate.opsForValue().set(DifyRedisKey.REFRESH_TOKEN, response.getRefreshToken());
                return;
            }
        }
        // 如果刷新token失败或没有刷新token，则重新登录
        LoginResponseVO loginResponse = difyServerClient.login();
        if (loginResponse != null) {
            String accessToken = loginResponse.getAccessToken();
            redisTemplate.opsForValue().set(DifyRedisKey.ACCESS_TOKEN, accessToken);
            redisTemplate.expire(DifyRedisKey.ACCESS_TOKEN, TOKEN_EXPIRE_MINUTES, TimeUnit.MINUTES);
            redisTemplate.opsForValue().set(DifyRedisKey.REFRESH_TOKEN, loginResponse.getRefreshToken());
        }

    }
}
