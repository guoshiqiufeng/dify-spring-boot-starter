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

| 功能               | Dify接口地址                       | 状态 |   
|------------------|--------------------------------|----|
| 【CHAT】发送消息       | /v1/chat-messages              | ✅  |    
| 【CHAT】发送消息并获取消息流 | /v1/chat-messages              | ✅  |
| 【CHAT】停止消息流      | /v1/chat-messages/{}/stop      | ✅  |   
| 【CHAT】消息反馈（点赞）   | /v1/chat-messages/{}/feedbacks | ✅  |    
| 【CHAT】获取会话列表     | /v1/conversations              | ✅  |    
| 【CHAT】获取消息列表     | /v1/messages                   | ✅  |    
| 【CHAT】获取建议消息列表   | /v1/messages/{}/suggested      | ✅  |    
| 【CHAT】删除会话       | /v1/conversations/{}           | ✅  |    
| 【CHAT】会话重命名      | /v1/conversations/{}/name      | ✅  |    
| 【CHAT】获取应用参数     | /v1/parameters                 | ✅  |    
| 【CHAT】文本转语音      | /v1/text-to-audio              | ✅  |    
| 【CHAT】语音转文本      | /v1/audio-to-text              | ✅  |    
| 【SERVER】         |                                | 🚧 |    

注：

## 代码托管

> **[GitHub](https://github.com/guoshiqiufeng/dify-spring-boot-starter)**
