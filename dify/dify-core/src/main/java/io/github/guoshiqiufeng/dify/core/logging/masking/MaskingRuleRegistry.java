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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Registry for masking rules
 *
 * @author yanghq
 * @version 2.1.0
 * @since 2026/2/3
 */
public final class MaskingRuleRegistry {

    private final Map<String, MaskingRule> rulesByField;
    private final List<MaskingRule> rules;

    private MaskingRuleRegistry(List<MaskingRule> rules) {
        this.rules = new ArrayList<>(rules);
        this.rulesByField = new HashMap<>();
        for (MaskingRule rule : rules) {
            for (String fieldName : rule.getFieldNames()) {
                rulesByField.put(fieldName.toLowerCase(), rule);
            }
        }
    }

    /**
     * Find rule by field name (case-insensitive)
     *
     * @param fieldName field name
     * @return masking rule or null if not found
     */
    public MaskingRule findRule(String fieldName) {
        if (fieldName == null) {
            return null;
        }
        return rulesByField.get(fieldName.toLowerCase());
    }

    /**
     * Check if field is sensitive
     *
     * @param fieldName field name
     * @return true if sensitive
     */
    public boolean isSensitive(String fieldName) {
        return findRule(fieldName) != null;
    }

    /**
     * Get all rules
     *
     * @return all rules
     */
    public List<MaskingRule> getRules() {
        return new ArrayList<>(rules);
    }

    /**
     * Create default registry with built-in rules
     *
     * @return default registry
     */
    public static MaskingRuleRegistry createDefault() {
        List<MaskingRule> rules = new ArrayList<>();

        // Phone number rule
        rules.add(MaskingRule.builder()
                .name("phone")
                .fieldNames("phone", "mobile", "tel", "telephone")
                .type(MaskingRule.Type.PARTIAL)
                .kind(MaskingRule.ValueKind.PHONE)
                .keepPrefix(3)
                .keepSuffix(4)
                .maskChar('*')
                .minLength(7)
                .build());

        // ID card rule
        rules.add(MaskingRule.builder()
                .name("idcard")
                .fieldNames("idcard", "id_card", "identity", "identity_no", "idnumber")
                .type(MaskingRule.Type.PARTIAL)
                .kind(MaskingRule.ValueKind.ID_CARD)
                .keepPrefix(6)
                .keepSuffix(4)
                .maskChar('*')
                .minLength(10)
                .build());

        // Email rule
        rules.add(MaskingRule.builder()
                .name("email")
                .fieldNames("email", "mail")
                .type(MaskingRule.Type.PARTIAL)
                .kind(MaskingRule.ValueKind.EMAIL)
                .keepPrefix(2)
                .keepSuffix(1)
                .maskChar('*')
                .minLength(3)
                .build());

        // Token/secret rule (full masking)
        rules.add(MaskingRule.builder()
                .name("token")
                .fieldNames("password", "token", "secret", "apikey", "api_key", "authorization", "credential",
                        "x-api-key", "api-key", "x-auth-token", "cookie", "set-cookie",
                        "access_token", "accessToken", "refresh_token", "refreshToken",
                        "bearer_token", "bearerToken", "session_token", "sessionToken")
                .type(MaskingRule.Type.FULL)
                .kind(MaskingRule.ValueKind.TOKEN)
                .build());

        return new MaskingRuleRegistry(rules);
    }

    /**
     * Create registry from custom rules
     *
     * @param rules custom rules
     * @return registry
     */
    public static MaskingRuleRegistry of(List<MaskingRule> rules) {
        return new MaskingRuleRegistry(rules);
    }

    /**
     * Builder for registry
     *
     * @return builder
     */
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final List<MaskingRule> rules = new ArrayList<>();

        public Builder addRule(MaskingRule rule) {
            rules.add(rule);
            return this;
        }

        public Builder addDefaultRules() {
            rules.addAll(createDefault().getRules());
            return this;
        }

        public MaskingRuleRegistry build() {
            return new MaskingRuleRegistry(rules);
        }
    }
}
