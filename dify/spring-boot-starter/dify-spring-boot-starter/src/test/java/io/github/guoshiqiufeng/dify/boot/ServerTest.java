package io.github.guoshiqiufeng.dify.boot;

import cn.hutool.json.JSONUtil;
import io.github.guoshiqiufeng.dify.boot.base.BaseServerContainerTest;
import io.github.guoshiqiufeng.dify.server.DifyServer;
import io.github.guoshiqiufeng.dify.server.dto.response.DatasetApiKeyResponseVO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.List;

/**
 * @author yanghq
 * @version 1.0
 * @since 2025/3/31 09:55
 */
@Slf4j
public class ServerTest extends BaseServerContainerTest {

    @Resource
    private DifyServer difyServer;

    @Test
    public void datasetApiKeyTest() {
        List<DatasetApiKeyResponseVO> datasetApiKey = difyServer.getDatasetApiKey();
        log.debug("{}", JSONUtil.toJsonStr(datasetApiKey));
        List<DatasetApiKeyResponseVO> datasetApiKeyResponseVOS = difyServer.initDatasetApiKey();
        log.debug("{}", JSONUtil.toJsonStr(datasetApiKeyResponseVOS));
    }

}
