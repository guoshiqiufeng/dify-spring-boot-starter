---
lang: zh-CN
title: 介绍
description: 
---

# 简介

[dify-spring-boot-starter](https://github.com/guoshiqiufeng/dify-spring-boot-starter) 是一个 基于 springboot
实现的 Dify 接口调用 框架。

## 特性

- **无侵入**: 通过 spring-boot-starter的方式，无侵入式的集成调用。
- **统一规范**： 基于Dify 接口 规范。

## 支持的服务

- Chat (聊天相关)
- Server (没提供开放接口的功能)

## 功能

* 可用 - ✅
* 进行中 - 🚧

| 功能                     | Dify接口地址                                | 状态 |   
|------------------------|-----------------------------------------|----|
| 【CHAT】发送消息             | /v1/chat-messages                       | ✅  |    
| 【CHAT】发送消息并获取消息流       | /v1/chat-messages                       | ✅  |
| 【CHAT】停止消息流            | /v1/chat-messages/:task_id/stop         | ✅  |   
| 【CHAT】消息反馈（点赞）         | /v1/chat-messages/:message_id/feedbacks | ✅  |    
| 【CHAT】获取会话列表           | /v1/conversations                       | ✅  |    
| 【CHAT】获取消息列表           | /v1/messages                            | ✅  |    
| 【CHAT】获取建议消息列表         | /v1/messages/:message_id/suggested      | ✅  |    
| 【CHAT】删除会话             | /v1/conversations/:conversation_id      | ✅  |    
| 【CHAT】会话重命名            | /v1/conversations/:conversation_id/name | ✅  |    
| 【CHAT】获取应用参数           | /v1/parameters                          | ✅  |    
| 【CHAT】文本转语音            | /v1/text-to-audio                       | ✅  |    
| 【CHAT】语音转文本            | /v1/audio-to-text                       | ✅  |    
| 【WORKFLOW】 执行工作流       | /v1/workflows/run                       | ✅  |   
| 【WORKFLOW】 执行工作流并获取工作流 | /v1/workflows/run                       | ✅  |   
| 【WORKFLOW】 停止工作流响应     | /v1/workflows/tasks/:task_id/stop       | ✅  |  
| 【WORKFLOW】 获取工作流日志     | /v1/workflows/logs                      | ✅  |   
| 【SERVER】               |                                         | 🚧 |    

注：

## 代码托管

> **[GitHub](https://github.com/guoshiqiufeng/dify-spring-boot-starter)**
