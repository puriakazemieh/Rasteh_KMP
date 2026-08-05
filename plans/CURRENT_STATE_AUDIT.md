# ممیزی وضعیت فعلی پروژهٔ راسته

## 1. نتیجهٔ مدیریتی

پروژه از مرحلهٔ نمونهٔ نمایشی عبور کرده و حجم قابل‌توجهی کد و API دارد، اما هنوز production-ready نیست. کلاینت و سرور هر دو modular monolith هستند و برای ادامه انتخاب مناسبی‌اند؛ مشکل اصلی نبود ماژول نیست، بلکه موازی‌شدن دو مدل تجارت، ضعف تست، نقص کنترل‌های مالی/امنیتی و گسترش سطحی فیچرهاست.

ارزیابی فعلی:

| محور | وضعیت | توضیح |
|---|---|---|
| کامپایل کلاینت | سبز | JVM و JS در baseline فعلی موفق |
| تست سرور | زرد | ۴ تست موفق، اما پوشش مسیرهای حیاتی تقریباً صفر |
| معماری ماژولی | زرد | لایه‌بندی خوب، coupling و دو دامنهٔ موازی زیاد |
| امنیت | قرمز | TLS ناامن، IDOR، token/log/config ناامن و seed ریسکی |
| پول و پرداخت | قرمز | مبلغ ترکیبی اشتباه، wallet exploit، callback/idempotency ناقص |
| داده و migration | قرمز | `ddl-auto=update` و init script غیرنسخه‌دار |
| تجربه چندپلتفرمی | زرد/قرمز | targetها تعریف شده‌اند، parity و release pipeline کامل نیست |
| طراحی/دسترسی‌پذیری | زرد | design system موجود، اما responsive/a11y/IA ناتمام |
| آمادگی عملیات | قرمز | SLO، metrics، tracing، backup/restore و runbook ناکافی |
| آمادگی کسب‌وکار | زرد | مسئله و عرضه روشن، اما MVP بیش‌ازحد گسترده و نقش حقوقی نامشخص |

تصمیم stop-ship: تا taskهای 01، 02 و 03 و بخش‌های بحرانی 12 تکمیل نشوند، نباید پرداخت واقعی یا انتشار عمومی انجام شود.

## 2. روش و دامنهٔ بررسی

- بررسی ایستا روی دو مخزن و فایل‌های build/workflow/config؛
- inventory ماژول، entity، controller، service و screen؛
- ممیزی معماری، authorization، concurrency، money، privacy، release و UX؛
- Hallmark audit روی طراحی کدشده و design system؛
- اجرای واقعی:
  - `Rasteh_KMP`: `:composeApp:compileKotlinJvm` و `:composeApp:compileKotlinJs` موفق؛
  - `Rasteh_Kotlin_Spring_Boot`: `test` موفق، ۴ تست، صفر failure/error/skipped.

محدودیت‌ها:

- اپ روی دستگاه، browser و simulator اجرا نشد؛ screenshot/runtime/performance audit انجام نشده است.
- signing، store submission، درگاه واقعی، SMS واقعی، production database و شبکهٔ واقعی آزموده نشدند.
- هیچ مقدار secret یا credential در این گزارش بازتولید نشده است.
- فایل `RTK.md` که در دستور workspace ارجاع شده بود، در هیچ‌یک از دو مخزن یافت نشد.

## 3. تصویر کلاینت KMP

### 3.1 ساختار

کلاینت حدود ۶۹۲ فایل Kotlin و نزدیک ۴۷ هزار خط دارد. ۲۲ ماژول Gradle از `settings.gradle.kts:47-69` ثبت شده‌اند:

- shell: `composeApp`؛
- core: `common`, `domain`, `data`, `network`, `designSystem`, `navigation`؛
- feature: auth, main, bazaar, cart, catalog, blog, settings, profile, orders, details, support؛
- admin: products, orders, options, wallet, blog.

targetهای Android، iOS ARM64 device/simulator، JVM Desktop و JS Browser تعریف شده‌اند. compile/target Android برابر 36 و minSdk برابر 24 است. Android release فعلاً minification را غیرفعال دارد و versioning ابتدایی است.

### 3.2 نقاط قوت

- تفکیک feature/core و استفاده از Koin، Ktor و Compose Multiplatform؛
- design tokens، dark theme، RTL و adaptive navigation rail؛
- domain/data/network separation برای بسیاری از مسیرها؛
- API و UI قابل‌توجه برای مصرف‌کننده، فروشنده و ادمین؛
- compile موفق JVM/JS روی baseline.

### 3.3 بدهی ساختاری

چند فایل blast radius بالایی دارند:

- `feature/details/.../DetailsScreen.kt` حدود ۱۳۰۴ خط؛
- `feature/profile/.../ProfileScreen.kt` حدود ۹۵۷ خط؛
- `feature/admin/products/.../ManageProductViewModel.kt` حدود ۸۲۵ خط؛
- `feature/admin/products/.../ManageProductScreen.kt` حدود ۸۰۳ خط؛
- `core/navigation/.../AppNavigation.kt` حدود ۷۲۷ خط.

`core:navigation` به featureها وابسته است و app نیز دوباره همهٔ featureها را می‌شناسد. این وارونگی باعث coupling، build سنگین، routeهای بدون guard و سختی lazy loading شده است. هدف task 04 این است که هر feature یک entry/route registrar ارائه کند و app shell آن‌ها را compose کند.

### 3.4 دو commerce stack

در کلاینت دو مدل مستقل وجود دارد:

- `core/domain/.../catalog/Product.kt` + cart/checkout/order/payment؛
- `core/domain/.../marketplace/Product.kt` + shop order/history/detail جدا.

`Screen.kt` و `AppNavigation.kt` نیز دو product detail، دو order history و جریان‌های متفاوت دارند. نتیجه این است که محصول فروشگاه بازارچه از variant/inventory/payment کامل بهره نمی‌برد و محصول قابل‌پرداخت کلاسیک، shop ownership ندارد.

### 3.5 ریسک‌های بحرانی کلاینت

#### پرداخت اشتباه به‌عنوان موفقیت

- `AppNavigation.kt:95-101` deep-link status را route می‌کند؛
- `AppNavigation.kt:682-698` آرگومان success/error را در composition نهایی مصرف نمی‌کند؛
- `PaymentViewModel.kt:24-44` با ورود به صفحه همیشه cart را پاک می‌کند؛
- `PaymentCompleted.kt:47-67` موفقیت پاک‌کردن cart را موفقیت خرید نمایش می‌دهد.

اثر: callback ناموفق یا دستکاری‌شده می‌تواند cart را خالی و پیام موفقیت نشان دهد، بدون verify وضعیت سفارش از سرور.

#### سفارش تکراری

`CheckoutScreen.kt:270-276` و `CheckoutViewModel.kt:236-301` guard کامل UI/domain ندارند؛ هر کلیک می‌تواند order/payment attempt جدید بسازد. راه‌حل فقط disable دکمه نیست؛ client attempt id و server idempotency key هر دو لازم‌اند.

#### token و log

- `TokenManager.kt:18-33` tokenها را در Settings عمومی ذخیره می‌کند؛
- Android از SharedPreferences، iOS از NSUserDefaults و Web از browser storage استفاده می‌کند؛
- `HttpClientFactory.kt:41-48` همیشه `LogLevel.ALL` دارد؛
- `ResultHandler.kt` response/error body و stack trace را log می‌کند.

اثر: token، پروفایل، سفارش و wallet می‌توانند وارد log یا storage قابل‌خواندن شوند.

#### محیط تولید

`PlatformConfig.*.kt` برای هر target URL متفاوت و hardcoded دارد: tunnel موقت Android، API دیگری برای JS و localhost/HTTP برای iOS/JVM. Android نیز cleartext را سراسری فعال کرده است. artifactهای فعلی reproducible production config ندارند.

#### cancellation و money

`ResultHandler.kt` با `catch(Exception)` می‌تواند `CancellationException` را ببلعد و state قدیمی تولید کند. در domainهای catalog، marketplace، order، wallet و service از `Double` برای پول استفاده شده و rounding/serialization مالی قابل‌اعتماد نیست.

### 3.6 تست و CI کلاینت

هیچ `commonTest`, `androidTest`, `iosTest`, `jsTest`, `jvmTest` یا `*Test.kt` پیدا نشد. CI خودکار فقط JVM/JS compile می‌کند؛ Android artifact debug است، iOS best-effort و Web از development distribution استفاده می‌کند چون production minification با OOM روبه‌رو شده است.

## 4. تصویر بک‌اند

### 4.1 ساختار

سرور یک modular monolith با Kotlin 2.2.21، Spring Boot 4.0.3، Java 17، MVC/JPA/Security و PostgreSQL است:

- حدود ۳۷۲ فایل Kotlin و ۱۳٬۵۰۰ خط کد؛
- ۶۹ controller؛
- ۶۱ service؛
- ۱۰۹ repository declaration؛
- ۶۱ entity؛
- حدود ۲۲۳ مرز تراکنش؛
- فقط یک فایل تست با ۴ تست واحد.

bounded areaهای فعلی شامل identity, marketplace, catalog, cart, order, payment, wallet, interaction, discount, customer, blog, story, features, services, engagement, advanced, admin و shared است.

### 4.2 نقاط قوت

- انتخاب modular monolith برای این مرحله مناسب است؛
- domainهای متعدد و API گسترده وجود دارد؛
- مدل کلاسیک variant/inventory از مدل سادهٔ بازارچه غنی‌تر است؛
- در مسیر order کلاسیک برخی lock/versionها وجود دارند؛
- PostgreSQL، validation، security و OpenAPI dependencies حاضرند.

### 4.3 دو مسیر تجارت متناقض

1. `products → variants → inventory → cart → orders → payment/wallet`
2. `shops → shop_products → marketplace_orders`

`catalog/.../ProductEntity.kt` shop ندارد؛ `marketplace/.../ShopProductEntity.kt` variant، inventory reservation، cart، address و payment ندارد. Admin stats نیز عمدتاً stack کلاسیک را می‌شمارد. یکپارچه‌سازی این دو مهم‌ترین تصمیم معماری است.

### 4.4 نقص‌های stop-ship سرور

#### TLS ناامن

`RastehApplication.kt:23-44` trust manager و hostname verifier ناامن تعریف می‌کند. این ریسک MITM روی ارتباط درگاه/SMS است و باید پیش از هر انتشار حذف شود.

#### seed و secret defaults

`application.properties` fallbackهای حساس و seed روشن دارد و `DataSeeder.kt`/`MarketplaceShopSeeder.kt` حساب‌های ثابت می‌سازند. production باید fail-fast باشد، seed فقط profile توسعه/تست اجرا شود و تمام credentialهای در معرض ریسک rotate شوند.

#### IDOR و admin boundary

`order/api/OrderController.kt:43-53` و `OrderService.kt:244-254` اجازه می‌دهند کاربر authenticated بدون scope مالکیت shipping یا tracking سفارش دیگر را لمس کند. بعضی admin controllerها نیز annotation صریح ندارند. policy باید deny-by-default و query باید owner/shop scoped باشد.

#### wallet exploit و هم‌زمانی

DTOهای wallet validation کافی ندارند. `WalletService.kt:53-81` با مبلغ منفی می‌تواند balance را افزایش دهد؛ entity version/lock و ledger immutable ندارد. این نقص مالی مستقیم است.

#### callback و پرداخت ترکیبی

- callback در `PaymentController.kt:40-57` است ولی در whitelist `SecurityConfig.kt` نیست و ممکن است 401 شود؛
- `PaymentService` payment attempt تکراری و verify بدون lock/CAS می‌سازد؛
- unique constraint روی authority/order/business reference دیده نمی‌شود؛
- `OrderService` مبلغ wallet/gateway را جدا می‌کند، اما `PaymentController.kt:28-31` کل `totalPrice` را به gateway می‌دهد.

اثر: callback واقعی fail، double processing یا charge دوبارهٔ سهم پرداخت‌شده از wallet.

#### marketplace order

`MarketplaceOrderService.kt` quantity منفی، stock بدون lock/version، status transition آزاد و compensation ناقص دارد. oversell، افزایش ساختگی موجودی و state نامعتبر محتمل است.

#### migration

`application.properties` از `ddl-auto=update` استفاده می‌کند. init scriptها فقط هنگام volume جدید Docker اجرا می‌شوند، تعداد tableها با entityها همخوان نیست و config مهاجرت سفارشی خطاها را swallow می‌کند. Flyway/Liquibase وجود ندارد.

#### upload

`FileStorageService.kt` نام اصلی را وارد path دیسک local می‌کند. controllerها عمدتاً به client `contentType` اعتماد دارند و فایل‌ها عمومی سرو می‌شوند. object storage، magic-byte validation، re-encode، AV scan و lifecycle لازم است.

## 5. وضعیت فیچرها

معیارها:

- **پایه موجود**: مسیر API/UI قابل‌شناسایی است، نه لزوماً production-ready.
- **ناقص**: بخشی از lifecycle یا اتصال اصلی کم است.
- **نمایشی**: façade/placeholder یا داده بدون invariant مالی/عملیاتی.
- **مفقود**: نیاز صریح محصول بدون implementation معنی‌دار.

| حوزه | وضعیت | توضیح |
|---|---|---|
| ثبت‌نام/login/JWT/refresh/profile/address | پایه موجود | OTP/session hardening و account lifecycle ناقص |
| شهر/راسته/پاساژ/location | پایه موجود | shop lat/lng و geo search/PostGIS ناقص |
| ثبت و تأیید فروشگاه | پایه موجود | KYB، مدارک، audit، claim و membership ناقص |
| نقش CUSTOMER/VENDOR/ADMIN | پایه موجود | SUPERADMIN در کلاینت ناسازگار؛ multi-role و MARKETER مفقود |
| بازاریاب میدانی | مفقود | maker-checker، assignment، incentive و anti-fraud ندارد |
| ویترین فروشگاه | پایه موجود | مدل جدا، media/category/variant محدود |
| فروش مستقیم فروشنده | ناقص | marketplace order به payment/address/shipping وصل نیست |
| catalog/variant/inventory کلاسیک | نسبتاً موجود | فروشگاه/offer ندارد |
| cart/discount/order کلاسیک | ناقص | pricing snapshot، refund، idempotency و discount consistency مشکل دارد |
| زرین‌پال | ناقص/stop-ship | callback، مبلغ ترکیبی، verify و idempotency blocker |
| wallet/withdrawal | stop-ship | validation، ledger و concurrency ناامن |
| bookmark/favorite/save | موجود ولی تکراری | مدل‌های موازی نیاز به ادغام دارند |
| chat/offer | CRUD پایه | pagination، realtime، notification و cross-shop invariant ناقص |
| review/report | پایه | verified outcome، self-review guard و moderation audit ناقص |
| blog/story/media | پایه | upload security/lifecycle ناقص |
| orders/tracking | دوگانه | ownership و state machine ناقص |
| Visual Search | placeholder | صفحهٔ «به‌زودی» |
| Concierge | نمایشی | FAQ ثابت، نه سرویس هوشمند/عملیاتی |
| Wayfinding | ناقص | فهرست طبقات؛ نقشه/graph navigation فاز بعد |
| Video Player | ناقص | iOS/Web/Desktop پیاده نشده |
| flash sale/group buy/loyalty/alert | façade | به checkout/inventory/notification کامل وصل نیست |
| gift card/subscription/escrow/parking/live | نمایشی/پرریسک | جریان مالی واقعی یا validation کافی ندارد |
| payout/commission/settlement/invoice/tax | مفقود | برای marketplace تراکنشی ضروری |
| privacy export/delete/retention | مفقود | deactivate جای deletion workflow نیست |
| observability/backup/DR/runbook | مفقود | release blocker عملیاتی |

## 6. معماری و نقش‌های کاربری

مدل فعلی «یک enum نقش سراسری» برای بازارگاه مناسب نیست. کاربر می‌تواند هم مشتری و هم مالک یک فروشگاه و هم مدیر فروشگاه دوم باشد. پیشنهاد:

- `PlatformRole`: `USER`, `ADMIN`, `SUPERADMIN`, `FIELD_AGENT`, `AGENT_SUPERVISOR`؛
- `ShopMembershipRole`: `OWNER`, `MANAGER`, `STAFF`, `CATALOG_EDITOR`, `ORDER_OPERATOR`؛
- vendor یک capability ناشی از عضویت approved است، نه یک role جهانی؛
- authorization ترکیب RBAC + ABAC مالکیت/عضویت فروشگاه باشد.

بازاریاب فقط draft/verification evidence می‌سازد. تأیید نهایی فروشگاه، تغییر مالکیت و تغییر حساب تسویه نیازمند checker مستقل و audit trail است.

## 7. کیفیت داده و performance

- بیشتر APIهای marketplace `List` بدون pagination برمی‌گردانند؛
- queryهای search از `%query%` و full scan استفاده می‌کنند؛
- dashboard و analytics برای count، collectionهای کامل را می‌خوانند؛
- N+1 در wallet/admin محتمل و شناسایی شده است؛
- schema فاقد بسیاری از `CHECK`, FK و unique invariantهای پول/stock/status است.

هدف: pagination سقف‌دار، projection/aggregate SQL، PostgreSQL FTS/`pg_trgm`، PostGIS، indexهای composite و query budget در تست.

## 8. حریم خصوصی و عملیات

داده‌های حساس شامل token، پروفایل، آدرس، location، chat، IBAN، order snapshot، مدارک فروشنده و audit مالی‌اند. اکنون data inventory، purpose registry، retention matrix، export/delete، consent ledger و immutable admin audit کامل وجود ندارد.

در عملیات نیز actuator/micrometer/tracing/health، readiness، request-id، alert، backup/restore drill و distributed lock برای scheduled jobs کامل نیست. Dockerfile non-root/healthcheck ندارد.

## 9. اولویت‌بندی نهایی

### P0 — قبل از هر پرداخت/انتشار

1. TLS ناامن سرور؛
2. wallet مبلغ منفی و concurrency؛
3. IDOR سفارش و admin deny-by-default؛
4. مبلغ اشتباه gateway و callback/idempotency؛
5. callback کلاینت و پاک‌شدن cart؛
6. seed/secrets/log/token storage؛
7. quantity/stock/status marketplace order.

### P1 — قبل از pilot عمومی

1. Flyway و schema invariants؛
2. یکپارچه‌سازی commerce stack؛
3. role/membership/field agent؛
4. تست integration/security/concurrency؛
5. production environments و HTTPS؛
6. privacy/account deletion؛
7. upload امن و observability.

### P2 — برای product-market fit

1. shop claim و geospatial directory؛
2. Product/Variant/ShopOffer/InventorySnapshot؛
3. search/SEO/freshness؛
4. reservation/quote/verified review؛
5. vendor/admin/agent workspace؛
6. responsive/accessibility/i18n.

### P3 — پس از اثبات pilot

flash sale، group buy، loyalty، subscription، gift card، escrow، parking، live، advanced AI، چندفروشندگی در یک cart و B2B wholesale.

## 10. جمع‌بندی

دارایی اصلی فعلی پروژه breadth کد، شناخت دامنه و یک design/network/domain پایهٔ قابل‌استفاده است. بزرگ‌ترین تهدید نیز همین breadth است: قابلیت‌های زیاد و دو مسیر تجاری اجازه نمی‌دهند هستهٔ marketplace عمیق و قابل‌اعتماد شود. مسیر درست، توقف گسترش feature، رفع P0ها، انتخاب مدل canonical، ساخت یک micro-cluster و اندازه‌گیری نتیجهٔ محلی تأییدشده است.

