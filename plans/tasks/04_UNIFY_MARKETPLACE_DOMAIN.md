# Task 04 — یکپارچه‌سازی دامنهٔ marketplace

**Status:** NOT STARTED  
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

- [ ] مدل canonical در server/client منبع حقیقت است.
- [ ] backfill/reconciliation قابل‌بازتولید و بدون اختلاف unexplained است.
- [ ] API v1 versioned و paginated است.
- [ ] legacy endpointها read-only/deprecated و telemetry دارند.
- [ ] هیچ flow جدید به entity legacy write نمی‌کند.
- [ ] rollback/roll-forward و compatibility window ثبت شده است.

## STOP conditions

- mapping مالی/شناسه‌ای مبهم؛
- داده orphan بدون تصمیم owner؛
- dual-write بدون reconciliation؛
- درخواست حذف فوری table legacy؛
- migration Big Bang بدون staged rollout.

## Executor prompt

```text
Task 04 را به‌صورت expand/backfill/verify/contract اجرا کن، نه big-bang. دو stack catalog/marketplace و order را به مدل canonical Merchant/Shop/Product/Variant/ShopOffer/Inventory/Order تبدیل کن. mapping و compatibility را پیش از کد ثبت کن، API v1 و adapters بساز، migration dry-run و reconciliation اجرا کن. table/endpoint legacy را در همین task حذف نکن. اگر mapping پول یا ownership مبهم است BLOCKED شو.
```
