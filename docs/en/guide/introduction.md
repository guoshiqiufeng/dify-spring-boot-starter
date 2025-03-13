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

| Feature                                | Dify interface address                  | status |   
|----------------------------------------|-----------------------------------------|--------|
| 【CHAT】Send Message                     | /v1/chat-messages                       | ✅      |    
| 【CHAT】Send Message Stream              | /v1/chat-messages                       | ✅      |    
| 【CHAT】Stop Message Stream              | /v1/chat-messages/:task_id/stop         | ✅      |   
| 【CHAT】Message Feedback (Likes)         | /v1/chat-messages/:message_id/feedbacks | ✅      |    
| 【CHAT】Get session list                 | /v1/conversations                       | ✅      |    
| 【CHAT】Getting a list of messages       | /v1/messages                            | ✅      |    
| 【CHAT】Get a list of suggested messages | /v1/messages/:message_id/suggested      | ✅      |    
| 【CHAT】Deleting a session               | /v1/conversations/:conversation_id      | ✅      |    
| 【CHAT】Session rename                   | /v1/conversations/:conversation_id/name | ✅      |  
| 【CHAT】Getting Application Parameters   | /v1/parameters                          | ✅      |    
| 【CHAT】text-to-speech                   | /v1/text-to-audio                       | ✅      |    
| 【CHAT】speech-to-text                   | /v1/audio-to-text                       | ✅      |  
| 【WORKFLOW】 Run Workflow                | /v1/workflows/run                       | ✅      |   
| 【WORKFLOW】 Run Workflow Stream         | /v1/workflows/run                       | ✅      |   
| 【WORKFLOW】 Stop Workflow Stream        | /v1/workflows/tasks/:task_id/stop       | ✅      |  
| 【WORKFLOW】 Workflow Logs               | /v1/workflows/logs                      | ✅      |  
| 【SERVER】                               |                                         | 🚧     |    

Tips：

## Code hosting

> **[GitHub](https://github.com/guoshiqiufeng/dify-spring-boot-starter)**
