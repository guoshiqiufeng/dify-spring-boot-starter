---
lang: zh-cn
title: Workflow API
description: 
---

# Workflow API

## 接口概述

工作流服务接口提供完整的工作流管理和执行能力，包含工作流运行、日志查询、流式响应等核心功能。所有接口均需要有效的API密钥进行身份验证。
使用`DifyWorkflow`接口实例。

## 1. 工作流运行

### 1.1 运行工作流

#### 方法

```java
WorkflowRunResponse runWorkflow(WorkflowRunRequest request);
```

#### 请求参数

WorkflowRunRequest

| 参数名    | 类型                  | 是否必须 | 描述     |
|--------|---------------------|------|--------|
| apiKey | String             | 是    | apiKey |
| inputs | Map<String,Object> | 是    | 输入参数   |

#### 响应参数

WorkflowRunResponse

| 参数名     | 类型     | 描述     |
|---------|--------|--------|
| id      | String | 运行实例id |
| status  | String | 运行状态   |
| message | String | 运行消息   |
| taskId  | String | 任务id   |

### 1.2 运行工作流流式响应

#### 方法

```java
Flux<WorkflowRunResponse> runWorkflowStream(WorkflowRunRequest request);
```

#### 请求参数

与运行工作流接口相同

#### 响应参数

返回消息流，每条消息格式与运行工作流响应相同

### 1.3 停止工作流流式响应

#### 方法

```java
WorkflowStopResponse stopWorkflowStream(String apiKey, String taskId, String userId);
```

#### 请求参数

| 参数名    | 类型     | 是否必须 | 描述     |
|--------|--------|------|--------|
| apiKey | String | 是    | apiKey |
| taskId | String | 是    | 任务id   |
| userId | String | 是    | 用户id   |

#### 响应参数

WorkflowStopResponse

| 参数名    | 类型     | 描述    |
|--------|--------|-------|
| result | String | 停止结果  |

### 1.4 获取工作流运行详情

#### 方法

```java
WorkflowRunResponse info(String workflowRunId, String apiKey);
```

#### 请求参数

| 参数名           | 类型     | 是否必须 | 描述      |
|---------------|--------|------|---------|
| workflowRunId | String | 是    | 运行实例id  |
| apiKey        | String | 是    | apiKey  |

#### 响应参数

与运行工作流响应相同

## 2. 工作流日志

### 2.1 查询工作流日志

#### 方法

```java
DifyPageResult<WorkflowLogs> logs(WorkflowLogsRequest request);
```

#### 请求参数

WorkflowLogsRequest

| 参数名    | 类型      | 是否必须 | 描述          |
|--------|---------|------|-------------|
| apiKey | String  | 是    | apiKey      |
| lastId | String  | 否    | 最后一条记录id    |
| limit  | Integer | 否    | 每页记录数,默认20条 |

#### 响应参数

WorkflowLogs

| 参数名        | 类型     | 描述     |
|------------|--------|--------|
| id         | String | 日志id   |
| status     | String | 运行状态   |
| message    | String | 日志消息   |
| createdAt  | Long   | 创建时间戳  |
| finishedAt | Long   | 完成时间戳  |
