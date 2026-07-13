# پلنِ ساخت — Rasteh_KMP (کلاینت)

> مارکت‌پلیسِ محلیِ پاساژ/راسته (ترکیبی از دیوار/شیپور/باسلام/ترب): یک اپِ **یکپارچه** با سه نقش
> (خریدار / فروشنده / ادمینِ پاساژ)، کاتالوگِ دوحالته («خرید آنلاین»/«فقط بازدید حضوری»)،
> کشفِ **راسته × محل**، نشان‌کردن (Bookmark)، چتِ درون‌برنامه‌ای، پیشنهادِ قیمت، و ۲۰ قابلیتِ ویژه.
> زبانِ رابط: **فارسی، راست‌به‌چپ (RTL)**، اعداد به‌صورتِ **ارقامِ فارسی**.

این سند خروجیِ **تلفیقِ دو منبع** است:
1. تحلیلِ اورلپ با پروژه‌ی خواهرِ تک‌مستأجری (`shop-kotlin-kmp`) — بازاستفادهٔ حداکثریِ core.
2. **بستهٔ طراحیِ نهایی** (`design_handoff_unified_app` — hi-fi، ~۵۸ صفحه) که UI/تعامل/توکن‌ها را قطعی می‌کند.

نسخهٔ هم‌سویِ سرور: `RASTEH_SERVER_PLAN.md` در ریپویِ `Rasteh_Kotlin_Spring_Boot`.

---

## ۰) منبعِ حقیقت: بستهٔ طراحی

- **`design_handoff_v2/`** — **نسخهٔ به‌روز و منبعِ حقیقتِ فعلی** (بازطراحیِ خانه/فروشگاه/محصول + مدلِ راسته×محل + حذفِ فالو).
- `design_handoff_unified_app/` — نسخهٔ اول (تاریخی؛ برای شرحِ کاملِ ۲۰ قابلیت هنوز مرجع است).
- در هر دو: `Unified App.dc.html` (اپِ کامل)، `README.md` (توکن‌ها/مدلِ داده/صفحات/رفتار/state).

**Fidelity: high-fidelity.** رنگ‌ها/تایپوگرافی/فاصله‌ها/شعاع‌ها/تعاملات نهایی‌اند و باید **پیکسل‌به‌پیکسل**
با Compose Multiplatform بازسازی شوند. HTML مستقیماً به تولید نمی‌رود. **هرجا v2 و v1 اختلاف دارند، v2 حاکم است.**

### تغییراتِ کلیدیِ v2 (این پلن بر اساسِ آن‌ها به‌روز شد)
1. **مدلِ «راسته × محل»**: خانه با گریدِ **راسته‌ها** (صنف: مبل/موبایل/طلا/پوشاک…) آغاز می‌شود؛ کلیک روی راسته → **باتم‌شیت** انتخابِ **محل** (پاساژ/بازار) → صفحهٔ `rastehSearch`.
2. **حذفِ کاملِ فالو + هدیهٔ دنبال‌کننده**: `shopDetail` دیگر نوارِ آمار و دکمهٔ «دنبال کردن» ندارد؛ جایِ آن **«پیام به فروشگاه» + «تماس»**. سازوکارِ ذخیره → **Bookmark (نشان‌کردن)**.
3. **حذفِ سربرگِ خانه** (شهر/زنگوله/آواتار)؛ خانه با **سرچ‌بار** شروع می‌شود.
4. **`listing`**: بخشِ «دیدن در فروشگاه‌های دیگر» حالا **بعد از نظرات** است؛ نوارِ پایینِ چسبان دو حالته (خریدنی/فقط‌حضوری).
5. **نقشِ `superadmin`** اضافه شد (customer/vendor/admin/superadmin).
6. قابِ دستگاه ۳۷۵px؛ رنگِ اختصاصیِ آیکونِ هر راسته در توکن‌ها.

---

## ۱) تصمیمِ تکنولوژی

**Kotlin Multiplatform + Compose Multiplatform** (همان استکِ `shop-kotlin-kmp`) چون:
- کدِ UI/دیتا/دامین بینِ اندروید/iOS/دسکتاپ به‌اشتراک می‌رود (ونـدورها احتمالاً از دسکتاپ/تبلت هم مدیریت می‌کنند).
- ماژول‌هایِ core (network/data/designSystem/navigation) مستقیماً کپی‌شدنی‌اند.

استک: KMP + Compose MP، Koin DI، Ktor Client، Clean Architecture لایه‌ای per-module
(`network` → `domain` → `data` → `feature`)، الگویِ AppResult/ViewModel/Effect (state/effect/Channel).

---

## ۲) سیستمِ طراحی (Design System) — کپیِ دقیق از توکن‌ها

این بخش تازه است و مستقیماً از `README.md` طراحی می‌آید. **قبل از هر feature** باید `core/designSystem` منطبق شود.

### فونت
- **Vazirmatn** (وزن‌های ۴۰۰/۵۰۰/۶۰۰/۷۰۰/۸۰۰). عنوانِ بزرگ ۸۰۰، عنوانِ بخش ۷۰۰–۸۰۰، بدنه ۴۰۰–۶۰۰.

### رنگ‌ها (oklch — Compose از `Color(...)` معادلِ محاسبه‌شده استفاده کند یا oklch→sRGB helper)
| نقش | مقدار |
|---|---|
| گرادیانِ اصلی (بنفش) | `linear-gradient(135deg, oklch(0.56 0.22 300), oklch(0.4 0.19 288))` |
| برندِ تک‌رنگ (متن/آیکونِ فعال) | `oklch(0.5 0.19 300)` |
| بنفشِ تیره (تیتر) | `oklch(0.24 0.02 280)` / `oklch(0.26 0.03 285)` |
| متنِ ثانویه | `oklch(0.55 0.02 280)` |
| پس‌زمینهٔ بیرونِ فریم | `oklch(0.95 0.012 300)` |
| پس‌زمینهٔ اپ | `oklch(0.985 0.005 300)` |
| کارت/سطح | `white` |
| بوردر/جداکننده | `oklch(0.9 0.01 300)` / `oklch(0.91 0.01 300)` |
| ورودی/چیپِ خاکستری | `oklch(0.96 0.01 300)` |
| سبزِ موفقیت / «خرید آنلاین» | `oklch(0.5 0.13 150)` |
| قرمزِ هشدار / حراج / زنده / گزارش | `oklch(0.58 0.22 25)` |
| نارنجی/کهربایی (هشدارِ ملایم) | `oklch(0.6 0.16 60)` |
| طلایی (سطحِ وفاداری) | `oklch(0.7 0.13 70)` · ستارهٔ امتیاز `oklch(0.65 0.15 70)` |
| آبی (نقشه/پارکینگ) | `oklch(0.5 0.13 240)` |
| ارزان‌ترین (بوردر سبز listing) | `oklch(0.6 0.13 150)` |

### رنگِ آیکونِ راسته‌ها (v2 — گریدِ خانه)
هر راسته رنگِ اختصاصیِ آیکون دارد: مبل `oklch(0.55 0.13 45)` · موبایل `oklch(0.5 0.14 260)` · پوشاک `oklch(0.55 0.15 350)` ·
طلا `oklch(0.62 0.13 85)` · لوازم‌خانگی `oklch(0.55 0.12 200)` · کیف‌وکفش `oklch(0.5 0.13 30)` · آرایشی `oklch(0.58 0.15 350)` ·
کتاب `oklch(0.55 0.11 150)` · اسباب‌بازی `oklch(0.6 0.14 60)` · همه `oklch(0.5 0.02 280)`.
(این رنگ‌ها از سرور می‌آیند — فیلدِ `colorOklch` روی `Rasteh`.)

### شعاعِ گوشه
- چیپ/فیلد `10–11px`؛ کارت `12–14px`؛ باتم‌شیت `22px` بالا؛ آواتار `50%`. قابِ دستگاه در v2 عرضِ ۳۷۵px.

### فاصله و سایه
- paddingِ صفحه ۱۶px افقی؛ هدرها `13px 16px`؛ gapِ کارت‌ها ۸–۱۲px؛ gapِ گریدِ دوستونه ۱۰–۱۱px.
- سایهٔ کارتِ شناور: `0 2px 8px oklch(0.5 0.05 300/0.05)`.

### قواعدِ کلیدیِ RTL/i18n
- کلِ اپ `LayoutDirection.Rtl`؛ استفاده از logical spacing.
- **helperِ `faDigits`**: تبدیلِ ارقامِ لاتین → فارسی (۰۱۲۳۴۵۶۷۸۹) در همهٔ اعداد/قیمت/تایمر (کپیِ الگو از طراحی).
- تصاویر در طراحی **ایموجی + placeholderِ رنگی‌اند**؛ کامپوننتِ تصویر باید fallbackِ ایموجی داشته باشد.
- آیکون‌ها inline SVG سبکِ **Lucide** (stroke ~۱.۷)؛ از یک ست آیکونِ سازگار (compose-icons/Lucide) استفاده شود.

---

## ۳) نقش‌ها و ناوبری (یک اپِ یکپارچه)

**اپِ واحد** با سوییچِ درون‌پروفایلیِ نقش (`role = customer | vendor | admin | superadmin`).
با انتخابِ نقش، **درونِ همان تبِ پروفایل** میان‌بر + لیست‌های آن نقش inline ظاهر می‌شود (نه صفحهٔ جدا).

**خانهٔ v2 (بازطراحی — از بالا):** ۱) **سرچ‌بار** (بالاترین عنصر؛ سربرگِ شهر/زنگوله/آواتار **حذف شد**) →
۲) **گریدِ راسته‌ها** (۵ ستون × ۲ ردیف؛ کلیک → **باتم‌شیتِ انتخابِ محل**) → ۳) تبِ **فروشگاه‌ها/محصولات** →
۴) **اخیراً دیده‌شده** → ۵) **نشان‌شده‌ها (Bookmark)** → ۶) **جدیدترین فروشگاه‌ها/محصولات**.

- **Bottom-nav** خریدار: **خانه، جست‌وجو، نشان‌شده‌ها، پیام‌ها، پروفایل**.
- جریانِ کشف: `feed → (کلیک راسته) rastehSheet → rastehSearch(محل) → shopDetail/listing`.
- Router واقعی به‌جای متغیرِ `screen` (هر `screen` = یک Screen در `core/navigation`؛ sealed class + NavHost).
- Stateهای سراسری (Koin/store): auth/نقش، `city`، `cart`، `bookmarks/shopBookmarks`، `rastehSheetId`، `selectedLocation`، `homeTab/rastehTab/shopTab`، `messages`، `notifBadge`.
  (`followedShops` **حذف شد** — v2 فالو ندارد.)

---

## ۴) ماژول‌بندی

### بدون‌تغییر — کپیِ مستقیم از `shop-kotlin-kmp`
```
core/network        -- Ktor client, interceptors, DTO پایه
core/data           -- ریپازیتوری‌ها، الگویِ AppResult
core/designSystem   -- تم، تایپوگرافی، کامپوننت‌های پایه (سپس منطبق بر بخشِ ۲)
core/navigation     -- Screen sealed class + NavHost
core/common         -- AppResult، وضعیت‌های عمومی، helperِ faDigits (جدید)
feature/auth        -- login/signup (+ گزینهٔ «می‌خواهم فروشنده شوم»، نقشِ VENDOR)
feature/orders      -- تاریخچه/وضعیتِ سفارش (+ نمایشِ نامِ فروشگاه)
```

### اقتباسی — کپی + تغییر
```
feature/catalog     -- کارتِ محصول: badge نوع «خرید آنلاین»/«بازدید حضوری»؛ دکمهٔ «تماس/مسیریابی» به‌جای «افزودن به سبد» وقتی visitOnly
feature/details(listing) -- v2: گالریِ نقطه‌دار، انتخابِ مدل(chip)، کارتِ فروشگاه، توضیحات، **نظرات**، سپس بخشِ «دیدن در فروشگاه‌های دیگر»
                            (ارزان‌ترین با بوردر سبز + badge؛ فقط برای buyable)، لینکِ «مقایسهٔ همه» → compare.
                            نوارِ پایینِ چسبان: buyable = افزودن‌به‌سبد + پیشنهادِ قیمت + چت؛ visitOnly = چت + تماس
feature/cart        -- سبدِ تک‌ونـدوری: هشدار هنگامِ افزودنِ آیتم از فروشگاهِ دیگر
feature/admin/*     -- الگویِ AdminXxxScreen اما اسکوپ‌شده به vendorId فعلی (نه دیدِ سراسری)
```

### کاملاً جدید — از روی صفحاتِ طراحیِ v2
```
feature/home(feed)          -- v2: سرچ‌بار (بدونِ سربرگ) + گریدِ راسته‌ها + تبِ فروشگاه/محصول + اخیراً دیده‌شده + نشان‌شده‌ها + جدیدترین‌ها
feature/rasteh              -- **جدیدِ v2**: گریدِ راسته‌ها، باتم‌شیتِ انتخابِ محل (rastehSheetOpen)، صفحهٔ rastehSearch (تبِ فروشگاه/محصولِ یک محل + شمارش)
feature/shop(shopDetail)    -- v2: کاور + بلوکِ هویت + دکمه‌های **«پیام به فروشگاه» (اصلی) و «تماس»** (بدونِ نوارِ آمار و دکمهٔ فالو)،
                               تب‌ها: محصولات/حراجی/ویترینو/نظرات/اطلاعات(+گزارشِ تخلف)
feature/search              -- فیلترِ راسته/محل/دسته/قیمت/برند/وضعیت(new/used)/مرتب‌سازی
feature/bookmarks           -- تبِ محصولات/فروشگاه‌ها (سازوکارِ ذخیره، جایگزینِ فالو)
feature/chat                -- chatList + chatThread
feature/offer(makeOffer)    -- پیشنهادِ قیمت (فقط اگر فروشگاه acceptsOffers)
feature/city(citySelect)    -- جست‌وجوی شهر، «موقعیتِ فعلی»، لیست با تیک
feature/activity            -- فیدِ فعالیت (پسند/ذخیره/پیام)
feature/notifications, feature/wallet, feature/referral, feature/settings, feature/support, feature/editProfile, feature/myReviews
feature/profile             -- تبِ پروفایل + سوییچِ نقش (customer/vendor/admin/superadmin) inline؛ myListings، logoutConfirm
feature/vendor/onboarding(becomeVendor) -- فرمِ ثبت: نام، **راسته**، **محل**، طبقه، نوع(buyable/visitOnly)، تلفن، آدرس، ساعتِ کاری، about،
                               toggleهای «چتِ درون‌برنامه‌ای» و «پذیرشِ پیشنهادِ قیمت»؛ حالتِ pending «در انتظارِ تأیید»
feature/vendor/dashboard(vendorDash) -- کارتِ فروشگاه + نوارِ آمار (بازدیدِ امروز/پیامِ جدید/سفارشِ در انتظار) + گریدِ میان‌بر
feature/vendor/addProduct   -- اسلاتِ عکس، نام، دسته، قیمت، وضعیت، موجودی، درصدِ تخفیف، توضیح + حالتِ موفقیت
feature/vendor/manageListings, editShop, vendorOrders, vendorAnalytics, shopQr
feature/admin/dashboard(adminDash) -- تب‌های approvals/reports/quickAdd/supervisors + کارتِ آمار
feature/admin/vendorApproval, feature/admin/reportDetail  (+ نمایِ سراسریِ superadmin)
feature/mall-map(floorMap/wayfind) -- MVP: فهرستِ طبقاتِ محل؛ بعدی: نقشهٔ گرافیکی با مختصات و مسیرِ خط‌چین
```
> **حذف‌شده در v2 (نساز):** `feature/follow` (manageFollowing) و `feature/vendor/followerPerk` (FOLLOW15).
> صفحاتشان در پروتوتایپ باقی مانده ولی legacy هستند.

### ۲۰ قابلیتِ ویژه (`feature/features` launcher → هر کدام یک صفحه)
گریدِ دوستونهٔ ۲۰ کاشی. صفحات: `concierge, wayfind, live, flash, groupbuy, loyalty, appointment, compare,
escrow, tracking, returns, giftcard, warranty, events, community, pricealert, stories, vipsub, parking, visualsearch`.
هر کدام یک `feature/features/<name>` جدا با ViewModel/state خودش (فازبندی در بخشِ ۵).

---

## ۵) فازبندی (هم‌راستا با فازهایِ سرور)

### فازِ ۰ — اسکلت + دیزاین‌سیستم
- کپیِ `core/*` کامل از `shop-kotlin-kmp`، تغییرِ نام‌پکیج به `com.kazemieh.rasteh`.
- **منطبق‌کردنِ `core/designSystem`** با توکن‌های بخشِ ۲ (رنگ، Vazirmatn، شعاع، RTL، `faDigits`).
- کپیِ `feature/auth` + enum نقشِ `VENDOR` + گزینهٔ «می‌خواهم فروشنده شوم» در signup.
- تنظیمِ `settings.gradle.kts` + `composeApp/build.gradle.kts` برای ماژول‌های بالا.

### فازِ ۱ — خانهٔ v2 + راسته/محل + فروشگاه + onboarding + تأییدِ ادمین
- `feature/home(feed)` بازطراحیِ v2 (سرچ‌بار + گریدِ راسته‌ها، بدونِ سربرگ)، `feature/city`.
- `feature/rasteh`: گریدِ راسته‌ها + **باتم‌شیتِ انتخابِ محل** + صفحهٔ `rastehSearch` (تبِ فروشگاه/محصولِ محل).
- `feature/shop(shopDetail)` v2 (دکمه‌های «پیام»/«تماس»، بدونِ آمار/فالو، تب‌ها).
- `feature/vendor/onboarding` (فرم با راسته/محل + pending)، `feature/admin/vendorApproval` (approve/reject).

### فازِ ۲ — کاتالوگِ دوحالته
- `feature/catalog` + `feature/details(listing)` v2: شرطِ buyable/visitOnly، «دیدن در فروشگاه‌های دیگر» **بعد از نظرات**، نوارِ پایینِ دوحالته.
- `feature/vendor/addProduct` + `manageListings` + `editShop` (اسکوپِ vendorId).
- `feature/vendor/category-mode` (سوییچِ Showcase/Commerce per دسته).

### فازِ ۳ — چت + پیشنهاد + بوکمارک  ~~(فالو حذف شد)~~
- `feature/chat` (chatList/chatThread)، `feature/offer(makeOffer)`، `feature/bookmarks` (جایگزینِ فالو)، `feature/activity`.
- ~~`feature/follow` / `feature/vendor/followerPerk`~~ — **حذف‌شده در v2** (نساز).

### فازِ ۴ — سبد/سفارش + جست‌وجو/مقایسه
- `feature/cart` (هشدارِ تک‌ونـدوری) + `orderConfirm`، `feature/orders` (نامِ فروشگاه)، `feature/vendor/vendorOrders`.
- `feature/search` (فیلترِ راسته/محل/…)، `compare` (مقایسهٔ فروشندگان)، تبِ نظراتِ shopDetail.

### فازِ ۵ — قابلیت‌هایِ موجِ اول
- `features` launcher + `flash` (تایمرِ زندهٔ setInterval معادل + نوارِ موجودی)، `groupbuy`، `loyalty`، `pricealert`.
- `feature/wallet`، `feature/referral`.

### فازِ ۶ — خدماتی + نقشه
- `wayfind/floorMap` (فهرستِ طبقات → نقشهٔ گرافیکی)، `appointment`، `returns`، `warranty`، `giftcard`، `events`، `stories`.

### فازِ ۷ — پیشرفته
- `live` (ویدئوی زنده + چت + کارتِ محصول)، `escrow` (تایم‌لاین)، `tracking` (نقشه + مراحل)، `vipsub`، `parking`، `community`، `concierge`، `visualsearch`.

### فازِ ۸ — صیقل
- ریویو/پرسش‌وپاسخ، Notificationها (تأیید فروشگاه، سفارشِ جدید، پیشنهادِ قیمت، هشدارِ قیمت)، گزارشِ فروشِ ونـدور (`vendorAnalytics`).

---

## ۶) جدولِ «چه‌فایلی از کجا کپی شود»

| مقصد در Rasteh_KMP | منبع در shop-kotlin-kmp | تغییرِ لازم |
|---|---|---|
| `core/network,data,designSystem,navigation,common` | همنام | تغییرِ پکیج + منطبق‌کردنِ designSystem با توکن‌ها + `faDigits` |
| `feature/auth` | همنام | `+VENDOR` role، گزینهٔ فروشنده‌شدن |
| `feature/cart` | همنام | چکِ تک‌ونـدوری |
| `feature/orders` | همنام | نمایشِ نامِ فروشگاه |
| `feature/catalog`, `feature/details` | همنام | شرطِ buyable/visitOnly + «دیدن در فروشگاه‌های دیگر» (بعد از نظرات) |
| `feature/search` | همنام (Phase B) | فیلترِ راسته/محل/وضعیت |
| کامپوننتِ جدولِ اسپک/attributes | از `DetailsScreen.kt`/`ComparisonScreen.kt` | الگو |
| `feature/admin/*` | الگویِ AdminXxxScreen | اسکوپِ vendorId (inline)؛ + نمایِ superadmin |
| `feature/home,rasteh,shop,chat,offer,city,bookmarks,activity,wallet,mall-map`, vendorِ*, ۲۰ قابلیت | ندارد | جدید (از روی طراحیِ v2) |
| ~~`feature/follow`, `feature/vendor/followerPerk`~~ | — | **حذف‌شده در v2** |

---

## ۷) قراردادهایِ ثابت (ادامه از پروژه‌ی فروشگاه)

- هر ماژولِ Gradleِ جدید → include در `settings.gradle.kts` **و** صریح در `composeApp/build.gradle.kts`.
- الگویِ AppResult/ViewModel/Effect دقیقاً مثلِ پروژه‌ی فروشگاه (state/effect/Channel).
- **RTL سراسری** + `faDigits` روی هر عددِ نمایشی؛ تایمرِ flash با `HH : MM : SS` فارسی.
- **state مشترک** برای `bookmarks`/`shopBookmarks`/`cart`/`rastehSheetId`/`selectedLocation`/`messages`/`notifBadge` (طبقِ بخشِ State طراحیِ v2؛ `followedShops` حذف شد).
- تصویر/لوگو: الگویِ «افزودن با لینک» یا آپلودِ ساده؛ همیشه fallbackِ ایموجی.
- برایِ تستِ محلی: کاربرِ ابری قادر به build نیست (۴۰۳ از Gradle distribution)؛ build و `./gradlew` رویِ دستگاهِ کاربر.

---

## ۸) MVP در برابرِ بعدی

**MVP (فازِ ۰ تا ۴):** دیزاین‌سیستمِ کامل (RTL/فارسی)، خانهٔ v2 + راسته/محل + `rastehSearch`،
فروشگاه (پیام/تماس) + کاتالوگِ دوحالته، ثبتِ ونـدور با تأیید، چت، پیشنهادِ قیمت، بوکمارک (نشان‌کردن)،
سبد/سفارشِ تک‌ونـدوری، جست‌وجو + «دیدن در فروشگاه‌های دیگر». (بدونِ فالو — در v2 حذف شد.)

**موجِ بعدی (فازِ ۵ به بعد):** ۲۰ قابلیتِ ویژه (flash/groupbuy/loyalty/pricealert →
wayfind/appointment/returns/warranty/giftcard/events/stories →
live/escrow/tracking/vipsub/parking/community/concierge/visualsearch) + wallet/referral.

**آینده:** نقشهٔ گرافیکیِ پاساژ، اپِ ونـدورِ جدا از اپِ خریدار، آنالیتیکسِ ونـدور، featured-listingِ پولی، چند-پاساژ/چندشهری.

---

## ۹) هم‌ترازسازی با پایهٔ جدیدِ فروشگاه (برنچِ `admin-profile-pages-redesign`)

> اسپکِ مارکت‌پلیس (`design_handoff_v2`) **تغییری نکرده** — کلِ فازهای ۰–۸ روی همان ساخته شد.
> ورودیِ تازه فقط **پایهٔ به‌روزترِ اپِ فروشگاه** است (دیزاین‌سیستمِ کارمیلا + ریسپانسیو + وایت‌لیبل).
> این دو ارتقاء عرضی‌اند و مستقل از دامنهٔ راسته؛ به‌صورتِ دو فازِ جدید افزوده می‌شوند.

### فازِ R — لایه‌بندیِ ریسپانسیو/تطبیقی (چندصفحه‌ای)
- انتقالِ `core/designSystem/WindowSize.kt` از پایهٔ فروشگاه (بدونِ وابستگیِ اضافه؛ `BoxWithConstraints`،
  رده‌های Material3: Compact `<600` / Medium `600–840` / Expanded `≥840`).
- `ProvideWindowSizeClass` در ریشهٔ `App()` دورِ `AppNavHost`.
- خانهٔ راسته: `adaptiveGridColumns` (۲/۳/۴ ستون بر اساسِ عرض) به‌جای ۳ ستونِ ثابت.
- پوستهٔ اصلی: روی صفحاتِ بزرگ نوارِ پایین با **`SideNavRail`** جایگزین می‌شود (rail روی تبلت/دسکتاپ).
- صفحاتِ لیست/جزئیات/فرم: `responsiveMaxWidth()` تا روی نمایشگرِ پهن مرکزی و خوانا بمانند.

### فازِ WL — معماریِ وایت‌لیبل/برند (سبک‌شده برای راسته)
- `core/designSystem/brand/Brand.kt`: `BrandPalette`/`BrandColors`/`BrandFeatures`/`BrandConfig`/`BrandRegistry`
  هم‌الگو با پایهٔ فروشگاه، اما با پرچم‌های **مرتبط با راسته** (chat/offers/bookmarks/loyalty/community/...).
- `RastehBrand` پیش‌فرض با پالتِ بنفشِ مارکت‌پلیس (oklch) و نامِ «راسته».
- تزریق در Koin؛ گیت‌کردنِ چند بخشِ UI پشتِ پرچم‌های برند (برای وایت‌لیبلِ آینده).

---

## ۱۰) بازطراحیِ نهاییِ دیزاین (`design_handoff_final`) — تحلیلِ تفاوت‌ها و پلنِ جدید

> منبع: بستهٔ `design_handoff_final` (خریدار+فروشنده+ادمین، سه اپِ Hi-Fi + Unified به‌روز).
> **پایهٔ راسته/محل و مفهومِ حذفِ فالو تغییر نکرد**؛ اما صفحهٔ اصلی و چند صفحه **کاملاً پرمحتواتر** شدند.

### دلتا نسبت به آنچه ساخته‌ایم
| صفحه | وضعیتِ فعلیِ راسته | دیزاینِ جدید | اقدام |
|---|---|---|---|
| **خانه (feed)** | سرچ + گریدِ راسته | سرچ + گریدِ راستهٔ **۵‌ستونه با آیکونِ رنگیِ هر راسته** + **تب‌های سگمنتد فروشگاه/محصول** + **اخیراً دیده‌شده** + **نشان‌شده‌ها** + **جدیدترین فروشگاه/محصول** | **بازطراحیِ کامل (D1)** |
| **rastehSearch** | فقط لیستِ فروشگاه | **دو تبِ فروشگاه/محصول** + سربرگِ چسبان | افزودنِ تب‌ها (D2) |
| **shopDetail** | هدر + محصولات + نظرات + دکمه‌ها | **کاور** + بلوکِ هویت + **۵ تب: محصولات/حراجی/ویترینو/نظرات/اطلاعات** | غنی‌سازی (D3) |
| **صفحهٔ محصول (`listing`)** | ندارد (فقط کارتِ کالا) | **صفحهٔ مستقل**: گالری، مدل‌ها، کارتِ فروشنده، نظرات، **«دیدن در فروشگاه‌های دیگر»** + ارزان‌ترین، نوارِ اکشنِ چسبان | **صفحهٔ جدید (D4)** |
| **compare** | ندارد | مقایسهٔ فروشندگانِ یک محصول | صفحهٔ جدید (D5) |
| **پنلِ فروشنده** | onboarding + دادهٔ محصول | **اپِ فروشنده**: داشبورد/محصولات/سفارش‌ها+پیام/پروفایلِ فروشگاه با تبِ پایین | پنلِ اختصاصی (D6) |
| **ادمین** | صفِ تأیید | + **مدیریتِ راسته/محل** + **گزارشِ تخلف** + آمار | غنی‌سازی (D7) |

### نیازهای سمتِ سرور (کوچک)
- جست‌وجوی **محصول بر اساسِ محل** (برای تبِ محصولِ rastehSearch) — افزودنِ فیلترِ `locationId` به `searchProducts`.
- **«فروشندگان دیگر»**: گروه‌بندیِ محصولاتِ هم‌عنوان در محل (endpoint یا کوئری).
- **گزارشِ تخلف** (`report`) روی فروشگاه/محصول (موجودیت + endpoint).
- بقیه (راسته/محل/کاتالوگ/نظر/سفارش/چت/پیشنهاد/بوکمارک) از فازهای ۰–۸ آماده‌اند.

### فازبندیِ اجرا (فقط راسته)
- **D1 — خانهٔ v3:** گریدِ راستهٔ ۵‌ستونه با رنگ/آیکون، تب‌های سگمنتد، اخیراً دیده‌شده (لوکال)، نشان‌شده‌ها، جدیدترین فروشگاه/محصول.
- **D2 — rastehSearch:** تب‌های فروشگاه/محصول (+ فیلترِ محصولِ محل در سرور).
- **D3 — shopDetail:** کاور + ۵ تب.
- **D4 — صفحهٔ محصول (`listing`):** گالری/مدل/فروشنده/نظرات/«فروشندگان دیگر»/نوارِ چسبان.
- **D5 — compare:** مقایسهٔ فروشندگان.
- **D6 — پنلِ فروشنده:** اپِ فروشنده با تبِ پایین.
- **D7 — ادمین:** مدیریتِ راسته/محل + گزارشِ تخلف + آمار.

> ترتیب: D1 → D2 → D3 → D4 → D5 → D6 → D7. build سمتِ کاربر (محیطِ ابری قادر نیست).
