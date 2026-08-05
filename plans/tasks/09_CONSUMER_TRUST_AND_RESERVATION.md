# Task 09 — تجربهٔ مصرف‌کننده، اعتماد و رزرو

**Status:** NOT STARTED  
**Planned at:** client `2a53bcf` / server `66c83ed`  
**Risk:** Medium  
**Dependencies:** Tasks 05, 07 and 08

## Outcome

کاربر بدون اجبار به checkout بتواند فروشگاه/محصول را ذخیره، دنبال، مقایسه، تماس/مسیر، quote یا reserve کند و signal اعتماد معنادار ببیند.

## Current evidence

- favorites و bookmarks مدل‌های تکراری دارند؛
- review بدون خرید/مراجعهٔ تأییدشده و self-review guard کافی است؛
- Offer ارتباط محصول/shop را کامل validate نمی‌کند؛
- report target/status validation و moderation workflow ناقص است؛
- chat polling/unbounded و notification ناقص است.

## MVP journeys

1. Search → Shop/Product → Save/Follow؛
2. Shop/Product → Direction/Call/Message؛
3. RESERVABLE → quote/reservation → merchant response → pickup/visit verification؛
4. Outcome → verified review؛
5. Abuse → report → moderation status/appeal.

## Trust model

Review types:

- `VERIFIED_PURCHASE`؛
- `VERIFIED_RESERVATION_OR_VISIT`؛
- `GENERAL`.

badgeها و scoreها جدا نمایش داده شوند. self-review، duplicate، conflict-of-interest و rate abuse کنترل شود.

## Steps

1. favorite/bookmark را به SavedEntity canonical با target type یا explicit tables امن ادغام کن.
2. Follow shop و notification preference.
3. quote/reservation state machine، expiry، capacity/stock hold متناسب با mode.
4. merchant response SLA و reminder/auto-expire.
5. visit/pickup verification با one-time code/QR؛ location صرفاً optional و privacy-aware.
6. review eligibility و weight؛ merchant reply و appeal.
7. report target resolver، enum reason/status، evidence و moderation audit.
8. chat scope shop/conversation membership، pagination و abuse controls.
9. consumer Activity timeline واحد برای quote/reservation/order/message.
10. UI وضعیت empty/loading/error/offline و recovery.
11. events برای high-intent و WVLO؛ raw call/direction به‌تنهایی outcome حساب نشود.

## Verification

- save idempotent و migration بدون duplicate؛
- self-review و review بدون eligibility برچسب/رد صحیح؛
- one-time visit code replay نشود؛
- reservation expiry stock/capacity را آزاد کند؛
- merchant فقط reservation فروشگاه خود را تغییر دهد؛
- report target invalid رد و audit باقی بماند؛
- chat authorization/pagination/rate limit؛
- offline/retry client duplicate action نسازد.

## Done when

- [ ] save/follow canonical و idempotent است.
- [ ] quote/reservation lifecycle end-to-end و قابل‌ردیابی است.
- [ ] reviewها نوع/وزن/eligibility روشن دارند.
- [ ] moderation/report audit و appeal دارد.
- [ ] Activity timeline outcome واحد می‌دهد.
- [ ] WVLO event فقط برای نتیجهٔ تأییدشده تولید می‌شود.

## STOP conditions

- اگر reservation deposit/hold مالی پیشنهاد شد، آن را بدون task 12 و تصمیم شریک پرداخت نساز.
- اگر review ranking policy تصویب نشده، score واحد منتشر نکن.
- location دقیق را برای visit verification اجباری نکن.

## Executor prompt

```text
Task 09 را اجرا کن. saved/follow را canonical، quote/reservation را stateful و review را بر اساس verified purchase/visit/general بساز. report/chat/moderation باید authorization، pagination، audit و anti-abuse داشته باشند. WVLO فقط برای outcome قابل اثبات تولید شود. هیچ بیعانه یا hold مالی را قبل از task 12 اضافه نکن. تست idempotency، replay، expiry و ownership اجباری است.
```
