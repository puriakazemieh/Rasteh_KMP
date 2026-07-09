# پلنِ ساخت — Rasteh_KMP (کلاینت)

> مارکت‌پلیسِ محلیِ پاساژ/راسته (ترکیبی از دیوار/شیپور/باسلام/ترب): یک اپِ **یکپارچه** با سه نقش
> (خریدار / فروشنده / ادمینِ پاساژ)، کاتالوگِ دوحالته («خرید آنلاین»/«فقط بازدید حضوری»)،
> دنبال‌کردن + هدیهٔ دنبال‌کننده، چتِ درون‌برنامه‌ای، پیشنهادِ قیمت، و ۲۰ قابلیتِ ویژه.
> زبانِ رابط: **فارسی، راست‌به‌چپ (RTL)**، اعداد به‌صورتِ **ارقامِ فارسی**.

این سند خروجیِ **تلفیقِ دو منبع** است:
1. تحلیلِ اورلپ با پروژه‌ی خواهرِ تک‌مستأجری (`shop-kotlin-kmp`) — بازاستفادهٔ حداکثریِ core.
2. **بستهٔ طراحیِ نهایی** (`design_handoff_unified_app` — hi-fi، ~۵۸ صفحه) که UI/تعامل/توکن‌ها را قطعی می‌کند.

نسخهٔ هم‌سویِ سرور: `RASTEH_SERVER_PLAN.md` در ریپویِ `Rasteh_Kotlin_Spring_Boot`.

---

## ۰) منبعِ حقیقت: بستهٔ طراحی

مرجعِ نهاییِ UI پوشهٔ `design_handoff_unified_app` است:
- `Unified App.dc.html` — اپِ کاملِ hi-fi (~۵۸ صفحه در یک state machine با متغیرِ `screen`؛ هر `screen` = یک route).
- `README.md` — توکن‌های طراحی، مدلِ داده، فهرستِ صفحات، رفتار و state.

**Fidelity: high-fidelity.** رنگ‌ها/تایپوگرافی/فاصله‌ها/شعاع‌ها/تعاملات نهایی‌اند و باید **پیکسل‌به‌پیکسل**
با Compose Multiplatform بازسازی شوند. HTML مستقیماً به تولید نمی‌رود؛ بازسازی با الگوهای همین کدبیس.

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
| طلایی (سطحِ وفاداری) | `oklch(0.7 0.13 70)` |
| آبی (نقشه/پارکینگ) | `oklch(0.5 0.13 240)` |

### شعاعِ گوشه
- چیپ/دکمهٔ گردِ کامل `999px`؛ کارت `12–18px` (رایج ۱۲/۱۳/۱۴)؛ فریمِ بیرونی `44px`، صفحهٔ داخل `34px`؛ آواتار `50%`.

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

**تصمیمِ طراحی (به‌روزشده):** برخلافِ پیش‌نویسِ قبلی که اپ‌های جدا پیشنهاد می‌داد، طراحی **یک اپِ واحد**
دارد که نقش با سوییچِ درون‌پروفایلی عوض می‌شود (`role = customer | vendor | admin`).
با انتخابِ نقش، **درونِ همان تبِ پروفایل** آمار + میان‌بر + لیست‌های آن نقش inline ظاهر می‌شود (نه صفحهٔ جدا).

- **Bottom-nav** خریدار (۵ آیتم): **خانه، جست‌وجو، ذخیره‌ها، پیام‌ها، پروفایل**.
- Router واقعی به‌جای متغیرِ `screen` (هر `screen` = یک Screen در `core/navigation`؛ الگویِ sealed class + NavHost).
- Stateهای سراسری (Koin/store): auth/نقش، `city`، `cart`، `followedShops`، `bookmarks/shopBookmarks`، `messages`، `notifBadge`.

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
feature/details(listing) -- سبکِ ترب: گالریِ نقطه‌دار، انتخابِ مدل(chip)، بخشِ «فروشندگانِ دیگرِ این کالا»
                            (ارزان‌ترین با بوردر سبز + badge)، لینکِ «مقایسهٔ همه» → compare
feature/cart        -- سبدِ تک‌ونـدوری: هشدار هنگامِ افزودنِ آیتم از فروشگاهِ دیگر
feature/admin/*     -- الگویِ AdminXxxScreen اما اسکوپ‌شده به vendorId فعلی (نه دیدِ سراسری)
```

### کاملاً جدید — از روی صفحاتِ طراحی
```
feature/home(feed)          -- هدر (انتخابِ شهر، لوگو، زنگولهٔ badge، آواتار)، سرچ‌بار، چیپِ دسته، تبِ فروشگاه/محصول،
                               بنرِ «امکاناتِ ویژه»، ردیفِ «دیده‌شدهٔ اخیر»، «دنبال‌شده‌ها» (+badge هدیه)، لیستِ «فروشگاه‌های بازارچه»، دکمهٔ «نقشهٔ پاساژ»
feature/shop(shopDetail)    -- سبکِ باسلام: کاورِ گرادیانی، آواتارِ روی کاور، تیکِ تأیید، نوارِ آمار (دنبال‌کننده/رضایت/عملکرد)،
                               دکمه‌های «دنبال‌کردن»(toggle)/«پیام»، تب‌ها: محصولات/حراجی/ویترینو/نظرات/اطلاعات(+گزارشِ تخلف)
feature/follow              -- toggleِ دنبال، manageFollowing (لغوِ دنبال + کارتِ «هدیهٔ دنبال‌کننده» FOLLOW15)
feature/search              -- فیلترِ دسته/قیمت/برند/وضعیت(new/used)/مرتب‌سازی/طبقه/شهر
feature/bookmarks           -- تبِ محصولات/فروشگاه‌ها
feature/chat                -- chatList + chatThread
feature/offer(makeOffer)    -- پیشنهادِ قیمت (فقط اگر فروشگاه acceptsOffers)
feature/city(citySelect)    -- جست‌وجوی شهر، «موقعیتِ فعلی»، لیست با تیک
feature/activity            -- فیدِ فعالیت (پسند/ذخیره/پیام/دنبال)
feature/notifications, feature/wallet, feature/referral, feature/settings, feature/support, feature/editProfile, feature/myReviews
feature/profile             -- تبِ پروفایل + سوییچِ نقش (customer/vendor/admin) inline؛ منویِ «کالاها و سفارش‌های منِ» (myListings)، logoutConfirm (دیالوگِ خروج، پاک‌کردنِ توکن)
feature/vendor/onboarding(becomeVendor) -- فرمِ ثبت: نام، دسته، طبقه، نوع(buyable/visitOnly)، تلفن، آدرس، ساعتِ کاری، about،
                               toggleهای «چتِ درون‌برنامه‌ای» و «پذیرشِ پیشنهادِ قیمت»؛ حالتِ pending «در انتظارِ تأیید»
feature/vendor/dashboard(vendorDash) -- کارتِ فروشگاه + نوارِ آمار (بازدیدِ امروز/پیامِ جدید/سفارشِ در انتظار) + گریدِ میان‌بر
feature/vendor/addProduct   -- اسلاتِ عکس، نام، دسته، قیمت، وضعیت، موجودی، درصدِ تخفیف، توضیح + حالتِ موفقیت
feature/vendor/manageListings, editShop, vendorOrders, vendorAnalytics, shopQr
feature/vendor/followerPerk -- کارتِ گرادیانیِ قرمز، کدِ فعال (FOLLOW15)، ساختِ کدِ جدید + toggleِ «قیمتِ ویژه به دنبال‌کننده‌ها»
feature/admin/dashboard(adminDash) -- تب‌های approvals/reports/quickAdd/supervisors + کارتِ آمار
feature/admin/vendorApproval, feature/admin/reportDetail
feature/mall-map(floorMap/wayfind) -- MVP: فهرستِ طبقات؛ بعدی: نقشهٔ گرافیکی با مختصات و مسیرِ خط‌چین
```

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

### فازِ ۱ — خانه + فروشگاه + onboarding + تأییدِ ادمین
- `feature/home(feed)` با همهٔ بلوک‌هایِ طراحی، `feature/city`، `feature/shop(shopDetail)` (تب‌ها + نوارِ آمار).
- `feature/vendor/onboarding` (فرم + حالتِ pending)، `feature/admin/vendorApproval` (approve/reject).

### فازِ ۲ — کاتالوگِ دوحالته
- `feature/catalog` + `feature/details(listing)`: شرطِ buyable/visitOnly روی قیمت/دکمهٔ خرید + «فروشندگانِ دیگر».
- `feature/vendor/addProduct` + `manageListings` + `editShop` (اسکوپِ vendorId).
- `feature/vendor/category-mode` (سوییچِ Showcase/Commerce per دسته).

### فازِ ۳ — دنبال‌کردن + چت + پیشنهاد + بوکمارک
- `feature/follow` + `manageFollowing` + `feature/vendor/followerPerk`.
- `feature/chat` (chatList/chatThread)، `feature/offer(makeOffer)`، `feature/bookmarks`، `feature/activity`.

### فازِ ۴ — سبد/سفارش + جست‌وجو/مقایسه
- `feature/cart` (هشدارِ تک‌ونـدوری) + `orderConfirm`، `feature/orders` (نامِ فروشگاه)، `feature/vendor/vendorOrders`.
- `feature/search` (همهٔ فیلترها)، `compare` (مقایسهٔ فروشندگان)، تبِ نظراتِ shopDetail.

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
| `feature/catalog`, `feature/details` | همنام | شرطِ buyable/visitOnly + «فروشندگانِ دیگر» |
| `feature/search` | همنام (Phase B) | فیلترِ طبقه/شهر/وضعیت |
| کامپوننتِ جدولِ اسپک/attributes | از `DetailsScreen.kt`/`ComparisonScreen.kt` | الگو |
| `feature/admin/*` | الگویِ AdminXxxScreen | اسکوپِ vendorId (inline در پروفایل) |
| `feature/home,shop,follow,chat,offer,city,bookmarks,activity,wallet,mall-map`, vendorِ*, ۲۰ قابلیت | ندارد | جدید (از روی طراحی) |

---

## ۷) قراردادهایِ ثابت (ادامه از پروژه‌ی فروشگاه)

- هر ماژولِ Gradleِ جدید → include در `settings.gradle.kts` **و** صریح در `composeApp/build.gradle.kts`.
- الگویِ AppResult/ViewModel/Effect دقیقاً مثلِ پروژه‌ی فروشگاه (state/effect/Channel).
- **RTL سراسری** + `faDigits` روی هر عددِ نمایشی؛ تایمرِ flash با `HH : MM : SS` فارسی.
- **state مشترک** برای `followedShops`/`bookmarks`/`cart`/`messages`/`notifBadge` با badge (طبقِ بخشِ State طراحی).
- تصویر/لوگو: الگویِ «افزودن با لینک» یا آپلودِ ساده؛ همیشه fallbackِ ایموجی.
- برایِ تستِ محلی: کاربرِ ابری قادر به build نیست (۴۰۳ از Gradle distribution)؛ build و `./gradlew` رویِ دستگاهِ کاربر.

---

## ۸) MVP در برابرِ بعدی

**MVP (فازِ ۰ تا ۴):** دیزاین‌سیستمِ کامل (RTL/فارسی)، خانه/فروشگاه/کاتالوگِ دوحالته، ثبتِ ونـدور با تأیید،
دنبال‌کردن + هدیهٔ دنبال‌کننده، چت، پیشنهادِ قیمت، بوکمارک، سبد/سفارشِ تک‌ونـدوری، جست‌وجو + مقایسهٔ فروشندگان.

**موجِ بعدی (فازِ ۵ به بعد):** ۲۰ قابلیتِ ویژه (flash/groupbuy/loyalty/pricealert →
wayfind/appointment/returns/warranty/giftcard/events/stories →
live/escrow/tracking/vipsub/parking/community/concierge/visualsearch) + wallet/referral.

**آینده:** نقشهٔ گرافیکیِ پاساژ، اپِ ونـدورِ جدا از اپِ خریدار، آنالیتیکسِ ونـدور، featured-listingِ پولی، چند-پاساژ/چندشهری.
