# Task 01 — رفع فوری نقص‌های امنیتی و مالی

**Status:** IN PROGRESS
**Planned at:** client `2a53bcf` / server `66c83ed`  
**Risk:** Critical  
**Dependencies:** Task 00

## Outcome

exploitهای قطعی و جریان‌های مالی اشتباه بسته شوند، بدون آنکه refactor کامل commerce در این task انجام شود. این یک stop-ship patch با تست regression است.

## Execution evidence

- **Started at:** 2026-08-05 (Asia/Tehran)
- **Baseline:** client `63d3b91dd6b43c6771303eedf40afea1c56c4323`; server `a4009fc4c0cfb140d75cb04e8905f2fe6681c085`.
- **Progress:** wallet negative/zero guard with a passing regression test; trust-all TLS removed; production seed default disabled; public payment callback; gateway remainder amount; and `CancellationException` rethrow have been implemented. IDOR/admin, payment idempotency, inventory/status and checkout remain in progress.
- **2026-08-05 verification:** server `test` and `bootJar` passed; client JVM/JS compilation passed. Client `check` was run independently for more than four minutes but remained in Kotlin/JS NPM configuration (`jsNpmAggregated` / `jsTestNpmAggregated`) and never reached test execution or a terminal result. It was stopped by its explicit PID only and is not recorded as a pass. Task 01 therefore remains `IN PROGRESS`; no commit or DONE transition is permitted yet.
- **2026-08-05 smoke:** server started against the local Docker database with an in-memory, randomly generated `JWT_SECRET_KEY`, with seed/payment/wallet disabled. A forged anonymous callback returned `302` to the failed result only. The payment recovery scheduler was regression-tested and now performs no PSP call when payments are disabled. No runtime secret was persisted.
- **2026-08-05 follow-up:** pessimistic wallet row locking was added for balance mutations. Server `test` passed after this change. Client JVM and JS compilation passed after the deep-link hardening. Full client `check` completed successfully (`1683 actionable tasks`, `47 executed`, `1636 up-to-date`).
- **2026-08-05 blocked evidence:** a regression test now serializes replayed payment callbacks through `PaymentRepository.findByAuthorityForUpdate`; its focused server test passed. Repository review found no staging PSP verify/callback contract or externally supplied staging configuration, while `PAYMENT_ENABLED` remains false by default. Per the STOP condition, production payment enablement cannot be assumed or tested locally. No secret or merchant identifier was inspected or recorded.
- **2026-08-05 ZarinPal sandbox follow-up:** ZarinPal was selected for sandbox contract work. Contract tests now assert the v4 sandbox request/verify endpoints, exact `IRR` integer amount forwarding (no تومان multiplication), and an explicit HTTPS callback URL. A live sandbox request with a runtime-generated, unprinted identifier was accepted; it neither used a production credential nor completed a payment. Server `test` and `bootJar` passed afterward. End-to-end callback verification remains pending a public HTTPS callback routed to the staging server.
- **2026-08-05 temporary callback reachability:** a short-lived HTTPS tunnel routed to the local staging server and an anonymous forged callback reached the public endpoint, which redirected only to the failed result. The tunnel and local server were stopped immediately afterward. Startup exposed an existing `wallets.version` NULL schema/data condition; no DDL, cleanup, or destructive migration was executed because production schema repair belongs to the safe migration work in Task 03.
- **2026-08-05 approved sandbox checkout:** a ZarinPal sandbox checkout for 1,000 IRR was completed through a second ephemeral HTTPS tunnel. The provider redirected to the configured callback, but it returned HTTP 502 because the local staging server could not be relaunched with the ephemeral callback configuration in this guarded environment. The exact tunnel process was stopped immediately. This is not evidence of server-side verification; the task is `BLOCKED` until an externally provisioned staging deployment can accept the callback and run the provider verification/replay assertions.
- **2026-08-05 resumed scope:** the product owner authorized completion of all independently testable Task 01 work. Only the live staging callback/verify rehearsal remains deferred; `PAYMENT_ENABLED` stays disabled until that evidence exists.

## Blocker — external PSP staging contract

**Resumed at:** 2026-08-05. The product owner selected ZarinPal sandbox for contract-level staging work. Production enablement and a live callback rehearsal remain blocked until an externally provisioned HTTPS callback endpoint is available.

**Owner:** release owner plus the licensed payment-provider contact.
**Decision required:** provide a staging merchant/callback verification rehearsal with non-production credentials held outside the repository, or formally keep the payment feature disabled for the next release.

Options and impact:

1. **Provision and rehearse an approved PSP staging contract.** Enables an end-to-end provider verification test, callback allow-list validation, and evidence for production enablement; requires the release owner to manage credentials and callback domain outside source control.
2. **Ship with `PAYMENT_ENABLED=false` and wallet disabled.** Keeps the stop-ship financial path unavailable and preserves the implemented callback replay guard, but checkout/payment remains unavailable and this task cannot be marked DONE.
3. **Adopt another licensed PSP under a new approved contract.** Requires an ADR and new contract/compatibility tests; it expands scope and delays this task.

**Rollback/compatibility:** the added callback row lock is backward-compatible and does not require a destructive migration. Payment remains disabled until option 1 is evidenced. Tasks that depend on Task 01 must not start without an explicit waiver; independent tasks retain their declared dependency rules.

## Cycle evidence — 2026-08-05

- **Baseline:** client `63d3b91dd6b43c6771303eedf40afea1c56c4323`; server `a4009fc4c0cfb140d75cb04e8905f2fe6681c085`.
- **Changed in this cycle:**
  - server `payment/persistence/PaymentRepository.kt`: pessimistic row lock for callback consumption;
  - server `src/test/.../payment/PaymentReplayProtectionTest.kt`: replay regression proving one provider verification and one order/cart effect;
  - client `core/common/build.gradle.kts` and `core/common/src/commonTest/.../PaymentEventBusTest.kt`: portable coroutine-test support for the existing deep-link regression test;
  - this task file and `plans/README.md`: synchronized blocked status and evidence.
- **Verification:**
  - server `./gradlew.bat --no-daemon --stacktrace test --tests com.kazemieh.rasteh.payment.PaymentReplayProtectionTest` → PASS;
  - server `./gradlew.bat --no-daemon --stacktrace test` → PASS;
  - server `./gradlew.bat --no-daemon --stacktrace bootJar` → PASS;
  - client `./gradlew.bat --no-daemon :core:common:compileTestKotlinJs --stacktrace` → PASS;
  - client `./gradlew.bat --no-daemon check` → PASS (`1692 actionable tasks`);
  - client `./gradlew.bat --no-daemon :composeApp:compileKotlinJvm :composeApp:compileKotlinJs` → PASS.
- **Failure and safe correction:** the first client `check` exposed an unresolved `runBlocking` in the JS common test source. The test now uses multiplatform `runTest` with `kotlinx-coroutines-test`; no assertion was weakened or removed.
- **Migrations/contracts:** none added in this cycle. The callback lock is code-level and backward-compatible; payment remains feature-flagged off. No OpenAPI/schema compatibility window changes are claimed.
- **Security/privacy review:** replay handling now has a server-side database lock inside the transactional verification boundary. No credential, merchant identifier, token, or personal data was inspected, emitted, or written to the evidence.
- **Commit/SHA:** no commit created; the pre-existing dirty worktrees were preserved.
- **Remaining risk:** provider callback semantics, amount/replay behavior, and production enablement remain unproven until an approved staging PSP rehearsal is supplied. This is the blocker above.
- **ZarinPal sandbox continuation:**
  - **Changed:** server `ZarinPalService.kt`, `PaymentService.kt`, `application.properties`, and `ZarinPalSandboxContractTest.kt`; no client contract, schema, or migration changed.
  - **Verification:** focused ZarinPal sandbox contract and replay tests → PASS; server `test` → PASS; server `bootJar` → PASS; live sandbox request probe → accepted with a generated in-memory identifier and redacted output.
  - **Compatibility:** the new `ZARINPAL_CALLBACK_URL` is required only when `PAYMENT_ENABLED=true`; the default remains disabled. Existing numeric values that are not exact IRR integers are rejected before any provider call rather than rounded or multiplied.
  - **Remaining risk:** a real end-to-end sandbox callback still needs a public HTTPS URL that routes to the staging server and a completed sandbox checkout. Production remains disabled.
  - **2026-08-05 approved sandbox checkout:** an ephemeral HTTPS tunnel was created and a ZarinPal sandbox checkout for 1,000 IRR was completed. The provider redirected to the configured callback, but the tunnel returned HTTP 502 because the local staging server could not be relaunched with the ephemeral callback configuration in this guarded environment. The exact tunnel process was stopped immediately. No production credential, merchant identifier, card data, OTP, or personal data was stored or emitted.
  - **Result/action:** this is evidence of sandbox request and checkout reachability only, not a successful server-side verification. Keep `PAYMENT_ENABLED` disabled; rerun the approved checkout against a persistent staging deployment with a configured HTTPS callback, then assert the provider verify response, atomic order update, and replay behavior.

## Cycle evidence — 2026-08-05 (independently testable completion)

- **Resumed scope:** the product owner authorized completion of all work that does not require a live staging callback. Task status is `IN PROGRESS`; production payment remains disabled.
- **Client/server payment contract:** `idempotencyKey` now travels from KMP checkout through the payment data/domain layers to the server request DTO. The server persists the user-scoped key in a backward-compatible nullable expansion migration (`V2__payment_idempotency_key.sql`) with a partial unique index. Existing clients without the field receive a validation failure rather than an unsafe duplicate payment; the new client and server must be deployed together before enabling payments. Rollback is feature-flag rollback (`PAYMENT_ENABLED=false`); the new nullable columns/index are retained safely.
- **Payment safety:** checkout has an in-flight submission gate; the server locks the owned order before looking up/creating its payment attempt, so duplicate checkout requests serialize before a second PSP call. The gateway receives only `gatewayPaidAmount` (the wallet remainder), and cancellation/failure does not clear the cart.
- **Regression evidence:**
  - `PaymentStartIdempotencyTest`, `PaymentGatewayRemainderTest`, `PaymentReplayProtectionTest`, and `PaymentCancellationTest` → PASS;
  - `WalletWithdrawalConcurrencyTest` → PASS against two real concurrent H2 transactions; exactly one 80-IRR withdrawal succeeded from a 100-IRR wallet;
  - `MarketplaceOversellConcurrencyTest` → PASS against two real concurrent H2 transactions; exactly one order consumed the final unit;
  - `AuthorizationHttpTest` → PASS: USER received 403 on `/api/admin/**` and 404 for another user's order;
  - client `CheckoutSubmissionGateTest`, `PaymentReturnPolicyTest`, and `SafeApiCallCancellationTest` → PASS;
  - server `test` and `bootJar` → PASS; client `:composeApp:compileKotlinJvm :composeApp:compileKotlinJs` → PASS.
- **Verification failure:** the post-change client `./gradlew.bat --no-daemon check` continued beyond the 6-minute command limit. Its exact owned Gradle processes were stopped; it is not a PASS, and V02 remains unchecked. No test, assertion, or check task was removed.
- **Staging callback:** the approved 1,000-IRR ZarinPal sandbox checkout still redirected to a temporary tunnel that returned HTTP 502 because the local staging server could not be relaunched with the ephemeral callback configuration. The tunnel was stopped. A persistent HTTPS staging callback and provider-side verify/replay rehearsal remain the only PSP evidence gap.
- **Security/privacy:** temporary test JWT values were generated only in memory; no credential, merchant ID, card data, OTP, token, or personal data was written to source or evidence.
- **Commit/SHA:** no commit created; pre-existing dirty worktrees were preserved.

## In scope

### Server paths

- `src/main/kotlin/com/kazemieh/rasteh/RastehApplication.kt`
- `src/main/resources/application.properties`
- seedها: `DataSeeder.kt`, `MarketplaceShopSeeder.kt`
- `order/api/OrderController.kt`, `order/application/OrderService.kt`
- admin question/review controllers و `SecurityConfig.kt`
- `wallet/api/dto/WalletDtos.kt`, `WalletController.kt`, `WalletService.kt`, `WalletEntity.kt`
- `payment/api/PaymentController.kt`, `payment/application/PaymentService.kt`, `PaymentEntity.kt`
- `marketplace/order/OrderDtos.kt`, `MarketplaceOrderService.kt`
- repository/entityهای inventory مرتبط.

### Client paths

- `core/navigation/.../AppNavigation.kt`
- `feature/cart/.../checkout/CheckoutScreen.kt`
- `feature/cart/.../checkout/CheckoutViewModel.kt`
- `feature/cart/.../payment_completed/PaymentViewModel.kt`
- `feature/cart/.../payment_completed/PaymentCompleted.kt`
- `core/network/.../ResultHandler.kt`

## Out of scope

- migration کامل دو commerce stack؛
- UI redesign؛
- settlement چندفروشندگی؛
- featureهای جدید.

## Implementation steps

1. trust-all TLS/hostname verifier را حذف و تست اتصال با trust manager پیش‌فرض اضافه کن.
2. seed production را خاموش کن؛ profile guard و fail-fast config برای secretهای ضروری ایجاد کن. هیچ مقدار واقعی را log نکن.
3. order tracking/shipping را با principal و owner/shop/admin scope محدود کن. `/api/admin/**` را deny-by-default کن و endpointهای admin بدون annotation را پوشش بده.
4. wallet amount را در DTO با positive constraint و `@Valid` محدود کن؛ DB CHECK اضافه کن. برای concurrent mutation قفل/version یا conditional update و business reference یکتا اضافه کن.
5. callback دقیق payment را public کن، اما state-changing verify فقط با authority ذخیره‌شده و verify provider انجام شود.
6. PaymentAttempt را با unique authority/idempotency key و state transition اتمیک محافظت کن.
7. مبلغ gateway را از `gatewayPaidAmount` بگیر. assertion بگذار که wallet + gateway = order payable snapshot.
8. nested item validation، `quantity > 0`، stock lock/version و status transition guard برای marketplace order اضافه کن.
9. client checkout را هنگام in-flight هم در UI و هم ViewModel guard کن و attempt id را به server contract بفرست.
10. payment completion با orderId state را از server بخواند؛ deep-link `success` فقط hint است. failure/cancel cart را پاک نکند.
11. `CancellationException` در ResultHandler پیش از catch عمومی rethrow شود.

## Required tests

### اجرای باقی‌مانده (فقط پس از خروجی موفق تیک می‌خورد)

- [x] W01 — آزمون هم‌زمانی دو برداشت: موجودی هرگز منفی نشود.
- [x] P01 — آزمون callback تکراری: فقط یک اثر روی سفارش/ledger.
- [x] P02 — آزمون پرداخت جزئی کیف‌پول: فقط remainder به gateway برود.
- [x] A01 — آزمون HTTP برای منع USER از `/api/admin/**` و مالکیت سفارش دیگران.
- [x] C01 — آزمون double-tap checkout: فقط یک create/payment call.
- [x] C02 — آزمون failure/cancel: cart پاک نشود.
- [x] C03 — آزمون deep-link: بدون verify سرور موفقیت نمایش داده نشود.
- [x] C04 — آزمون cancellation: خطای stale در UI ایجاد نشود.
- [x] V01 — `test` و `bootJar` سرور.
- [ ] V02 — `check`، `compileKotlinJvm` و `compileKotlinJs` کلاینت.

Server:

- مبلغ wallet صفر/منفی رد شود؛
- دو برداشت concurrent نتوانند balance منفی/ساختگی بسازند؛
- user A tracking/shipping order B را 403/404 بگیرد؛
- USER به admin endpoints دسترسی نداشته باشد؛
- callback anonymous قابل دریافت ولی authority ساختگی بی‌اثر باشد؛
- replay callback فقط یک ledger/order effect داشته باشد؛
- wallet partial payment فقط remainder را charge کند؛
- quantity منفی/صفر رد و دو order concurrent oversell نکنند؛
- status transition نامعتبر رد شود.

Client:

- double tap یک create call؛
- failed/cancelled callback cart را نگه دارد؛
- success تا verify server نمایش داده نشود؛
- cancellation stale error نسازد.

## Verification

```powershell
# Server
.\gradlew.bat --no-daemon --stacktrace test
.\gradlew.bat --no-daemon --stacktrace bootJar

# Client
.\gradlew.bat --no-daemon check
.\gradlew.bat --no-daemon :composeApp:compileKotlinJvm :composeApp:compileKotlinJs
```

## Done when

- [ ] تمام regression testهای بالا پاس‌اند.
- [ ] هیچ trust-all TLS یا production seed default باقی نمانده است.
- [ ] ownership/admin policy server-side enforce می‌شود.
- [ ] negative/concurrent wallet exploit بسته است.
- [ ] gateway amount و callback replay صحیح‌اند.
- [ ] negative quantity/oversell/status abuse بسته است.
- [ ] client هیچ موفقیت مالی را از deep link یا cart clear نتیجه نمی‌گیرد.
- [ ] evidence مالی با test name و output ثبت شده است.

## STOP conditions

- اگر provider verify/callback contract در staging قابل آزمون نیست، mock contract بساز اما production enable را BLOCKED نگه دار.
- اگر schema production فعلی unknown است، constraint destructive اجرا نکن؛ cleanup/preflight migration بنویس.
- اگر fix کوتاه‌مدت ledger invariant را نمی‌تواند تضمین کند، wallet/feature مالی را feature-flag off کن.

## Executor prompt

```text
Task 01 را به‌عنوان stop-ship patch اجرا کن. فقط نقص‌های مستند TLS، seed/secrets، IDOR/admin، wallet، payment callback/amount/idempotency، marketplace quantity/stock/status و client payment/double-submit/cancellation را تغییر بده. ابتدا regression test بنویس. هیچ secret را چاپ نکن. هر تغییر schema migration نسخه‌دار و preflight داشته باشد. اگر invariant مالی قابل اثبات نیست قابلیت را خاموش کن و task را DONE اعلام نکن. تمام commandهای Verification را اجرا و evidence ثبت کن.
```
