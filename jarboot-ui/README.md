# jarboot前端ui
---
## 使用umi脚手架编译打包
关于<a href="https://umijs.org/zh-CN/docs" target="_blank">umi</a>详见官方网站

安装依赖,

```bash
$ yarn
```

启动调试服务器，代理默认配置了9899端口,

```bash
$ yarn start
```
构建实际生产环境,

```bash
$ yarn build
```

将构建好的html、css和js文件从<code>dist</code>目录拷贝到如下<code>static</code>目录
```bash
jarboot
├─logs
├─services
│  ├─demo1-service
|  |   └─demo1-service.jar
│  └─demo2-service
|      └─demo2-service.jar
└─static
   ├─index.html
   ├─umi.css
   └─umi.js
```
