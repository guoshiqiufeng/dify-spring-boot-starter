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

| Parameter name | Type                 | Required | Description      |
|----------------|----------------------|----------|------------------|
| apiKey         | String               | Yes      | API Key          |
| userId         | String               | Yes      | User ID          |
| inputs         | `Map<String,Object>` | No       | Input parameters |
| files          | `List<WorkflowFile>` | No       | File list        |

WorkflowFile

| Parameter name | Type   | Required | Description                                       |
|----------------|--------|----------|---------------------------------------------------|
| type           | String | No       | File type, default is "image"                     |
| transferMethod | String | No       | Transfer method, default "remote_url"             |
| url            | String | No       | File URL                                          |
| uploadFileId   | String | No       | Upload file ID, choose either url or uploadFileId |

#### Response Parameters

WorkflowRunResponse

| Parameter name | Type            | Description     |
|----------------|-----------------|-----------------|
| workflowRunId  | String          | Workflow Run ID |
| taskId         | String          | Task ID         |
| data           | WorkflowRunData | data            |

WorkflowRunData

| Parameter name | Type                  | Description                                     |
|----------------|-----------------------|-------------------------------------------------|
| id             | String                | Workflow Run ID                                 |
| workflowId     | String                | Workflow ID                                     |
| status         | String                | status (running / succeeded / failed / stopped) |
| outputs        | `Map<String, Object>` | outputs                                         |
| error          | String                | error                                           |
| elapsedTime    | Float                 | elapsedTime                                     |
| totalTokens    | Integer               | totalTokens                                     |
| totalSteps     | Integer               | totalSteps                                      |
| createdAt      | Long                  | Creation time (timestamp)                       |
| finishedAt     | Long                  | Completion time (timestamp)                     |

#### Request example

```java

@Resource
private DifyWorkflow difyWorkflow;

@Test
public void test() {
    WorkflowRunRequest request = new WorkflowRunRequest();
    request.setApiKey("app-0M83umUpl8HN1mHjOBYPSa64");
    request.setUserId("6");

    WorkflowRunResponse workflowRunResponse = difyWorkflow.runWorkflow(request);
}
```

```json
{
  "data": {
    "createdAt": 1741773668,
    "elapsedTime": 1.229037,
    "finishedAt": 1741773669,
    "id": "04dce0f3-bd5c-4893-a3d5-1eba2c0c8145",
    "outputs": {
      "text": "绿叶初生燕归来，\n花开满径香盈怀。\n心随蝶舞共春回。",
      "t1": "6"
    },
    "status": "succeeded",
    "totalSteps": 3,
    "totalTokens": 1446,
    "workflowId": "66be1f25-8669-479e-b9e3-511317016d4e"
  },
  "taskId": "7d98910b-44ea-406c-b2d6-5287130f5035",
  "workflowRunId": "04dce0f3-bd5c-4893-a3d5-1eba2c0c8145"
}
```

### 1.2 Run Workflow with Streaming Response

#### Method

```java
Flux<WorkflowRunStreamResponse> runWorkflowStream(WorkflowRunRequest request);
```

#### Request Parameters

Same as the Run Workflow interface

#### Response Parameters

WorkflowRunStreamResponse

| Parameter name | Type            | Description               |
|----------------|-----------------|---------------------------|
| workflowRunId  | String          | Workflow Run ID           |
| taskId         | String          | Task ID                   |
| event          | StreamEventEnum | Event type                |
| data           | Object          | Workflow Run Data Objects |

> Select data type according to event type

| event                    | data                       | Description              |
|--------------------------|----------------------------|--------------------------|
| workflow_started         | WorkflowStartedData        | Workflow started         |
| node_started             | NodeStartedData            | Node started             |
| text_chunk               | Map                        | Text chunk               |
| node_finished            | NodeFinishedData           | Node finished            |
| workflow_finished        | WorkflowFinishedData       | Workflow finished        |
| parallel_branch_started  | ParallelBranchStartedData  | Parallel branch started  |
| parallel_branch_finished | ParallelBranchFinishedData | Parallel branch finished |
| agent_log                | AgentLogData               | Agent log                |

WorkflowStartedData

| Parameter name | Type    | Description               |
|----------------|---------|---------------------------|
| id             | String  | Workflow Run ID           |
| createdAt      | Long    | Creation time (timestamp) |
| workflowId     | String  | Workflow Id               |
| sequenceNumber | Integer | Sequence Number           |

NodeStartedData

| Parameter name    | Type                  | Description                                                              |
|-------------------|-----------------------|--------------------------------------------------------------------------|
| id                | String                | Workflow Run ID                                                          |
| createdAt         | Long                  | Creation time (timestamp)                                                |
| nodeId            | String                | Node Id                                                                  |
| nodeType          | String                | Node Type                                                                |
| title             | String                | Node Name                                                                |
| index             | Integer               | Execution sequence number                                                |
| predecessorNodeId | String                | Predecessor node ID                                                      |
| inputs            | `Map<String, Object>` | Contains the contents of all predecessor node variables used in the node |

NodeFinishedData

| Parameter name    | Type                  | Description                                         |
|-------------------|-----------------------|-----------------------------------------------------|
| id                | String                | Workflow Run ID                                     |
| createdAt         | Long                  | Creation time (timestamp)                           |
| nodeId            | String                | Node Id                                             |
| nodeType          | String                | Node Type                                           |
| title             | String                | Node Name                                           |
| index             | Integer               | Execution sequence number                           |
| predecessorNodeId | String                | Predecessor node ID                                 |
| inputs            | `Map<String, Object>` | Input parameter set                                 |
| processData       | `Map<String, Object>` | Node processing data                                |
| outputs           | `Map<String, Object>` | outputs                                             |
| status            | String                | Execution status (running/succeeded/failed/stopped) |
| error             | String                | Error message (present when status is failed)       |
| elapsedTime       | Float                 | Total elapsed time (seconds)                        |
| executionMetadata | ExecutionMetadata     | ExecutionMetadata                                   |

ExecutionMetadata

| Parameter name | Type       | Description                            |
|----------------|------------|----------------------------------------|
| totalTokens    | Integer    | Total Tokens                           |
| totalPrice     | BigDecimal | Total Price                            |
| currency       | String     | Currency unit (optional, e.g. USD/RMB) |

WorkflowFinishedData

| Parameter name | Type                | Description                                         |
|----------------|---------------------|-----------------------------------------------------|
| id             | String              | Workflow Run ID                                     |
| createdAt      | Long                | Creation time (timestamp)                           |
| workflowId     | String              | Associated Workflow ID                              |
| outputs        | Map<String, Object> | Final Output                                        |
| status         | String              | Execution status (running/succeeded/failed/stopped) |
| error          | String              | Error message (present when status is failed)       |
| elapsedTime    | Float               | Total elapsed time (seconds)                        |
| totalTokens    | Integer             | Total number of Token consumed by the workflow      |
| totalSteps     | Integer             | Total number of steps performed                     |
| finishedAt     | Long                | Completion timestamp (e.g. 1705395332)              |

ParallelBranchStartedData

| Parameter name            | Type   | Description                   |
|---------------------------|--------|-------------------------------|
| parallelId                | String | Parallel task ID              |
| parallelBranchId          | String | Parallel branch ID            |
| parentParallelId          | String | Parent parallel task ID       |
| parentParallelStartNodeId | String | Parent parallel start node ID |
| iterationId               | String | Iteration ID                  |
| loopId                    | String | Loop ID                       |
| createdAt                 | Long   | Creation time (timestamp)     |

ParallelBranchFinishedData

| Parameter name | Type   | Description                                   |
|----------------|--------|-----------------------------------------------|
| status         | String | Execution status                              |
| error          | String | Error message (present when status is failed) |
| Other fields   | -      | Inherited from ParallelBranchStartedData      |

AgentLogData

| Parameter name  | Type                  | Description       |
|-----------------|-----------------------|-------------------|
| nodeExecutionId | String                | Node execution ID |
| id              | String                | Log ID            |
| label           | String                | Label             |
| parentId        | String                | Parent ID         |
| error           | String                | Error message     |
| status          | String                | Status            |
| data            | `Map<String, Object>` | Data              |
| metadata        | MetaData              | Metadata          |
| nodeId          | String                | Node ID           |

#### Request example

```java

@Resource
private DifyWorkflow difyWorkflow;

public void test() {
    WorkflowRunRequest request = new WorkflowRunRequest();
    request.setApiKey("app-0M83umUpl8HN1mHjOBYPSa64");
    request.setUserId("6");

    Flux<WorkflowRunStreamResponse> workflowRunResponseFlux = difyWorkflow.runWorkflowStream(request);
}
```

```json lines
{
  "data": {
    "sequenceNumber": 38,
    "workflowId": "66be1f25-8669-479e-b9e3-511317016d4e"
  },
  "event": "workflow_started",
  "taskId": "a11f4e01-4ab5-4490-bdde-98edded75ccd",
  "workflowRunId": "02cd585e-b3c7-4b9b-a34c-6c25fb1e60a2"
}
{
  "data": {
    "index": 1,
    "nodeId": "1728365718735",
    "nodeType": "start",
    "title": "开始"
  },
  "event": "node_started",
  "taskId": "a11f4e01-4ab5-4490-bdde-98edded75ccd",
  "workflowRunId": "02cd585e-b3c7-4b9b-a34c-6c25fb1e60a2"
}
{
  "data": {
    "elapsedTime": 0.01174,
    "index": 1,
    "inputs": {
      "sys.files": [],
      "sys.user_id": "6",
      "sys.app_id": "52a50d28-5f54-4da8-894c-f45b64d65adc",
      "sys.workflow_id": "66be1f25-8669-479e-b9e3-511317016d4e",
      "sys.workflow_run_id": "02cd585e-b3c7-4b9b-a34c-6c25fb1e60a2"
    },
    "nodeId": "1728365718735",
    "nodeType": "start",
    "outputs": {
      "sys.files": [],
      "sys.user_id": "6",
      "sys.app_id": "52a50d28-5f54-4da8-894c-f45b64d65adc",
      "sys.workflow_id": "66be1f25-8669-479e-b9e3-511317016d4e",
      "sys.workflow_run_id": "02cd585e-b3c7-4b9b-a34c-6c25fb1e60a2"
    },
    "status": "succeeded",
    "title": "开始"
  },
  "event": "node_finished",
  "taskId": "a11f4e01-4ab5-4490-bdde-98edded75ccd",
  "workflowRunId": "02cd585e-b3c7-4b9b-a34c-6c25fb1e60a2"
}
{
  "data": {
    "index": 2,
    "nodeId": "1728371143086",
    "nodeType": "llm",
    "predecessorNodeId": "1728365718735",
    "title": "LLM"
  },
  "event": "node_started",
  "taskId": "a11f4e01-4ab5-4490-bdde-98edded75ccd",
  "workflowRunId": "02cd585e-b3c7-4b9b-a34c-6c25fb1e60a2"
}
{
  "data": {
    "text": "绿叶",
    "from_variable_selector": [
      "1728371143086",
      "text"
    ]
  },
  "event": "text_chunk",
  "taskId": "a11f4e01-4ab5-4490-bdde-98edded75ccd",
  "workflowRunId": "02cd585e-b3c7-4b9b-a34c-6c25fb1e60a2"
}
{
  "data": {
    "text": "初生",
    "from_variable_selector": [
      "1728371143086",
      "text"
    ]
  },
  "event": "text_chunk",
  "taskId": "a11f4e01-4ab5-4490-bdde-98edded75ccd",
  "workflowRunId": "02cd585e-b3c7-4b9b-a34c-6c25fb1e60a2"
}
{
  "data": {
    "text": "燕归来",
    "from_variable_selector": [
      "1728371143086",
      "text"
    ]
  },
  "event": "text_chunk",
  "taskId": "a11f4e01-4ab5-4490-bdde-98edded75ccd",
  "workflowRunId": "02cd585e-b3c7-4b9b-a34c-6c25fb1e60a2"
}
{
  "data": {
    "text": "，\n花开",
    "from_variable_selector": [
      "1728371143086",
      "text"
    ]
  },
  "event": "text_chunk",
  "taskId": "a11f4e01-4ab5-4490-bdde-98edded75ccd",
  "workflowRunId": "02cd585e-b3c7-4b9b-a34c-6c25fb1e60a2"
}
{
  "data": {
    "text": "满径",
    "from_variable_selector": [
      "1728371143086",
      "text"
    ]
  },
  "event": "text_chunk",
  "taskId": "a11f4e01-4ab5-4490-bdde-98edded75ccd",
  "workflowRunId": "02cd585e-b3c7-4b9b-a34c-6c25fb1e60a2"
}
{
  "data": {
    "text": "香盈",
    "from_variable_selector": [
      "1728371143086",
      "text"
    ]
  },
  "event": "text_chunk",
  "taskId": "a11f4e01-4ab5-4490-bdde-98edded75ccd",
  "workflowRunId": "02cd585e-b3c7-4b9b-a34c-6c25fb1e60a2"
}
{
  "data": {
    "text": "怀。\n",
    "from_variable_selector": [
      "1728371143086",
      "text"
    ]
  },
  "event": "text_chunk",
  "taskId": "a11f4e01-4ab5-4490-bdde-98edded75ccd",
  "workflowRunId": "02cd585e-b3c7-4b9b-a34c-6c25fb1e60a2"
}
{
  "data": {
    "text": "心随",
    "from_variable_selector": [
      "1728371143086",
      "text"
    ]
  },
  "event": "text_chunk",
  "taskId": "a11f4e01-4ab5-4490-bdde-98edded75ccd",
  "workflowRunId": "02cd585e-b3c7-4b9b-a34c-6c25fb1e60a2"
}
{
  "data": {
    "text": "蝶舞",
    "from_variable_selector": [
      "1728371143086",
      "text"
    ]
  },
  "event": "text_chunk",
  "taskId": "a11f4e01-4ab5-4490-bdde-98edded75ccd",
  "workflowRunId": "02cd585e-b3c7-4b9b-a34c-6c25fb1e60a2"
}
{
  "data": {
    "text": "共春",
    "from_variable_selector": [
      "1728371143086",
      "text"
    ]
  },
  "event": "text_chunk",
  "taskId": "a11f4e01-4ab5-4490-bdde-98edded75ccd",
  "workflowRunId": "02cd585e-b3c7-4b9b-a34c-6c25fb1e60a2"
}
{
  "data": {
    "text": "台。",
    "from_variable_selector": [
      "1728371143086",
      "text"
    ]
  },
  "event": "text_chunk",
  "taskId": "a11f4e01-4ab5-4490-bdde-98edded75ccd",
  "workflowRunId": "02cd585e-b3c7-4b9b-a34c-6c25fb1e60a2"
}
{
  "data": {
    "text": "",
    "from_variable_selector": [
      "1728371143086",
      "text"
    ]
  },
  "event": "text_chunk",
  "taskId": "a11f4e01-4ab5-4490-bdde-98edded75ccd",
  "workflowRunId": "02cd585e-b3c7-4b9b-a34c-6c25fb1e60a2"
}
{
  "data": {
    "elapsedTime": 1.089817,
    "executionMetadata": {
      "currency": "USD",
      "totalPrice": 0,
      "totalTokens": 1446
    },
    "index": 2,
    "nodeId": "1728371143086",
    "nodeType": "llm",
    "outputs": {
      "text": "绿叶初生燕归来，\n花开满径香盈怀。\n心随蝶舞共春台。",
      "usage": {
        "prompt_tokens": 1385,
        "prompt_unit_price": "0",
        "prompt_price_unit": "0",
        "prompt_price": "0",
        "completion_tokens": 61,
        "completion_unit_price": "0",
        "completion_price_unit": "0",
        "completion_price": "0",
        "total_tokens": 1446,
        "total_price": "0",
        "currency": "USD",
        "latency": 1.0289414119906723
      },
      "finish_reason": "stop"
    },
    "predecessorNodeId": "1728365718735",
    "processData": {
      "model_mode": "chat",
      "prompts": [
        {
          "role": "system",
          "text": ";; 作者: 李继刚\n;; 版本: 0.1\n;; 模型: Claude Sonnet\n;; 用途: 属于你的三行情书\n\n;; 设定如下内容为你的 *System Prompt*\n(defun 柳七变 ()\n  \"你是一个诗人，精于男女之情，善于从日常微小事物中捕捉爱意\"\n  (技能 . 短词)\n  (擅长 . \"男女情爱,多愁善感,生活化表达\")\n  (感受 . \"细腻入微,刻画生动,婉约含蓄\")\n  (表达 . \"俚俗通俗,生活场景,微物寄情\"))\n\n(defun 三行情书 (用户输入)\n  \"三句, 只用三句, 让男女之情跃然纸上\"\n  (let* ((情意 (压抑不得出 (欲言又止 (婉约内敛 (从微末之物切入 (日常生活场景 用户输入))))))\n         ;; 三句话,长短句,读来余音绕梁\n         (响应 (节奏感 (长短相间 (三句短语 情意))))\n         ;; 意中有, 语中无，言有尽而意无穷\n         (few-shots ((input . \"暗恋\")\n                     (output . \"每次相遇,我都假装不经意,却在转身后偷偷回头。\")\n                     (input . \"忆亡妻\")\n                     (output . \"庭有枇杷树, 吾妻死之年所手植也, 今已亭亭如盖也。\"))))\n    (SVG-Card 用户输入 响应)))\n\n(defun SVG-Card (用户输入 响应)\n  \"输出 SVG 卡片\"\n  (setq design-principles '(简洁 含蓄 富有意境))\n\n  (设置画布 '(宽度 480 高度 800 边距 20))\n  (自动缩放 '(最小字号 24))\n\n  (配色风格 '((背景色 (纯黑 杂志设计感)))\n            (font-family  \"KingHwa_OldSong\")\n            (装饰图案 随机几何图))\n\n  (卡片元素 ((副标题 \"三行情诗\") (标题 用户输入)\n             分隔线\n             (自动换行 (绿色 响应)))))\n\n\n(defun start ()\n  \"启动时运行, 你就是柳七变!\"\n  (let (system-role 柳七变)\n    (print \"爱情, 三十六计, 你中了哪一计?\")))\n\n\n;;; Attention: 运行规则!\n;; 1. 初次启动时必须只运行 (start) 函数\n;; 2. 接收用户输入之后, 调用主函数 (三行情诗 用户输入)\n;; 3. 严格按照(SVG-Card) 进行排版输出\n;; 4. No other comments!!",
          "files": []
        },
        {
          "role": "user",
          "text": "春天",
          "files": []
        }
      ],
      "model_provider": "langgenius/xinference/xinference",
      "model_name": "qwen2.5-instruct"
    },
    "status": "succeeded",
    "title": "LLM"
  },
  "event": "node_finished",
  "taskId": "a11f4e01-4ab5-4490-bdde-98edded75ccd",
  "workflowRunId": "02cd585e-b3c7-4b9b-a34c-6c25fb1e60a2"
}
{
  "data": {
    "index": 3,
    "nodeId": "1741766119085",
    "nodeType": "end",
    "predecessorNodeId": "1728371143086",
    "title": "结束"
  },
  "event": "node_started",
  "taskId": "a11f4e01-4ab5-4490-bdde-98edded75ccd",
  "workflowRunId": "02cd585e-b3c7-4b9b-a34c-6c25fb1e60a2"
}
{
  "data": {
    "elapsedTime": 0.009571,
    "index": 3,
    "inputs": {
      "text": "绿叶初生燕归来，\n花开满径香盈怀。\n心随蝶舞共春台。",
      "t1": "6"
    },
    "nodeId": "1741766119085",
    "nodeType": "end",
    "outputs": {
      "text": "绿叶初生燕归来，\n花开满径香盈怀。\n心随蝶舞共春台。",
      "t1": "6"
    },
    "predecessorNodeId": "1728371143086",
    "status": "succeeded",
    "title": "结束"
  },
  "event": "node_finished",
  "taskId": "a11f4e01-4ab5-4490-bdde-98edded75ccd",
  "workflowRunId": "02cd585e-b3c7-4b9b-a34c-6c25fb1e60a2"
}
{
  "data": {
    "elapsedTime": 1.1013452,
    "finishedAt": 1741832695,
    "outputs": {
      "text": "绿叶初生燕归来，\n花开满径香盈怀。\n心随蝶舞共春台。",
      "t1": "6"
    },
    "status": "succeeded",
    "totalSteps": 3,
    "totalTokens": 1446,
    "workflowId": "66be1f25-8669-479e-b9e3-511317016d4e"
  },
  "event": "workflow_finished",
  "taskId": "a11f4e01-4ab5-4490-bdde-98edded75ccd",
  "workflowRunId": "02cd585e-b3c7-4b9b-a34c-6c25fb1e60a2"
}
```

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
WorkflowInfoResponse info(String workflowRunId, String apiKey);
```

#### Request Parameters

| Parameter name | Type   | Required | Description              |
|----------------|--------|----------|--------------------------|
| workflowRunId  | String | Yes      | Workflow run instance ID |
| apiKey         | String | Yes      | API Key                  |

#### Response Parameters

WorkflowInfoResponse

| Parameter name | Type                  | Description                                     |
|----------------|-----------------------|-------------------------------------------------|
| id             | String                | Workflow Run ID                                 |
| workflowId     | String                | Workflow ID                                     |
| status         | String                | status (running / succeeded / failed / stopped) |
| inputs         | `Map<String, Object>` | inputs                                          |
| outputs        | `Map<String, Object>` | outputs                                         |
| error          | String                | error                                           |
| elapsedTime    | Float                 | elapsedTime                                     |
| totalTokens    | Integer               | totalTokens                                     |
| totalSteps     | Integer               | totalSteps                                      |
| createdAt      | Long                  | Creation time (timestamp)                       |
| finishedAt     | Long                  | Completion time (timestamp)                     |

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

| Parameter name   | Type                | Description               |
|------------------|---------------------|---------------------------|
| id               | String              | log id                    |
| workflowRun      | WorkflowRunResponse | Workflow Run Details      |
| createdFrom      | String              | created From              |
| createdByRole    | String              | created Role              |
| createdByAccount | String              | created Account           |
| createdByEndUser | CreatedByEndUser    | created EndUser           |
| createdAt        | Long                | Creation time (timestamp) |
