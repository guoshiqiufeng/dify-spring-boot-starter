# MultipartFile Support for DifyFile

本文档说明如何在 Spring 应用中将 `MultipartFile` 转换为 `DifyFile`。

## 概述

`DifyFileConverter` 工具类提供了 Spring `MultipartFile` 到 `DifyFile` 的转换功能，使得在 Spring 应用中可以方便地使用 Dify SDK 进行文件上传操作。

## 依赖

确保在项目中引入 `dify-client-integration-spring` 依赖：

```gradle
implementation 'io.github.guoshiqiufeng:dify-client-integration-spring:2.0.0'
```

或 Maven：

```xml
<dependency>
    <groupId>io.github.guoshiqiufeng</groupId>
    <artifactId>dify-client-integration-spring</artifactId>
    <version>2.0.0</version>
</dependency>
```

## 使用方法

### 基本用法

```java
import io.github.guoshiqiufeng.dify.client.integration.spring.file.DifyFileConverter;
import io.github.guoshiqiufeng.dify.core.pojo.DifyFile;
import org.springframework.web.multipart.MultipartFile;

// 方式 1: 使用 from 方法
DifyFile difyFile = DifyFileConverter.from(multipartFile);

// 方式 2: 使用 convert 方法 (别名)
DifyFile difyFile = DifyFileConverter.convert(multipartFile);
```

### Spring Controller 示例

```java
import io.github.guoshiqiufeng.dify.client.integration.spring.file.DifyFileConverter;
import io.github.guoshiqiufeng.dify.core.pojo.DifyFile;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/files")
public class FileUploadController {

    @PostMapping("/upload")
    public String uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            // 转换 MultipartFile 为 DifyFile
            DifyFile difyFile = DifyFileConverter.from(file);

            // 使用 DifyFile 进行后续操作
            // 例如：上传到 Dify 平台
            // difyClient.uploadFile(difyFile);

            return "File uploaded successfully: " + difyFile.getFilename();
        } catch (IOException e) {
            return "File upload failed: " + e.getMessage();
        }
    }

    @PostMapping("/batch-upload")
    public String uploadMultipleFiles(@RequestParam("files") MultipartFile[] files) {
        try {
            for (MultipartFile file : files) {
                DifyFile difyFile = DifyFileConverter.from(file);
                // 处理每个文件
            }
            return "All files uploaded successfully";
        } catch (IOException e) {
            return "Batch upload failed: " + e.getMessage();
        }
    }
}
```

### 与 Dify SDK 集成示例

```java
import io.github.guoshiqiufeng.dify.client.integration.spring.file.DifyFileConverter;
import io.github.guoshiqiufeng.dify.core.pojo.DifyFile;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/dify")
public class DifyIntegrationController {

    private final DifyClient difyClient;

    public DifyIntegrationController(DifyClient difyClient) {
        this.difyClient = difyClient;
    }

    @PostMapping("/upload-document")
    public ResponseEntity<?> uploadDocument(
            @RequestParam("file") MultipartFile file,
            @RequestParam("datasetId") String datasetId) {
        try {
            // 转换文件
            DifyFile difyFile = DifyFileConverter.from(file);

            // 调用 Dify API 上传文档
            // DocumentResponse response = difyClient.uploadDocument(datasetId, difyFile);

            return ResponseEntity.ok("Document uploaded successfully");
        } catch (IOException e) {
            return ResponseEntity.badRequest()
                .body("Upload failed: " + e.getMessage());
        }
    }
}
```

## API 说明

### DifyFileConverter.from(MultipartFile)

将 Spring `MultipartFile` 转换为 `DifyFile`。

**参数:**
- `multipartFile`: Spring MultipartFile 实例

**返回:**
- `DifyFile`: 转换后的 DifyFile 实例

**异常:**
- `IllegalArgumentException`: 当 multipartFile 为 null 或为空时
- `IOException`: 当文件读取失败时

### DifyFileConverter.convert(MultipartFile)

`from()` 方法的别名，提供更好的 API 可读性。

## 注意事项

1. **文件大小限制**: 转换过程会将整个文件读入内存，请注意文件大小限制
2. **空文件检查**: 转换器会自动检查并拒绝空文件
3. **内容类型**: 会自动从 MultipartFile 中提取 MIME 类型
4. **文件名**: 会保留原始文件名

## 完整示例：文件上传表单

### HTML 表单

```html
<!DOCTYPE html>
<html>
<head>
    <title>File Upload</title>
</head>
<body>
    <form method="POST" action="/api/files/upload" enctype="multipart/form-data">
        <input type="file" name="file" />
        <button type="submit">Upload</button>
    </form>
</body>
</html>
```

### Spring Boot Controller

```java
import io.github.guoshiqiufeng.dify.client.integration.spring.file.DifyFileConverter;
import io.github.guoshiqiufeng.dify.core.pojo.DifyFile;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class FileUploadWebController {

    @GetMapping("/upload")
    public String uploadPage() {
        return "upload";
    }

    @PostMapping("/api/files/upload")
    public String handleFileUpload(
            @RequestParam("file") MultipartFile file,
            RedirectAttributes redirectAttributes) {

        try {
            DifyFile difyFile = DifyFileConverter.from(file);

            // 处理文件...

            redirectAttributes.addFlashAttribute("message",
                "You successfully uploaded " + difyFile.getFilename() + "!");

        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("message",
                "Failed to upload file: " + e.getMessage());
        }

        return "redirect:/upload";
    }
}
```

## 相关资源

- [DifyFile 文档](../../dify-core/README.md)
- [Spring MultipartFile 文档](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/web/multipart/MultipartFile.html)
