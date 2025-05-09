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
package io.github.guoshiqiufeng.dify.boot.application;

import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import java.time.Duration;

/**
 * @author yanghq
 * @version 1.0
 * @since 2025/3/31 09:42
 */
@SpringBootApplication(excludeName = "io.github.guoshiqiufeng.dify.boot")
public class DifyTestSpringBootApplication {

    public static void main(String[] args) {
        SpringApplication.run(DifyTestSpringBootApplication.class, args);
    }

    @Bean
    public RestClient.Builder restClientBuilder() {
        HttpClient httpClient = HttpClientBuilder.create()
                .setConnectionManager(new PoolingHttpClientConnectionManager())
                .build();
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory(httpClient);
        factory.setConnectTimeout(Duration.ofSeconds(10));
        factory.setReadTimeout(Duration.ofSeconds(60));
        return RestClient.builder().requestInterceptor(new LoggingInterceptor()).requestFactory(factory);
    }

}
