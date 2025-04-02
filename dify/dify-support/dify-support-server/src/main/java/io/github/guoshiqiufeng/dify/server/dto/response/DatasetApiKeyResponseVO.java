package io.github.guoshiqiufeng.dify.server.dto.response;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Data;

import java.io.Serializable;

/**
 * @author yanghq
 * @version 1.0
 * @since 2025/4/1 13:41
 */
@Data
public class DatasetApiKeyResponseVO implements Serializable {
    private static final long serialVersionUID = 5622933209445579199L;

    private String id;

    private String type;

    private String token;

    @JsonAlias("last_used_at")
    private Long lastUsedAt;

    @JsonAlias("created_at")
    private Long createdAt;

}
