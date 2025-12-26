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
package io.github.guoshiqiufeng.dify.core.pojo;

import org.junit.jupiter.api.Test;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

class DifyResultTest {

    @Test
    void testEmptyConstructor() {
        DifyResult<String> result = new DifyResult<>();
        assertNull(result.getData());
        assertNull(result.getResult());
    }

    @Test
    void testSettersAndGetters() {
        DifyResult<Integer> result = new DifyResult<>();

        // Test data setter/getter
        result.setData(42);
        assertEquals(Integer.valueOf(42), result.getData());

        // Test result setter/getter
        result.setResult("success");
        assertEquals("success", result.getResult());
    }

    @Test
    void testWithDifferentDataTypes() {
        // Test with String data
        DifyResult<String> stringResult = new DifyResult<>();
        stringResult.setData("test data");
        stringResult.setResult("success");
        assertEquals("test data", stringResult.getData());
        assertEquals("success", stringResult.getResult());

        // Test with Integer data
        DifyResult<Integer> intResult = new DifyResult<>();
        intResult.setData(123);
        intResult.setResult("error");
        assertEquals(Integer.valueOf(123), intResult.getData());
        assertEquals("error", intResult.getResult());

        // Test with complex object data
        DifyResult<TestData> objectResult = new DifyResult<>();
        TestData testData = new TestData("test name", 25);
        objectResult.setData(testData);
        objectResult.setResult("success");
        assertEquals(testData, objectResult.getData());
        assertEquals("success", objectResult.getResult());
    }

    @Test
    void testConstantValues() {
        assertEquals("success", DifyResult.SUCCESS);
    }

    @Test
    void testEquals() {
        DifyResult<String> result1 = new DifyResult<>();
        result1.setData("same data");
        result1.setResult("success");

        DifyResult<String> result2 = new DifyResult<>();
        result2.setData("same data");
        result2.setResult("success");

        DifyResult<String> result3 = new DifyResult<>();
        result3.setData("different data");
        result3.setResult("error");

        // Test equals
        assertEquals(result1, result2);
        assertNotEquals(result1, result3);

        // Test hashCode
        assertEquals(result1.hashCode(), result2.hashCode());
        assertNotEquals(result1.hashCode(), result3.hashCode());
    }

    // Helper class for testing
    static class TestData {
        private String name;
        private int age;

        public TestData(String name, int age) {
            this.name = name;
            this.age = age;
        }

        // Getters and setters
        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            TestData testData = (TestData) o;
            return age == testData.age &&
                    (Objects.equals(name, testData.name));
        }

        @Override
        public int hashCode() {
            return 31 * (name != null ? name.hashCode() : 0) + age;
        }
    }
}
