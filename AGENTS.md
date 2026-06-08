# AGENTS.md

## Build & Run

- **Java 17** required (maven.compiler.source/target = 17)
- **Build cluster** (MySQL + Redis): `mvn package -P cluster`
- **Build standalone** (SQLite): `mvn package -P stand-alone`
- **Default port**: 8888
- **Tests**: Only a `contextLoads` smoke test — no unit/integration tests for logic
- **Default credentials**: superadmin / 123456 (defined in `application.yml` and hardcoded in code)

## Profiles

Two Maven profiles that change the dependency set:

| Profile | Database | Session | Loadbalancer |
|---------|----------|---------|--------------|
| `cluster` | MySQL | Redis (spring-session-data-redis) | spring-cloud-starter-loadbalancer |
| `stand-alone` | SQLite | In-memory | excluded |

The active profiles in `application.yml` are: `datasource-cluster, gateway, security, wechat, release, loadbalance`.

## Architecture

- Spring Cloud Gateway + Spring Security 6 (WebFlux/reactive)
- MyBatis Plus for ORM
- Static Vue frontend at `src/main/resources/static/web/dist/`
- `PasswordGeneratorUtils.main()` generates bcrypt passwords (not a CLI tool)
- Downstream services receive real IP via `XReal-IP` header, userId via `userId` header, username via `username` header

## Key Config Files

- `src/main/resources/application.yml` — superadmin, active profiles
- `src/main/resources/application-gateway.yml` — routes, StripPrefix (key must be `parts`), rate limiting
- `src/main/resources/application-security.yml` — csrf, proxy-url, antpatterns for static resources
- `src/main/resources/application-datasource-*.yml` — DB/DataSource config
- `package/cluster/` and `package/stand-alone/` — pre-built deployment configs with different DB drivers

## Gotchas

- Filter names in gateway YAML use `parts` as the key for `StripPrefix`, not `paths`
- CSRF is enabled by default; disable via `spring.security.csrf-enable: false`
- Route matchers with smaller paths must come before larger ones (e.g. `/gateway/test/**` before `/gateway/**`)
- The `stand-alone` profile's Spring Boot plugin explicitly excludes Redis/session jars from the fat jar via `excludes`
- `maven-resources-plugin` excludes `*.properties` and `*.yml` from resources — config is loaded from external `config/` dir in deployment, not from classpath
- Password is `{bcrypt}$2a$10$0EQexC0XYw58x.ys.Ym8QO3H2Llr0G4wEAFddm8PkOUGy6hQraaui` for "123456" (verified in application.yml)