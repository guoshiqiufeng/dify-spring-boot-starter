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
package io.github.guoshiqiufeng.dify.core.bean;

import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for BeanUtils
 *
 * @author yanghq
 * @version 1.0
 * @since 2026/01/13
 */
class BeanUtilsTest {

    @Test
    void testCopyPropertiesBasic() {
        SourceBean source = new SourceBean();
        source.setName("John");
        source.setAge(30);
        source.setEmail("john@example.com");

        TargetBean target = new TargetBean();
        BeanUtils.copyProperties(source, target);

        assertEquals("John", target.getName());
        assertEquals(30, target.getAge());
        assertEquals("john@example.com", target.getEmail());
    }

    @Test
    void testCopyPropertiesWithNullSource() {
        TargetBean target = new TargetBean();
        assertThrows(IllegalArgumentException.class, () -> {
            BeanUtils.copyProperties(null, target);
        });
    }

    @Test
    void testCopyPropertiesWithNullTarget() {
        SourceBean source = new SourceBean();
        assertThrows(IllegalArgumentException.class, () -> {
            BeanUtils.copyProperties(source, null);
        });
    }

    @Test
    void testCopyPropertiesWithNullValues() {
        SourceBean source = new SourceBean();
        source.setName(null);
        source.setAge(30);

        TargetBean target = new TargetBean();
        target.setName("OldName");

        BeanUtils.copyProperties(source, target);

        assertNull(target.getName()); // Null values are copied
        assertEquals(30, target.getAge());
    }

    @Test
    void testCopyPropertiesWithIgnoreProperties() {
        SourceBean source = new SourceBean();
        source.setName("John");
        source.setAge(30);
        source.setEmail("john@example.com");

        TargetBean target = new TargetBean();
        target.setName("OldName");

        BeanUtils.copyProperties(source, target, "name");

        assertEquals("OldName", target.getName()); // name was ignored
        assertEquals(30, target.getAge());
        assertEquals("john@example.com", target.getEmail());
    }

    @Test
    void testCopyPropertiesWithMultipleIgnoreProperties() {
        SourceBean source = new SourceBean();
        source.setName("John");
        source.setAge(30);
        source.setEmail("john@example.com");

        TargetBean target = new TargetBean();
        BeanUtils.copyProperties(source, target, "name", "age");

        assertNull(target.getName());
        assertEquals(0, target.getAge());
        assertEquals("john@example.com", target.getEmail());
    }

    @Test
    void testCopyPropertiesWithIncompatibleTypes() {
        SourceBean source = new SourceBean();
        source.setName("John");
        source.setAge(30);

        IncompatibleBean target = new IncompatibleBean();
        BeanUtils.copyProperties(source, target);

        // name should be copied (String to String)
        assertEquals("John", target.getName());
        // age should not be copied (int to String - incompatible)
        assertNull(target.getAge());
    }

    @Test
    void testCopyPropertiesWithMissingProperties() {
        SourceBean source = new SourceBean();
        source.setName("John");
        source.setAge(30);

        PartialBean target = new PartialBean();
        BeanUtils.copyProperties(source, target);

        // Only name exists in target
        assertEquals("John", target.getName());
    }

    @Test
    void testCopyToListBasic() {
        List<SourceBean> sourceList = new ArrayList<>();

        SourceBean source1 = new SourceBean();
        source1.setName("John");
        source1.setAge(30);
        sourceList.add(source1);

        SourceBean source2 = new SourceBean();
        source2.setName("Jane");
        source2.setAge(25);
        sourceList.add(source2);

        List<TargetBean> targetList = BeanUtils.copyToList(sourceList, TargetBean.class);

        assertEquals(2, targetList.size());
        assertEquals("John", targetList.get(0).getName());
        assertEquals(30, targetList.get(0).getAge());
        assertEquals("Jane", targetList.get(1).getName());
        assertEquals(25, targetList.get(1).getAge());
    }

    @Test
    void testCopyToListWithNullTargetClass() {
        List<SourceBean> sourceList = new ArrayList<>();
        assertThrows(IllegalArgumentException.class, () -> {
            BeanUtils.copyToList(sourceList, null);
        });
    }

    @Test
    void testCopyToListWithNullSourceList() {
        List<TargetBean> targetList = BeanUtils.copyToList(null, TargetBean.class);
        assertNotNull(targetList);
        assertTrue(targetList.isEmpty());
    }

    @Test
    void testCopyToListWithEmptySourceList() {
        List<SourceBean> sourceList = new ArrayList<>();
        List<TargetBean> targetList = BeanUtils.copyToList(sourceList, TargetBean.class);
        assertNotNull(targetList);
        assertTrue(targetList.isEmpty());
    }

    @Test
    void testCopyToListWithNullElements() {
        List<SourceBean> sourceList = new ArrayList<>();
        sourceList.add(new SourceBean());
        sourceList.add(null);
        sourceList.add(new SourceBean());

        List<TargetBean> targetList = BeanUtils.copyToList(sourceList, TargetBean.class);

        // Null elements should be skipped
        assertEquals(2, targetList.size());
    }

    @Test
    void testCopyToListWithIgnoreProperties() {
        List<SourceBean> sourceList = new ArrayList<>();

        SourceBean source = new SourceBean();
        source.setName("John");
        source.setAge(30);
        sourceList.add(source);

        List<TargetBean> targetList = BeanUtils.copyToList(sourceList, TargetBean.class, "age");

        assertEquals(1, targetList.size());
        assertEquals("John", targetList.get(0).getName());
        assertEquals(0, targetList.get(0).getAge()); // age was ignored
    }

    @Test
    void testCopyToListWithSet() {
        Set<SourceBean> sourceSet = new HashSet<>();

        SourceBean source1 = new SourceBean();
        source1.setName("John");
        sourceSet.add(source1);

        SourceBean source2 = new SourceBean();
        source2.setName("Jane");
        sourceSet.add(source2);

        List<TargetBean> targetList = BeanUtils.copyToList(sourceSet, TargetBean.class);

        assertEquals(2, targetList.size());
    }

    @Test
    void testCopyPropertiesWithNestedObjects() {
        SourceBean source = new SourceBean();
        source.setName("John");
        source.setAge(30);

        TargetBean target = new TargetBean();
        BeanUtils.copyProperties(source, target);

        assertEquals("John", target.getName());
        assertEquals(30, target.getAge());
    }

    @Test
    void testCopyPropertiesDoesNotCopyClassProperty() {
        SourceBean source = new SourceBean();
        source.setName("John");

        TargetBean target = new TargetBean();
        BeanUtils.copyProperties(source, target);

        // Class property should not be copied
        assertEquals(TargetBean.class, target.getClass());
    }

    @Test
    void testCopyPropertiesWithReadOnlyProperties() {
        ReadOnlyBean source = new ReadOnlyBean("John");
        TargetBean target = new TargetBean();

        BeanUtils.copyProperties(source, target);

        // Read-only property should be copied if target has a setter
        assertEquals("John", target.getName());
    }

    @Test
    void testCopyPropertiesWithWriteOnlyProperties() {
        SourceBean source = new SourceBean();
        source.setName("John");

        WriteOnlyBean target = new WriteOnlyBean();
        BeanUtils.copyProperties(source, target);

        // Write-only property should receive the value
        assertEquals("John", target.getStoredName());
    }

    @Test
    void testCopyPropertiesWithBooleanProperties() {
        BooleanBean source = new BooleanBean();
        source.setActive(true);
        source.setEnabled(false);

        BooleanBean target = new BooleanBean();
        BeanUtils.copyProperties(source, target);

        assertTrue(target.isActive());
        assertFalse(target.isEnabled());
    }

    @Test
    void testCopyPropertiesWithPrimitiveTypes() {
        PrimitiveBean source = new PrimitiveBean();
        source.setIntValue(42);
        source.setLongValue(123L);
        source.setDoubleValue(3.14);
        source.setBooleanValue(true);

        PrimitiveBean target = new PrimitiveBean();
        BeanUtils.copyProperties(source, target);

        assertEquals(42, target.getIntValue());
        assertEquals(123L, target.getLongValue());
        assertEquals(3.14, target.getDoubleValue(), 0.001);
        assertTrue(target.isBooleanValue());
    }

    // Test beans

    public static class SourceBean {
        private String name;
        private int age;
        private String email;

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

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }
    }

    public static class TargetBean {
        private String name;
        private int age;
        private String email;

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

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }
    }

    public static class IncompatibleBean {
        private String name;
        private String age; // Different type from source

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getAge() {
            return age;
        }

        public void setAge(String age) {
            this.age = age;
        }
    }

    public static class PartialBean {
        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    public static class ReadOnlyBean {
        private final String name;

        public ReadOnlyBean(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    public static class WriteOnlyBean {
        private String storedName;

        public void setName(String name) {
            this.storedName = name;
        }

        public String getStoredName() {
            return storedName;
        }
    }

    public static class BooleanBean {
        private boolean active;
        private boolean enabled;

        public boolean isActive() {
            return active;
        }

        public void setActive(boolean active) {
            this.active = active;
        }

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }
    }

    public static class PrimitiveBean {
        private int intValue;
        private long longValue;
        private double doubleValue;
        private boolean booleanValue;

        public int getIntValue() {
            return intValue;
        }

        public void setIntValue(int intValue) {
            this.intValue = intValue;
        }

        public long getLongValue() {
            return longValue;
        }

        public void setLongValue(long longValue) {
            this.longValue = longValue;
        }

        public double getDoubleValue() {
            return doubleValue;
        }

        public void setDoubleValue(double doubleValue) {
            this.doubleValue = doubleValue;
        }

        public boolean isBooleanValue() {
            return booleanValue;
        }

        public void setBooleanValue(boolean booleanValue) {
            this.booleanValue = booleanValue;
        }
    }
}
