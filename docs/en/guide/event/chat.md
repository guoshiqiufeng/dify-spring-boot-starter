---
lang: zh-cn
title: Chat event
description: 
---

## How to Use

Using the spring scan instance, implement the `PipelineProcess` interface, specifying the generic type as
`ChatMessagePipelineModel`.
> Currently only calls to `sendChatMessageStream` trigger the corresponding chat event.

```java
@Slf4j
@Component
public class ChatInterceptor implements PipelineProcess<ChatMessagePipelineModel> {

    /**
     * 处理
     *
     * @param context 内容
     */
    @Override
    public void process(PipelineContext<ChatMessagePipelineModel> context) {
        log.debug("ChatInterceptor context:{}", context);
    }
}
```

## Custom Filters

> Override the `support` method to implement custom filtering logic

```java

@Slf4j
@Component
public class ChatInterceptor implements PipelineProcess<ChatMessagePipelineModel> {

    /**
     * 是否支持
     *
     * @param context 内容
     * @return 是否支持 true 支持 false 不支持
     */
    @Override
    public boolean support(PipelineContext<ChatMessagePipelineModel> context) {
        return "message_end".equals(context.getModel().getEvent());
    }

    /**
     * 处理
     *
     * @param context 内容
     */
    @Override
    public void process(PipelineContext<ChatMessagePipelineModel> context) {
        log.debug("ChatInterceptor context:{}", context);
    }
}
```

## Customize the execution order

> Override the `order` method, which returns a sort value. The smaller the value, the earlier it is executed.

```java
@Slf4j
@Component
public class ChatInterceptor implements PipelineProcess<ChatMessagePipelineModel> {

    /**
     * 获取排序，越小越靠前
     *
     * @return 排序
     */
    @Override
    public Long order() {
        return 233L;
    }
    
    /**
     * 是否支持
     *
     * @param context 内容
     * @return 是否支持 true 支持 false 不支持
     */
    @Override
    public boolean support(PipelineContext<ChatMessagePipelineModel> context) {
        return "message_end".equals(context.getModel().getEvent());
    }
    
    /**
     * 处理
     *
     * @param context 内容
     */
    @Override
    public void process(PipelineContext<ChatMessagePipelineModel> context) {
        log.debug("ChatInterceptor context:{}", context);
    }
}
```
