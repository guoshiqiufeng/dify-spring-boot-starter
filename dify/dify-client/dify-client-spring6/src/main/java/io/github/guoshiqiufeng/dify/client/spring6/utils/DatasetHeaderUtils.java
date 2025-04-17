package io.github.guoshiqiufeng.dify.client.spring6.utils;

import cn.hutool.core.util.StrUtil;
import io.github.guoshiqiufeng.dify.dataset.dto.request.BaseDatasetRequest;
import org.springframework.http.HttpHeaders;

import java.util.function.Consumer;

/**
 * @author yanghq
 * @version 1.0
 * @since 2025/4/17 11:41
 */
public class DatasetHeaderUtils {

    public static Consumer<HttpHeaders> getHttpHeadersConsumer(BaseDatasetRequest request) {
        return headers -> {
            if (StrUtil.isNotEmpty(request.getApiKey())) {
                headers.setBearerAuth(request.getApiKey());
            }
        };
    }

    public static Consumer<HttpHeaders> getHttpHeadersConsumer(String apikey) {
        return headers -> {
            if (StrUtil.isNotEmpty(apikey)) {
                headers.setBearerAuth(apikey);
            }
        };
    }
}
