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
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for SpringUtil
 *
 * @author yanghq
 * @version 1.0
 * @since 2026/01/18
 */
class SpringUtilTest {

    private ApplicationContext applicationContext;
    private SpringUtil springUtil;

    @Configuration
    static class TestConfig {
        @Bean("namedBean")
        public TestBean namedBean() {
            return new TestBean("named");
        }

        @Bean
        public AnotherBean anotherBean() {
            return new AnotherBean(123);
        }
    }

    static class TestBean {
        private final String value;

        public TestBean(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    static class AnotherBean {
        private final int number;

        public AnotherBean(int number) {
            this.number = number;
        }

        public int getNumber() {
            return number;
        }
    }

    @BeforeEach
    void setUp() {
        applicationContext = new AnnotationConfigApplicationContext(TestConfig.class);
        springUtil = new SpringUtil();
        springUtil.setApplicationContext(applicationContext);
    }

    @AfterEach
    void tearDown() throws Exception {
        // Reset the static applicationContext field
        Field field = SpringUtil.class.getDeclaredField("applicationContext");
        field.setAccessible(true);
        field.set(null, null);
    }

    @Test
    void testSetApplicationContext() {
        assertNotNull(SpringUtil.getApplicationContext());
        assertEquals(applicationContext, SpringUtil.getApplicationContext());
    }

    @Test
    void testGetApplicationContextWhenNotInitialized() throws Exception {
        // Reset applicationContext to null
        Field field = SpringUtil.class.getDeclaredField("applicationContext");
        field.setAccessible(true);
        field.set(null, null);

        UtilException exception = assertThrows(UtilException.class,
            SpringUtil::getApplicationContext);
        assertEquals("ApplicationContext is not initialized", exception.getMessage());
    }

    @Test
    void testGetBeanByType() {
        TestBean bean = SpringUtil.getBean(TestBean.class);
        assertNotNull(bean);
        assertEquals("named", bean.getValue());
    }

    @Test
    void testGetBeanByTypeNotFound() {
        UtilException exception = assertThrows(UtilException.class,
            () -> SpringUtil.getBean(String.class));
        assertTrue(exception.getMessage().contains("Failed to get bean of type: java.lang.String"));
        assertInstanceOf(NoSuchBeanDefinitionException.class, exception.getCause());
    }

    @Test
    void testGetBeanByName() {
        Object bean = SpringUtil.getBean("namedBean");
        assertNotNull(bean);
        assertInstanceOf(TestBean.class, bean);
        assertEquals("named", ((TestBean) bean).getValue());
    }

    @Test
    void testGetBeanByNameNotFound() {
        UtilException exception = assertThrows(UtilException.class,
            () -> SpringUtil.getBean("nonExistentBean"));
        assertTrue(exception.getMessage().contains("Failed to get bean with name: nonExistentBean"));
        assertInstanceOf(NoSuchBeanDefinitionException.class, exception.getCause());
    }

    @Test
    void testGetBeanByNameAndType() {
        TestBean bean = SpringUtil.getBean("namedBean", TestBean.class);
        assertNotNull(bean);
        assertEquals("named", bean.getValue());
    }

    @Test
    void testGetBeanByNameAndTypeNotFound() {
        UtilException exception = assertThrows(UtilException.class,
            () -> SpringUtil.getBean("nonExistentBean", TestBean.class));
        assertTrue(exception.getMessage().contains("Failed to get bean with name: nonExistentBean"));
        assertTrue(exception.getMessage().contains("type: " + TestBean.class.getName()));
        assertInstanceOf(NoSuchBeanDefinitionException.class, exception.getCause());
    }

    @Test
    void testContainsBeanExists() {
        assertTrue(SpringUtil.containsBean("namedBean"));
        assertTrue(SpringUtil.containsBean("anotherBean"));
    }

    @Test
    void testContainsBeanNotExists() {
        assertFalse(SpringUtil.containsBean("nonExistentBean"));
    }

    @Test
    void testContainsBeanWhenContextNotInitialized() throws Exception {
        // Reset applicationContext to null
        Field field = SpringUtil.class.getDeclaredField("applicationContext");
        field.setAccessible(true);
        field.set(null, null);

        assertFalse(SpringUtil.containsBean("anyBean"));
    }

    @Test
    void testGetMultipleDifferentBeans() {
        TestBean testBean = SpringUtil.getBean(TestBean.class);
        AnotherBean anotherBean = SpringUtil.getBean(AnotherBean.class);

        assertNotNull(testBean);
        assertNotNull(anotherBean);
        assertEquals("named", testBean.getValue());
        assertEquals(123, anotherBean.getNumber());
    }

    @Test
    void testGetBeanByNameReturnsCorrectType() {
        Object bean = SpringUtil.getBean("anotherBean");
        assertInstanceOf(AnotherBean.class, bean);
        assertEquals(123, ((AnotherBean) bean).getNumber());
    }

    @Test
    void testIsSpringEnvironmentWhenInitialized() {
        assertTrue(SpringUtil.isSpringEnvironment());
    }

    @Test
    void testIsSpringEnvironmentWhenNotInitialized() throws Exception {
        // Reset applicationContext to null
        Field field = SpringUtil.class.getDeclaredField("applicationContext");
        field.setAccessible(true);
        field.set(null, null);

        assertFalse(SpringUtil.isSpringEnvironment());
    }
}
