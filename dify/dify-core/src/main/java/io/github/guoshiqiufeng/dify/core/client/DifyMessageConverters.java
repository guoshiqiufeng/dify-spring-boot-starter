package io.github.guoshiqiufeng.dify.core.client;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestClient;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.function.Consumer;

/**
 * @author yanghq
 * @version 0.8.0
 * @since 2025/4/11 13:42
 */
public class DifyMessageConverters {

    public static Consumer<RestClient.Builder> messageConvertersConsumer() {
        return builder ->
                builder.messageConverters(ms -> ms.stream()
                        .filter(c -> c instanceof MappingJackson2HttpMessageConverter)
                        .findFirst()
                        .ifPresent(converter -> {
                            MappingJackson2HttpMessageConverter jacksonConverter = (MappingJackson2HttpMessageConverter) converter;
                            ObjectMapper objectMapper = jacksonConverter.getObjectMapper();
                            objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
                        }));
    }
}
