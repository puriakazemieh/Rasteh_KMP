# پلنِ ساخت — Rasteh_KMP (کلاینت)

> مارکت‌پلیسِ محلی برایِ پاساژ/راسته: چند فروشگاهِ مستقل زیرِ یک چتر، هرکدوم با پروفایلِ خودش،
> که می‌توانند محصولاتشان را به‌صورتِ «فقط نمایشی» (Showcase) یا «کامل قابلِ‌خرید» (Commerce) عرضه کنند.

نسخه‌یِ هم‌سویِ سرور: `RASTEH_SERVER_PLAN.md` در ریپویِ `Rasteh_Kotlin_Spring_Boot`. این سند
خروجیِ تحلیلِ اورلپ با پروژه‌ی خواهرِ تک‌مستأجری (`shop-kotlin-kmp`) است.

---

## ۱) تصمیمِ تکنولوژی (فرق با پیشنهادِ اولیه‌یِ کاربر)

پیشنهادِ اولیه Android-only با Jetpack Compose بود. به‌جایِ آن **Kotlin Multiplatform + Compose
Multiplatform** را ادامه می‌دهیم (همان استکِ `shop-kotlin-kmp`) چون:
- کدِ UI/دیتا/دامین بینِ اندروید، iOS، و دسکتاپ به‌اشتراک می‌رود (Vendorها احتمالاً از دسکتاپ/تبلت هم مدیریت می‌کنند).
- حجمِ زیادی از ماژول‌هایِ core (network/data/designSystem/navigation) مستقیماً کپی‌شدنی‌اند.

استک: Kotlin Multiplatform + Compose Multiplatform، Koin DI، Ktor Client، Clean Architecture
لایه‌ایِ per-module (`network` → `domain` → `data` → `feature`).

---

## ۲) نقش‌ها و اپ‌هایِ خروجی

سه چهره برایِ سه نقش، شبیهِ الگویِ white-label فعلی (یک codebase، چند فلیورِ build):

| اپ/فلیور | مخاطب | ماژول‌هایِ فعال |
|---|---|---|
| Rasteh Customer | خریدار | catalog, search, cart(اختیاری), vendor-profile, mall-map |
| Rasteh Vendor | صاحبِ فروشگاه | vendor-onboarding, vendor-catalog-admin, vendor-orders |
| Rasteh Admin (یا وب‌پنل جدا) | ادمینِ پاساژ | admin-vendor-approval, admin-mall, admin-reports |

برایِ MVP پیشنهاد می‌شود Customer و Vendor یک اپِ واحد باشند (کاربر با نقشش تشخیص داده می‌شود و
UIِ متفاوت می‌بیند)، و پنلِ ادمین جدا (یا حتی صرفاً وب، مثلِ الگویِ فعلیِ ادمین در پروژه‌ی فروشگاه).

---

## ۳) ماژول‌بندی

### بدون‌تغییر — کپیِ مستقیم از `shop-kotlin-kmp`

```
core/network        -- Ktor client, interceptors, DTO پایه
core/data           -- پیاده‌سازیِ ریپازیتوری‌ها، الگویِ AppResult
core/designSystem   -- تم، تایپوگرافی، کامپوننت‌هایِ پایه
core/navigation     -- الگویِ Screen sealed class + NavHost
core/common         -- AppResult, وضعیت‌هایِ عمومی
feature/auth        -- ثبت‌نام/ورود (نقشِ Vendor به‌عنوانِ یک enum اضافه می‌شود)
feature/cart        -- با محدودیتِ تک‌ونـدوری (فازِ ۳ سرور را ببین)
feature/orders      -- تاریخچه/وضعیتِ سفارش، بدون تغییرِ ساختاری
```

### اقتباسی — کپی + تغییر

```
feature/catalog     -- کارتِ محصول باید نشان بدهد Showcase است یا Commerce
                       (دکمه‌ی «تماس با فروشگاه» / «مسیریابی» به‌جایِ «افزودن به سبد» وقتی Showcase است)
feature/details     -- صفحه‌ی جزئیاتِ محصول: شرطیِ purchasable/showcase از الگویِ فعلیِ
                       DetailsScreen.kt (که همین حالا هم رشته‌ی فنی/attributes جنریک دارد) ادامه پیدا می‌کند
feature/admin/*     -- الگویِ AdminXxxScreen.kt/ViewModel/Module از پروژه‌ی فروشگاه، ولی همه‌جا
                       اسکوپ‌شده به vendorId فعلی (نه دیدِ سراسری)
```

### کاملاً جدید

```
feature/vendor/onboarding   -- فرمِ ثبتِ فروشگاه: نام، لوگو/کاور (همان الگویِ «افزودن با لینک»
                               یا آپلودِ ساده مثلِ AdminProductImageController)، آدرس+پلاک/طبقه،
                               دسته‌بندی، تلفن، ساعتِ کاری، توضیح
feature/vendor/profile      -- پروفایلِ عمومیِ فروشگاه (نمایِ کاربر) — شبیهِ الگویِ
                               "public instructor profile" که در فازِ P پروژه‌ی آکادمی ساختیم
feature/vendor/category-mode-- سوییچِ Showcase/Commerce per دسته‌ی محصول در پنلِ ونـدور
feature/search              -- کپیِ Phase-B search پروژه‌ی فروشگاه + فیلترِ طبقه/دسته/بازه‌قیمت
feature/mall-map            -- فهرست/نقشه‌ی طبقاتِ پاساژ برایِ مسیریابیِ حضوری (MVP: فهرستِ ساده‌ی
                               «طبقه ۱: ۱۲ فروشگاه»؛ نسخه‌ی بعدی: نقشه‌ی گرافیکی با مختصات)
feature/admin/vendor-approval -- صفِ تاییدِ فروشگاه‌هایِ جدید برایِ ادمینِ پاساژ
```

---

## ۴) فازبندی

### فازِ ۰ — اسکلت
- کپیِ `core/*` کامل از `shop-kotlin-kmp`، تغییرِ نام‌پکیج به `com.kazemieh.rasteh`
- کپیِ `feature/auth` + اضافه‌کردنِ enum نقشِ `VENDOR`
- تنظیمِ `settings.gradle.kts` + `composeApp/build.gradle.kts` مطابقِ ماژول‌هایِ بالا

### فازِ ۱ — Vendor onboarding + پروفایل
- `feature/vendor/onboarding`: فرمِ ثبت + وضعیتِ pending (نمایشِ «در انتظارِ تاییدِ ادمین»)
- `feature/vendor/profile`: نمایشِ عمومیِ پروفایلِ فروشگاه برایِ خریدار (آدرس، ساعتِ کاری، دکمه‌ی تماس/مسیریابی)
- `feature/admin/vendor-approval`: لیستِ درخواست‌ها + approve/reject

### فازِ ۲ — کاتالوگِ دوحالته
- `feature/catalog` + `feature/details`: شرطِ Showcase/Commerce رویِ نمایشِ قیمت/دکمه‌ی خرید
- `feature/admin/vendor-catalog`: مدیریتِ محصولِ Vendor (کپیِ الگویِ ManageProductViewModel با اسکوپِ vendorId)
- `feature/vendor/category-mode`: سوییچِ per-دسته

### فازِ ۳ — سبد/سفارشِ تک‌ونـدوری
- `feature/cart`: پیامِ هشدار هنگامِ افزودنِ آیتم از ونـدورِ دیگر
- `feature/orders`: بدون تغییرِ اساسی؛ نمایشِ نامِ ونـدور رویِ هر سفارش
- `feature/admin/vendor-orders`: سفارش‌هایِ مخصوصِ همان ونـدور

### فازِ ۴ — جست‌وجو/کشف
- `feature/search`: فیلترِ طبقه/دسته/بازه‌قیمت + فهرستِ «پرطرفدارترین فروشگاه‌ها»
- `feature/mall-map`: فهرستِ طبقات (نسخه‌ی ساده)

### فازِ ۵ — صیقل
- ریویو/پرسش‌وپاسخ رویِ محصول (کپیِ الگویِ موجود)
- Notification: تاییدِ فروشگاه، سفارشِ جدید
- گزارشِ فروشِ Vendor در پنلِ خودش

---

## ۵) جدولِ «چه‌فایلی از کجا کپی شود»

| مقصد در Rasteh_KMP | منبع در shop-kotlin-kmp | تغییرِ لازم |
|---|---|---|
| `core/network`, `core/data`, `core/designSystem`, `core/navigation`, `core/common` | همنام | تغییرِ پکیج فقط |
| `feature/auth` | همنام | `+VENDOR` role |
| `feature/cart` | همنام | چکِ تک‌ونـدوری |
| `feature/orders` | همنام | نمایشِ نامِ ونـدور |
| `feature/catalog`, `feature/details` | همنام | شرطِ Showcase/Commerce |
| `feature/search` | همنام (Phase B) | فیلترِ طبقه/مال |
| کامپوننتِ جدولِ اسپک/attributes | از `DetailsScreen.kt`/`ComparisonScreen.kt` | بدون تغییر، فقط الگو |
| `feature/vendor/profile` | الگو از "public instructor profile" (فازِ P آکادمی) | ساختارِ مشابه، دیتایِ متفاوت |
| `feature/vendor/onboarding`, `feature/admin/vendor-approval` | ندارد | جدید |
| `feature/mall-map` | ندارد | جدید |

---

## ۶) قراردادهایِ ثابت (ادامه از پروژه‌یِ فروشگاه)

- هر ماژولِ Gradleِ جدید → include در `settings.gradle.kts` **و** صریح در `composeApp/build.gradle.kts`.
- الگویِ AppResult/ViewModel/Effect دقیقاً مثلِ پروژه‌ی فروشگاه (state/effect/Channel).
- تصویر/لوگو: همان الگویِ «افزودن با لینک» مگر اینکه آپلودِ مستقیم لازم شود (تصمیمِ فازِ ۱).
- برایِ تستِ محلی: کاربرِ ابری قادر به build نیست (۴۰۳ از Gradle distribution)؛ کاربر باید رویِ دستگاهِ خودش `./gradlew` بزند.

---

## ۷) MVP در برابرِ بعدی

**MVP (فازِ ۰ تا ۴):** ثبتِ ونـدور با تایید، پروفایلِ عمومی، کاتالوگِ دوحالته، سبد/سفارشِ تک‌ونـدوری، جست‌وجو با فیلترِ طبقه.

**بعدی:** نقشه‌ی گرافیکیِ پاساژ، اپِ ونـدور جدا از اپِ خریدار، آنالیتیکسِ ونـدور، featured-listing پولی، پشتیبانیِ چند-پاساژ/چندشهر.
