package io.github.guoshiqiufeng.dify.autoconfigure;

import org.springframework.boot.autoconfigure.service.connection.ConnectionDetails;

/**
 * @author yanghq
 * @version 0.8.0
 * @since 2025/4/7 16:53
 */
public interface DifyConnectionDetails extends ConnectionDetails {

    String getUrl();
}
