---
lang: en-US
title: Guide
description: en-US
---

# Guide

[dify-spring-boot-starter](https://github.com/guoshiqiufeng/dify-spring-boot-starter) is a Dify API client framework based on Spring Boot and pure Java.

::: tip Version 2.0 Major Update
Version 2.0.0 introduces a modular architecture refactoring with support for pure Java projects (no Spring required), flexible HTTP clients, and multiple JSON codec options. See [Changelog](https://github.com/guoshiqiufeng/dify-spring-boot-starter/blob/next/CHANGELOG-2.0.md)
:::

## Characteristics

- **Non-intrusive**: Non-intrusive integration calls through spring-boot-starter approach
- **Standardized**: Based on Dify interface specifications
- **Modular Architecture**: Framework-agnostic core abstractions supporting multiple HTTP client implementations
- **Pure Java Support**: Version 2.0+ supports pure Java projects (no Spring required)
- **Flexible Codecs**: Support for Gson, Jackson 2.x, and Jackson 3.x JSON libraries

## Supported Services

- Chat (Chat Related)
- Workflow (Workflow Related)
- Dataset (Knowledge Base)
- Server (Features without open interfaces)
- Status (Service Status Monitoring)

## Core Modules

### Version 2.0 Module Architecture

- **dify-core**: Core module with base classes and interfaces
- **dify-client**: Network request layer encapsulation (framework-agnostic)
  - **dify-client-core**: Client core abstractions and interfaces
  - **dify-client-codec**: Codec layer
    - dify-client-codec-gson: Gson implementation
    - dify-client-codec-jackson: Jackson 2.x/3.x implementation
  - **dify-client-integration**: HTTP client integration layer
    - dify-client-integration-okhttp: OkHttp implementation (pure Java)
    - dify-client-integration-spring: Spring WebClient/RestClient implementation
- **dify-support**: Dify business logic interface definitions
  - dify-support-chat: Chat feature interfaces
  - dify-support-dataset: Dataset feature interfaces
  - dify-support-workflow: Workflow feature interfaces
  - dify-support-server: Server management feature interfaces
- **dify-support-impl**: Unified business logic implementation (shared across all Spring versions)
- **dify-status**: Service status monitoring module
- **starter**: Starters
  - **dify-java-starter**: Pure Java project starter (2.0+)
  - **dify-spring-boot-starter**: Spring Boot 3.1+ starter
  - **dify-spring-boot2-starter**: Spring Boot 2.x/3.0.x starter
  - **dify-spring-boot4-starter**: Spring Boot 4.x starter

## Features

Legend:

- ✅ Available
- 🚧 In Progress

### Chat Features

| Feature                                             | Status |
|-----------------------------------------------------|--------|
| Send Message                                        | ✅      |
| Send Message Stream                                 | ✅      |
| Stop Message Stream                                 | ✅      |
| Message Feedback (Likes)                            | ✅      |
| Message Feedbacks                                   | ✅      |
| Get Session List                                    | ✅      |
| Get Message List                                    | ✅      |
| Get Suggested Messages List                         | ✅      |
| Delete Session                                      | ✅      |
| Rename Session                                      | ✅      |
| Get Application Parameters                          | ✅      |
| Text-to-Speech                                      | ✅      |
| Speech-to-Text                                      | ✅      |
| File Upload                                         | ✅      |
| File Preview                                        | ✅      |
| Get Application Basic Information                   | ✅      |
| Get Application Meta Information                    | ✅      |
| Get Application WebApp Settings                     | ✅      |
| Get Annotation List                                 | ✅      |
| Create Annotation                                   | ✅      |
| Update Annotation                                   | ✅      |
| Delete Annotation                                   | ✅      |
| Initial Annotation Reply Settings                   | ✅      |
| Query Initial Annotation Reply Settings Task Status | ✅      |

### Workflow Features

| Feature              | Status |
|----------------------|--------|
| Run Workflow         | ✅      |
| Run Workflow Stream  | ✅      |
| Stop Workflow Stream | ✅      |
| Workflow Logs        | ✅      |

### Dataset (Knowledge Base) Features

| Feature                                                      | Status |
|--------------------------------------------------------------|--------|
| Create Document from Text                                    | ✅      |
| Create Document from File                                    | ✅      |
| Create Empty Knowledge Base                                  | ✅      |
| Get Knowledge Base Details by ID                             | ✅      |
| Update Knowledge Base                                        | ✅      |
| Get Knowledge Base List                                      | ✅      |
| Delete Knowledge Base                                        | ✅      |
| Update Document with Text                                    | ✅      |
| Update Document with File                                    | ✅      |
| Get Document Embedding Status (Progress)                     | ✅      |
| Delete Document                                              | ✅      |
| Get Document List of Knowledge Base                          | ✅      |
| Get Document Details                                         | ✅      |
| Get Document Details with Metadata Filtering                 | ✅      |
| Get Segment Details                                          | ✅      |
| Add Chunks to Document                                       | ✅      |
| Get Chunks from Document                                     | ✅      |
| Delete Chunk in Document                                     | ✅      |
| Update Chunk in Document                                     | ✅      |
| Create Child Chunk                                           | ✅      |
| Get Child Chunks                                             | ✅      |
| Delete Child Chunk                                           | ✅      |
| Update Child Chunk                                           | ✅      |
| Get Upload File  (dify was temporarily removed after v1.9.2) | ✅      |
| Retrieve Chunks from Knowledge Base                          | ✅      |
| Create Knowledge Metadata                                    | ✅      |
| Update Knowledge Metadata                                    | ✅      |
| Delete Knowledge Metadata                                    | ✅      |
| Disable/Enable Built-in Metadata                             | ✅      |
| Update Documents Metadata                                    | ✅      |
| Get Knowledge Metadata List                                  | ✅      |
| Get Available Embedding Models                               | ✅      |
| Get Rerank Models                                            | ✅      |
| Create Knowledge Base Type Tag                               | ✅      |
| Modify Knowledge Base Type Tag Name                          | ✅      |
| Delete Knowledge Base Type Tag                               | ✅      |
| Bind Dataset to Knowledge Base Type Tag                      | ✅      |
| Unbind Dataset and Knowledge Base Type Tag                   | ✅      |
| Query Tags Bound to Dataset                                  | ✅      |

### Server Features

| Feature                                                 | Status |
|---------------------------------------------------------|--------|
| Get All Applications List                               | ✅      |
| Get Applications Page List                              | ✅      |
| Get Application Information                             | ✅      |
| Get Application API Keys                                | ✅      |
| Initialize Application API Key                          | ✅      |
| Delete Application API Key                              | ✅      |
| Get Knowledge Base API Keys                             | ✅      |
| Initialize Knowledge Base API Key                       | ✅      |
| Delete Knowledge Base API Key                           | ✅      |
| Get Application Chat Conversation List                  | ✅      |
| Get Application Daily Conversation Statistics           | ✅      |
| Get Application Daily End Users Statistics              | ✅      |
| Get Application Average Session Interactions Statistics | ✅      |
| Get Application Tokens Per Second Statistics            | ✅      |
| Get Application User Satisfaction Rate Statistics       | ✅      |
| Get Application Token Costs Statistics                  | ✅      |
| Get Application Daily Messages Statistics               | ✅      |

## Code Hosting

> **[GitHub](https://github.com/guoshiqiufeng/dify-spring-boot-starter)**
