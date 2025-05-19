package io.github.guoshiqiufeng.dify.core.exception;

/**
 * @author yanghq
 * @version 1.0
 * @since 2025/5/19 13:41
 */
public class DifyClientException extends BaseException {

    public DifyClientException(DiftClientExceptionEnum exceptionEnum) {
        super(exceptionEnum.getCode(), exceptionEnum.getMsg());
    }
}
