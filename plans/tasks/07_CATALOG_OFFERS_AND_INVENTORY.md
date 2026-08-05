# Task 07 — کاتالوگ، Offer، واریانت و موجودی تازه

**Status:** NOT STARTED  
**Planned at:** client `2a53bcf` / server `66c83ed`  
**Risk:** High  
**Dependencies:** Tasks 04 and 06

## Outcome

هر فروشگاه بتواند یک محصول را به‌صورت ویترین، رزروپذیر یا قابل‌خرید عرضه کند؛ قیمت و موجودی از Product جدا و freshness قابل‌دیدن/اندازه‌گیری باشد.

## Core contract

```text
Product → Variant → ShopOffer
                       ├── mode
                       ├── Money price (optional by mode)
                       ├── InventoryPosition
                       ├── InventorySnapshot(lastConfirmedAt, source)
                       ├── FulfillmentOption
                       └── Media
```

Invariantها:

- SHOWCASE بدون price/inventory مجاز؛
- RESERVABLE حداقل availability/response SLA؛
- BUYABLE price exact، active variant، sellable inventory و fulfillment لازم؛
- stock منفی ممنوع؛ available = onHand - reserved؛
- stale offer به inquiry-only تنزل می‌کند.

## Steps

1. taxonomy/category/attribute strategy و product master ownership تعیین کن.
2. canonical tables/value objects و DB CHECK/UNIQUE/index.
3. Money exact و currency contract؛ Double legacy adapter فقط موقت.
4. offer CRUD با ETag/version و shop membership.
5. inventory adjustment ledger و reservation/release transaction.
6. freshness policy per category، `lastConfirmedAt/expiresAt` و scheduled expiry با distributed lock.
7. one-tap confirm و bulk update/import CSV؛ schema error report.
8. image pipeline interface؛ اجرای امنیت storage در task 13 هماهنگ شود.
9. client editor mode-aware: فیلدهای غیرلازم نمایش داده نشوند.
10. storefront/product card freshness، availability و CTA صحیح نشان دهد.
11. admin catalog merge/duplicate queue و audit.
12. analytics events برای update، stale، out-of-stock cancellation.

## Files/areas

Server: canonical `catalog`, `listing`, `inventory` modules، Flyway، scheduler/outbox.  
Client: domain/network/data product contracts، bazaar/catalog/admin product screens، `ProductCard.kt`, `ShopDetailScreen.kt`, `ProductDetailScreen.kt`.

## Verification

- mode invariant matrix؛
- exact money serialization/rounding؛
- concurrent reserve/release بدون oversell؛
- stale transition با fake clock؛
- bulk import partial/error behavior idempotent؛
- membership/active shop authorization؛
- client editor validation و CTA snapshot؛
- query pagination/index performance.

## Done when

- [ ] یک Product/Variant و چند ShopOffer مستقل ممکن است.
- [ ] سه mode در DB/API/UI enforce و قابل‌فهم‌اند.
- [ ] inventory ledger/reservation concurrency-safe است.
- [ ] freshness timestamp و expiry در search/detail دیده می‌شود.
- [ ] bulk/one-tap update برای فروشنده وجود دارد.
- [ ] هیچ write جدید به ShopProduct legacy انجام نمی‌شود.

## STOP conditions

- اگر واحد پول task 00 قفل نشده؛
- اگر stock source of truth برای فروشگاه متصل به POS نامشخص؛
- اگر Product merge خودکار با confidence پایین است؛
- اگر BUYABLE بدون reservation/price exact فعال می‌شود.

## Executor prompt

```text
Task 07 را اجرا کن. Product/Variant را از ShopOffer جدا و سه mode SHOWCASE/RESERVABLE/BUYABLE را با invariant دقیق بساز. Money exact، inventory adjustment/reservation، freshness SLA/expiry و bulk update ضروری‌اند. UI باید mode-aware و زمان آخرین تأیید را نشان دهد. concurrent oversell و stale behavior را با تست واقعی پوشش بده؛ هیچ مسیر legacy جدید نساز.
```
