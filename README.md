# 依赖推送工具 Maven deploy tool

## 使用指南 USE

- (一次) 需要配置 Maven 的环境变量
- (一次) 修改 settings.xml 文件
- (一次) 修改 conf.properties 文件
- 在 IDE 中运行 Main 或者执行 jar 

可以在 conf.properties 设置 url 后双击`start.bat`

- 或者参考命令：
```shell
java -jar maven-deploy-tool.jar -D url=file:///D:/MAVEN_REPO/deploy_local
```

- 配置文件中的`true`可直接注释关闭
- 建议先关闭`skipRepoHave`，\
  仅拷贝特定域名文件夹下的包，\
  因为判断远程是否有这个包需要时间
- `getGavFromPath`需要按 Maven 的目录层级摆放，\
  多用于标准的本地仓库


### 非 Maven Web 项目上传

- jar 在 tomcat/webapps/${youApp}/WEB-INF/lib
- 多个微服务建议先把文件都拷贝到某个文件夹中，避免同名 jar 重复判断是否要跳过


## 已完成 DONE

- (可设置) 跳过成功
- (可设置) 跳过远程已有不让更新
- (可设置) 跳过远程仓库已有的包
- (可设置) 跳过大文件 (支持算式和下划线)
- 跳过 .zip/.tar


- 找不到 pom 文件时自动从 jar 中解压
- (可设置) 没有 jar 文件就在路径中获取\
  groupId artifactId version\
  并生成不带依赖等内容的 pom
- 支持推送同个目录下多个不同 artifactId 的 jar


- 在控制台中打印错误日志中的关键原因，\
  并换行缩进两个空格


- 配置文件支持 UTF-8
- 配置项支持命令行`-D key=value`，\
  优先级：命令行 > 环境变量 > 配置文件


- 成功失败在包目录中创建`.deploy.succ/fail`标识
- 获取到本地日志写入包目录`.get.log`
- 上传到远程日志写入包目录`.deploy.log`
- 附带删除脚本


* (可设置) 根据 CPU 核心数的倍数多线程运行 (支持小数)
* (可设置) 命令行超时时间避免进程卡住 (支持算数和下划线)
* 自动生成 jstack 脚本便于检查性能问题

* 在 IDEA 中可以直接点击链接
  * file:///格式打印文件
  * 日志中增加(%F:%L)输出代码所在文件行号


## 未完成 TODO

- 推送成功转移目录，便于失败的再次尝试
- 找不到 pom 文件时自动从 jar 中解压生成的临时文件夹自动删除
- 集成删除生成的文件功能
- GavFromPath 支持 classifier
- 跳过远程仓库已有的包实现方式升级：\
  在有 Nexus 索引的情况下使用索引而不是 get 包
- 未传参或设置推送目录时使用 GUI 图形界面
- 做成 Maven 插件推送到中央仓库


## 贡献代码注意 NOTE

- 提交前务必执行
  - 分享的 Main
  - "package zip"/`mvn verify`，单元测试可以检查难以发现的 bug
- 需要思考逻辑的地方添加单元测试，用于检查不能直接看出来的问题

* 代码拆分成多个文件
* 注意日志级别
  * error 需要人工介入的问题，不会因为单个中断
  * warn  自动处理但可能有问题
  * info  只有最后的成功和不影响结果的失败
  * debug 为了排除问题而打的日志
  * trace 目前只有命令行日志打印用了这个级别
  * 一个类中多个日志打印时需细分级别，\
    方便在日志配置文件中控制，\
    但是默认 warn 级别就不要输出对一般情况下没用的日志
* 注意日志后面要粘贴到 Excel 用来分析的
  * 在进程中配置了一个跟踪进度的`MDC`
  * 格式统一`描述\t路径链接或详情`，因为链接处理不同所以不放进`MDC`


## 代码扫描 SonarCloud

指标  | 徽章
---   | ---
安全  | [![Security Rating](https://sonarcloud.io/api/project_badges/measure?project=LinWanCen_maven-deploy-tool&metric=security_rating)](https://sonarcloud.io/dashboard?id=LinWanCen_maven-deploy-tool)
可维护| [![Maintainability Rating](https://sonarcloud.io/api/project_badges/measure?project=LinWanCen_maven-deploy-tool&metric=sqale_rating)](https://sonarcloud.io/dashboard?id=LinWanCen_maven-deploy-tool)
可靠性| [![Reliability Rating](https://sonarcloud.io/api/project_badges/measure?project=LinWanCen_maven-deploy-tool&metric=reliability_rating)](https://sonarcloud.io/dashboard?id=LinWanCen_maven-deploy-tool)
错误  | [![Bugs](https://sonarcloud.io/api/project_badges/measure?project=LinWanCen_maven-deploy-tool&metric=bugs)](https://sonarcloud.io/dashboard?id=LinWanCen_maven-deploy-tool)
漏洞  | [![Vulnerabilities](https://sonarcloud.io/api/project_badges/measure?project=LinWanCen_maven-deploy-tool&metric=vulnerabilities)](https://sonarcloud.io/dashboard?id=LinWanCen_maven-deploy-tool)
代码行| [![Lines of Code](https://sonarcloud.io/api/project_badges/measure?project=LinWanCen_maven-deploy-tool&metric=ncloc)](https://sonarcloud.io/dashboard?id=LinWanCen_maven-deploy-tool)

