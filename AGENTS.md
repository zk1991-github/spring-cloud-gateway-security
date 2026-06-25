# AGENTS.md — spring-cloud-gateway-security

## Build & Run

- **Java 17**, Spring Boot 3.4.4, Spring Cloud 2024.0.1
- `mvn package -P cluster` (MySQL + Redis + load balancer) or `mvn package -P stand-alone` (SQLite only, no Redis)
- No Maven wrapper; `mvn` must be on PATH
- No lint/typecheck scripts; only `mvn test`
- Single test class: `SpringCloudGatewaySecurityApplicationTests` — `@SpringBootTest` context load only
- Default port: **8888**
- Default superadmin: `superadmin` / `123456` (bcrypt hash in `application.yml`)

## Important Build Quirk

`maven-resources-plugin` **excludes all `.yml` and `.properties` files** from `src/main/resources` during the build. Config files must be placed **outside the JAR** alongside it at deployment. The JAR contains only mappers, static resources, and META-INF.

## Project Structure (single module)

```
src/main/java/com/github/zk/spring/cloud/gateway/security/
  config/          — SecurityConfig (WebFlux), FilterConfig, RedisConfig, etc.
  filter/          — Custom GatewayFilterFactory implementations (Session, Monitor, RequestBodyOperation, TokenCheck)
  authentication/  — Reactive auth managers, CustomReactiveAuthorizationManager
  controller/      — Login, User, Permission, WeChat, Dict, RequestMonitor, Generator endpoints
  service/         — Interfaces (+ impl/) for user, role, permission, group permission, whitelist, dict, WeChat
  dao/             — MyBatis Plus mappers (interface only; XML in src/main/resources/mapper/)
  core/            — LoginProcessor, GatewaySecurityCache interface + Redis/Map implementations
  monitor/         — RequestMonitor interface + Redis/Map implementations
  autoconfigure/   — AutoConfiguration for cache selection via spring.factories
  pojo/            — DTOs, entities
  property/        — @ConfigurationProperties classes
  jackson2/        — Custom serializers for WeChat auth tokens (needed for Redis session serialization)
  log/             — Login logging abstraction
  handler/         — UnifiedExceptionHandler, CustomMetaObjectHandler (MyBatis Plus auto-fill)
  listener/        — MySpringApplicationRunListener
  util/            — PasswordGeneratorUtils, IpUtils, MacUtils, MysqlToSqliteUtils
```

## Key Architecture Facts

- **Reactive (WebFlux)** — `@EnableWebFluxSecurity`, `SecurityWebFilterChain`, reactive auth managers. Not Spring MVC.
- **Two cache modes** selected at startup by `AutoConfigurationCache`:
  - Cluster: `GatewaySecurityCacheRedis` + `RequestMonitorRedis` (Redis-based)
  - Standalone: `GatewaySecurityCacheMap` + `RequestMonitorMap` (in-memory)
- **Custom GatewayFilterFactory** naming convention: e.g. `SessionGatewayFilterFactory` → filter name `Session` in YAML routes
- **MapperScan** base package: `com.github.zk.spring.cloud.gateway.security.dao`
- **Default user impl** can be replaced via `@ConditionalOnMissingBean` on `DefaultUserImpl`
- CSRF uses `CookieServerCsrfTokenRepository` with `httpOnly=false` when enabled
- API routes are `/gateway/**` by default, gateway controller endpoints are **not** under `/gateway` (they're on root paths like `/login`, `/user/**`, etc.)

## Configuration Profile Activation

`application.yml` activates: `datasource-cluster, gateway, security, wechat, release, loadbalance`
Key profile config files:
- `application-datasource-cluster.yml` — MySQL + Redis connection
- `application-datasource-standalone.yml` — SQLite connection
- `application-gateway.yml` — routes, session, rate limiting
- `application-security.yml` — CSRF, IP whitelist, static resource skipping, proxy URL
- `application-loadbalance.yml` — load balancer instances + health checks
- `application-wechat.yml` — WeChat mini-program config

## Common Pitfalls

- `application-release.yml` has a **hardcoded Windows path** for `web.release` — change for deployment
- Route matchers: narrower paths **must** come before broader paths (e.g. `/gateway/test/**` before `/gateway/**`)
- `StripPrefix` filter key must be `parts` (not `paths` as sometimes documented in older gateway versions)
- Source IP is forwarded in header `XReal-IP`, not from `getRemoteAddr()`
- User ID and username are added to forwarded request headers as `userId` and `username`
- Password generator utility: `PasswordGeneratorUtils` has a `main()` method (run locally to generate bcrypt hashes)
- The `application-wechat.yml` contains real-looking `appid`/`appsecret` values — these are **test/placeholder** credentials; replace in deployment
