# Task 01 — رفع فوری نقص‌های امنیتی و مالی

**Status:** NOT STARTED  
**Planned at:** client `2a53bcf` / server `66c83ed`  
**Risk:** Critical  
**Dependencies:** Task 00

## Outcome

exploitهای قطعی و جریان‌های مالی اشتباه بسته شوند، بدون آنکه refactor کامل commerce در این task انجام شود. این یک stop-ship patch با تست regression است.

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

