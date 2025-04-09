package io.github.guoshiqiufeng.dify.server.client;

/**
 * 请求执行器接口
 *
 * @author yanghq
 * @version 0.8.0
 * @since 2025/4/09 15:44
 */
@FunctionalInterface
public interface RequestSupplier<T> {
    T get();
}
