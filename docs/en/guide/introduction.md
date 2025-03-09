---
lang: en-US
title: Guide
description: en-US
---

# Guide

[dify-spring-boot-starter](https://github.com/guoshiqiufeng/dify-spring-boot-starter) It is based on
spring-cloud-stream
Implementation of the Redis messaging framework.

## Characterization

- **Non-intrusive**: Non-intrusive integration calls by way of spring-boot-starter.
- **Standardize**： Based on the Dify interface specification.

## Supported

- Chat (Chat Related)
- Server (Doesn't provide open interface functionality)

## Features

* Available - ✅
* In progress - 🚧

| Feature                                       | Dify interface address    | status |   
|-----------------------------------------------|---------------------------|--------|
| 【CHAT】Send a message                          | /v1/chat-messages         | ✅      |    
| 【CHAT】Send a message and get the message flow | /v1/chat-messages         | ✅      |    
| 【CHAT】Stop Message Flow                       | /v1/chat-messages/{}/stop | ✅      |    
| 【CHAT】Get session list                        | /v1/conversations         | ✅      |    
| 【CHAT】Getting a list of messages              | /v1/messages              | ✅      |    
| 【CHAT】Get a list of suggested messages        | /v1/messages/{}/suggested | ✅      |    
| 【CHAT】Deleting a session                      | /v1/conversations/{}      | ✅      |    
| 【CHAT】Getting Application Parameters          | /v1/parameters            | ✅      |    
| 【CHAT】text-to-speech                          | /v1/text-to-audio         | ✅      |    
| 【CHAT】speech-to-text                          | /v1/audio-to-text         | ✅      |    
| 【SERVER】                                      |                           | 🚧     |    

Tips：

## Code hosting

> **[GitHub](https://github.com/guoshiqiufeng/dify-spring-boot-starter)**
