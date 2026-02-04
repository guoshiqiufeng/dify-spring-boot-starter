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
 * Masking context for masking operations
 *
 * @author yanghq
 * @version 2.1.0
 * @since 2026/2/3
 */
public final class MaskingContext {

    private final MaskingConfig config;
    private final MaskingRuleRegistry registry;
    private final int maxBodyLength;
    private final boolean enabled;

    public MaskingContext(MaskingConfig config) {
        this.config = config;
        this.registry = config.getRegistry();
        this.maxBodyLength = config.getMaxBodyLength();
        this.enabled = config.isEnabled();
    }

    public MaskingConfig getConfig() {
        return config;
    }

    public MaskingRuleRegistry getRegistry() {
        return registry;
    }

    public int getMaxBodyLength() {
        return maxBodyLength;
    }

    public boolean isEnabled() {
        return enabled;
    }
}
