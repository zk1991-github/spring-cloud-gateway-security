## 说明

### 1. 具备的功能

- 支持登录鉴权
- 默认提供超级管理员（具备全部功能权限）
- 支持Session失效时间设置
- 支持最大同时在线用户数设置
- 支持查询在线用户数
- 支持查询当前登录用户(简要信息)
- 支持异地登录踢出功能
- 支持分布式部署
- 微信小程序认证鉴权
- 登录日志记录
- 多次密码输入错误，锁定账户（默认3次）
- 用户修改密码
- 提供密码加密处理
- 接口权限管理
- 权限管理界面
- 负载均衡
- 真实IP转发下游

#### v4.1.6新增功能
- 匿名访问接口设置

### 2. 功能介绍

#### 1. 登录鉴权

##### (1) Web

前端通过`POST`请求`/login`地址，`Content-Type`需要设置为`application/x-www-form-urlencoded`。用户名和密码的参数分别为`username`和`password`

##### (2) 小程序

小程序通过 `POST`请求 `/login/weChatLogin`地址，`Content-Type` 设置为 `application/json`，传参为 `weChatCode` 和 `weChatUserInfo`，结构如下：

```json
{
  "weChatCode": "xx",
  "weChatUserInfo": {
    "nickName": "xxx",
    "gender": 0
  }
}
```

#### 2. 提供超级管理员

在 `application.yml` 配置，默认用户名/密码：superadmin/123456。
可自行修改，密码生成方式，使用 `com.github.zk.spring.cloud.gateway.security.util.PasswordGeneratorUtils` 类

#### 3. Session失效时间设置

在 `application-gateway.yml`中 `spring.cloud.gateway.session.timeout`单位（分钟）

#### 4. 最大同时在线用户数设置

在 `application-gateway.yml`中 `spring.cloud.gateway.session.maxSessions`。设置`-1`不限制用户数

#### 5. 查询在线用户数

请求地址 `/gateway/getOnlineNums` 需要在数据库权限表中配置 `/getOnlineNums`权限

#### 6. 查询当前登录用户

请求地址 `/gateway/queryUser` ，需要在数据库权限表中配置 `/queryUser`权限

#### 7. 异地登录踢出功能

同一个账号在其他地方登录时，踢出上一次登录的用户

#### 8. 日志记录

日志支持开关设置，默认关闭状态，如需打开，在`yml`中配置如下：
```yaml
log:
  enabled: true
```
只记录登录成功的用户日志，分为两种模式：
1. 在控制台打印日志（默认）
2. 记录至数据库
```properties
log:
  database: true
```

#### 9. 用户修改密码
POST请求地址 `/gateway/updatePassword`，参数为JSON格式
```json
{
    "username": "admin",
    "oldPassword": "123456",
    "newPassword": "12345"
}
```

#### 10. 密码加密
用于注册用户时，提供密码加密功能。在`application-gateway.yml`配置文件中相应的url路由下配置 - RequestBodyOperation，如：
```yaml
spring:
  cloud:
    gateway:
      routes:
        - id: server_route
          # 转发地址
          uri: http://127.0.0.1:8080
          predicates:
            # 拦截请求路径
            - name: Path
              args:
                matcher: /gateway/**
          filters:
            - name: StripPrefix
              args:
                parts: 1
            - RequestBodyOperation
```
#### 11. 接口权限管理

包括API权限增删改查，角色与权限的双向绑定，公开权限的控制等功能

#### 12. 权限管理界面

- 在 `application-release.yml` 配置发布地址。
- 界面请求地址 `http://<IP>:<PORT>/web/dist/index.html`

#### 13. 负载均衡配置
- 在 `application-loadbalance.yml` 和 `application-gateway.yml` 中配置负载均衡
- 首先在 `application-loadbalance.yml` 配置负载均衡节点和负载均衡策略
```yaml
spring:
  cloud:
    discovery:
      client:
        simple:
          instances:
            ## 负载均衡地址
            providerService:
              - uri: http://1.1.1.1:8080
              - uri: http://1.1.1.2:8080
    loadbalancer:
      healthCheck:
        ## 健康检查地址， 可以采用服务其中一个地址作为健康检查地址
        path:
          providerService: /health/healthCheck
        ## 健康检查初始延迟
        initialDelay: 0
        ## 重新运行健康检查周期间隔
        interval: 5s
#        refetchInstances: true
      configurations: health-check
```
- 然后在 `application-gateway.yml` 配置路由，注意地址使用 `lb://` 开始
```yaml
- id: server_route2
  # 转发地址, providerService为"application-loadbalance.yml"配置文件中
  # spring.cloud.discovery:client.simple.instances 下的key
  uri: lb://providerService
  predicates:
    # 拦截请求路径
    - name: Path
      args:
        matcher: /loadbalance/**
  filters:
    # 跳过1个前缀
    - name: StripPrefix
      args:
        # 此处 key 必须为 “parts”
        parts: 1
    #            - StripPrefix=1
    # token身份认证 JwtCheckGatewayFilterFactory
    - TokenCheck
    # 请求流量限制，默认5MB
    - name: RequestSize
      args:
        maxSize: 5000000
```
#### 14. 获取真实IP

通过请求头`XReal-IP` 获取真实的请求ip，如需获取网关IP，使用`request#getRemoteAddress`方式

### 3. 使用注意事项

- 当前服务由于采用跳转方式，需要与前端同源，否则登录成功后会因无法获取用户，导致返回失败
- 用户的表结构至少需要包含当前表数据及关系（数据内容不限制）
- 修改 `application-datasource.yml` 中的数据库地址为用户表所在数据库地址
- 修改 `application-gateway.yml` 中转发地址为所需的服务地址和拦截地址（拦截地址范围大的写在范围小的配置之后）
- 小程序相关配置在 `application-wechat.yml` , 其中 `roleIds` 需要与数据库中的角色id对应，设置完成后自动绑定相关权限
- 当前端需要代理时，服务端在 `application-gateway.yml` 中也需要配置代理地址，结构如下：
```yaml
spring:
  web:
    proxy:
      url: "/proxy"
```
> 注意： 代理时需要代理服务自动去掉代理的路径，如nginx代理地址为`/proxy`,请求时应将`/proxy`去掉，配置如：
```text
location /proxy/ {
  proxy_pass  http://127.0.0.1:8080/;
}
```
- 静态资源放行，在 `application-gateway.yml` 中配置，结构如下：
```yaml
spring:
  static:
    antpatterns: "/js/**,/css/**"
```

- 允许匿名访问的转发配置，不能添加 `-tokenCheck` 配置。匿名接口登录后依然可以访问

### 4. 响应code值说明

| 序号 | code  | 描述               |
| ---: | :---: | :--------------- |
|    1 |   0   | 成功              |
|    2 | 9000  | 需要跳转到登录界面   |
|    3 | 10000 | 失败              |