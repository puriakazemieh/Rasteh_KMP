# وضعیت API و دامنهٔ canonical

- Status: Accepted baseline
- Date: 2026-08-05
- Contract authority: ADR 0001 تا 0004 در repository server

## دامنهٔ هدف

`MerchantOrganization → ShopLocation` برای هویت تجاری و شعبهٔ فیزیکی است. `Product → Variant → ShopOffer → InventoryPosition` منبع حقیقت کاتالوگ و عرضه است. order در MVP رزروی به payment متصل نیست. دو مدل فعلی catalog و marketplace legacy هستند و نباید برای feature جدید توسعه یابند.

## قرارداد HTTP جدید

- API canonical با `/api/v1` شروع می‌شود؛ endpointهای بدون version legacy هستند.
- timestampها ISO-8601 UTC هستند.
- money همیشه `{ "minorUnits": <integer>, "currency": "IRR" }` است.
- listها pagination با سقف size دارند.
- خطا envelope پایدار `code`، `message`، `correlationId` و `fieldErrors` دارد.
- createهای حساس `Idempotency-Key` و updateهای concurrent ETag/version دارند.
- enum client unknown-safe است.
- OpenAPI و contract test پیش از انتشار هر contract جدید به‌روز می‌شوند.

## lifecycle deprecation

1. schema/API canonical به‌صورت additive عرضه می‌شود.
2. client adapter به `/api/v1` منتقل و legacy dual-read فقط با telemetry فعال می‌شود.
3. writeهای جدید legacy freeze می‌شوند.
4. reconciliation با اختلاف صفر و contract consumerها ثبت می‌شود.
5. legacy حداقل 90 روز پس از عرضهٔ پایدار v1، read-only می‌ماند.
6. حذف در release جداگانه، با owner sign-off و rollback/roll-forward plan انجام می‌شود.

هیچ migration destructive، rename ناسازگار یا حذف endpoint در window سازگاری مجاز نیست.
