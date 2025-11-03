## Dify 数据集功能实现指南

请按照以下结构化步骤实现 Dify 数据集功能，确保代码质量与项目规范：

### 1. 接口定义
- **核心接口**：在以下位置创建数据集接口
  ```
  dify-support/dify-support-server/src/main/java/io/github/guoshiqiufeng/dify/server/DifyDataset.java
  ```
- **客户端接口**：在以下位置定义客户端接口
  ```
  dify-support/dify-support-server/src/main/java/io/github/guoshiqiufeng/dify/server/client/DifyDatasetClient.java
  ```

### 2. 接口实现
- **服务端实现**：在以下位置实现核心功能
  ```
  dify-support/dify-support-server/src/main/java/io/github/guoshiqiufeng/dify/server/impl/DifyDatasetClientImpl.java
  ```
    - 遵循 Alibaba Java 代码规范
    - 确保实现完整的异常处理机制
    - 添加必要的 JavaDoc 注释

### 3. 客户端适配层
- **Spring 5 适配**：
  ```
  dify-client/dify-client-spring5/src/main/java/io/github/guoshiqiufeng/dify/client/spring5/dataset/DifyDatasetDefaultClient.java
  ```
- **Spring 6 适配**：
  ```
  dify-client/dify-client-spring6/src/main/java/io/github/guoshiqiufeng/dify/client/spring6/dataset/DifyDatasetDefaultClient.java
  ```
    - 确保与 Spring 框架版本兼容
    - 实现自动配置支持

### 4. 数据模型
- **请求模型**：定义数据集相关的请求数据结构
  ```
  dify-support/dify-support-server/src/main/java/io/github/guoshiqiufeng/dify/server/dto/request/DatasetRequest.java
  dify-support/dify-support-server/src/main/java/io/github/guoshiqiufeng/dify/server/dto/request/DocumentRequest.java
  ```
- **响应模型**：定义数据集相关的响应数据结构
  ```
  dify-support/dify-support-server/src/main/java/io/github/guoshiqiufeng/dify/server/dto/response/DatasetResponse.java
  dify-support/dify-support-server/src/main/java/io/github/guoshiqiufeng/dify/server/dto/response/DocumentResponse.java
  ```

### 5. 测试验证
- **单元测试**：在对应 test 目录下实现完整测试用例
    - 覆盖正常流程与异常场景
    - 使用 Mock 框架验证交互逻辑
- **集成测试**：
  ```
  dify-spring-boot-starter/src/test/java/io/github/guoshiqiufeng/dify/boot/DatasetTest.java
  dify-spring-boot2-starter/src/test/java/io/github/guoshiqiufeng/dify/boot/DatasetTest.java
  ```
    - 验证 Starter 自动配置功能
    - 测试实际数据集操作场景

### 6. 文档编写
- **功能文档**：
  ```
  docs/guide/feature/dataset.md (中文)
  docs/en/guide/feature/dataset.md (英文)
  ```
    - 包含功能介绍、使用示例、配置说明
    - 添加代码片段和流程图
    - 说明数据集管理和文档操作方式
- **项目声明**：
  ```
  docs/guide/introduction.md (中文)
  docs/en/guide/introduction.md (英文)
  ```
    - 在"已实现功能"部分添加新功能说明
    - 保持与其他功能描述风格一致

### 7. 最终验证
完成以上步骤后，执行以下命令：
```bash
./gradlew licenseFormat  # 确保许可证格式正确
./gradlew test           # 验证所有测试通过
```

### 注意事项
1. 所有实现必须严格遵循 Alibaba Java 代码规范
2. 保持中英文文档内容同步更新
3. 测试覆盖率不得低于 80%
4. 确保向后兼容性，避免破坏现有功能
5. 代码提交前需进行本地完整测试
