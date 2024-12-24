Web接口

> Web接口主要用于对网关鉴权系统界面的自定义开发使用，若使用本系统自带界面，则忽略此文档。Web接口中的IP为网关鉴权系统所在服务器IP，PORT为网关鉴权系统端口。
## 1. 登录
请求接口
```http request
POST http://<IP>:<PORT>/login
Content-Type: application/x-www-form-urlencoded

username=superadmin&password=123456
```
| 序号 | 参数     | 描述   | 类型   | 说明 |
| ---- | -------- | ------ | ------ | ---- |
| 1    | username | 用户名 | 字符串 | 必填 |
| 2    | password | 密码   | 字符串 | 必填 |
返回结构
- 成功样例
```json
{
    "data": {
        "id": "0",
        "username": "superadmin",
        "phone": null,
        "accountNonLocked": true,
        "roles": [
            {
                "id": "0",
                "roleName": "超级管理员",
                "description": null,
                "permissionInfos": [
                    {
                        "id": null,
                        "urlName": "所有权限",
                        "url": "/**",
                        "open": null,
                        "description": null,
                        "fixed": null,
                        "createTime": null,
                        "permissionInfoPage": null,
                        "keywords": null,
                        "roleInfos": null
                    }
                ]
            }
        ],
        "enabled": true,
        "credentialsNonExpired": true,
        "accountNonExpired": true,
        "authorities": null
    },
    "msg": "成功",
    "code": 200
}
```
| 序号 | 参数                  | 描述           | 类型    | 说明 |
| ---- | --------------------- | -------------- | ------- | ---- |
| 1    | id                    | 用户id         | 字符串  |      |
| 2    | username              | 用户名         | 字符串  |      |
| 3    | phone                 | 电话           | 数字    |      |
| 4    | accountNonLocked      | 是否未锁定     | boolean |      |
| 5    | roles                 | 角色对象       | Object  |      |
| 6    | enabled               | 是否启用用户   | boolean |      |
| 7    | credentialsNonExpired | 用户是否未过期 | boolean |      |
| 8    | accountNonExpired     | 密码是否未过期 | boolean |      |
| 9    | authorities           | 授予的权限     | 集合    |      |

roles对象

| 序号 | 参数            | 描述     | 类型   | 说明 |
| ---- | --------------- | -------- | ------ | ---- |
| 1    | id              | 角色id   | 字符串 |      |
| 2    | roleName        | 角色名   | 字符串 |      |
| 3    | description     | 描述     | 字符串 |      |
| 4    | permissionInfos | 权限对象 | Object |      |

permissionInfos对象

| 序号 | 参数        | 描述     | 类型    | 说明 |
| ---- | ----------- | -------- | ------- | ---- |
| 1    | id          | 权限id   | 字符串  |      |
| 2    | urlName     | 权限名称 | 字符串  |      |
| 3    | url         | 权限地址 | 字符串  |      |
| 4    | open        | 是否公开 | boolean |      |
| 5    | description | 权限描述 | 字符串  |      |
| 6    | fixed       | 是否固定 | boolean |      |
| 7    | createTime  | 创建时间 | 字符串  |      |

- 失败样例
```json
{
    "msg": "用户不存在",
    "code": 707
}
```
```json
{
    "msg": "密码错误",
    "code": 708
}
```

```json
{
    "msg": "用户帐号已被锁定",
    "code": 709
}
```



## 2. 登出

请求接口

```http request
GET http://<IP>:<PORT>/login/logout
```

返回结构

- 成功样例

```json
{
    "msg": "成功",
    "code": 200
}
```

- 失败样例

```text
无
```

## 3. 获取登录用户信息

请求接口

```http request
GET http://<IP>:<PORT>/gateway/getUser
```

返回结构

- 成功样例

```json
{
    "data": {
        "id": "0",
        "username": "superadmin",
        "phone": null,
        "accountNonLocked": true,
        "roles": [
            {
                "id": "0",
                "roleName": "超级管理员",
                "description": null,
                "permissionInfos": [
                    {
                        "id": null,
                        "urlName": "所有权限",
                        "url": "/**",
                        "open": null,
                        "description": null,
                        "fixed": null,
                        "createTime": null,
                        "permissionInfoPage": null,
                        "keywords": null,
                        "roleInfos": null
                    }
                ]
            }
        ],
        "enabled": true,
        "accountNonExpired": true,
        "credentialsNonExpired": true,
        "authorities": null
    },
    "msg": "成功",
    "code": 200
}
```

- 失败样例

```json
"无"
```

## 4. 查询在线用户数

请求接口

```http request
GET http://<IP>:<PORT>/gateway/getOnlineNums
```

返回结构

- 成功样例

```json
{
    "data": 1,
    "msg": "成功",
    "code": 200
}
```

- 失败样例

```json
"无"
```

## 5. 修改密码

请求接口

```http request
POST http://<IP>:<PORT>/gateway/updatePassword
Content-Type: application/json

{
    "username": "admin",
    "oldPassword": "123456",
    "newPassword": "1234567"
}
```

| 序号 | 参数        | 描述   | 类型   | 说明 |
| ---- | ----------- | ------ | ------ | ---- |
| 1    | username    | 用户名 | 字符串 | 必填 |
| 2    | oldPassword | 旧密码 | 字符串 | 必填 |
| 3    | newPassword | 新密码 | 字符串 | 必填 |

返回结构

- 成功样例

```json
{
    "data": true,
    "msg": "成功",
    "code": 200
}
```

- 失败样例

```json
{
    "msg": "修改失败",
    "code": 703
}
```

## 6. 查询转发统计信息

请求接口

```http request
GET http://<IP>:<PORT>/gateway/queryRequestStatistic
```

返回结构

- 成功样例

```json
{
    "data": [
        {
            "urlPath": "/qqq/test",
            "successNum": 2,
            "failNum": 1,
            "successRate": "66.67%",
            "responseDurationAvg": 50
        },
        {
            "urlPath": "/aaa/test",
            "successNum": 1,
            "failNum": 1,
            "successRate": "50.00%",
            "responseDurationAvg": 100
        },
        {
            "urlPath": "/appVersion/test",
            "successNum": 6,
            "failNum": 5,
            "successRate": "54.55%",
            "responseDurationAvg": 9779
        },
        {
            "urlPath": "/menu/getMenuByUserId",
            "successNum": 1,
            "failNum": 1,
            "successRate": "50.00%",
            "responseDurationAvg": 812
        }
    ],
    "msg": "成功",
    "code": 200
}
```

| 序号 | 参数                | 描述         | 类型   | 说明     |
| ---- | ------------------- | ------------ | ------ | -------- |
| 1    | urlPath             | 转发地址     | 字符串 |          |
| 2    | successNum          | 转发成功次数 | 数字   |          |
| 3    | failNum             | 转发失败次数 | 数字   |          |
| 4    | successRate         | 成功率       | 字符串 |          |
| 5    | responseDurationAvg | 平均响应时间 | 数字   | 单位毫秒 |

- 失败样例

```json
{
    "msg": "后台出现异常错误",
    "code": 500
}
```

## 7. 根据类型id查询字典信息

请求接口

```http request
GET http://<IP>:<PORT>/gateway/queryDictByDictTypeId?dictTypeId=<param>
```

| 序号 | 参数       | 描述       | 类型 | 说明 |
| ---- | ---------- | ---------- | ---- | ---- |
| 1    | dictTypeId | 字典类型id | 数字 |      |

返回结构

- 成功样例

```json
{
    "data": [
        {
            "id": "1",
            "dictTypeId": 1,
            "dictVal": "0",
            "dictName": "私有"
        },
        {
            "id": "2",
            "dictTypeId": 1,
            "dictVal": "1",
            "dictName": "公开"
        },
        {
            "id": "3",
            "dictTypeId": 1,
            "dictVal": "2",
            "dictName": "匿名"
        }
    ],
    "msg": "成功",
    "code": 200
}
```

| 序号 | 参数       | 描述       | 类型   | 说明 |
| ---- | ---------- | ---------- | ------ | ---- |
| 1    | id         | 字典id     | 字符串 |      |
| 2    | dictTypeId | 字典类型id | 数字   |      |
| 3    | dictVal    | 字典值     | 字符串 |      |
| 4    | dictName   | 字典名称   | 字符串 |      |

- 失败样例

```json
{
    "msg": "查询失败",
    "code": 704
}
```

## 8. 添加权限

请求接口

```http request
POST http://<IP>:<PORT>/gateway/addPermission
Content-Type: application/json

{
    "urlName": "添加用户",
    "url": "/user/**",
    "open": 0,
    "fixed": 0,
    "roleInfos": [{"id": 1},{"id": 2}]
}
```

| 序号 | 参数      | 描述     | 类型   | 说明                          |
| ---- | --------- | -------- | ------ | ----------------------------- |
| 1    | urlName   | 权限名称 | 字符串 | 必填                          |
| 2    | url       | 权限地址 | 字符串 | 必填                          |
| 3    | open      | 公开状态 | 数字   | 0：私有<br>1：公开<br>2：匿名 |
| 4    | fixed     | 是否固定 | 数字   | 1：不固定<br>2：固定          |
| 5    | roleInfos | 角色数组 | Array  |                               |

返回结构

- 成功样例

```json
{
    "data": 1,
    "msg": "成功",
    "code": 200
}
```

- 失败样例

```json
{
    "msg": "添加失败",
    "code": 701
}
```

## 9. 删除权限

请求接口

```http request
GET http://<IP>:<PORT>/gateway/delPermission?id=<param>
```

| 序号 | 参数 | 描述   | 类型 | 说明 |
| ---- | ---- | ------ | ---- | ---- |
| 1    | id   | 权限id | 数字 | 必填 |

返回结构

- 成功样例

```json
{
    "data": 1,
    "msg": "删除成功",
    "code": 200
}
```

- 失败样例

```json
{
    "msg": "删除失败",
    "code": 702
}
```

## 10. 批量删除权限

请求接口

```http request
GET http://<IP>:<PORT>/gateway/delPermissions?ids=<param1>,<param2>
```

| 序号 | 参数 | 描述   | 类型 | 说明                   |
| ---- | ---- | ------ | ---- | ---------------------- |
| 1    | ids  | 权限id | 数字 | 必填<br>多个id逗号分隔 |

返回结构

- 成功样例

```json
{
    "data": 2,
    "msg": "成功",
    "code": 200
}
```

- 失败样例

```json
{
    "msg": "删除失败",
    "code": 702
}
```

## 11. 修改权限

请求接口

```http request
POST http://<IP>:<PORT>/gateway/updatePermission
Content-Type: application/json

{
    "id": "1706248088114479106",
    "urlName": "测试请求修改",
    "url": "/user-update/**",
    "open": 0,
    "fixed": 0,
    "roleInfos": [{"id": 1},{"id": 2}]
}
```

| 序号 | 参数      | 描述     | 类型   | 说明         |
| ---- | --------- | -------- | ------ | ------------ |
| 1    | id        | 权限id   | 字符串 | 必填         |
| 2    | urlName   | 权限名称 | 字符串 | 需修改时填写 |
| 3    | url       | 权限地址 | 字符串 | 需修改时填写 |
| 4    | open      | 公开状态 | 数字   | 需修改时填写 |
| 5    | fixed     | 是否固定 | 数字   | 需修改时填写 |
| 6    | roleInfos | 角色数组 | Array  | 需修改时填写 |

返回结构

- 成功样例

```json
{
    "data": 1,
    "msg": "成功",
    "code": 200
}
```

- 失败样例

```json
{
    "msg": "修改失败",
    "code": 703
}
```

## 12. 查询权限

请求接口

```http request
GET http://<IP>:<PORT>/gateway/queryPermission?keywords=<param1>&current=<param2>&size=<param3>
```

| 序号 | 参数     | 描述         | 类型   | 说明 |
| ---- | -------- | ------------ | ------ | ---- |
| 1    | keywords | 关键字       | 字符串 |      |
| 2    | current  | 页码         | 数字   | 必填 |
| 3    | size     | 每页显示条数 | 数字   | 必填 |

返回结构

- 成功样例

```json
{
    "data": {
        "total": 7,
        "details": [
            {
                "id": "1499320522679283714",
                "urlName": "批量删除权限",
                "url": "/delPermission",
                "open": 0,
                "description": "根据权限id，批量删除权限",
                "fixed": 1,
                "createTime": "2022-03-03 17:47:23",
                "permissionInfoPage": null,
                "keywords": null,
                "roleInfos": [
                    {
                        "id": "1",
                        "roleName": "管理员",
                        "description": null,
                        "permissionInfos": null
                    }
                ]
            },
            {
                "id": "1495638064385564674",
                "urlName": "根据角色id，查询权限",
                "url": "/queryPermissionsByRoleId",
                "open": 0,
                "description": "根据角色id，查询权限",
                "fixed": 1,
                "createTime": "2022-02-21 13:54:37",
                "permissionInfoPage": null,
                "keywords": null,
                "roleInfos": [
                    {
                        "id": "1",
                        "roleName": "管理员",
                        "description": null,
                        "permissionInfos": null
                    }
                ]
            },
            {
                "id": "1495637948090097665",
                "urlName": "根据角色绑定权限",
                "url": "/bindPermissionByRole",
                "open": 0,
                "description": "根据角色绑定权限",
                "fixed": 1,
                "createTime": "2022-02-21 13:54:09",
                "permissionInfoPage": null,
                "keywords": null,
                "roleInfos": [
                    {
                        "id": "1",
                        "roleName": "管理员",
                        "description": null,
                        "permissionInfos": null
                    }
                ]
            },
            {
                "id": "1495637762299207682",
                "urlName": "修改权限",
                "url": "/updatePermission",
                "open": 0,
                "description": "修改权限",
                "fixed": 1,
                "createTime": "2022-02-21 13:53:25",
                "permissionInfoPage": null,
                "keywords": null,
                "roleInfos": [
                    {
                        "id": "1",
                        "roleName": "管理员",
                        "description": null,
                        "permissionInfos": null
                    }
                ]
            },
            {
                "id": "1495637664693559298",
                "urlName": "删除权限",
                "url": "/delPermission",
                "open": 0,
                "description": "删除权限",
                "fixed": 1,
                "createTime": "2022-02-21 13:53:02",
                "permissionInfoPage": null,
                "keywords": null,
                "roleInfos": [
                    {
                        "id": "1",
                        "roleName": "管理员",
                        "description": null,
                        "permissionInfos": null
                    }
                ]
            },
            {
                "id": "1495637567519924225",
                "urlName": "新增权限",
                "url": "/addPermission",
                "open": 0,
                "description": "新增权限",
                "fixed": 1,
                "createTime": "2022-02-21 13:52:39",
                "permissionInfoPage": null,
                "keywords": null,
                "roleInfos": [
                    {
                        "id": "1",
                        "roleName": "管理员",
                        "description": null,
                        "permissionInfos": null
                    }
                ]
            },
            {
                "id": "1495637399244447746",
                "urlName": "分页查询权限",
                "url": "/queryPermission",
                "open": 0,
                "description": "分页查询权限",
                "fixed": 1,
                "createTime": "2022-02-21 13:51:59",
                "permissionInfoPage": null,
                "keywords": null,
                "roleInfos": [
                    {
                        "id": "1",
                        "roleName": "管理员",
                        "description": null,
                        "permissionInfos": null
                    }
                ]
            }
        ]
    },
    "msg": "成功",
    "code": 200
}
```

- 失败样例

```json
{
    "msg": "查询失败",
    "code": 704
}
```

## 13. 角色绑定权限

请求接口

```http request
POST http://<IP>:<PORT>/gateway/bindPermissionByRole
Content-Type: application/json

{
    "id": "2",
    "permissionInfos": [{"id": "1499320522679283714"}, {"id": "1495638064385564674"}]
}
```

| 序号 | 参数            | 描述     | 类型   | 说明 |
| ---- | --------------- | -------- | ------ | ---- |
| 1    | id              | 角色id   | 字符串 | 必填 |
| 2    | permissionInfos | 权限数组 | Array  |      |

permissionInfos对象

| 序号 | 参数 | 描述   | 类型   | 说明 |
| ---- | ---- | ------ | ------ | ---- |
| 1    | id   | 权限id | 字符串 | 必填 |

返回结构

- 成功样例

```json
{
    "data": true,
    "msg": "成功",
    "code": 200
}
```

- 失败样例

```json
{
    "msg": "绑定失败",
    "code": 705
}
```

## 14. 查询所有角色

请求接口

```http request
GET http://<IP>:<PORT>/gateway/queryAllRoles
```

返回结构

- 成功样例

```json
{
    "data": [
        {
            "id": "1",
            "roleName": "管理员",
            "description": null,
            "permissionInfos": null
        },
        {
            "id": "2",
            "roleName": "普通用户",
            "description": null,
            "permissionInfos": null
        }
    ],
    "api": "/gateway/queryAllRoles",
    "msg": "成功",
    "code": 200
}
```

| 序号 | 参数     | 描述     | 类型   | 说明 |
| ---- | -------- | -------- | ------ | ---- |
| 1    | id       | 角色id   | 字符串 |      |
| 2    | roleName | 角色名称 | 字符串 |      |

- 失败样例

```json
{
    "msg": "查询失败",
    "code": 704
}
```

## 15. 根据角色id查询权限

请求接口

```http request
GET http://<IP>:<PORT>/gateway/queryPermissionsByRoleId?roleId=<param>
```

| 序号 | 参数   | 描述   | 类型 | 说明 |
| ---- | ------ | ------ | ---- | ---- |
| 1    | roleId | 角色id | 数字 | 必填 |

返回结构

- 成功样例

```json
{
    "data": [
        {
            "id": "1499320522679283714",
            "urlName": "批量删除权限",
            "url": "/delPermission",
            "open": 0,
            "description": "根据权限id，批量删除权限",
            "fixed": null,
            "createTime": "2022-03-03 17:47:23",
            "permissionInfoPage": null,
            "keywords": null,
            "roleInfos": null
        },
        {
            "id": "1495638064385564674",
            "urlName": "根据角色id，查询权限",
            "url": "/queryPermissionsByRoleId",
            "open": 0,
            "description": "根据角色id，查询权限",
            "fixed": null,
            "createTime": "2022-02-21 13:54:37",
            "permissionInfoPage": null,
            "keywords": null,
            "roleInfos": null
        }
    ],
    "msg": "成功",
    "code": 200
}
```

| 序号 | 参数        | 描述     | 类型    | 说明 |
| ---- | ----------- | -------- | ------- | ---- |
| 1    | id          | 权限id   | 字符串  |      |
| 2    | urlName     | 权限名称 | 字符串  |      |
| 3    | url         | 权限地址 | 字符串  |      |
| 4    | open        | 是否公开 | boolean |      |
| 5    | description | 权限描述 | 字符串  |      |
| 6    | fixed       | 是否固定 | boolean |      |
| 7    | createTime  | 创建时间 | 字符串  |      |

- 失败样例

```json
{
    "msg": "查询失败",
    "code": 704
}
```
## 16. 生成加密密码

请求接口

```http request
GET http://<IP>:<PORT>/gateway/passwordGenerator?password=<param>
```

| 序号 | 参数       | 描述   | 类型 | 说明  |
| ---- |----------| ---- | ---- |-----|
| 1    | password | 密码 | 字符串 | 必填  |

返回结构

- 成功样例

```json
{
  "data": "{bcrypt}$2a$10$A6he6KCgCnvtq2IS6hOWY.cNXbwzSo5eBJyVuonSFVztPIJXznRXq",
  "msg": "成功",
  "code": 200
}
```

- 失败样例

```json
{
    "msg": "后台出现异常错误",
    "code": 500
}
```
## 17. 私有权限分页查询

请求接口

```http request
GET http://<IP>:<PORT>/gateway/queryPrivatePermission?keywords=<param1>&current=<param2>&size=<param3>
```

| 序号 | 参数     | 描述         | 类型   | 说明 |
| ---- | -------- | ------------ | ------ | ---- |
| 1    | current  | 页码         | 数字   | 必填 |
| 2    | size     | 每页显示条数 | 数字   | 必填 |
| 3    | keywords | 关键字       | 字符串 |      |

返回结构

- 成功样例

```json
{
  "data": {
    "records": [
      {
        "id": "1778708514361913345",
        "urlName": "刷新权限",
        "url": "/updateSecurityContext",
        "open": 0,
        "description": "",
        "fixed": 1,
        "createTime": "2024-04-12 16:55:31",
        "roleInfos": [
          {
            "id": "2",
            "roleName": "普通用户",
            "description": null,
            "permissionInfos": null
          },
          {
            "id": "1",
            "roleName": "管理员",
            "description": null,
            "permissionInfos": null
          }
        ]
      },
      {
        "id": "1778621402404941825",
        "urlName": "分页查询私有权限",
        "url": "/queryPrivatePermission",
        "open": 0,
        "description": "",
        "fixed": 1,
        "createTime": "2024-04-12 11:09:22",
        "roleInfos": [
          {
            "id": "2",
            "roleName": "普通用户",
            "description": null,
            "permissionInfos": null
          },
          {
            "id": "1",
            "roleName": "管理员",
            "description": null,
            "permissionInfos": null
          }
        ]
      }
    ],
    "total": 11,
    "size": 10,
    "current": 1,
    "orders": [],
    "optimizeCountSql": true,
    "searchCount": true,
    "maxLimit": null,
    "countId": null,
    "pages": 2
  },
  "msg": "成功",
  "code": 200
}
```

- 失败样例

```json
{
    "msg": "查询失败！",
    "code": 704
}
```

## 18. 清空所有用户会话

请求接口

```http request
GET http://<IP>:<PORT>/gateway/clearAllSession
```

返回结构

- 成功样例

```json
"无"
```

- 失败样例

```json
"无"
```

## 19. 获取csrf令牌

请求接口

```http request
GET http://<IP>:<PORT>/gateway/csrfTokenGenerator
```

返回结构

- 成功样例

```json
{
  "msg": "成功",
  "code": 200
}
```

- 失败样例

```json
"无"
```

## 20. 权限分组

请求接口

```http request
POST http://<IP>:<PORT>/gateway/groupPermission
```

| 序号 | 参数       | 描述   | 类型 | 说明  |
| ---- |----------| ---- | ---- |-----|
| 1    | permissionIds | 权限id | 数组 | 必填  |
| 2 | groupName | 分组名称 | 字符串 | 选填 |
| 3 | groupId | 组id | 数字 | 选填 |

返回结构

- 成功样例

```json
{
  "msg": "成功",
  "code": 200
}
```

- 失败样例

```json
{
    "msg": "后台出现异常错误",
    "code": 500
}
```

## 21. 分页查询权限及分组

请求接口

```http request
GET http://<IP>:<PORT>/gateway/queryPermissionGroupPage?keywords=<param1>&current=<param2>&size=<param3>
```

| 序号 | 参数     | 描述     | 类型   | 说明 |
| ---- | -------- | -------- | ------ | ---- |
| 1    | keywords | 关键字   | 字符串 | 选填 |
| 2    | current  | 当前页码 | 数字   | 必填 |
| 3    | size     | 页容量   | 数字   | 必填 |

返回结构

- 成功样例

```json
{
    "data": {
        "records": [
            {
                "id": "1834832757904396289",
                "groupName": "测试分组1",
                "createTime": null,
                "permissionInfos": [
                    {
                        "id": "1495637399244447746",
                        "groupId": "1834832757904396289",
                        "groupName": null,
                        "urlName": "分页查询权限",
                        "url": "/queryPermission",
                        "open": 0,
                        "description": "分页查询权限",
                        "fixed": 1,
                        "createTime": "2022-02-21 13:51:59",
                        "roleInfos": [
                            {
                                "id": "1",
                                "roleName": "管理员",
                                "description": null,
                                "permissionInfos": null
                            }
                        ]
                    },
                    {
                        "id": "1495637567519924225",
                        "groupId": "1834832757904396289",
                        "groupName": null,
                        "urlName": "新增权限",
                        "url": "/addPermission",
                        "open": 0,
                        "description": "新增权限",
                        "fixed": 1,
                        "createTime": "2022-02-21 13:52:39",
                        "roleInfos": [
                            {
                                "id": "1",
                                "roleName": "管理员",
                                "description": null,
                                "permissionInfos": null
                            },
                            {
                                "id": "2",
                                "roleName": "普通用户",
                                "description": null,
                                "permissionInfos": null
                            }
                        ]
                    }
                ]
            },
            {
                "id": "0",
                "groupName": "未分组",
                "createTime": null,
                "permissionInfos": [
                    {
                        "id": "1495637948090097665",
                        "groupId": "0",
                        "groupName": null,
                        "urlName": "根据角色绑定权限",
                        "url": "/bindPermissionByRole",
                        "open": 0,
                        "description": "根据角色绑定权限",
                        "fixed": 1,
                        "createTime": "2022-02-21 13:54:09",
                        "roleInfos": [
                            {
                                "id": "1",
                                "roleName": "管理员",
                                "description": null,
                                "permissionInfos": null
                            },
                            {
                                "id": "2",
                                "roleName": "普通用户",
                                "description": null,
                                "permissionInfos": null
                            }
                        ]
                    }
                ]
            }
        ],
        "total": 16,
        "size": 10,
        "current": 1,
        "orders": [],
        "optimizeCountSql": true,
        "searchCount": true,
        "maxLimit": null,
        "countId": null,
        "pages": 2
    },
    "msg": "成功",
    "code": 200
}
```

- 失败样例

```json
{
    "msg": "查询失败",
    "code": 704
}
```

## 22. 移出分组

请求接口

```http request
GET http://<IP>:<PORT>/gateway/moveOutGroup?id=<param1>&groupId=<param2>
```

| 序号 | 参数    | 描述   | 类型 | 说明 |
| ---- | ------- | ------ | ---- | ---- |
| 1    | id      | 权限id | 数字 | 必填 |
| 2    | groupId | 组id   | 数字 | 必填 |

返回结构

- 成功样例

```json
{
  "msg": "成功",
  "code": 200
}
```

- 失败样例

```json
{
    "msg": "后台出现异常错误",
    "code": 500
}
```