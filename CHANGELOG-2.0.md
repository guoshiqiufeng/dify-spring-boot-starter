# Changelog - Version 2.0.0

## Overview

Version 2.0.0 represents a major architectural refactoring of the dify-spring-boot-starter project. This release introduces a modular, framework-agnostic design that supports both Spring and pure Java projects, with improved flexibility, maintainability, and reduced code duplication.

## Breaking Changes

### 1. Module Structure Refactoring

**Removed Modules:**
- `dify-client-spring5` - Spring 5.x specific client implementation
- `dify-client-spring6` - Spring 6.x specific client implementation
- `dify-client-spring7` - Spring 7.x specific client implementation

**New Modules:**
- `dify-client-core`: Core HTTP client abstractions and interfaces
  - Framework-agnostic HTTP client interfaces
  - Request/response handling abstractions
  - URI building and HTTP headers management
- `dify-client-codec`: JSON serialization/deserialization layer
  - `dify-client-codec-gson`: Gson implementation
  - `dify-client-codec-jackson`: Jackson implementation module containing both:
    - Jackson 2.x implementation (for Spring Boot 2.x/3.x)
    - Jackson 3.x implementation (for Spring Boot 4.x)
- `dify-client-integration`: HTTP client implementations
  - `dify-client-integration-okhttp`: OkHttp-based client for pure Java projects
  - `dify-client-integration-spring`: Spring WebClient/RestClient integration
- `dify-support-impl`: Business logic implementation layer
  - Unified client implementations for Chat, Dataset, Server, and Workflow
  - Builder pattern implementations

### 2. Dependency Changes

**Removed:**
- Hutool dependencies (replaced with custom utility classes)
  - Custom `BeanUtils`, `CollUtil`, `StrUtil`, `Assert` implementations
  - Reduced external dependencies and improved control

**Added:**
- OkHttp 4.x support for pure Java projects
- Multiple JSON codec options:
  - Gson for lightweight pure Java projects
  - Jackson 2.x for Spring Boot 2.x/3.x projects
  - Jackson 3.x for Spring Boot 4.x projects (both in `dify-client-codec-jackson` module)
- Flexible HTTP client factory pattern

### 3. Package Structure Changes

The package structure has been reorganized to reflect the new modular architecture:
- Core abstractions: `io.github.guoshiqiufeng.dify.client.core`
- Codec implementations: `io.github.guoshiqiufeng.dify.client.codec`
- Integration implementations: `io.github.guoshiqiufeng.dify.client.integration`
- Business logic: `io.github.guoshiqiufeng.dify.support.impl`

### 4. API Changes

**Builder Pattern Updates:**
All builders now require an `HttpClientFactory` parameter when used manually. However, **Spring Boot auto-configuration users don't need to make any changes** - the framework automatically configures everything.

**Manual Builder Usage (requires updates):**
```java
// Old (1.x) - Spring-specific
DifyChatClient client = DifyChatBuilder.builder()
    .baseUrl("https://api.dify.ai")
    .apiKey("your-api-key")
    .build();

// New (2.0) - Framework-agnostic
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

**Spring Boot Auto-Configuration (no changes needed):**
```java
@Autowired
private DifyChatClient difyChatClient;  // Still works automatically!
```

## New Features

### 1. Pure Java Project Support

Version 2.0.0 introduces support for pure Java projects (non-Spring) through the new `dify-java-starter`:

```xml
<dependency>
    <groupId>io.github.guoshiqiufeng.dify</groupId>
    <artifactId>dify-java-starter</artifactId>
</dependency>
```

**Example Usage:**
```java
import io.github.guoshiqiufeng.dify.client.integration.okhttp.http.JavaHttpClientFactory;
import io.github.guoshiqiufeng.dify.client.codec.jackson.JacksonJsonMapper;

// Create HTTP client factory (OkHttp)
JavaHttpClientFactory httpClientFactory = new JavaHttpClientFactory(new JacksonJsonMapper());

// Create DifyServerClient
DifyServerClient difyServerClient = DifyServerBuilder.builder()
        .baseUrl("https://your-dify-api.example.com")
        .httpClientFactory(httpClientFactory)
        .serverProperties(new DifyProperties.Server("admin@example.com", "password"))
        .build();
```

### 2. Flexible JSON Codec Support

Choose your preferred JSON library based on your project requirements:

- **Gson** (`dify-client-codec-gson`): Lightweight and fast, good for pure Java projects
- **Jackson 2.x** (`dify-client-codec-jackson`): Industry standard for Spring Boot 2.x/3.x projects
- **Jackson 3.x** (`dify-client-codec-jackson`): Latest version for Spring Boot 4.x projects

All codec implementations are in their respective modules, with Jackson 2.x and 3.x both provided in the `dify-client-codec-jackson` module.

Each codec implementation provides:
- Custom deserializers for streaming responses
- Support for Jackson annotations in Gson (via `JacksonAnnotationTypeAdapterFactory`)
- Consistent API across all implementations

### 3. DifyFile MultipartFile Support

Enhanced file handling with `DifyFileConverter` for seamless Spring MultipartFile integration:

```java
// Automatic conversion from Spring MultipartFile to DifyFile
DifyFile difyFile = DifyFileConverter.convert(multipartFile);

// Use in file upload requests
FileUploadRequest request = FileUploadRequest.builder()
    .file(difyFile)
    .user("user-123")
    .build();
```

See [MULTIPARTFILE_SUPPORT.md](dify/dify-client/dify-client-integration/dify-client-integration-spring/MULTIPARTFILE_SUPPORT.md) for detailed documentation.

### 4. SpringHttpClientFactory

New factory for Spring-based projects with support for both WebClient and RestClient:

```java
import io.github.guoshiqiufeng.dify.client.integration.spring.http.SpringHttpClientFactory;

SpringHttpClientFactory httpClientFactory = new SpringHttpClientFactory(
        WebClient.builder(),
        RestClient.builder(),  // Spring 6.1+ / Spring Boot 3.2+
        new JacksonJsonMapper()
);
```

**Note**: In Spring Boot 2.x environments, RestClient is not available. Pass `null` for the RestClient parameter.

**Features:**
- Automatic Spring version detection
- Fallback from RestClient to WebClient for older Spring versions
- Integrated logging support via `DifyLoggingFilter` and `DifyRestLoggingInterceptor`

### 5. Enhanced HTTP Client Features

**Client Logging:**
- Built-in logging support for request/response debugging
- Configurable log levels and formats
- Support for both WebClient and RestClient logging

**Request Headers:**
- Customizable default headers for all requests
- Per-request header overrides
- Automatic content-type handling

**Error Handling:**
- Improved error handling and warning messages
- Custom `ResponseErrorHandler` implementations
- Better exception messages with context

**Content Disposition:**
- Enhanced file upload/download support
- Proper filename handling with encoding
- Support for various content disposition formats

### 6. Builder Pattern Improvements

All client builders now support the new modular architecture with consistent APIs:

- `DifyChatBuilder`: Build chat clients with custom HTTP factories
- `DifyDatasetBuilder`: Build dataset clients with custom HTTP factories
- `DifyServerBuilder`: Build server clients with custom HTTP factories
- `DifyWorkflowBuilder`: Build workflow clients with custom HTTP factories

**Common Builder Features:**
- Fluent API design
- Validation of required parameters
- Support for custom HTTP client factories
- Timeout configuration
- Default header configuration

### 7. Server API Enhancements

New server endpoints support:
- Document retry functionality (`DocumentRetryRequest`)
- Dataset error documents retrieval (`DatasetErrorDocumentsResponse`)
- Enhanced login with password encryption support
- Token refresh with fallback to login

## Improvements

### 1. Modular Architecture

The new architecture separates concerns into distinct layers:

**Core Layer** (`dify-client-core`):
- Framework-agnostic abstractions
- HTTP client interfaces
- Request/response models
- No external dependencies beyond Java standard library

**Codec Layer** (`dify-client-codec`):
- Pluggable JSON serialization
- Multiple implementation options
- Custom deserializers for streaming responses

**Integration Layer** (`dify-client-integration`):
- HTTP client implementations
- OkHttp for pure Java
- Spring WebClient/RestClient for Spring projects

**Support Layer** (`dify-support-impl`):
- Business logic and API implementations
- Unified across all Spring versions
- No duplication of client code

### 2. Custom Utility Classes

Replaced Hutool dependencies with custom implementations:

- `BeanUtils`: Bean property manipulation and copying
- `CollUtil`: Collection utilities (isEmpty, isNotEmpty, etc.)
- `StrUtil`: String utilities (isEmpty, isNotEmpty, isBlank, etc.)
- `Assert`: Assertion utilities for validation
- `MultipartBodyBuilder`: Multipart form data building

**Benefits:**
- Reduced external dependencies
- Better control over functionality
- Improved performance
- Smaller artifact sizes

### 3. Spring Version Detection

Automatic Spring version detection for optimal compatibility:

- `SpringVersionDetector`: Detects Spring Framework version at runtime
- `SpringVersion`: Enum for Spring 5/6/7 versions
- Automatic selection of appropriate HTTP client (WebClient vs RestClient)

### 4. Improved Test Coverage

Enhanced test coverage across all modules:
- Comprehensive unit tests for all new modules
- Integration tests for HTTP clients
- Codec serialization/deserialization tests
- Builder pattern tests
- Total test count increased significantly

### 5. Build Configuration

- Added `-Xlint:unchecked` compiler flag for better compile-time warnings
- Improved dependency management for multi-version Spring support
- BOM (Bill of Materials) for easier dependency management
- Gradle build optimization

### 6. Code Quality Improvements

- Eliminated code duplication across Spring versions
- Consistent error handling patterns
- Better separation of concerns
- Improved code maintainability

## Migration Guide

### For Spring Boot Projects

**Before (1.x):**
```xml
<dependency>
    <groupId>io.github.guoshiqiufeng.dify</groupId>
    <artifactId>dify-spring-boot-starter</artifactId>
    <version>1.7.0</version>
</dependency>
```

**After (2.0):**
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

    <!-- OR Spring Boot 4.x -->
    <dependency>
        <groupId>io.github.guoshiqiufeng.dify</groupId>
        <artifactId>dify-spring-boot4-starter</artifactId>
    </dependency>

    <!-- OR Spring Boot 2.x / 3.0.x -->
    <dependency>
        <groupId>io.github.guoshiqiufeng.dify</groupId>
        <artifactId>dify-spring-boot2-starter</artifactId>
    </dependency>
</dependencies>
```

### For Pure Java Projects

**New in 2.0:**
```xml
<dependency>
    <groupId>io.github.guoshiqiufeng.dify</groupId>
    <artifactId>dify-java-starter</artifactId>
</dependency>
```

### Code Changes

Most application code using auto-configuration remains compatible. Manual builder usage requires updates:

**1. Spring Boot Auto-Configuration (No Changes Required):**
```java
@Autowired
private DifyChatClient difyChatClient;  // Still works! No changes needed!
```

**2. Manual Builder Usage (Requires Updates):**
```java
// Old (1.x)
DifyChatClient client = DifyChatBuilder.builder()
    .baseUrl("https://api.dify.ai")
    .apiKey("your-api-key")
    .build();

// New (2.0) - Spring projects
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

// New (2.0) - Pure Java projects
JavaHttpClientFactory factory = new JavaHttpClientFactory(new JacksonJsonMapper());

DifyChatClient client = DifyChatBuilder.builder()
    .baseUrl("https://api.dify.ai")
    .clientConfig(clientConfig)
    .httpClientFactory(factory)
    .build();
```

**3. Package Import Updates:**
If you directly imported internal classes, update package names:
- `io.github.guoshiqiufeng.dify.client.spring*` → `io.github.guoshiqiufeng.dify.support.impl.*`

## Compatibility

### Supported Versions

- **Java**: 8+
- **Spring Boot**: 2.x, 3.x, 4.x
- **Spring Framework**: 5.x, 6.x, 7.x

### Minimum Requirements

- Java 8
- Spring Boot 2.x (for Spring projects)
- No Spring requirement for pure Java projects

### Recommended Versions

- Java 17+
- Spring Boot 3.x or 4.x

## Documentation

- [English Documentation](https://guoshiqiufeng.github.io/dify-spring-boot-starter/en)
- [中文文档](https://guoshiqiufeng.github.io/dify-spring-boot-starter/)
- [Examples](https://github.com/guoshiqiufeng/dify-spring-boot-starter-examples)
- [DeepWiki](https://deepwiki.com/guoshiqiufeng/dify-spring-boot-starter)

## Statistics

- **Commits**: 31 commits from 1.7.0 to 2.0.0
- **Files Changed**: 848 files
- **Insertions**: +27,360 lines
- **Deletions**: -38,934 lines
- **Net Change**: -11,574 lines (improved code efficiency through elimination of duplication)

## Acknowledgments

Thank you to all contributors who helped make this release possible:
- @yanghq for the major refactoring work
- @evelynn996 for server API enhancements
- Community members for feedback and testing during the development cycle

## Support

If you encounter any issues or have questions:
- [GitHub Issues](https://github.com/guoshiqiufeng/dify-spring-boot-starter/issues)
- [Documentation](https://guoshiqiufeng.github.io/dify-spring-boot-starter/)

---

