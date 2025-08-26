---
lang: zh-CN
title: 介绍
description: 
---

## 特性

- 支持自定义过滤
- 可以自定义实现自己的统一业务处理如：使用统计、消息接收解密

> 使用spring扫描实例, 实现`PipelineProcess`接口

## PipelineProcess 接口

| 方法名称    | 请求参数类型          | 返回参数类型  | 备注                                |
|---------|-----------------|---------|-----------------------------------|
| support | PipelineContext | boolean | 用户判断当前过滤器是否生效，返回true 支持 false 不支持 |
| order   | -               | Long    | 获取排序，越小越靠前                        |
| process | PipelineContext | -       | 过滤器处理逻辑                           |

## PipelineContext

| 参数名称      | 类型      | 备注          |
|-----------|---------|-------------|
| needBreak | boolean | 是否终止过滤器整个链路 |
| code      | String  | 类型编码        |
| model     | 泛型      | 拦截内容        |
