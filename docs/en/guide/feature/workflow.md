---
lang: en-US
title: Workflow API
description: 
---

# Workflow API

## Interface Overview

The workflow service interface provides complete workflow management and execution capabilities, including workflow
running,
log querying, streaming response, and other core functions. All interfaces require a valid API key for authentication.
Use the `DifyWorkflow` interface instance.

## 1. Workflow Execution

### 1.1 Run Workflow

#### Method

```java
WorkflowRunResponse runWorkflow(WorkflowRunRequest request);
```

#### Request Parameters

WorkflowRunRequest

| Parameter name | Type               | Required | Description      |
|----------------|--------------------|----------|------------------|
| apiKey         | String             | Yes      | API Key          |
| inputs         | Map<String,Object> | Yes      | Input parameters |

#### Response Parameters

WorkflowRunResponse

| Parameter name | Type   | Description              |
|----------------|--------|--------------------------|
| id             | String | Workflow run instance ID |
| status         | String | Execution status         |
| message        | String | Execution message        |
| taskId         | String | Task ID                  |

### 1.2 Run Workflow with Streaming Response

#### Method

```java
Flux<WorkflowRunResponse> runWorkflowStream(WorkflowRunRequest request);
```

#### Request Parameters

Same as the Run Workflow interface

#### Response Parameters

Returns a message stream, each message formatted the same as the Run Workflow response

### 1.3 Stop Workflow Stream

#### Method

```java
WorkflowStopResponse stopWorkflowStream(String apiKey, String taskId, String userId);
```

#### Request Parameters

| Parameter name | Type   | Required | Description |
|----------------|--------|----------|-------------|
| apiKey         | String | Yes      | API Key     |
| taskId         | String | Yes      | Task ID     |
| userId         | String | Yes      | User ID     |

#### Response Parameters

WorkflowStopResponse

| Parameter name | Type   | Description     |
|----------------|--------|-----------------|
| result         | String | Stopping result |

### 1.4 Get Workflow Run Details

#### Method

```java
WorkflowRunResponse info(String workflowRunId, String apiKey);
```

#### Request Parameters

| Parameter name | Type   | Required | Description              |
|----------------|--------|----------|--------------------------|
| workflowRunId  | String | Yes      | Workflow run instance ID |
| apiKey         | String | Yes      | API Key                  |

#### Response Parameters

Same as the Run Workflow response

## 2. Workflow Logs

### 2.1 Query Workflow Logs

#### Method

```java
DifyPageResult<WorkflowLogs> logs(WorkflowLogsRequest request);
```

#### Request Parameters

WorkflowLogsRequest

| Parameter name | Type    | Required | Description                  |
|----------------|---------|----------|------------------------------|
| apiKey         | String  | Yes      | API Key                      |
| lastId         | String  | No       | Last record ID               |
| limit          | Integer | No       | Records per page, default 20 |

#### Response Parameters

WorkflowLogs

| Parameter name | Type   | Description          |
|----------------|--------|----------------------|
| id             | String | Log ID               |
| status         | String | Execution status     |
| message        | String | Log message          |
| createdAt      | Long   | Creation timestamp   |
| finishedAt     | Long   | Completion timestamp |
