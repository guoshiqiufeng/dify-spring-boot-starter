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

/**
 * Masking configuration
 *
 * @author yanghq
 * @version 2.1.0
 * @since 2026/2/3
 */
public final class MaskingConfig {

    private final boolean enabled;
    private final int maxBodyLength;
    private final MaskingRuleRegistry registry;

    private MaskingConfig(Builder builder) {
        this.enabled = builder.enabled;
        this.maxBodyLength = builder.maxBodyLength;
        this.registry = builder.registry != null ? builder.registry : MaskingRuleRegistry.createDefault();
    }

    public boolean isEnabled() {
        return enabled;
    }

    public int getMaxBodyLength() {
        return maxBodyLength;
    }

    public MaskingRuleRegistry getRegistry() {
        return registry;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static MaskingConfig createDefault() {
        return builder().build();
    }

    public static class Builder {
        private boolean enabled = true;
        private int maxBodyLength = 1000;
        private MaskingRuleRegistry registry;

        public Builder enabled(boolean enabled) {
            this.enabled = enabled;
            return this;
        }

        public Builder maxBodyLength(int maxBodyLength) {
            this.maxBodyLength = maxBodyLength;
            return this;
        }

        public Builder registry(MaskingRuleRegistry registry) {
            this.registry = registry;
            return this;
        }

        public MaskingConfig build() {
            return new MaskingConfig(this);
        }
    }
}
