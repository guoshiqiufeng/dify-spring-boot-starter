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

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

/**
 * Bean utilities for property copying similar to Spring BeanUtils
 *
 * @author yanghq
 * @version 1.0
 * @since 2026/1/5 14:27
 */
public class BeanUtils {

    /**
     * Copy property values from the source bean to the target bean.
     * <p>Only properties with matching names and types will be copied.
     * Properties in the source bean with null values will also be copied.
     *
     * @param source the source bean
     * @param target the target bean
     * @throws IllegalArgumentException if source or target is null
     */
    public static void copyProperties(Object source, Object target) {
        copyProperties(source, target, (String[]) null);
    }

    /**
     * Copy a list of source objects to a list of target objects.
     * <p>Creates new instances of the target class for each source object
     * and copies properties using {@link #copyProperties(Object, Object)}.
     *
     * @param <T>         the target object type
     * @param sourceList  the source object list
     * @param targetClass the target class
     * @return list of target objects, or empty list if sourceList is null or empty
     * @throws IllegalArgumentException if targetClass is null
     */
    public static <T> List<T> copyToList(Collection<?> sourceList, Class<T> targetClass) {
        return copyToList(sourceList, targetClass, (String[]) null);
    }

    /**
     * Copy a list of source objects to a list of target objects,
     * ignoring the specified properties.
     *
     * @param <T>              the target object type
     * @param sourceList       the source object list
     * @param targetClass      the target class
     * @param ignoreProperties property names to ignore (can be null)
     * @return list of target objects, or empty list if sourceList is null or empty
     * @throws IllegalArgumentException if targetClass is null
     */
    public static <T> List<T> copyToList(Collection<?> sourceList, Class<T> targetClass, String... ignoreProperties) {
        if (targetClass == null) {
            throw new IllegalArgumentException("Target class must not be null");
        }

        if (sourceList == null || sourceList.isEmpty()) {
            return Collections.emptyList();
        }

        List<T> targetList = new ArrayList<>(sourceList.size());
        for (Object source : sourceList) {
            if (source == null) {
                continue;
            }
            try {
                T target = targetClass.getDeclaredConstructor().newInstance();
                copyProperties(source, target, ignoreProperties);
                targetList.add(target);
            } catch (Exception ex) {
                throw new RuntimeException("Failed to create instance of " + targetClass.getName(), ex);
            }
        }
        return targetList;
    }

    /**
     * Copy property values from the source bean to the target bean,
     * ignoring the specified properties.
     *
     * @param source           the source bean
     * @param target           the target bean
     * @param ignoreProperties property names to ignore (can be null)
     * @throws IllegalArgumentException if source or target is null
     */
    public static void copyProperties(Object source, Object target, String... ignoreProperties) {
        if (source == null) {
            throw new IllegalArgumentException("Source must not be null");
        }
        if (target == null) {
            throw new IllegalArgumentException("Target must not be null");
        }

        Set<String> ignorePropsSet = ignoreProperties != null
                ? new HashSet<>(Arrays.asList(ignoreProperties))
                : new HashSet<>();

        Class<?> sourceClass = source.getClass();
        Class<?> targetClass = target.getClass();

        PropertyDescriptor[] sourceDescriptors = getPropertyDescriptors(sourceClass);
        PropertyDescriptor[] targetDescriptors = getPropertyDescriptors(targetClass);

        for (PropertyDescriptor sourcePd : sourceDescriptors) {
            String propertyName = sourcePd.getName();

            // Skip ignored properties and "class" property
            if (ignorePropsSet.contains(propertyName) || "class".equals(propertyName)) {
                continue;
            }

            Method readMethod = sourcePd.getReadMethod();
            if (readMethod == null) {
                continue;
            }

            // Find matching property in target
            PropertyDescriptor targetPd = findPropertyDescriptor(targetDescriptors, propertyName);
            if (targetPd == null) {
                continue;
            }

            Method writeMethod = targetPd.getWriteMethod();
            if (writeMethod == null) {
                continue;
            }

            // Check if types are compatible
            Class<?> sourcePropertyType = readMethod.getReturnType();
            Class<?> targetPropertyType = writeMethod.getParameterTypes()[0];

            if (!targetPropertyType.isAssignableFrom(sourcePropertyType)) {
                continue;
            }

            try {
                if (!Modifier.isPublic(readMethod.getDeclaringClass().getModifiers())) {
                    readMethod.setAccessible(true);
                }
                Object value = readMethod.invoke(source);

                if (!Modifier.isPublic(writeMethod.getDeclaringClass().getModifiers())) {
                    writeMethod.setAccessible(true);
                }
                writeMethod.invoke(target, value);
            } catch (Exception ex) {
                throw new RuntimeException("Could not copy property '" + propertyName +
                        "' from source to target", ex);
            }
        }
    }

    /**
     * Get property descriptors for the given class.
     *
     * @param clazz the class to introspect
     * @return array of PropertyDescriptors
     */
    private static PropertyDescriptor[] getPropertyDescriptors(Class<?> clazz) {
        try {
            java.beans.BeanInfo beanInfo = java.beans.Introspector.getBeanInfo(clazz);
            return beanInfo.getPropertyDescriptors();
        } catch (Exception ex) {
            throw new RuntimeException("Failed to introspect class: " + clazz.getName(), ex);
        }
    }

    /**
     * Find a PropertyDescriptor by name from an array of descriptors.
     *
     * @param descriptors the array to search
     * @param name        the property name to find
     * @return the matching PropertyDescriptor, or null if not found
     */
    private static PropertyDescriptor findPropertyDescriptor(PropertyDescriptor[] descriptors, String name) {
        for (PropertyDescriptor pd : descriptors) {
            if (pd.getName().equals(name)) {
                return pd;
            }
        }
        return null;
    }
}
