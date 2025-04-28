/*
 * Copyright (c) 2025-2025, fubluesky (fubluesky@foxmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.guoshiqiufeng.dify.server.exception;

import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Utility class to help test exceptions
 *
 * @author yanghq
 * @version 0.10.0
 * @since 2025/4/27
 */
public class ExceptionTestUtil {

    /**
     * A method that throws the given exception
     *
     * @param exception the exception to throw
     * @param <T>       the type of exception
     * @throws T the exception
     */
    public static <T extends Throwable> void throwException(T exception) throws T {
        throw exception;
    }

    /**
     * Execute a runnable and assert that it throws the expected exception
     *
     * @param exceptionClass the expected exception class
     * @param runnable       the code to execute
     * @param <T>            the type of exception
     * @return the thrown exception
     */
    public static <T extends Throwable> T assertThrowsException(Class<T> exceptionClass, RunnableWithException runnable) {
        return assertThrows(exceptionClass, () -> {
            try {
                runnable.run();
            } catch (Throwable t) {
                if (exceptionClass.isInstance(t)) {
                    throw t;
                } else {
                    throw new RuntimeException("Unexpected exception", t);
                }
            }
        });
    }

    /**
     * Functional interface for a runnable that can throw any exception
     */
    @FunctionalInterface
    public interface RunnableWithException {
        void run() throws Throwable;
    }
} 