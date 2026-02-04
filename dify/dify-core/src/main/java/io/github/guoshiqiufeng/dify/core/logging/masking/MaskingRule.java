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
package io.github.guoshiqiufeng.dify.core.logging.masking;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Masking rule for sensitive field
 *
 * @author yanghq
 * @version 2.1.0
 * @since 2026/2/3
 */
public final class MaskingRule {

    /**
     * Masking type
     */
    public enum Type {
        /**
         * Full masking (e.g., "***MASKED***")
         */
        FULL,

        /**
         * Partial masking (e.g., "138****5678")
         */
        PARTIAL
    }

    /**
     * Value kind for specialized masking
     */
    public enum ValueKind {
        /**
         * Generic value (default)
         */
        GENERIC,

        /**
         * Phone number
         */
        PHONE,

        /**
         * ID card number
         */
        ID_CARD,

        /**
         * Email address
         */
        EMAIL,

        /**
         * Token or secret
         */
        TOKEN
    }

    private final String name;
    private final Set<String> fieldNames; // lower-case
    private final Type type;
    private final ValueKind kind;
    private final int keepPrefix;
    private final int keepSuffix;
    private final char maskChar;
    private final int minLength;

    private MaskingRule(Builder builder) {
        this.name = builder.name;
        this.fieldNames = Collections.unmodifiableSet(builder.fieldNames);
        this.type = builder.type;
        this.kind = builder.kind;
        this.keepPrefix = builder.keepPrefix;
        this.keepSuffix = builder.keepSuffix;
        this.maskChar = builder.maskChar;
        this.minLength = builder.minLength;
    }

    /**
     * Check if this rule matches the given field name (case-insensitive)
     *
     * @param fieldLower field name in lower case
     * @return true if matches
     */
    public boolean matchesField(String fieldLower) {
        return fieldNames.contains(fieldLower);
    }

    /**
     * Apply masking to the raw value
     *
     * @param rawValue raw value
     * @return masked value
     */
    public String apply(String rawValue) {
        if (rawValue == null || rawValue.isEmpty()) {
            return rawValue;
        }

        if (type == Type.FULL) {
            return "***MASKED***";
        }

        // Partial masking
        if (rawValue.length() < minLength) {
            return "***MASKED***"; // Too short, use full masking
        }

        switch (kind) {
            case PHONE:
                return maskPhone(rawValue);
            case ID_CARD:
                return maskIdCard(rawValue);
            case EMAIL:
                return maskEmail(rawValue);
            default:
                return maskGeneric(rawValue);
        }
    }

    private String maskPhone(String value) {
        if (value.length() < keepPrefix + keepSuffix) {
            return "***MASKED***";
        }
        String prefix = value.substring(0, keepPrefix);
        String suffix = value.substring(value.length() - keepSuffix);
        int maskLength = value.length() - keepPrefix - keepSuffix;
        return prefix + repeat(maskChar, maskLength) + suffix;
    }

    private String maskIdCard(String value) {
        if (value.length() < keepPrefix + keepSuffix) {
            return "***MASKED***";
        }
        String prefix = value.substring(0, keepPrefix);
        String suffix = value.substring(value.length() - keepSuffix);
        int maskLength = value.length() - keepPrefix - keepSuffix;
        return prefix + repeat(maskChar, maskLength) + suffix;
    }

    private String maskEmail(String value) {
        int atIndex = value.indexOf('@');
        if (atIndex <= 0) {
            return "***MASKED***";
        }

        String localPart = value.substring(0, atIndex);
        String domain = value.substring(atIndex);

        if (localPart.length() < keepPrefix + keepSuffix) {
            return repeat(maskChar, localPart.length()) + domain;
        }

        String prefix = localPart.substring(0, keepPrefix);
        String suffix = localPart.substring(localPart.length() - keepSuffix);
        int maskLength = localPart.length() - keepPrefix - keepSuffix;
        return prefix + repeat(maskChar, maskLength) + suffix + domain;
    }

    private String maskGeneric(String value) {
        if (value.length() < keepPrefix + keepSuffix) {
            return "***MASKED***";
        }
        String prefix = value.substring(0, keepPrefix);
        String suffix = value.substring(value.length() - keepSuffix);
        int maskLength = value.length() - keepPrefix - keepSuffix;
        return prefix + repeat(maskChar, maskLength) + suffix;
    }

    private String repeat(char ch, int count) {
        if (count <= 0) {
            return "";
        }
        char[] chars = new char[count];
        for (int i = 0; i < count; i++) {
            chars[i] = ch;
        }
        return new String(chars);
    }

    public String getName() {
        return name;
    }

    public Set<String> getFieldNames() {
        return fieldNames;
    }

    public Type getType() {
        return type;
    }

    public ValueKind getKind() {
        return kind;
    }

    public int getKeepPrefix() {
        return keepPrefix;
    }

    public int getKeepSuffix() {
        return keepSuffix;
    }

    public char getMaskChar() {
        return maskChar;
    }

    public int getMinLength() {
        return minLength;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String name;
        private Set<String> fieldNames = new HashSet<>();
        private Type type = Type.FULL;
        private ValueKind kind = ValueKind.GENERIC;
        private int keepPrefix = 0;
        private int keepSuffix = 0;
        private char maskChar = '*';
        private int minLength = 0;

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder fieldNames(String... names) {
            for (String name : names) {
                this.fieldNames.add(name.toLowerCase());
            }
            return this;
        }

        public Builder type(Type type) {
            this.type = type;
            return this;
        }

        public Builder kind(ValueKind kind) {
            this.kind = kind;
            return this;
        }

        public Builder keepPrefix(int keepPrefix) {
            this.keepPrefix = keepPrefix;
            return this;
        }

        public Builder keepSuffix(int keepSuffix) {
            this.keepSuffix = keepSuffix;
            return this;
        }

        public Builder maskChar(char maskChar) {
            this.maskChar = maskChar;
            return this;
        }

        public Builder minLength(int minLength) {
            this.minLength = minLength;
            return this;
        }

        public MaskingRule build() {
            if (name == null || name.isEmpty()) {
                throw new IllegalArgumentException("Rule name is required");
            }
            if (fieldNames.isEmpty()) {
                throw new IllegalArgumentException("At least one field name is required");
            }
            return new MaskingRule(this);
        }
    }
}
