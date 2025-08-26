---
lang: en
title: Instructions
description: 
---

## Features

- Support for custom filters
- The user can realize his own unified business processing, such as encrypted message sending and receiving and
  decryption of messages.

> Using the Spring Scanning Instance to Implement the `PipelineProcess` Interface

## PipelineProcess Interface

| Method Name | Request Parameter Types | Return parameter type | Remarks                                                                                                          |
|-------------|-------------------------|-----------------------|------------------------------------------------------------------------------------------------------------------|
| support     | PipelineContext         | boolean               | The user determines whether the current filter is effective, return true is supported and false is not supported |
| order       | -                       | Long                  | Get sorting, smaller comes first                                                                                 |
| process     | PipelineContext         | -                     | Filter processing logic                                                                                          |

## PipelineContext

| Parameter name | Types    | Remarks                               |
|----------------|----------|---------------------------------------|
| needBreak      | boolean  | Whether to terminate the filter chain |
| code           | String   | Type encodings                        |
| model          | Generics | Intercepted content                   |
