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

| 参数名    | 类型                 | 是否必须 | 描述     |
|--------|--------------------|------|--------|
| apiKey | String             | 是    | apiKey |
| userId | String             | 是    | 用户 id  |
| inputs | Map<String,Object> | 否    | 输入参数   |

#### 响应参数

WorkflowRunResponse

| 参数名           | 类型              | 描述        |
|---------------|-----------------|-----------|
| workflowRunId | String          | 工作流运行 ID  |
| taskId        | String          | 任务 ID     |
| data          | WorkflowRunData | 工作流运行数据对象 |

WorkflowRunData

| 参数名         | 类型                  | 描述                                            |
|-------------|---------------------|-----------------------------------------------|
| id          | String              | 运行实例 ID                                       |
| workflowId  | String              | 工作流 ID                                        |
| status      | String              | 执行状态 (running / succeeded / failed / stopped) |
| outputs     | Map<String, Object> | 输出结果                                          |
| error       | String              | 错误信息                                          |
| elapsedTime | Float               | 耗时（秒）                                         |
| totalTokens | Integer             | 总 token 数量                                    |
| totalSteps  | Integer             | 总步骤数                                          |
| createdAt   | Long                | 创建时间（时间戳）                                     |
| finishedAt  | Long                | 完成时间（时间戳）                                     |

#### 请求示例

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

### 1.2 运行工作流流式响应

#### 方法

```java
Flux<WorkflowRunResponse> runWorkflowStream(WorkflowRunRequest request);
```

#### 请求参数

与运行工作流接口相同

#### 响应参数

WorkflowRunStreamResponse

| 参数名           | 类型              | 描述        |
|---------------|-----------------|-----------|
| workflowRunId | String          | 工作流运行 ID  |
| taskId        | String          | 任务 ID     |
| event         | StreamEventEnum | 类型        |
| data          | Object          | 工作流运行数据对象 |

> 根据event类型选择 data 类型

| event             | data                 | 描述    |
|-------------------|----------------------|-------|
| workflow_started  | WorkflowStartedData  | 工作流开始 |
| node_started      | NodeStartedData      | 节点开始  |
| text_chunk        | Map                  | 文本块   |
| node_finished     | NodeFinishedData     | 节点结束  |
| workflow_finished | WorkflowFinishedData | 工作流结束 |

WorkflowStartedData

| 参数名            | 类型      | 描述                   |
|----------------|---------|----------------------|
| id             | String  | 运行实例 ID              |
| createdAt      | Long    | 创建时间（时间戳）            |
| workflowId     | String  | 关联的工作流 ID            |
| sequenceNumber | Integer | 自增序列号（在应用内自增，从 1 开始） |

NodeStartedData

| 参数名               | 类型                  | 描述                     |
|-------------------|---------------------|------------------------|
| id                | String              | 运行实例 ID                |
| createdAt         | Long                | 创建时间（时间戳）              |
| nodeId            | String              | 节点 ID                  |
| nodeType          | String              | 节点类型                   |
| title             | String              | 节点名称                   |
| index             | Integer             | 执行序号（用于展示追踪节点顺序）       |
| predecessorNodeId | String              | 前驱节点 ID（可选，用于画布执行路径展示） |
| inputs            | Map<String, Object> | 包含节点中使用的所有前置节点变量内容     |

NodeFinishedData

| 参数名               | 类型                  | 描述                                     |
|-------------------|---------------------|----------------------------------------|
| id                | String              | 运行实例 ID                                |
| createdAt         | Long                | 创建时间（时间戳）                              |
| nodeId            | String              | 节点 ID                                  |
| nodeType          | String              | 节点类型                                   |
| title             | String              | 节点名称                                   |
| index             | Integer             | 执行序号                                   |
| predecessorNodeId | String              | 前驱节点 ID                                |
| inputs            | Map<String, Object> | 输入参数集合                                 |
| processData       | Map<String, Object> | 节点处理数据（可选）                             |
| outputs           | Map<String, Object> | 输出结果集合                                 |
| status            | String              | 执行状态（running/succeeded/failed/stopped） |
| error             | String              | 错误信息（当状态为 failed 时存在）                  |
| elapsedTime       | Float               | 耗时（秒）                                  |
| executionMetadata | ExecutionMetadata   | 执行元数据                                  |

ExecutionMetadata 结构

| 参数名         | 类型         | 描述                 |
|-------------|------------|--------------------|
| totalTokens | Integer    | 总 Token 数          |
| totalPrice  | BigDecimal | 总成本                |
| currency    | String     | 货币单位（可选，如 USD/RMB） |

WorkflowFinishedData

| 参数名         | 类型                  | 描述                                     |
|-------------|---------------------|----------------------------------------|
| id          | String              | 运行实例 ID                                |
| createdAt   | Long                | 创建时间（时间戳）                              |
| workflowId  | String              | 关联的工作流 ID                              |
| outputs     | Map<String, Object> | 最终输出结果                                 |
| status      | String              | 执行状态（running/succeeded/failed/stopped） |
| error       | String              | 错误信息（当状态为 failed 时存在）                  |
| elapsedTime | Float               | 总耗时（秒）                                 |
| totalTokens | Integer             | 工作流总消耗 Token 数                         |
| totalSteps  | Integer             | 总执行步骤数                                 |
| finishedAt  | Long                | 完成时间戳（如 1705395332）                    |

#### 请求示例

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

| 参数名    | 类型     | 描述   |
|--------|--------|------|
| result | String | 停止结果 |

### 1.4 获取工作流运行详情

#### 方法

```java
WorkflowRunResponse info(String workflowRunId, String apiKey);
```

#### 请求参数

| 参数名           | 类型     | 是否必须 | 描述     |
|---------------|--------|------|--------|
| workflowRunId | String | 是    | 运行实例id |
| apiKey        | String | 是    | apiKey |

#### 响应参数

WorkflowInfoResponse

| 参数名         | 类型                  | 描述                                            |
|-------------|---------------------|-----------------------------------------------|
| id          | String              | 运行实例 ID                                       |
| workflowId  | String              | 工作流 ID                                        |
| status      | String              | 执行状态 (running / succeeded / failed / stopped) |
| inputs      | Map<String, Object> | 输入参数                                          |
| outputs     | Map<String, Object> | 输出结果                                          |
| error       | String              | 错误信息                                          |
| elapsedTime | Float               | 耗时（秒）                                         |
| totalTokens | Integer             | 总 token 数量                                    |
| totalSteps  | Integer             | 总步骤数                                          |
| createdAt   | Long                | 创建时间（时间戳）                                     |
| finishedAt  | Long                | 完成时间（时间戳）                                     |

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

| 参数名        | 类型     | 描述    |
|------------|--------|-------|
| id         | String | 日志id  |
| status     | String | 运行状态  |
| message    | String | 日志消息  |
| createdAt  | Long   | 创建时间戳 |
| finishedAt | Long   | 完成时间戳 |
