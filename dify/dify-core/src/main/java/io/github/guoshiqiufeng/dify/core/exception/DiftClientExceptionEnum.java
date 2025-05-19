package io.github.guoshiqiufeng.dify.core.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author yanghq
 * @version 1.0
 * @since 2025/5/19 13:42
 */
@Getter
@AllArgsConstructor
public enum DiftClientExceptionEnum implements BaseExceptionEnum {

    /**
     * unauthorized
     */
    UNAUTHORIZED(401, "Access token is invalid"),
    /**
     * notFound
     */
    NOT_FOUND(404, "Not Found"),


    ;

    private final Integer code;

    private final String msg;
}
