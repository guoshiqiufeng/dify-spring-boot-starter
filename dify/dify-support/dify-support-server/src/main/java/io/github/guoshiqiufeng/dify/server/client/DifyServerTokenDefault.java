package io.github.guoshiqiufeng.dify.server.client;

import io.github.guoshiqiufeng.dify.server.dto.response.LoginResponseVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;

import java.util.concurrent.locks.ReentrantLock;

/**
 * @author yanghq
 * @version 0.8.0
 * @since 2025/4/9 13:29
 */
@Slf4j
public class DifyServerTokenDefault extends DifyServerToken {

    private String accessToken;

    private String refreshToken;

    private final ReentrantLock tokenLock = new ReentrantLock();

    @Override
    public void addAuthorizationHeader(HttpHeaders headers, DifyServerClient difyServerClient) {
        if (accessToken == null) {
            obtainToken(difyServerClient);
        }
        headers.setBearerAuth(accessToken);
    }

    public void obtainToken(DifyServerClient difyServerClient) {
        tokenLock.lock();
        try {
            if (accessToken == null) {
                LoginResponseVO loginResponse = difyServerClient.login();
                if (loginResponse != null) {
                    this.accessToken = loginResponse.getAccessToken();
                    this.refreshToken = loginResponse.getRefreshToken();
                }
            }
        } finally {
            tokenLock.unlock();
        }
    }

    /**
     * refresh or obtain new token
     */
    @Override
    public void refreshOrObtainNewToken(DifyServerClient difyServerClient) {
        tokenLock.lock();
        try {
            if (refreshToken != null) {
                LoginResponseVO response = difyServerClient.refreshToken(refreshToken);
                if (response != null) {
                    this.accessToken = response.getAccessToken();
                    this.refreshToken = response.getRefreshToken();
                    return;
                }
            }
            // 如果刷新token失败或没有刷新token，则重新登录
            LoginResponseVO loginResponse = difyServerClient.login();
            if (loginResponse != null) {
                this.accessToken = loginResponse.getAccessToken();
                this.refreshToken = loginResponse.getRefreshToken();
            }
        } finally {
            tokenLock.unlock();
        }
    }

}
