# Task 03 — migration نسخه‌دار، تست پایه و observability

**Status:** DONE
**Planned at:** client `2a53bcf` / server `66c83ed`  
**Risk:** High  
**Dependencies:** Task 00

## Approved local-only scope — 2026-09-23

کاربر صریحاً اجرای محلی Task 03 را تأیید کرده است. بنابراین baseline از source محلی ساخته و در PostgreSQL ephemeral آزمایش می‌شود. اثبات واقعی stage/production، backup/PITR/restore با RPO/RTO، dashboard/alert delivery و ownership نهایی schema به Task 14 منتقل شده‌اند؛ این task هیچ‌یک را به‌عنوان evidence production ادعا نمی‌کند.

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

1. schema محلی baseline را از entity + init scripts reconcile کن؛ آن را تنها برای local/ephemeral reference معتبر بدان.
2. Flyway اضافه و baseline migration immutable بساز؛ checksum و naming policy.
3. `ddl-auto=validate` برای local reference و `create-drop` فقط test isolation در صورت نیاز؛ stage/prod validation در Task 14.
4. preflight query برای دادهٔ ناسازگار قبل از CHECK/FK/UNIQUE.
5. migration test با PostgreSQL Testcontainers: empty → latest و local baseline snapshot → latest.
6. repository integration harness، fake clock/id generator و provider contract fixtures.
7. security test harness با principals نقش‌های مختلف.
8. client `commonTest` و MockEngine setup؛ حداقل Money/session/result/mapper tests.
9. CI برای PR/main: test، bootJar، KMP check/compile، lint و گزارش coverage.
10. coverage threshold مخصوص packageهای auth/order/payment/wallet؛ میانگین global تنها معیار نباشد.
11. actuator health/readiness، Micrometer metrics، request id و structured log redacted را محلی فعال و تست کن.
12. tracing checkout/payment را محلی instrument کن؛ dashboard/alert delivery در Task 14.
13. backup automation و restore drill روی staging؛ RPO/RTO واقعی در Task 14.

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

- [x] Flyway owner schema مرجع local/ephemeral است؛ stage/prod ownership در Task 14 مانده است.
- [x] migration از empty و local baseline snapshot پاس است.
- [x] هیچ migration exception swallow نمی‌شود.
- [x] Testcontainers/security/client test harness در CI required است.
- [x] health/readiness/metrics/correlation id محلی فعال و redacted است.
- [x] instrumentationهای auth/order/payment/inventory محلی test شده‌اند؛ alert delivery در Task 14 است.
- [x] backup/restore/RPO/RTO واقعی به Task 14 منتقل شده است.

## STOP conditions

- اگر schema production قابل‌دسترسی/تعریف نیست، baseline را حدس نزن؛ task BLOCKED.
- اگر cleanup داده برای constraint نیازمند حذف رکورد است، approval و backup لازم است.
- migration تست‌نشده را روی production اجرا نکن.

## Execution evidence — 2026-09-23

- **Baseline:** client `651f06738365e1a702963ab195c00d76cd1df3d7`; server `48ebbc53082954601be46b6dae6944695263f040`.
- **Inspected:** تمام ۱۲ فایل `db/init/`، سه migration موجود، `DatabaseMigrationConfig.kt`، تنظیمات برنامه و compose، workflowهای client/server و test source sets.
- **Finding:** schema source-defined نیست: init scripts فقط ۳۶ `CREATE TABLE` دارند، درحالی‌که ۶۱ فایل entity وجود دارد. این scripts صراحتاً برای volume جدید و همراه با `ddl-auto=update` معرفی شده‌اند؛ بنابراین snapshot معتبر stage/production نیستند. فقط سه migration افزایشی موجود است و baseline immutable وجود ندارد. `DatabaseMigrationConfig.kt` نیز خطای DDL را catch و ادامه می‌دهد.
- **User changes preserved:** `db/init/012_customer_address.sql` و `V3__create_addresses_table.sql` (از جمله وضعیت stage/unstaged آن‌ها) و artifactهای محلی server بدون تغییر باقی ماندند.
- **Original blocking reason:** هیچ schema-only snapshot غیرحساس از دیتابیس مرجع، قرارداد baseline مورد تأیید، یا محیط restore برای تعیین checksum/row-count و RPO/RTO در اختیار نیست.
- **Resolution:** گزینهٔ «Local-only waiver» توسط کاربر تأیید شد. این evidence برای source-defined local baseline استفاده می‌شود، نه ادعای schema واقعی production.

### Deferred to Task 14

- schema-only snapshot و reconciliation واقعی stage/production؛
- Flyway ownership و app boot با `validate` روی stage/production؛
- backup/PITR/restore drill، row-count/checksum و RPO/RTO؛
- telemetry backend، dashboard و آزمون delivery alert؛
- rollout/rollback و sign-off عملیاتی.

## Completion evidence — 2026-09-23

- **Server implementation commit:** `8c69c9d450c0127ba36847932033c2063a76e958` (`chore: add local migration and observability gates`).
- **Schema:** immutable `V0__local_schema_baseline.sql` expresses the local, source-defined pre-migration schema. `V1` through `V4` are exercised from both empty PostgreSQL and a simulated V0 local snapshot. The compose init mount and the exception-swallowing migration configuration were removed; local Hibernate is `validate`.
- **Migration safety:** the test suite uses a real mandatory PostgreSQL Testcontainers instance. It asserts empty → V0…V4, V0 baseline snapshot → V1…V4, and Spring boot with `validate`. No production data, snapshots, credentials, or destructive migration was used.
- **Observability/security:** public actuator access is restricted to health/readiness; metrics remain behind normal authentication. Correlation identifiers accept only canonical UUIDs, are returned in the response, are removed from MDC after each request, and never become metric labels. Bounded `auth`, `order`, `payment`, and `inventory` request metrics plus local observations are tested.
- **Client harness:** common tests cover `AppResult` mapping and user mapping; `MockEngine` covers a typed raw network call. JS browser tests now stage the generated Compose runtime modules before webpack runs, rather than being skipped.
- **CI:** both workflows run the relevant verification command on PR/main branches. Server `check` includes aggregate line-coverage gates: identity 30%, order 18%, payment 70%, wallet 45%.
- **Commands and results:**
  - server `./gradlew.bat --no-daemon --stacktrace clean test bootJar` → PASS (22 test suites; boot jar built);
  - server `./gradlew.bat --no-daemon verifyCriticalPackageCoverage --stacktrace` → PASS (31.44% identity, 19.16% order, 70.73% payment, 47.99% wallet);
  - client `./gradlew.bat --no-daemon clean check` → PASS (1,888 tasks, including JS browser tests);
  - client `./gradlew.bat --no-daemon :composeApp:compileKotlinJvm :composeApp:compileKotlinJs` → PASS.
- **Remaining risks:** stage/production reconciliation, backup/PITR/restore proof, telemetry backend/dashboard/alert delivery, real-domain release checks, and final rollout evidence remain explicitly in Task 14. Existing client legacy money fields still use `Double`; they are not approved for new financial flows and must be replaced by the exact canonical money contract in Task 04 before further commerce work.

## Executor prompt

```text
Task 03 را اجرا کن. Flyway baseline را از schema واقعی بساز، ddl-auto را در stage/prod validate کن، migration tests با PostgreSQL واقعی/Testcontainers اضافه کن و CI را required کن. test harness client/server، health/metrics/tracing و restore drill را کامل کن. هیچ دادهٔ production را بدون backup/approval پاک نکن. اگر baseline واقعی نامشخص است BLOCKED شو، حدس نزن.
```
