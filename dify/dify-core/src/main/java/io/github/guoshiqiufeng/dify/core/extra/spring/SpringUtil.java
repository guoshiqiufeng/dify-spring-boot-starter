/*
 * Copyright (c) 2025-2026, fubluesky (fubluesky@foxmail.com)
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
package io.github.guoshiqiufeng.dify.core.extra.spring;

import io.github.guoshiqiufeng.dify.core.exception.UtilException;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * Spring utility class for getting beans from ApplicationContext
 *
 * @author yanghq
 * @version 1.0
 * @since 2026/1/5 14:30
 */
@Component
public class SpringUtil implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        SpringUtil.applicationContext = applicationContext;
    }

    /**
     * Get ApplicationContext
     *
     * @return ApplicationContext
     * @throws UtilException if ApplicationContext is not initialized
     */
    public static ApplicationContext getApplicationContext() {
        if (applicationContext == null) {
            throw new UtilException("ApplicationContext is not initialized");
        }
        return applicationContext;
    }

    /**
     * Get bean by type
     *
     * @param clazz bean class
     * @param <T>   bean type
     * @return bean instance
     * @throws UtilException if bean cannot be found or ApplicationContext is not initialized
     */
    public static <T> T getBean(Class<T> clazz) {
        try {
            return getApplicationContext().getBean(clazz);
        } catch (BeansException e) {
            throw new UtilException("Failed to get bean of type: " + clazz.getName(), e);
        }
    }

    /**
     * Get bean by name
     *
     * @param name bean name
     * @return bean instance
     * @throws UtilException if bean cannot be found or ApplicationContext is not initialized
     */
    public static Object getBean(String name) {
        try {
            return getApplicationContext().getBean(name);
        } catch (BeansException e) {
            throw new UtilException("Failed to get bean with name: " + name, e);
        }
    }

    /**
     * Get bean by name and type
     *
     * @param name  bean name
     * @param clazz bean class
     * @param <T>   bean type
     * @return bean instance
     * @throws UtilException if bean cannot be found or ApplicationContext is not initialized
     */
    public static <T> T getBean(String name, Class<T> clazz) {
        try {
            return getApplicationContext().getBean(name, clazz);
        } catch (BeansException e) {
            throw new UtilException("Failed to get bean with name: " + name + " and type: " + clazz.getName(), e);
        }
    }

    /**
     * Check if bean exists
     *
     * @param name bean name
     * @return true if bean exists, false otherwise
     */
    public static boolean containsBean(String name) {
        if (applicationContext == null) {
            return false;
        }
        return applicationContext.containsBean(name);
    }

    /**
     * Check if Spring environment is available
     *
     * @return true if ApplicationContext is initialized, false otherwise
     */
    public static boolean isSpringEnvironment() {
        return applicationContext != null;
    }
}
