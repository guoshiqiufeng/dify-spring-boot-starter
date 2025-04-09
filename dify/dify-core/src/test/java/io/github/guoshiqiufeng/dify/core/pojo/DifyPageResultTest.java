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
package io.github.guoshiqiufeng.dify.core.pojo;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

class DifyPageResultTest {

    @Test
    void testEmptyConstructor() {
        DifyPageResult<String> result = new DifyPageResult<>();
        assertNull(result.getData());
        assertNull(result.getHasMore());
        assertNull(result.getLimit());
        assertNull(result.getPage());
        assertNull(result.getTotal());
    }

    @Test
    void testSettersAndGetters() {
        DifyPageResult<String> result = new DifyPageResult<>();

        // Test data setter/getter
        List<String> data = Arrays.asList("item1", "item2", "item3");
        result.setData(data);
        assertEquals(data, result.getData());

        // Test hasMore setter/getter
        result.setHasMore(true);
        assertTrue(result.getHasMore());

        // Test limit setter/getter
        result.setLimit(10);
        assertEquals(Integer.valueOf(10), result.getLimit());

        // Test page setter/getter
        result.setPage(2);
        assertEquals(Integer.valueOf(2), result.getPage());

        // Test total setter/getter
        result.setTotal(100);
        assertEquals(Integer.valueOf(100), result.getTotal());
    }

    @Test
    void testWithDifferentDataTypes() {
        // Test with String data
        DifyPageResult<String> stringResult = new DifyPageResult<>();
        List<String> stringData = Arrays.asList("value1", "value2", "value3");
        stringResult.setData(stringData);
        stringResult.setHasMore(true);
        stringResult.setLimit(10);
        stringResult.setPage(1);
        stringResult.setTotal(25);

        assertEquals(stringData, stringResult.getData());
        assertTrue(stringResult.getHasMore());
        assertEquals(Integer.valueOf(10), stringResult.getLimit());
        assertEquals(Integer.valueOf(1), stringResult.getPage());
        assertEquals(Integer.valueOf(25), stringResult.getTotal());

        // Test with Integer data
        DifyPageResult<Integer> intResult = new DifyPageResult<>();
        List<Integer> intData = Arrays.asList(1, 2, 3, 4, 5);
        intResult.setData(intData);
        intResult.setHasMore(false);
        intResult.setLimit(20);
        intResult.setPage(3);
        intResult.setTotal(50);

        assertEquals(intData, intResult.getData());
        assertFalse(intResult.getHasMore());
        assertEquals(Integer.valueOf(20), intResult.getLimit());
        assertEquals(Integer.valueOf(3), intResult.getPage());
        assertEquals(Integer.valueOf(50), intResult.getTotal());

        // Test with complex object data
        DifyPageResult<TestData> objectResult = new DifyPageResult<>();
        List<TestData> objectData = Arrays.asList(
                new TestData("name1", 21),
                new TestData("name2", 22),
                new TestData("name3", 23)
        );
        objectResult.setData(objectData);
        objectResult.setHasMore(true);
        objectResult.setLimit(15);
        objectResult.setPage(2);
        objectResult.setTotal(45);

        assertEquals(objectData, objectResult.getData());
        assertTrue(objectResult.getHasMore());
        assertEquals(Integer.valueOf(15), objectResult.getLimit());
        assertEquals(Integer.valueOf(2), objectResult.getPage());
        assertEquals(Integer.valueOf(45), objectResult.getTotal());
    }

    @Test
    void testEquals() {
        DifyPageResult<String> result1 = new DifyPageResult<>();
        result1.setData(Arrays.asList("item1", "item2"));
        result1.setHasMore(true);
        result1.setLimit(10);
        result1.setPage(1);
        result1.setTotal(20);

        DifyPageResult<String> result2 = new DifyPageResult<>();
        result2.setData(Arrays.asList("item1", "item2"));
        result2.setHasMore(true);
        result2.setLimit(10);
        result2.setPage(1);
        result2.setTotal(20);

        DifyPageResult<String> result3 = new DifyPageResult<>();
        result3.setData(Arrays.asList("different1", "different2"));
        result3.setHasMore(false);
        result3.setLimit(20);
        result3.setPage(2);
        result3.setTotal(40);

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
