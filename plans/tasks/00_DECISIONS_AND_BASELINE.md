# Task 00 — قفل تصمیم‌ها و خط مبنای قابل‌بازتولید

**Status:** NOT STARTED  
**Planned at:** client `2a53bcf` / server `66c83ed`  
**Risk:** Medium  
**Dependencies:** none

## Outcome

پیش از refactor، تصمیم‌های دامنه و انتشار به ADR تبدیل و baseline فنی قابل‌بازتولید می‌شود. خروجی این task کد feature جدید نیست؛ قرارداد تصمیمی است که از ساخت نسخهٔ سوم commerce جلوگیری می‌کند.

## Current evidence

- کلاینت دو مدل product/order و دو flow navigation دارد.
- سرور دو stack مستقل `catalog/order/payment` و `marketplace/shop_products/marketplace_orders` دارد.
- نقش حقوقی پلتفرم، واحد پول contract، خوشهٔ pilot و کانال‌های انتشار مشخص نشده‌اند.
- `README.md`ها با breadth واقعی featureها هم‌گام نیستند.
- `RTK.md` ارجاع‌شده وجود ندارد.

## Decisions required

1. MVP یکی از این‌ها را صریح انتخاب کند:
   - directory + showcase؛
   - directory + reservation/quote؛
   - transactional marketplace single-shop.
2. واحد ذخیره پول: پیشنهاد `IRR minorUnits: Long`؛ نمایش تومان فقط presentation.
3. مدل canonical: `Merchant/ShopLocation/Product/Variant/ShopOffer/Inventory/Order`.
4. سه Offer mode: `SHOWCASE`, `RESERVABLE`, `BUYABLE`.
5. نقش‌ها: platform role + shop membership؛ agent maker-checker.
6. خوشه و حداکثر دو دستهٔ pilot.
7. stack legacy که freeze/deprecate می‌شود و compatibility window.
8. provider strategy برای map/SMS/push/payment/storage/analytics و fallback.
9. کانال‌های launch: پیشنهاد Android + Web اول؛ iOS/Desktop فقط با owner و ارزش روشن.
10. مسئولیت حقوقی/پشتیبانی/refund و owner هر حوزه.

## Files to create/update during execution

Server repository:

- `docs/adr/0001-marketplace-boundary.md`
- `docs/adr/0002-money-and-pricing.md`
- `docs/adr/0003-identity-and-shop-membership.md`
- `docs/adr/0004-commerce-migration.md`
- `docs/adr/0005-platform-release-scope.md`
- `README.md`

Client repository:

- `docs/product/MVP_SCOPE.md`
- `docs/architecture/API_AND_DOMAIN_STATUS.md`
- `README.md`
- `RASTEH_KMP_PLAN.md` فقط برای اشاره به منبع حقیقت جدید، نه بازنویسی تاریخچه کاربر.

## Steps

1. `git status --short` و SHA هر دو repo را ثبت و تغییرات کاربر را حفظ کن.
2. inventory فعلی endpoint/screen/entity را با `CURRENT_STATE_AUDIT.md` cross-check کن.
3. جلسه/تصمیم مالک محصول را برای موارد بالا ثبت کن؛ فرض حقوقی یا پرداختی نکن.
4. ADRها را با Context, Decision, Consequences, Alternatives, Migration, Revisit trigger بنویس.
5. matrix بساز: `Production`, `Pilot`, `Experimental`, `Disabled`, `Remove` برای همه featureها.
6. مسیرهای experimental مالی را در تصمیم release به‌عنوان disabled-by-default ثبت کن.
7. قرارداد naming/versioning API و lifecycle deprecation را تعیین کن.
8. baseline commandها و خروجی را ثبت کن.

## Verification

```powershell
# Client
.\gradlew.bat --no-daemon :composeApp:compileKotlinJvm :composeApp:compileKotlinJs

# Server
.\gradlew.bat --no-daemon --stacktrace test
.\gradlew.bat --no-daemon --stacktrace bootJar
```

همچنین بررسی شود تمام ADRها به یک تصمیم canonical اشاره می‌کنند و تعارضی میان READMEها نیست.

## Done when

- [ ] نقش حقوقی MVP ثبت شده یا task با owner تصمیم BLOCKED شده است.
- [ ] واحد پول و مدل canonical تصویب شده‌اند.
- [ ] خوشه/دستهٔ pilot و non-goals روشن‌اند.
- [ ] deprecation owner/window مشخص است.
- [ ] feature status matrix کامل است.
- [ ] baseline commandها پاس و evidence ثبت شده است.
- [ ] همه taskهای بعدی با تصمیم‌ها سازگارند.

## STOP conditions

- اگر مالک محصول میان directory/reservation/transaction تصمیم نگرفته، ادامه نده.
- اگر واحد پول یا PSP/settlement interpretation نامشخص است، هیچ migration مالی طراحی نکن.
- اگر تغییرات محلی کاربر با فایل‌های هدف overlap دارند، ابتدا conflict را گزارش کن.

## Executor prompt

```text
Task 00 را اجرا کن. ابتدا plans/README.md، CURRENT_STATE_AUDIT.md، TARGET_SYSTEM_DESIGN.md و همین فایل را کامل بخوان. هیچ feature پیاده‌سازی نکن. تصمیم‌های دامنه/حقوقی/پلتفرم را به ADRهای self-contained تبدیل کن، status matrix بساز، baseline هر دو repo را اجرا و evidence ثبت کن. اگر تصمیم مالک لازم است، task را BLOCKED کن و گزینه‌ها و پیامدهای دقیق را بده؛ حدس نزن. فقط پس از تحقق همه Done whenها وضعیت را DONE کن.
```

