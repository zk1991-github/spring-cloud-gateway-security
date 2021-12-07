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

#### v3.0新增功能
- 小程序认证鉴权
- 支持Session永久有效设置
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

### 3. 使用注意事项

- 用户的表结构至少需要包含当前表数据及关系（数据内容不限制）
- 修改 `application-datasource.yml` 中的数据库地址为用户表所在数据库地址
- 修改 `application-gateway.yml` 中转发地址为所需的服务地址和拦截地址（拦截地址范围大的写在范围小的配置之后）
- 小程序相关配置在 `application-wechat.yml` , 其中 `roleIds` 需要与数据库中的角色id对应，设置完成后自动绑定相关权限