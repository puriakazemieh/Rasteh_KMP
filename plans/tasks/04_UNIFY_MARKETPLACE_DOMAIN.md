# Task 04 — یکپارچه‌سازی دامنهٔ marketplace

**Status:** DONE
**Planned at:** client `2a53bcf` / server `66c83ed`  
**Risk:** High  
**Dependencies:** Tasks 01 and 03

## Outcome

یک مدل canonical جایگزین دو stack موازی شود، بدون big-bang migration یا از دست‌رفتن داده. API قدیمی version/deprecate و telemetry برای مصرف باقی‌مانده ثبت شود.

## Current evidence

Server:

- `catalog/persistence/entity/ProductEntity.kt` variant/inventory دارد ولی shop ندارد؛
- `marketplace/persistence/entity/ShopProductEntity.kt` shop دارد ولی variant/reservation/payment ندارد؛
- `order/**` و `marketplace/order/**` دو order مستقل‌اند؛
- favorite/bookmark و product/shop review هم موازی‌اند.

Client:

- `core/domain/.../catalog/Product.kt` و `marketplace/Product.kt`؛
- `core/domain/.../order/Order.kt` و `marketplace/Order.kt`؛
- route/detail/historyهای دوگانه در `Screen.kt` و `AppNavigation.kt`.

## Target boundary

```text
MerchantOrganization → ShopLocation
Product → Variant → ShopOffer → InventoryPosition
Cart(single shop) → Order → PaymentAttempt
Save/Follow/Review روی reference canonical
```

`SHOWCASE`, `RESERVABLE`, `BUYABLE` modeهای `ShopOffer` هستند.

## Steps

1. ADR task 00 و schema Flyway task 03 را verify کن.
2. mapping document برای تمام field/endpoint/tableهای legacy بساز.
3. table/entity canonical را با UUID/ID strategy و external legacy ids اضافه کن.
4. API `/api/v1` canonical و error/pagination/money contract ایجاد کن.
5. adapterهای read-only برای map کردن legacy داده به canonical response بساز.
6. backfill dry-run: shop relation، product/variant/offer و order mapping؛ orphan/duplicate report تولید کن.
7. write path جدید را پشت feature flag فعال کن؛ dual-write فقط اگر invariant و reconciliation test دارد.
8. client domain contract واحد و repository adapter بساز؛ screenها هنوز مرحله‌ای migrate شوند.
9. telemetry روی endpoint/route legacy و deprecation headers.
10. پس از صفرشدن اختلاف و مصرف، legacy write را freeze؛ حذف table/route در release جدا.

## Files/areas

Server:

- `catalog/**`, `marketplace/**`, `cart/**`, `order/**`, `payment/**`, `interaction/**`
- `src/main/resources/db/migration/**`
- OpenAPI/config/test contractها.

Client:

- `core/domain/**/{catalog,marketplace,order}`
- `core/data/**/{catalog,marketplace,order}`
- `core/network/**/{catalog,marketplace,order}`
- `core/common/.../Screen.kt`
- `core/navigation/.../AppNavigation.kt`
- featureهای bazaar/catalog/details/cart/orders.

## Compatibility rules

- هیچ endpoint قدیمی بدون حداقل یک release notice حذف نشود؛
- money جدید دقیق باشد؛ تبدیل Double فقط در legacy adapter با validation/rounding صریح؛
- تمام fieldهای مالی legacy در client که هنوز `Double` هستند باید در مدل canonical به minor-unit integer یا value object دقیق منتقل شوند؛ هیچ flow مالی جدید نباید آن‌ها را مصرف کند.
- order مالی legacy read-only و auditپذیر بماند؛
- IDs در response جدید type/prefix یا contract روشن داشته باشند؛
- unknown enum در client crash نکند.

## Verification

- golden mapping برای Product/Variant/Offer/Order؛
- migration row counts/checksum و orphan report؛
- old/new API parity برای readهای منتخب؛
- dual-write reconciliation در صورت استفاده؛
- client route و saved state migration؛
- no duplicate favorite/bookmark/review؛
- production-like dataset performance.

## Verification

تمام commandهای task 03، به‌علاوه contract test و migration rehearsal اجرا شوند. query/report اختلاف باید صفر یا exceptionهای approved و مستند داشته باشد.

## Done when

- [x] مدل canonical در server/client برای read و backfill منبع حقیقت است.
- [x] backfill/reconciliation قابل‌بازتولید و بدون اختلاف unexplained در dataset آزمون است.
- [x] API v1 versioned و paginated است.
- [x] legacy endpointها deprecated و telemetry دارند؛ freeze write طبق تصمیم تأییدشده به Task 12 منتقل شد.
- [x] هیچ flow canonical جدید به entity legacy write نمی‌کند.
- [x] rollback/roll-forward و compatibility window ثبت شده است.

## STOP conditions

- mapping مالی/شناسه‌ای مبهم؛
- داده orphan بدون تصمیم owner؛
- dual-write بدون reconciliation؛
- درخواست حذف فوری table legacy؛
- migration Big Bang بدون staged rollout.

## Blocking evidence — 2026-09-27

- **Baseline:** client `6379fa1f36d6f685931e9047bdd0d937b4f8a11e`; server `d544c99b9fa22cb6074651ce292d0aa69e0d1929`.
- **Inspected:** ADR 0002 (exact IRR minor-unit money), ADR 0004 (canonical migration), client API/domain contract, catalog/product/variant/inventory entities, marketplace shop/product/order entities, both order controllers, payment conversion, and representative client price/offer/order flows.
- **Financial mapping conflict:** canonical money is `IRR minorUnits: Long`. The marketplace stack persists `shop_products.price` and `marketplace_orders.total_amount` as scale-0 `BigDecimal`, while the client labels, displays, and accepts those values as **تومان**. The catalog/order stack uses `BigDecimal` with scale 2; the payment path accepts only an exact integer **IRR** amount. The existing data contract never records a currency/unit per legacy row.
- **Why implementation must stop:** selecting `legacyValue × 10` or `legacyValue` for backfill is a business/financial decision. Either choice can materially misprice historical products, offers, orders, wallet values, or payment audit records. A sample-based assumption is not adequate for a forward-only migration.
- **No runtime/schema/client migration was made:** only synchronized roadmap/task status was changed. Existing local server artifacts remain untouched.

### Decision required from product/finance owner

1. **Treat marketplace legacy amounts as toman and convert to IRR (`×10`)** — preserves the current client wording; requires an approved exception/reconciliation report for non-integral or anomalous catalog values.
2. **Treat marketplace legacy amounts as IRR (no conversion)** — preserves PSP-aligned units; current client wording and any historical UI interpretation were wrong and must be corrected before migration.
3. **Quarantine all legacy financial records** — canonical catalog/offers can be backfilled only after a row-level unit classification; legacy orders/wallet/payment remain immutable, read-only audit records until an owner approves their unit. This is the safest option when test data and historical records cannot be reliably distinguished.

Required evidence for any option: an owner-approved unit rule, a sanitized dry-run report of row counts/sums before and after conversion, exceptions with explicit approval, and a reconciliation test. Until then, no amount backfill, dual-write, or canonical financial API will be implemented.

### Resolution — 2026-09-27

مالک محصول/مالی گزینهٔ ۲ را تأیید کرد: تمام مبلغ‌های legacy marketplace **IRR** هستند و بدون تبدیل به قرارداد canonical منتقل می‌شوند. UI و ورودی‌های client که آن‌ها را تومان نامیده‌اند باید به ریال اصلاح شوند. dry-run همچنان باید هر amount غیرصحیح یا منفی را به‌عنوان exception گزارش دهد؛ هیچ تبدیل `×10` مجاز نیست.

### Identifier-mapping blocker — 2026-09-27

- `catalog.products` دارای `id`, `slug`, `title`, category و variant/inventory است، اما هیچ `shop_id` یا external marketplace id ندارد.
- `marketplace.shop_products` دارای `id`, `shop_id`, `name`, `category_name`, stock و price است، اما slug، catalog product id، variant id، SKU یا شناسهٔ مشترک ندارد.
- marketplace order/item و offer به `shop_products` اشاره می‌کنند؛ catalog cart/order/item به `product_variants` اشاره می‌کنند. نام/دسته کلید هویتی امن برای merge نیستند.
- `shops.owner_user_id` تنها relation کسب‌وکار فعلی است؛ هیچ مدل یا کلیدی مشخص نمی‌کند که چند shop باید زیر یک `MerchantOrganization` canonical قرار گیرند.

بنابراین backfill خودکارِ `Product → Variant → ShopOffer` یا `MerchantOrganization → ShopLocation` بدون یک policy تأییدشده، duplicate/false merge ایجاد می‌کند. حدس بر اساس نام، slug تولیدشده یا مالک کاربر ممنوع است.

### Decision required from product/data owner

1. **One legacy record = one canonical record (recommended)** — برای هر `shop_product` یک canonical Product/Variant/ShopOffer جدا با `external_shop_product_id` ایجاد شود؛ هر catalog product/variant نیز جدا وارد شود. merge بین آن‌ها فقط پس از workflow دستیِ approved انجام شود. این امن‌ترین راه و بدون data loss است، ولی ابتدا duplicateهای نمایشی باقی می‌مانند.
2. **Approved crosswalk file/table** — مالک داده یک mapping صریح `shop_product_id ↔ catalog_product_id` و، در صورت وجود، `catalog_variant_id` ارائه کند؛ فقط همین ردیف‌ها merge می‌شوند و بقیه به‌عنوان unresolved گزارش می‌مانند. نیازمند مسئول نگهدارندهٔ crosswalk است.
3. **Rule-based automatic match** — معیارهای دقیق و قابل‌آزمون (مثلاً SKU خارجی یا ترکیب تأییدشدهٔ manufacturer code) تعیین شود. `name/category` به‌تنهایی پذیرفته نیست. بدون چنین شناسه‌ای این گزینه قابل اجرا نیست.

همچنین باید تعیین شود: `MerchantOrganization` در backfill «یک سازمان برای هر Shop» است یا «یک سازمان برای همهٔ Shopهای یک owner_user_id». این انتخاب بر عضویت، مالکیت، migration و آیندهٔ Task 05 اثر مستقیم دارد.

### Identifier-mapping resolution — 2026-09-27

مالک محصول گزینهٔ ۲ را انتخاب کرد: یک جدول crosswalk صریح بین `shop_product_id` و `catalog_product_id` (و در صورت نیاز `catalog_variant_id`) منبع معتبر merge است. پیش از ثبت یک mapping، هر record legacy جداگانه canonical می‌شود؛ هیچ match نام/دسته/slug خودکاری انجام نمی‌گیرد. هر Shop نیز یک `MerchantOrganization` مستقل می‌گیرد. این تصمیم false merge را حذف و امکان merge تأییدشده و auditپذیر بعدی را فراهم می‌کند.

### Canonical mapping specification — 2026-09-27

فایل import پیشنهادی: `server/docs/migrations/legacy-product-crosswalk.csv`. این فایل فقط header دارد و هیچ شناسه یا دادهٔ واقعی در Git ثبت نمی‌شود. هر ردیف واقعی ابتدا باید توسط مسئول داده تأیید و سپس در `legacy_product_crosswalk` ثبت شود.

| legacy source | canonical target | rule |
|---|---|---|
| `shops.id` | `merchant_organizations.external_shop_id` و `shop_locations_v1.external_shop_id` | دقیقاً یک Organization و یک ShopLocation برای هر Shop؛ merge بر مبنای owner ممنوع است. |
| `shops.owner_user_id` | `merchant_organizations.owner_user_id` | فقط مالک legacy را حفظ می‌کند، معادل عضویت چندفروشگاهی نیست. |
| `products.id` | `canonical_products.external_catalog_product_id` | هر catalog product یک Product canonical مستقل است. |
| `product_variants.id` | `canonical_variants.external_catalog_variant_id` | Variant به Product canonical متناظر متصل می‌شود. |
| `shop_products.id` بدون crosswalk تأییدشده | `canonical_products.external_shop_product_id` + Variant/Offer مستقل | نام، دسته، slug یا مالک برای merge استفاده نمی‌شود. |
| `shop_products.id` با crosswalk `APPROVED` | `shop_offers.product_id` به Product catalog canonical؛ Variant فقط اگر `catalog_variant_id` تأیید شده باشد | crosswalk تنها مرجع merge است و باید actor/timestamp approval داشته باشد. |
| `shop_products.price` | `shop_offers.price_minor_units` | IRR integer، بدون تبدیل؛ مقدار منفی یا کسری exception است. |
| `shop_products.stock` | `shop_offers.on_hand` | مقدار منفی exception است؛ `reserved=0` تا زمانی که reservation legacy reconcile شود. |
| `shops.type` | `shop_offers.sale_mode` | `BUYABLE → BUYABLE` و `VISIT_ONLY → SHOWCASE`; هیچ `RESERVABLE` خودکاری ساخته نمی‌شود. |

**Roll-forward:** migration V5 فقط expand است. backfill تنها پس از dry-run بدون exception و با feature flag جدا انجام می‌شود. **Rollback:** flagهای read/write خاموش می‌مانند و legacy schema/endpointها بدون حذف به کار ادامه می‌دهند. پنجرهٔ compatibility طبق ADR 0004 حداقل ۹۰ روز از زمان اعلام deprecation است.

## Execution evidence — 2026-09-27 (in progress)

- **Server changes:** V5 expand-only canonical schema; UUID entities/repositories; `/api/v1/offers` with paginated exact-IRR responses; legacy read adapter while `CANONICAL_MARKETPLACE_READS_ENABLED=false`; admin-only dry-run; deterministic, flag-gated backfill (`CANONICAL_MARKETPLACE_WRITES_ENABLED=false` by default); bounded legacy API telemetry/deprecation headers.
- **Client changes:** canonical `Money(minorUnits: Long, currency: "IRR")`, `ShopOffer`, v1 network API and repository adapter; default currency display changed to ریال. Legacy screens/models remain intact during the compatibility window.
- **Tests passed:** `server ./gradlew.bat --no-daemon test`; `server ./gradlew.bat --no-daemon test --tests "com.kazemieh.rasteh.shared.migration.FlywayMigrationTest"` (empty PostgreSQL, legacy-baseline upgrade, and V5 canonical tables all pass); client `:core:domain:compileKotlinJvm :core:network:compileKotlinJvm :core:data:compileKotlinJvm`.
- **Migration rehearsal correction:** V5 initially declared `shop_offers.currency` as PostgreSQL `CHAR(3)`, while Hibernate correctly validates the Kotlin String mapping as `VARCHAR(3)`. It was corrected before any application database migration; the rehearsal then passed. No user data was changed.
- **End-to-end rehearsal:** an isolated PostgreSQL test seeds a legacy owner/shop/catalog product/variant/shop product, runs dry-run, runs the flag-enabled backfill twice, and verifies one offer only with `external_shop_product_id`, `price_minor_units=125000`, and inventory preserved. This proves exact IRR transfer and idempotent retry without touching a user database.
- **Synthetic reconciliation dataset:** the rehearsal now creates two independent legacy Shops with different owners and modes (`BUYABLE` and `VISIT_ONLY`), plus catalog data and two marketplace products. It verifies two distinct MerchantOrganizations, exact IRR values, preserved stock, `SHOWCASE` mapping, and no duplicated offer after retry. No real-person or production data is used.

### Boundary decision — 2026-09-27

Task 04 owns the canonical schema, versioned read contract, explicit crosswalk mechanism, reproducible backfill/reconciliation, and deprecation telemetry. It **does not** freeze existing marketplace/cart/order/payment writes yet: no compatible v1 mutation flow exists for all active client paths, and a freeze would be a breaking release. The staged ownership is therefore:

- Task 07 moves new Offer/Variant/Inventory mutations behind the canonical write flag and reconciles dual-write where needed.
- Task 12 moves the single-shop cart, Order and PaymentAttempt state machine and then freezes legacy order/payment writes with idempotency and rollback evidence.
- Task 04 keeps legacy routes operational, marked deprecated and metered, until those two replacement flows are verified. This is the safe compatibility choice; it does not treat a placeholder as completed.

### Completion authorization — 2026-09-27

مالک محصول تأیید کرد که freeze و migration mutationهای Offer/Inventory به Task 07 و cart/order/payment/refund به Task 12 منتقل شود. با این waiver، معیارهای Task 04 به زیرساخت canonical، read contract، migration rehearsal، reproducible backfill/reconciliation و compatibility telemetry محدود و همگی انجام شده‌اند.
- **Post-completion operational work:** before enabling canonical reads in a deployed environment, run the same dry-run/reconciliation against that environment's data and populate only owner-approved crosswalk rows. Canonical mutations and the freeze of legacy commerce writes are explicitly owned by Tasks 07 and 12.

## Executor prompt

```text
Task 04 را به‌صورت expand/backfill/verify/contract اجرا کن، نه big-bang. دو stack catalog/marketplace و order را به مدل canonical Merchant/Shop/Product/Variant/ShopOffer/Inventory/Order تبدیل کن. mapping و compatibility را پیش از کد ثبت کن، API v1 و adapters بساز، migration dry-run و reconciliation اجرا کن. table/endpoint legacy را در همین task حذف نکن. اگر mapping پول یا ownership مبهم است BLOCKED شو.
```
