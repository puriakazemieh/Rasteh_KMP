# Task 00 — قفل تصمیم‌ها و خط مبنای قابل‌بازتولید

**Status:** DONE
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

## Execution evidence

- **Blocked at:** 2026-08-05 (Asia/Tehran)
- **Baseline:** client `599b47253bae08ad9911f49ce4904eb2d6598c8c`; server `66c83ede13c202f1a71c334d5e329a25687c1321`.
- **Working tree:** client status پیش از تغییر ثبت شد. ثبت نخست server به‌اشتباه از workdir client خوانده شده بود؛ هنگام اصلاح، تغییرات موجود server—including local Gradle/artifact files—حفظ شدند و هیچ‌یک پاک یا بازنویسی نشدند.
- **Reviewed:** `plans/README.md`, `plans/CURRENT_STATE_AUDIT.md`, and `plans/TARGET_SYSTEM_DESIGN.md`.
- **Blocker owner:** product owner, with legal/finance owner confirmation where applicable.
- **Why blocked:** the required MVP boundary, legal responsibility, money/PSP/settlement interpretation, pilot scope, providers, release channels, and deprecation ownership/window are still open. `TARGET_SYSTEM_DESIGN.md` lists these as open decisions. Creating ADRs would therefore assert business, legal, or financial choices without an owner decision.
- **Commands run:** `git status --short`, `git rev-parse HEAD` in both repositories (server read with an inline safe-directory setting). Verification commands were intentionally not run: Task 00 cannot meet its decision-dependent Done-when criteria until this blocker is resolved.
- **Migration/contracts:** none; no schema, API, or production migration was designed or changed.
- **Security/privacy review:** no secrets, credentials, merchant identifiers, or personal data were accessed, emitted, or changed.
- **Next action:** product owner must record the decisions below; then change this task and the roadmap row back to `NOT STARTED` (or directly `IN PROGRESS` when execution resumes) in sync.

### Decision approval

در 2026-08-05 مالک محصول پیشنهادهای «Decision options and effects» را برای اجرا تأیید کرد: MVP `directory + reservation/quote`، نقش پلتفرم معرفی/تسهیل‌گر رزرو (نه نگه‌دارندهٔ وجه)، قرارداد پول `IRR` با `Long`، مدل canonical و Offer modeهای پیشنهادشده، پایلوت تک‌خوشه با حداکثر دو دسته، Android + Web اول، و freeze/deprecation کنترل‌شدهٔ stack قدیمی. جزئیات و ownerهای عملیاتی در ADRها و اسناد خروجی این task ثبت می‌شوند.

**Pilot decision:** تهران، محدودهٔ تجاری لاله‌زار؛ category نخست الکتریکی و category دوم تعریف‌نشده است. مالکیت محصول، عملیات حضوری، انتشار Android/Web و تصمیم حقوقی/مالی به نقش‌های تعیین‌شدهٔ پروژه واگذار شده‌اند. نام اشخاص و credentialها عمداً در VCS ثبت نمی‌شوند. providerهای واقعی و SLA آن‌ها به Task 14 موکول شده‌اند؛ fallbackهای MVP غیرمالی‌اند و payment همچنان disabled است.

### اجرای تصمیم‌ها و مانع verification

- **Changed:** client: `README.md`, `RASTEH_KMP_PLAN.md`, `docs/product/MVP_SCOPE.md`, `docs/architecture/API_AND_DOMAIN_STATUS.md`, `plans/README.md`, `plans/tasks/00_DECISIONS_AND_BASELINE.md`; server: `README.md`, `docs/adr/0001-marketplace-boundary.md` تا `0005-platform-release-scope.md`.
- **Inventory cross-check:** وجود دو مدل `catalog/Product` و `marketplace/Product` و screenهای پرداخت/کیف‌پول/featureهای experimental در client، و controller/entityهای catalog، cart، marketplace، order، payment و engagement در server با `CURRENT_STATE_AUDIT.md` تطبیق داده شد. هیچ کد یا قرارداد runtime در این task تغییر نکرد.
- **Contract outcome:** `/api/v1`، envelope خطا، pagination، UTC، money دقیق، idempotency/ETag و lifecycle deprecation حداقل 90روزه در `API_AND_DOMAIN_STATUS.md` و ADR 0004 ثبت شد. migration یا OpenAPI runtime ایجاد نشد، زیرا API/schema هنوز تغییر نکرده است.
- **Verification attempted:** client `./gradlew.bat --no-daemon :composeApp:compileKotlinJvm :composeApp:compileKotlinJs`؛ server `./gradlew.bat --no-daemon --stacktrace test` و `./gradlew.bat --no-daemon --stacktrace bootJar`.
- **Verification result:** هر سه command با تنظیم `GRADLE_USER_HOME=C:\Users\p.kazemiyeh\.gradle` و cache موجود سیستم با exit code `0` تکمیل شدند: compile JVM/JS client، `test` server و `bootJar` server. تلاش نخست با cache جداگانه به‌علت دانلود Gradle ناکام مانده بود؛ از cache واقعی سیستم استفاده شد و هیچ dependency جدیدی برای حل failure اضافه نشد.
- **Runtime evidence:** در 2026-08-05، `docker compose up -d db` سرویس `db` را healthy کرد و `bootRun` سرور با اتصال PostgreSQL محلی و listener پورت `8080` شروع شد. درخواست health endpoint پاسخ `401` داد، یعنی endpoint نیازمند authentication است و anonymous readiness proof نیست. log startup همچنین هشدارهای constraint/schema و jobهای payment موجود را نشان داد؛ این‌ها بدهی‌های شناخته‌شدهٔ taskهای 01 و 03 هستند و در این task تغییر داده نشدند.
- **Android run evidence:** با `GRADLE_USER_HOME=C:\Users\p.kazemiyeh\.gradle`، command `./gradlew.bat --no-daemon :composeApp:assembleDebug` با exit code `0` تکمیل شد و APK در `composeApp/build/outputs/apk/debug/composeApp-debug.apk` تولید شد. `adb` در دسترس است، اما هنگام بررسی هیچ device یا emulator متصلی گزارش نشد؛ نصب/launch پس از اتصال device انجام می‌شود.
- **Safe rollback:** فقط مستندات این task تغییر کرده‌اند؛ migration، schema و source runtime دست‌نخورده‌اند. rollback در صورت نیاز با revert همین فایل‌های مستندات ممکن است و تغییر کاربر حذف نشده است.
- **Commit/SHA:** commit جدید ایجاد نشد؛ baseline client `599b47253bae08ad9911f49ce4904eb2d6598c8c` و server `66c83ede13c202f1a71c334d5e329a25687c1321` بود.
- **Former blocker resolution:** پایلوت تهران/لاله‌زار و category الکتریکی تصویب شد. انتخاب providerهای واقعی با fallback/SLA به Task 14 منتقل شده است؛ این واگذاری با scope غیرمالی MVP سازگار است.

### Completion evidence

- **Completed at:** 2026-08-05 (Asia/Tehran).
- **Accepted scope:** تهران، محدودهٔ تجاری لاله‌زار، با category نخست الکتریکی؛ MVP directory + reservation/quote است و payment/PSP/refund/settlement disabled می‌ماند.
- **Ownership and privacy:** نقش‌های مالک محصول، عملیات، انتشار Android/Web و حقوقی/مالی تعیین شده‌اند؛ نام اشخاص در VCS نگهداری نشده است.
- **Deferred provider decision:** providerهای واقعی map/SMS/push/storage/analytics، SLA، sandbox و runbook به Task 14 منتقل شدند. fallbackهای MVP غیرمالی و در `MVP_SCOPE.md` و ADR 0005 ثبت هستند.
- **Commands passed:** client `:composeApp:compileKotlinJvm :composeApp:compileKotlinJs` و `:composeApp:assembleDebug`; server `test` و `bootJar`; `git diff --check` در هر دو repository. buildها با `GRADLE_USER_HOME=C:\Users\p.kazemiyeh\.gradle` و cache محلی موجود اجرا شدند.
- **Runtime:** PostgreSQL Compose healthy و Spring Boot روی پورت local `8080` اجرا شد؛ health endpoint anonymous پاسخ `401` دارد و readiness عمومی محسوب نمی‌شود.
- **Changed files:** client `README.md`, `RASTEH_KMP_PLAN.md`, `docs/product/MVP_SCOPE.md`, `docs/architecture/API_AND_DOMAIN_STATUS.md`, `plans/README.md`, `plans/tasks/00_DECISIONS_AND_BASELINE.md`, `plans/tasks/14_PRODUCTION_AND_PLATFORM_RELEASES.md`; server `README.md`, ADRهای `0001` تا `0005`.
- **Migrations/contracts:** migration runtime اجرا/ایجاد نشد؛ قرارداد API/domain و برنامهٔ expand/contract/compatibility window ثبت شد.
- **Remaining risks:** debtهای امنیت/مالی، `ddl-auto=update`، schema warningهای startup، و provider/payment واقعی به taskهای وابسته منتقل شده‌اند. هیچ‌کدام به‌عنوان resolved اعلام نشده‌اند.
- **Commit:** هیچ commit جدیدی ایجاد نشد.

### Decision options and effects

1. **MVP boundary**
   - `directory + showcase`: lowest payment/legal risk; excludes reservations and transactions from MVP.
   - `directory + reservation/quote`: supports lead/reservation workflows; requires clear cancellation, support, and data-handling policy, but no settlement design.
   - `transactional marketplace (single shop)`: enables checkout; requires licensed PSP/settlement, refund/dispute ownership, idempotent payments, and the P0 security/finance work before pilot.
2. **Money and settlement**
   - Approve `IRR` integer minor units (recommended in the target design), with تومان only for presentation: gives a precise API/storage contract.
   - Choose another exact money contract: requires an explicit currency code, minor-unit rule, PSP amount mapping, and migration/compatibility impact.
   - Defer PSP/settlement: financial flows must remain disabled-by-default; no payment migration can be designed.
3. **Pilot and release scope**
   - Name one pilot cluster and up to two categories, and launch Android + Web only: bounds onboarding, support, analytics, and release work.
   - Select a different cluster/categories and/or add iOS/Desktop: requires an accountable owner and demonstrated channel value, signing, crash reporting, and rollback evidence for each added target.
4. **Operations and migration ownership**
   - Assign owners and compatibility windows for the legacy commerce stack, provider choices (map/SMS/push/payment/storage/analytics), legal/privacy controller, support/refund SLA, and deprecation: enables ADRs and a safe expand/contract plan.
   - Leave any owner/window undecided: the affected capability remains blocked; no assumed ADR or migration is permitted.

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
