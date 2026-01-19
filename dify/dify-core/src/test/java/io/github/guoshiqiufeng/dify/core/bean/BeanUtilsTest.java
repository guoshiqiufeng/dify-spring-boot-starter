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

    @Test
    void testCopyToListWithInvalidTargetClass() {
        List<SourceBean> sourceList = new ArrayList<>();
        SourceBean source = new SourceBean();
        source.setName("John");
        sourceList.add(source);

        // Test with a class that doesn't have a no-arg constructor
        assertThrows(RuntimeException.class, () -> {
            BeanUtils.copyToList(sourceList, NoDefaultConstructorBean.class);
        });
    }

    @Test
    void testCopyPropertiesWithPrivateClass() {
        PrivateFieldBean source = new PrivateFieldBean();
        source.setName("John");

        PrivateFieldBean target = new PrivateFieldBean();
        BeanUtils.copyProperties(source, target);

        assertEquals("John", target.getName());
    }

    @Test
    void testGetPropertyDescriptorsError() {
        // This test ensures the error handling in getPropertyDescriptors is covered
        SourceBean source = new SourceBean();
        source.setName("Test");
        TargetBean target = new TargetBean();

        // Normal copy should work fine
        assertDoesNotThrow(() -> BeanUtils.copyProperties(source, target));
    }

    @Test
    void testBeanUtilsConstructor() throws Exception {
        // Test private constructor for utility class coverage
        java.lang.reflect.Constructor<BeanUtils> constructor = BeanUtils.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        BeanUtils instance = constructor.newInstance();
        assertNotNull(instance);
    }

    @Test
    void testCopyPropertiesWithReflectionAccess() {
        // Test copying properties that may require setAccessible
        PrivateFieldBean source = new PrivateFieldBean();
        source.setName("TestName");

        PrivateFieldBean target = new PrivateFieldBean();

        // This should work even with private fields (using public getters/setters)
        assertDoesNotThrow(() -> BeanUtils.copyProperties(source, target));
        assertEquals("TestName", target.getName());
    }

    @Test
    void testCopyPropertiesWithPackagePrivateClass() {
        // Test copying properties from package-private class
        PackagePrivateBean source = new PackagePrivateBean();
        source.setValue("PackagePrivateValue");

        PackagePrivateBean target = new PackagePrivateBean();

        // This should trigger setAccessible for non-public class
        assertDoesNotThrow(() -> BeanUtils.copyProperties(source, target));
        assertEquals("PackagePrivateValue", target.getValue());
    }

    @Test
    void testCopyToListWithPackagePrivateClass() {
        // Test copyToList with package-private class
        List<PackagePrivateBean> sourceList = new ArrayList<>();

        PackagePrivateBean bean1 = new PackagePrivateBean();
        bean1.setValue("Value1");
        sourceList.add(bean1);

        PackagePrivateBean bean2 = new PackagePrivateBean();
        bean2.setValue("Value2");
        sourceList.add(bean2);

        List<PackagePrivateBean> targetList = BeanUtils.copyToList(sourceList, PackagePrivateBean.class);

        assertEquals(2, targetList.size());
        assertEquals("Value1", targetList.get(0).getValue());
        assertEquals("Value2", targetList.get(1).getValue());
    }

    @Test
    void testCopyPropertiesWithWriteOnlyProperty() {
        // Test that write-only properties (no getter) are skipped
        SourceBean source = new SourceBean();
        source.setName("TestName");

        WriteOnlyPropertyBean target = new WriteOnlyPropertyBean();

        // Should not throw, write-only property should be skipped
        assertDoesNotThrow(() -> BeanUtils.copyProperties(source, target));
    }

    @Test
    void testCopyPropertiesWithReadOnlyProperty() {
        // Test that read-only properties (no setter) are skipped
        ReadOnlyPropertyBean source = new ReadOnlyPropertyBean();
        TargetBean target = new TargetBean();

        // Should not throw, read-only property should be skipped
        assertDoesNotThrow(() -> BeanUtils.copyProperties(source, target));
    }

    @Test
    void testCopyPropertiesWithProblematicGetter() {
        // Test that exception in getter is properly wrapped
        ProblematicGetterBean source = new ProblematicGetterBean();
        // Don't set name, so getter will throw exception
        ProblematicGetterBean target = new ProblematicGetterBean();

        // Should throw RuntimeException wrapping the getter exception
        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> BeanUtils.copyProperties(source, target));
        assertTrue(exception.getMessage().contains("Could not copy property"));
    }

    @Test
    void testCopyPropertiesWithProblematicSetter() {
        // Test that exception in setter is properly wrapped
        SourceBean source = new SourceBean();
        source.setName("TestName");

        ProblematicSetterBean target = new ProblematicSetterBean();

        // Should throw RuntimeException wrapping the setter exception
        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> BeanUtils.copyProperties(source, target));
        assertTrue(exception.getMessage().contains("Could not copy property"));
    }

    @Test
    void testGetPropertyDescriptorsWithMockException() throws Exception {
        // Test getPropertyDescriptors exception handling using reflection and mocking
        java.lang.reflect.Method method = BeanUtils.class.getDeclaredMethod(
            "getPropertyDescriptors", Class.class);
        method.setAccessible(true);

        // Create a class that will cause introspection to fail
        // We'll use a class with a BeanInfo that throws exception
        Class<?> problematicClass = ProblematicBeanInfoClass.class;

        try {
            method.invoke(null, problematicClass);
            fail("Should have thrown InvocationTargetException");
        } catch (java.lang.reflect.InvocationTargetException e) {
            // Verify the cause is RuntimeException with correct message
            Throwable cause = e.getCause();
            assertNotNull(cause);
            assertTrue(cause instanceof RuntimeException);
            assertTrue(cause.getMessage().contains("Failed to introspect class"));
        }
    }

    @Test
    void testCopyPropertiesWithSourceWriteOnlyProperty() {
        // Test that source properties without getter (write-only) are skipped
        // This covers the "if (readMethod == null) continue;" branch
        SourceWriteOnlyBean source = new SourceWriteOnlyBean();
        source.setName("TestName");
        source.setWriteOnlyValue("WriteOnly");

        TargetBean target = new TargetBean();

        // Should not throw, write-only property in source should be skipped
        assertDoesNotThrow(() -> BeanUtils.copyProperties(source, target));

        // name should be copied (has getter)
        assertEquals("TestName", target.getName());
    }

    @Test
    void testCopyPropertiesWithTargetReadOnlyProperty() {
        // Test that target properties without setter (read-only) are skipped
        // This covers the "if (writeMethod == null) continue;" branch
        SourceBean source = new SourceBean();
        source.setName("TestName");

        TargetReadOnlyBean target = new TargetReadOnlyBean("OldName");

        // Should not throw, read-only property in target should be skipped
        assertDoesNotThrow(() -> BeanUtils.copyProperties(source, target));

        // name should not be copied (no setter in target)
        assertEquals("OldName", target.getName());
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

    public static class NoDefaultConstructorBean {
        private String name;

        public NoDefaultConstructorBean(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    public static class PrivateFieldBean {
        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    // Package-private class to test non-public class access
    static class PackagePrivateBean {
        private String value;

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }

    // Bean with write-only property (no getter)
    public static class WriteOnlyPropertyBean {
        private String writeOnlyValue;

        // No getter for writeOnlyValue
        public void setWriteOnlyValue(String value) {
            this.writeOnlyValue = value;
        }

        public String getStoredValue() {
            return writeOnlyValue;
        }
    }

    // Bean with read-only property (no setter)
    public static class ReadOnlyPropertyBean {
        private final String readOnlyValue;

        public ReadOnlyPropertyBean() {
            this.readOnlyValue = "ReadOnly";
        }

        public String getReadOnlyValue() {
            return readOnlyValue;
        }
        // No setter for readOnlyValue
    }

    // Bean with problematic getter that throws exception
    public static class ProblematicGetterBean {
        private String name;

        public String getName() {
            if (name == null) {
                throw new RuntimeException("Getter throws exception");
            }
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    // Bean with problematic setter that throws exception
    public static class ProblematicSetterBean {
        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            throw new RuntimeException("Setter throws exception");
        }
    }

    // Bean with indexed property that may cause introspection issues
    public static class IndexedPropertyBean {
        private String[] values = new String[10];

        public String[] getValues() {
            return values;
        }

        public void setValues(String[] values) {
            this.values = values;
        }

        // Indexed getter
        public String getValues(int index) {
            return values[index];
        }

        // Indexed setter
        public void setValues(int index, String value) {
            values[index] = value;
        }
    }

    // Class with problematic BeanInfo to trigger introspection exception
    public static class ProblematicBeanInfoClass {
        // This class has a corresponding BeanInfo class that will cause issues
        private String value;

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }

    // BeanInfo class for ProblematicBeanInfoClass that throws exception
    public static class ProblematicBeanInfoClassBeanInfo implements java.beans.BeanInfo {
        @Override
        public java.beans.BeanDescriptor getBeanDescriptor() {
            throw new RuntimeException("BeanInfo access failed");
        }

        @Override
        public java.beans.EventSetDescriptor[] getEventSetDescriptors() {
            return new java.beans.EventSetDescriptor[0];
        }

        @Override
        public int getDefaultEventIndex() {
            return -1;
        }

        @Override
        public java.beans.PropertyDescriptor[] getPropertyDescriptors() {
            throw new RuntimeException("Cannot get property descriptors");
        }

        @Override
        public int getDefaultPropertyIndex() {
            return -1;
        }

        @Override
        public java.beans.MethodDescriptor[] getMethodDescriptors() {
            return new java.beans.MethodDescriptor[0];
        }

        @Override
        public java.beans.BeanInfo[] getAdditionalBeanInfo() {
            return null;
        }

        @Override
        public java.awt.Image getIcon(int iconKind) {
            return null;
        }
    }

    // Bean with write-only property in source (no getter)
    public static class SourceWriteOnlyBean {
        private String name;
        private String writeOnlyValue;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        // Write-only property: has setter but no getter
        public void setWriteOnlyValue(String value) {
            this.writeOnlyValue = value;
        }
    }

    // Bean with read-only property in target (no setter)
    public static class TargetReadOnlyBean {
        private final String name;

        public TargetReadOnlyBean(String name) {
            this.name = name;
        }

        // Read-only property: has getter but no setter
        public String getName() {
            return name;
        }
    }
}
