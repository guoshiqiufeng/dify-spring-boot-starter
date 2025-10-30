---
lang: en-US
title: Guide
description: en-US
---

# Guide

[dify-spring-boot-starter](https://github.com/guoshiqiufeng/dify-spring-boot-starter) is a Spring Boot starter
implementation of the Dify API calling framework.

## Characteristics

- **Non-intrusive**: Non-intrusive integration calls through spring-boot-starter approach.
- **Standardized**: Based on Dify interface specifications.

## Supported Services

- Chat (Chat Related)
- Workflow (Workflow Related)
- Dataset (Knowledge Base)
- Server (Features without open interfaces)

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

| Feature                                      | Status |
|----------------------------------------------|--------|
| Create Document from Text                    | ✅      |
| Create Document from File                    | ✅      |
| Create Empty Knowledge Base                  | ✅      |
| Get Knowledge Base Details by ID             | ✅      |
| Update Knowledge Base                        | ✅      |
| Get Knowledge Base List                      | ✅      |
| Delete Knowledge Base                        | ✅      |
| Update Document with Text                    | ✅      |
| Update Document with File                    | ✅      |
| Get Document Embedding Status (Progress)     | ✅      |
| Delete Document                              | ✅      |
| Get Document List of Knowledge Base          | ✅      |
| Get Document Details                         | ✅      |
| Get Document Details with Metadata Filtering | ✅      |
| Get Segment Details                          | ✅      |
| Add Chunks to Document                       | ✅      |
| Get Chunks from Document                     | ✅      |
| Delete Chunk in Document                     | ✅      |
| Update Chunk in Document                     | ✅      |
| Create Child Chunk                           | ✅      |
| Get Child Chunks                             | ✅      |
| Delete Child Chunk                           | ✅      |
| Update Child Chunk                           | ✅      |
| Get Upload File                              | ✅      |
| Retrieve Chunks from Knowledge Base          | ✅      |
| Create Knowledge Metadata                    | ✅      |
| Update Knowledge Metadata                    | ✅      |
| Delete Knowledge Metadata                    | ✅      |
| Disable/Enable Built-in Metadata             | ✅      |
| Update Documents Metadata                    | ✅      |
| Get Knowledge Metadata List                  | ✅      |
| Get Available Embedding Models               | ✅      |
| Get Rerank Models                            | ✅      |
| Create Knowledge Base Type Tag               | ✅      |
| Modify Knowledge Base Type Tag Name          | ✅      |
| Delete Knowledge Base Type Tag               | ✅      |
| Bind Dataset to Knowledge Base Type Tag      | ✅      |
| Unbind Dataset and Knowledge Base Type Tag   | ✅      |
| Query Tags Bound to Dataset                  | ✅      |

### Server Features

| Feature                                           | Status |
|---------------------------------------------------|--------|
| Get All Applications List                         | ✅      |
| Get Applications Page List                        | ✅      |
| Get Application Information                       | ✅      |
| Get Application API Keys                          | ✅      |
| Initialize Application API Key                    | ✅      |
| Get Knowledge Base API Keys                       | ✅      |
| Initialize Knowledge Base API Key                 | ✅      |
| Get Application Chat Conversation List            | ✅      |
| Get Application Daily Conversation Statistics     | ✅      |
| Get Application Daily End Users Statistics        | ✅      |
| Get Application Average Session Interactions Statistics | ✅      |
| Get Application Tokens Per Second Statistics      | ✅      |

## Code Hosting

> **[GitHub](https://github.com/guoshiqiufeng/dify-spring-boot-starter)**
