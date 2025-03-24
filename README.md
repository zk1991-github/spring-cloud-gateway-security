# 网关鉴权说明文档

## 网关鉴权的用途

1. 统一的用户登录认证：通用的用户登录功能，项目不必再单独编写登录认证功能；为各微服务提供统一的用户登录认证。
2. 服务请求转发：所有请求发送至网关鉴权服务，网关鉴权服务将请求转发至相对应的服务器。
3. 请求权限控制：对所有转发的请求，根据配置进行权限控制，严格控制接口的访问权限，防止跳过前端界面直接进行接口访问漏洞。

## 一、快速开始

1. 部署架构

   网关鉴权软件一般是在Nginx等负载软件下层，通过负载软件使网关鉴权软件达到高可用，部署架构如图所示：
   ![](网关鉴权部署架构.jpg)
   
2. 软件部署

   集群版和单机版的区别：`集群版本`在服务重启后依然保留各用户登录状态；`单机版本`在服务重启后会使各用户登出，需要再次重新登录使用。

   （1） 集群版部署（推荐）

   ① 将package --> cluster文件夹下jar包和config文件夹拷贝至服务器。

   > 注意：jar包和config文件夹需要在同一目录下。

   ② 将database文件夹下的`security_mysql.sql`导入到数据库中。

   > 注意：如遇非`MySQL`数据库，请自行修改适配。

   ③ 修改`application-datasource-cluster.yml`中的数据库和`redis`连接地址。

   > 注意：如遇非`MySQL`数据库，还需修改driver-class-name数据库驱动配置。

   ④ 修改`application-gateway.yml`中`uri`转发地址和`matcher`拦截地址。

   > 注意：范围小的拦截地址要在范围大的拦截地址之前，如`/gateway/test/**`要在`/gateway/**`之前，否则范围小的拦截地址无效。

   ⑤ 在`application-security.yml`中增加`spring.security.proxy-url `代理配置。

   > 注意：代理地址前缀加`/`，代理地址与前端代理或Nginx等代理服务器代理地址保持一致。

   ⑥ 通过命令`nohup java -jar spring-cloud-gateway-security-vx.x.x.jar &`启动服务。

   （2）单机版部署

   ① 将package --> stand-alone文件夹下jar包和config文件夹拷贝至服务器。

   > 注意：jar包和config文件夹需要在同一目录下。

   ② 将database文件夹下的`security_db.db`复制到本地目录。

   > 注意：为了单机版更方便，选择sqlite作为数据库，也可使用其他关系型数据库。

   ③ 修改`application-datasource-standalone.yml`中的数据库地址

   ④ 修改`application-gateway.yml`中`uri`转发地址和`matcher`拦截地址。

   > 注意：范围小的拦截地址要在范围大的拦截地址之前，如`/gateway/test/**`要在`/gateway/**`之前，否则范围小的拦截地址无效。

   ⑤ 在`application-security.yml`中增加`spring.security.proxy-url `代理配置。

   > 注意：代理地址前缀加`/`，代理地址与前端代理或Nginx等代理服务器代理地址保持一致。

   ⑥ 通过命令`nohup java -jar spring-cloud-gateway-security-vx.x.x.jar &`启动服务。

3. 成功验证

   启动完成后，在浏览器中输入前端地址http://localhost:8888，出现前端登录界面，表示服务部署成功。
至此简单的网关鉴权已搭建完成，如需更多功能，请参考以下详细说明。

4. 集成须知

   如果启用了csrf令牌功能，需要在登录前调用“获取令牌”接口`http://ip:port/gateway/csrfTokenGenerator`。
后续请求需要在请求头携带csrf令牌进行访问，头信息key为`X-XSRF-TOKEN`。

## 二、网关对接

### 1. 登录认证

(1) web

   前端通过`POST`请求`/login`地址，`Content-Type`需要设置为`application/x-www-form-urlencoded`。用户名和密码的参数分别为`username`和`password`

(2) 小程序

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

### 2. 源IP获取

  由于经过网关转发请求，普通使用`HttpServletRequest#getRemoteAddress`方式只能获取到网关IP。下游服务获取真实源IP，可从请求头`XReal-IP`中获取，调用方法`HttpServletRequest#getHeader(String)`。

  为了配合下游源IP获取，代理服务应在转发服务节点配置头信息，如nginx需配置

```nginx
location /gatewayservice/ {
            proxy_pass http://127.0.0.1:8888/;
            proxy_http_version 1.1;
            proxy_set_header Upgrade $http_upgrade;
            proxy_set_header Connection "upgrade";

            proxy_set_header Host $host;
            proxy_set_header XReal-IP $remote_addr;
            proxy_set_header X-Forwarded-For $remote_addr;
            proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        }
```

### 3. 用户ID获取

   用户登录后经过网关转发的请求，网关会在请求头增加用户ID和用户名，下游服务可通过请求头获取相应数据，用户ID的key为`userId`，用户名的key为`username`。

### 4. 转发日志

   通过开启网关转发日志记录功能，所有经过转发的请求会记录在数据库表`gateway_request_monitor`中，如需编写逻辑，可直接对接数据库表。

### 5. 转发统计

   开启转发日志记录功能后，转发统计也被激活。请求接口`GET http://<IP>:<PORT>/gateway/queryRequestStatistic`即可获取统计信息，统计信息详情，请查看`web接口文档 6.查询转发统计信息`。

### 6. 登录日志

   通过开启登录日志记录功能，用户成功登录的日志信息会记录在数据库表`t_log`，如需编写逻辑，可直接对接数据库表。

## 三、配置说明

### 1. 网关拦截

```yaml
spring:
  cloud:
    gateway:
      session:
        # 不限制Session过期时间（转发时生效）：-1 单位：分钟
        timeout: 30
        # 不限制登录人数：-1
        maxSessions: 10000
      default-filters:
        - Session
      routes:
        - id: server_route
          # 转发地址
          uri: http://localhost:8080
          predicates:
            # 拦截请求路径
            - name: Path
              args:
                matcher: /gateway/**
          filters:
            # 跳过1个前缀
            - name: StripPrefix
              args:
                # 此处 key 必须为 “parts”
                parts: 1
            # 请求流量限制，默认5MB
            - name: RequestSize
              args:
                maxSize: 5000000
            # 限流
            - name: RequestRateLimiter
              args:
                # 每秒生成的令牌数
                redis-rate-limiter.replenishRate: 100
                # 最大令牌数量
                redis-rate-limiter.burstCapacity: 200
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
            # 请求流量限制，默认5MB
            - name: RequestSize
              args:
                maxSize: 5000000
        # sockJS路由需要与websocket路由结合使用
        - id: sockJS_route
          uri: http://localhost:8080
          predicates:
            - Path=/websocket/**
        - id: websocket_route
          uri: ws://localhost:8080
          predicates:
            - Path=/websocket/**
      # 开启/关闭网关
      enabled: true
      loadbalancer:
        use404: true
  application:
    name: gateway
```

   拦截主要配置`spring.cloud.gateway.routes`

   HTTP拦截配置如下：

（1）`id`为唯一即可

（2）`uri`是拦截后要转发的地址，只能写到端口号

（3）`predicates.name`为`Path`时，`args.matcher`为拦截的地址，可以为多级地址如`/a/b/**`

（4）`filters.name`为`StripPrefix`时，`args.paths`为转发后跳过的路径数，如配置为`1`时，`/a/b** `转发后路径为`/b/**`

（5）`filters.name`为`RequestSize`时，`args.maxSize`为请求转发的数据量最大值限制

   WebSocket拦截分为sockjs和普通websocket，普通websocket直接拦截连接端点，转发到`ws`协议地址；sockjs还需要配置拦截的`url`以及转发的`http`地址。

### 2. 超级管理员

   在配置文件中进行超级管理员设置，避免在数据库中误删除超级管理员。在`application.yml`配置超级管理员，配置格式如下：

```yaml
login:
  user:
    # 自定义超级管理员id
    id: 0
    # 用户名设置
    username: superadmin
    # 密码设置，可在com.github.zk.spring.cloud.gateway.security.util.PasswordGeneratorUtils类，使用main方法生成
    password: "{bcrypt}$2a$10$0EQexC0XYw58x.ys.Ym8QO3H2Llr0G4wEAFddm8PkOUGy6hQraaui"
    # 是否不锁定账户
    accountNonLocked: true
    # 角色设置
    roles:
      # 自定义id
      - id: 0
        # 自定义角色名称
        roleName: 超级管理员
        # 权限设置
        permissionInfos:
          # 自定义权限名称
          - urlName: 所有权限
            # 自定义权限 /** 表示全部权限
            url: /**
```

### 3. Session控制

   Session控制是在服务无请求时，自动登出服务的时间控制。在`application-gateway.yml`配置Session超时时间，单位为分钟，值为-1时表示无限时长，具体配置如下：

```yaml
spring:
  cloud:
    gateway:
      session:
        # 不限制Session过期时间（转发时生效）：-1 单位：分钟
        timeout: 30
```

### 4. 同时在线用户数控制

   同时在线用户数控制是对登录用户数量的限制，未退出的用户，重复登录时不受在线用户数量限制。在`application-gateway.yml`配置同时在线用户数控制，当值设置为-1时表示无限制，具体配置如下：

```yaml
spring:
  cloud:
    gateway:
      session:
        # 不限制登录人数：-1
        maxSessions: 10000
```

### 5. 登录日志

   登录日志是在用户登录后，将登录信息进行记录，支持在控制台打印日志和记录到数据库中两种方式。在`application-gateway.yml`配置日志记录，不配置时默认不记录登录日志，具体配置如下：

1. 控制台打印

```yaml
log:
  enabled: true
```

2. 数据库中记录

```properties
log:
  enabled: true
  database: true
```

### 6. 账户锁定

   用户登录系统时，输入密码错误多次后锁定账户，避免暴力破解密码，默认输入错误3次。在`application-gateway.yml`中配置密码错误次数，当值为-1时表示不锁定账户，同时可配置解锁时间，默认锁定时间5分钟,配置时需要携带单位，具体配置如下：

```yaml
spring:
  cloud:
    gateway:
      session:
        lockRecord: 5
        lockedTime: 1M
```

### 7. 密码加密

   当使用`POST`请求，传入`password`参数时，网关会自动将密码加密转发。在`application-gateway.yml`文件中配置`- RequestBodyOperation`，具体配置如下：

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

### 8. 转发日志

   当请求经过网关时，网关会对转发的请求进行日志记录。在`application-gateway.yml`文件中配置`-Monitor`拦截器，可以在路由配置外配置所有转发都经过拦截，也可以在路由配置内，配置该路由转发拦截记录，详细配置如下：

拦截所有请求

```yaml
spring:
  cloud:
    gateway:
      default-filters:
        - Monitor
```

或拦截路由下请求

```yaml
spring:
  cloud:
    gateway:
      routes:
        - id: server_route
          # 转发地址
          uri: http://127.0.0.1:8081
          predicates:
            # 拦截请求路径
            - name: Path
              args:
                matcher: /gateway/**
          filters:
            - Monitor
```

### 9. csrf拦截

   网关支持csrf拦截控制，防止跨站攻击。在`application-security.yml`文件中配置以关闭csrf拦截，默认开启，如图所示：

```yml
spring:
  secrurity:
    csrf-enable: false
```

### 10. 小程序

   网关支持小程序应用，主要配置在`application-wechat.yml`，其中注意`wechat.roleIds`需要与数据库中的角色id对应，设置完成后自动绑定相关权限。

### 11. 负载均衡

​	负载均衡是将请求转发到多个下游服务集群的方式，用来减轻下游服务的请求压力。当`application.yml`中`spring.profiles.active`配置了`loadbalance`时，负载均衡开启。`application-loadbalance.yml`配置如下：

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
      ##
      configurations: health-check
```

（1）`spring.cloud.discovery.client.simple.instances.providerService`中`uri`是服务的地址，根据实际服务，可配置多个地址用于负载均衡。

（2）`spring.cloud.loadbalancer.healthCheck.path.providerService`为健康检查地址，需要被负载的服务提供与配置地址相同的`GET`服务。

（3）其他配置保持默认即可。

## 四、 注意事项

### 1. 跨域问题

   如果未使用网关代理前端，则需要使用代理解决跨域问题，方式有两种

（1）前端代理

（2）Nginx代理

   Nginx代理后，也需要配置如上所示代理路径，Nginx中配置应注意末尾"/"，详细配置如下：

```text
location /proxy/ {
  proxy_pass  http://127.0.0.1:8888/;
}
```

   代理后，服务也需要在`application-security.yml`中配置代理路径，示例中`/proxy`根据实际地址修改，如下所示：

```yaml
spring:
  security:
    proxy-url: "/proxy"
```

### 2. 数据库依赖

   如果需要对用户字段进行扩展，可在导入的表结构中扩展字段。

### 3. 静态资源放行

   如需将静态资源跳过网关控制，在 `application-security.yml` 中配置，结构如下：

```yaml
spring:
  security:
    antpatterns: "/js/**,/css/**"
```

### 4. websocket测试

   访问 `http://IP:PORT/web/websocket.html` 界面，websocket测试访问地址为`ws://localhost:8888/websocket`，通过网关转发至目标服务。
目标websocket服务需要暴露端点为websocket。

### 5. 源码编译

   如需源码编译版本

- 单机版本，在maven的`profiles`选择`stand-alone`进行package打包
- 集群版本，在maven的`profiles`选择`cluster`进行package打包