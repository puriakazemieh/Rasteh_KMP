# Task 03 — migration نسخه‌دار، تست پایه و observability

**Status:** NOT STARTED  
**Planned at:** client `2a53bcf` / server `66c83ed`  
**Risk:** High  
**Dependencies:** Task 00

## Outcome

schema تولیدی قابل‌بازتولید، مسیرهای بحرانی دارای test harness و سرویس قابل‌مشاهده شود؛ پیش‌نیاز ایمن refactorهای دامنه.

## Current evidence

- server از `ddl-auto=update` استفاده می‌کند.
- ۱۱ init script فقط روی volume جدید compose اجرا می‌شوند و با ۶۱ entity کامل همخوان نیستند.
- migration config سفارشی خطا را swallow می‌کند.
- تنها ۴ تست واحد وجود دارد؛ integration/Testcontainers/security test نیست.
- actuator/micrometer/tracing/health و SLO/alert کامل نیست.

## Scope

Server first؛ در client نیز test source set/fixtures و CI gate اولیه ساخته می‌شود، اما feature behavior بزرگ در taskهای بعدی تست می‌شود.

## Steps

1. schema واقعی baseline را از entity + init scripts + staging snapshot غیرحساس reconcile کن.
2. Flyway اضافه و baseline migration immutable بساز؛ checksum و naming policy.
3. `ddl-auto=validate` برای stage/prod و `create-drop` فقط test isolation در صورت نیاز.
4. preflight query برای دادهٔ ناسازگار قبل از CHECK/FK/UNIQUE.
5. migration test با PostgreSQL Testcontainers: empty → latest و baseline snapshot → latest.
6. repository integration harness، fake clock/id generator و provider contract fixtures.
7. security test harness با principals نقش‌های مختلف.
8. client `commonTest` و MockEngine setup؛ حداقل Money/session/result/mapper tests.
9. CI برای PR/main: test، bootJar، KMP check/compile، lint و گزارش coverage.
10. coverage threshold مخصوص packageهای auth/order/payment/wallet؛ میانگین global تنها معیار نباشد.
11. actuator health/readiness، Micrometer metrics، request id و structured log redacted.
12. tracing checkout/payment و dashboard/alert اولیه.
13. backup automation و restore drill روی staging؛ RPO/RTO واقعی ثبت شود.

## Files likely affected

Server:

- `build.gradle.kts`
- `src/main/resources/application*.properties|yml`
- `src/main/resources/db/migration/V*.sql`
- حذف/بازنشسته‌سازی امن `db/init/*` و `DatabaseMigrationConfig.kt`
- `src/test/kotlin/**`
- `.github/workflows/build.yml`
- Docker compose/test config و ops docs.

Client:

- build files source sets؛
- `core/*/src/commonTest/**`
- `.github/workflows/ci.yml`, `build-all.yml`.

## Verification

```powershell
# Server
.\gradlew.bat --no-daemon --stacktrace clean test bootJar

# Client
.\gradlew.bat --no-daemon clean check
.\gradlew.bat --no-daemon :composeApp:compileKotlinJvm :composeApp:compileKotlinJs
```

Migration rehearsal:

1. empty Postgres → latest؛
2. sanitized baseline snapshot → latest؛
3. app boot با `validate`؛
4. restore backup و verify row counts/checksums.

## Done when

- [ ] Flyway تنها owner schema در stage/prod است.
- [ ] migration از empty و baseline پاس است.
- [ ] هیچ migration exception swallow نمی‌شود.
- [ ] Testcontainers/security/client test harness در CI required است.
- [ ] health/readiness/metrics/correlation id فعال و redacted است.
- [ ] alertهای auth/order/payment/inventory test شده‌اند.
- [ ] backup و restore drill با RPO/RTO ثبت شده است.

## STOP conditions

- اگر schema production قابل‌دسترسی/تعریف نیست، baseline را حدس نزن؛ task BLOCKED.
- اگر cleanup داده برای constraint نیازمند حذف رکورد است، approval و backup لازم است.
- migration تست‌نشده را روی production اجرا نکن.

## Executor prompt

```text
Task 03 را اجرا کن. Flyway baseline را از schema واقعی بساز، ddl-auto را در stage/prod validate کن، migration tests با PostgreSQL واقعی/Testcontainers اضافه کن و CI را required کن. test harness client/server، health/metrics/tracing و restore drill را کامل کن. هیچ دادهٔ production را بدون backup/approval پاک نکن. اگر baseline واقعی نامشخص است BLOCKED شو، حدس نزن.
```

