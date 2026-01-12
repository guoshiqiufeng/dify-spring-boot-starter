# 变更记录 - 版本 2.0.0

## 概述

版本 2.0.0 是 dify-spring-boot-starter 项目的一次重大架构重构。此版本引入了模块化、框架无关的设计,同时支持 Spring 和纯 Java 项目,具有更好的灵活性、可维护性,并减少了代码重复。

## 破坏性变更

### 1. 模块结构重构

**移除的模块:**
- `dify-client-spring5` - Spring 5.x 特定客户端实现
- `dify-client-spring6` - Spring 6.x 特定客户端实现
- `dify-client-spring7` - Spring 7.x 特定客户端实现

**新增模块:**
- `dify-client-core`: 核心 HTTP 客户端抽象和接口
  - 框架无关的 HTTP 客户端接口
  - 请求/响应处理抽象
  - URI 构建和 HTTP 头管理
- `dify-client-codec`: JSON 序列化/反序列化层
  - `dify-client-codec-gson`: Gson 实现
  - `dify-client-codec-jackson`: Jackson 实现模块,包含:
    - Jackson 2.x 实现(用于 Spring Boot 2.x/3.x)
    - Jackson 3.x 实现(用于 Spring Boot 4.x)
- `dify-client-integration`: HTTP 客户端实现
  - `dify-client-integration-okhttp`: 基于 OkHttp 的纯 Java 客户端
  - `dify-client-integration-spring`: Spring WebClient/RestClient 集成
- `dify-support-impl`: 业务逻辑实现层
  - Chat、Dataset、Server 和 Workflow 的统一客户端实现
  - 构建器模式实现

### 2. 依赖变更

**移除:**
- Hutool 依赖(替换为自定义工具类)
  - 自定义 `BeanUtils`、`CollUtil`、`StrUtil`、`Assert` 实现
  - 减少外部依赖并提高控制力

**新增:**
- 纯 Java 项目的 OkHttp 4.x 支持
- 多种 JSON 编解码器选项:
  - Gson 用于轻量级纯 Java 项目
  - Jackson 2.x 用于 Spring Boot 2.x/3.x 项目
  - Jackson 3.x 用于 Spring Boot 4.x 项目(两者都在 `dify-client-codec-jackson` 模块中)
- 灵活的 HTTP 客户端工厂模式

### 3. 包结构变更

包结构已重新组织以反映新的模块化架构:
- 核心抽象: `io.github.guoshiqiufeng.dify.client.core`
- 编解码器实现: `io.github.guoshiqiufeng.dify.client.codec`
- 集成实现: `io.github.guoshiqiufeng.dify.client.integration`
- 业务逻辑: `io.github.guoshiqiufeng.dify.support.impl`

### 4. API 变更

**构建器模式更新:**
手动使用构建器时,所有构建器现在都需要 `HttpClientFactory` 参数。但是,**使用 Spring Boot 自动配置的用户无需做任何更改** - 框架会自动配置所有内容。

**手动构建器使用(需要更新):**
```java
// 旧版本 (1.x) - Spring 特定
DifyChatClient client = DifyChatBuilder.builder()
    .baseUrl("https://api.dify.ai")
    .apiKey("your-api-key")
    .build();

// 新版本 (2.0) - 框架无关
HttpClientFactory factory = new SpringHttpClientFactory(
    WebClient.builder(),
    RestClient.builder(),
    new JacksonJsonMapper()
);

DifyChatClient client = DifyChatBuilder.builder()
    .baseUrl("https://api.dify.ai")
    .clientConfig(clientConfig)
    .httpClientFactory(factory)
    .build();
```

**Spring Boot 自动配置(无需更改):**
```java
@Autowired
private DifyChatClient difyChatClient;  // 仍然自动工作!
```

## 新功能

### 1. 纯 Java 项目支持

版本 2.0.0 通过新的 `dify-java-starter` 引入了对纯 Java 项目(非 Spring)的支持:

```xml
<dependency>
    <groupId>io.github.guoshiqiufeng.dify</groupId>
    <artifactId>dify-java-starter</artifactId>
</dependency>
```

**使用示例:**
```java
import io.github.guoshiqiufeng.dify.client.integration.okhttp.http.JavaHttpClientFactory;
import io.github.guoshiqiufeng.dify.client.codec.jackson.JacksonJsonMapper;

// 创建 HTTP 客户端工厂(OkHttp)
JavaHttpClientFactory httpClientFactory = new JavaHttpClientFactory(new JacksonJsonMapper());

// 创建 DifyServerClient
DifyServerClient difyServerClient = DifyServerBuilder.builder()
        .baseUrl("https://your-dify-api.example.com")
        .httpClientFactory(httpClientFactory)
        .serverProperties(new DifyProperties.Server("admin@example.com", "password"))
        .build();
```

### 2. 灵活的 JSON 编解码器支持

根据项目需求选择您喜欢的 JSON 库:

- **Gson** (`dify-client-codec-gson`): 轻量级且快速,适合纯 Java 项目
- **Jackson 2.x** (`dify-client-codec-jackson`): Spring Boot 2.x/3.x 项目的行业标准
- **Jackson 3.x** (`dify-client-codec-jackson`): Spring Boot 4.x 项目的最新版本

所有编解码器实现都在各自的模块中,Jackson 2.x 和 3.x 都在 `dify-client-codec-jackson` 模块中提供。

每个编解码器实现都提供:
- 流式响应的自定义反序列化器
- Gson 中对 Jackson 注解的支持(通过 `JacksonAnnotationTypeAdapterFactory`)
- 所有实现的一致 API

### 3. DifyFile MultipartFile 支持

通过 `DifyFileConverter` 增强文件处理,实现 Spring MultipartFile 的无缝集成:

```java
// 自动将 Spring MultipartFile 转换为 DifyFile
DifyFile difyFile = DifyFileConverter.convert(multipartFile);

// 在文件上传请求中使用
FileUploadRequest request = FileUploadRequest.builder()
    .file(difyFile)
    .user("user-123")
    .build();
```

详细文档请参阅 [MULTIPARTFILE_SUPPORT.md](dify/dify-client/dify-client-integration/dify-client-integration-spring/MULTIPARTFILE_SUPPORT.md)。

### 4. SpringHttpClientFactory

为基于 Spring 的项目提供新的工厂,支持 WebClient 和 RestClient:

```java
import io.github.guoshiqiufeng.dify.client.integration.spring.http.SpringHttpClientFactory;

SpringHttpClientFactory httpClientFactory = new SpringHttpClientFactory(
        WebClient.builder(),
        RestClient.builder(),  // Spring 6.1+ / Spring Boot 3.2+
        new JacksonJsonMapper()
);
```

**注意**: 在 Spring Boot 2.x 环境中,RestClient 不可用。请为 RestClient 参数传递 `null`。

**功能特性:**
- 自动 Spring 版本检测
- 从 RestClient 回退到 WebClient(用于旧版 Spring)
- 通过 `DifyLoggingFilter` 和 `DifyRestLoggingInterceptor` 集成日志支持

### 5. 增强的 HTTP 客户端功能

**客户端日志:**
- 内置日志支持,用于请求/响应调试
- 可配置的日志级别和格式
- 支持 WebClient 和 RestClient 日志记录

**请求头:**
- 为所有请求自定义默认请求头
- 每个请求的请求头覆盖
- 自动内容类型处理

**错误处理:**
- 改进的错误处理和警告消息
- 自定义 `ResponseErrorHandler` 实现
- 带上下文的更好异常消息

**Content Disposition:**
- 增强的文件上传/下载支持
- 带编码的正确文件名处理
- 支持各种 content disposition 格式

### 6. 构建器模式改进

所有客户端构建器现在都支持新的模块化架构,具有一致的 API:

- `DifyChatBuilder`: 使用自定义 HTTP 工厂构建聊天客户端
- `DifyDatasetBuilder`: 使用自定义 HTTP 工厂构建数据集客户端
- `DifyServerBuilder`: 使用自定义 HTTP 工厂构建服务器客户端
- `DifyWorkflowBuilder`: 使用自定义 HTTP 工厂构建工作流客户端

**通用构建器功能:**
- 流式 API 设计
- 必需参数验证
- 支持自定义 HTTP 客户端工厂
- 超时配置
- 默认请求头配置

### 7. Server API 增强

新增服务器端点支持:
- 文档重试功能(`DocumentRetryRequest`)
- 数据集错误文档检索(`DatasetErrorDocumentsResponse`)
- 增强的登录,支持密码加密
- 令牌刷新,带回退到登录

## 改进

### 1. 模块化架构

新架构将关注点分离到不同的层次:

**核心层** (`dify-client-core`):
- 框架无关的抽象
- HTTP 客户端接口
- 请求/响应模型
- 除 Java 标准库外无外部依赖

**编解码器层** (`dify-client-codec`):
- 可插拔的 JSON 序列化
- 多种实现选项
- 流式响应的自定义反序列化器

**集成层** (`dify-client-integration`):
- HTTP 客户端实现
- 纯 Java 的 OkHttp
- Spring 项目的 Spring WebClient/RestClient

**支持层** (`dify-support-impl`):
- 业务逻辑和 API 实现
- 所有 Spring 版本统一
- 无客户端代码重复

### 2. 自定义工具类

用自定义实现替换 Hutool 依赖:

- `BeanUtils`: Bean 属性操作和复制
- `CollUtil`: 集合工具(isEmpty、isNotEmpty 等)
- `StrUtil`: 字符串工具(isEmpty、isNotEmpty、isBlank 等)
- `Assert`: 验证的断言工具
- `MultipartBodyBuilder`: 多部分表单数据构建

**优势:**
- 减少外部依赖
- 更好地控制功能
- 提高性能
- 更小的构件大小

### 3. Spring 版本检测

自动检测 Spring 版本以实现最佳兼容性:

- `SpringVersionDetector`: 在运行时检测 Spring Framework 版本
- `SpringVersion`: Spring 5/6/7 版本枚举
- 自动选择适当的 HTTP 客户端(WebClient vs RestClient)

### 4. 改进的测试覆盖率

通过全面的单元测试和集成测试增强所有模块的测试覆盖率:
- 所有新模块的全面单元测试
- HTTP 客户端的集成测试
- 编解码器序列化/反序列化测试
- 构建器模式测试
- 测试总数显著增加

### 5. 构建配置

- 添加 `-Xlint:unchecked` 编译器标志以获得更好的编译时警告
- 改进多版本 Spring 支持的依赖管理
- BOM(物料清单)以便更轻松地管理依赖
- Gradle 构建优化

### 6. 代码质量改进

- 消除 Spring 版本之间的代码重复
- 一致的错误处理模式
- 更好的关注点分离
- 提高代码可维护性

## 迁移指南

### Spring Boot 项目

**之前 (1.x):**
```xml
<dependency>
    <groupId>io.github.guoshiqiufeng.dify</groupId>
    <artifactId>dify-spring-boot-starter</artifactId>
    <version>1.7.0</version>
</dependency>
```

**之后 (2.0):**
```xml
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>io.github.guoshiqiufeng.dify</groupId>
            <artifactId>dify-bom</artifactId>
            <version>2.0.0</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>

<dependencies>
    <!-- Spring Boot 3.1+ -->
    <dependency>
        <groupId>io.github.guoshiqiufeng.dify</groupId>
        <artifactId>dify-spring-boot-starter</artifactId>
    </dependency>

    <!-- 或者 Spring Boot 4.x -->
    <dependency>
        <groupId>io.github.guoshiqiufeng.dify</groupId>
        <artifactId>dify-spring-boot4-starter</artifactId>
    </dependency>

    <!-- 或者 Spring Boot 2.x / 3.0.x -->
    <dependency>
        <groupId>io.github.guoshiqiufeng.dify</groupId>
        <artifactId>dify-spring-boot2-starter</artifactId>
    </dependency>
</dependencies>
```

### 纯 Java 项目

**2.0 新增:**
```xml
<dependency>
    <groupId>io.github.guoshiqiufeng.dify</groupId>
    <artifactId>dify-java-starter</artifactId>
</dependency>
```

### 代码变更

使用自动配置的大多数应用程序代码保持兼容。手动构建器使用需要更新:

**1. Spring Boot 自动配置(无需更改):**
```java
@Autowired
private DifyChatClient difyChatClient;  // 仍然有效!无需更改!
```

**2. 手动构建器使用(需要更新):**
```java
// 旧版本 (1.x)
DifyChatClient client = DifyChatBuilder.builder()
    .baseUrl("https://api.dify.ai")
    .apiKey("your-api-key")
    .build();

// 新版本 (2.0) - Spring 项目
SpringHttpClientFactory factory = new SpringHttpClientFactory(
    WebClient.builder(),
    RestClient.builder(),
    new JacksonJsonMapper()
);

DifyChatClient client = DifyChatBuilder.builder()
    .baseUrl("https://api.dify.ai")
    .clientConfig(clientConfig)
    .httpClientFactory(factory)
    .build();

// 新版本 (2.0) - 纯 Java 项目
JavaHttpClientFactory factory = new JavaHttpClientFactory(new JacksonJsonMapper());

DifyChatClient client = DifyChatBuilder.builder()
    .baseUrl("https://api.dify.ai")
    .clientConfig(clientConfig)
    .httpClientFactory(factory)
    .build();
```

**3. 包导入更新:**
如果直接导入了内部类,请更新包名:
- `io.github.guoshiqiufeng.dify.client.spring*` → `io.github.guoshiqiufeng.dify.support.impl.*`

## 兼容性

### 支持的版本

- **Java**: 8+
- **Spring Boot**: 2.x、3.x、4.x
- **Spring Framework**: 5.x、6.x、7.x

### 最低要求

- Java 8
- Spring Boot 2.x(对于 Spring 项目)
- 纯 Java 项目无 Spring 要求

### 推荐版本

- Java 17+
- Spring Boot 3.x 或 4.x

## 文档

- [英文文档](https://guoshiqiufeng.github.io/dify-spring-boot-starter/en)
- [中文文档](https://guoshiqiufeng.github.io/dify-spring-boot-starter/)
- [示例项目](https://github.com/guoshiqiufeng/dify-spring-boot-starter-examples)
- [DeepWiki](https://deepwiki.com/guoshiqiufeng/dify-spring-boot-starter)

## 统计数据

- **提交数**: 从 1.7.0 到 2.0.0 共 31 次提交
- **变更文件数**: 848 个文件
- **新增代码**: +27,360 行
- **删除代码**: -38,934 行
- **净变化**: -11,574 行(通过消除重复提高了代码效率)

## 致谢

感谢所有为此版本做出贡献的贡献者:
- @yanghq 进行了主要的重构工作
- @evelynn996 进行了服务器 API 增强
- 社区成员在开发周期中提供的反馈和测试

## 支持

如果您遇到任何问题或有疑问:
- [GitHub Issues](https://github.com/guoshiqiufeng/dify-spring-boot-starter/issues)
- [文档](https://guoshiqiufeng.github.io/dify-spring-boot-starter/)

---

