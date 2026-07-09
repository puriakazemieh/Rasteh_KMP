# Rasteh — کلاینت (Kotlin Multiplatform + Compose Multiplatform)

اپِ یکپارچهٔ مارکت‌پلیسِ محلیِ پاساژ/راسته (خریدار / فروشنده / ادمین) برای اندروید، iOS، دسکتاپ (JVM) و وب.
نقشهٔ راه در [`RASTEH_KMP_PLAN.md`](./RASTEH_KMP_PLAN.md) و مرجعِ طراحی در پوشهٔ
[`design_handoff_unified_app/`](./design_handoff_unified_app) است.

## وضعیت: فازِ ۰ (اسکلت + دیزاین‌سیستم + auth)

این فاز پایهٔ اثبات‌شدهٔ `shop-kotlin-kmp` را به‌عنوانِ اسکلتِ بازاستفاده می‌گیرد و تغییرهایِ برندینگِ راسته را اعمال می‌کند:

- **هویتِ اپ**: پکیجِ `composeApp` از `com.kazemieh.shop` → `com.kazemieh.rasteh`؛ نامِ اپ «راسته»؛ `rootProject.name = "rasteh"`.
- **دیزاین‌سیستم** (`core/designSystem/Colors.kt`): پالتِ رنگ به **بنفشِ برندِ راسته** (oklch تبدیل‌شده به sRGB) مطابقِ `design_handoff_unified_app/README.md` — با حفظِ نامِ توکن‌ها برای سازگاری.
- **RTL/فارسی**: زبانِ پیش‌فرض به **فارسی (RTL)**؛ helperِ `faDigits` در `core/common/util/FaDigits.kt` برای نمایشِ ارقامِ فارسی و قیمت.
- **auth**: صفحاتِ ورود/ثبت‌نام رِبرند شد؛ سوییچِ **«می‌خواهم فروشنده شوم»** به صفحهٔ ثبت‌نام افزوده شد
  (پرچمِ فازِ ۰؛ نقشِ VENDOR فقط پس از onboarding و تأییدِ ادمین در فازِ ۱ اعطا می‌شود). نقش‌ها در `core/common/Roles.kt`.

> ماژول‌هایِ دیگرِ (catalog/cart/orders/details/admin/blog…) از پایهٔ shop همراه آمده‌اند و در فازهایِ بعد
> طبقِ پلن به مدلِ راسته (vendor-scoped، دوحالته، دنبال‌کردن، چت، ۲۰ قابلیت) تطبیق داده می‌شوند.
> ناوبری در `core/navigation` مونولیتیک است؛ در فازهایِ بعد به تدریج صفحاتِ راسته جایگزین می‌شوند.

## ساختار ماژول‌ها
```
core/common         AppResult، Roles، faDigits، Screen، EventBusها
core/network        Ktor client، DTOها
core/data           ریپازیتوری‌ها، ذخیرهٔ محلی
core/domain         مدل‌ها و UseCaseها
core/designSystem   تم، توکن‌های رنگ/تایپوگرافی، کامپوننت‌های پایه (رِبرندِ راسته)
core/navigation     NavHost + Screen graph
feature/auth        ورود/ثبت‌نام (+ سوییچِ فروشنده‌شدن)
feature/*           سایر فیچرها (پایهٔ بازاستفاده برای فازهای بعد)
composeApp          نقطهٔ ورودِ اندروید/iOS/JVM/Web
```

## اجرا (روی دستگاهِ محلی)

> توجه: در محیطِ ابری امکانِ build نیست (۴۰۳ هنگام دانلودِ توزیعِ Gradle). روی دستگاهِ خودتان اجرا کنید.

```bash
./gradlew :composeApp:assembleDebug         # اندروید
./gradlew :composeApp:run                    # دسکتاپ (JVM)
./gradlew :composeApp:jsBrowserDevelopmentRun # وب
```
اجرای iOS از طریقِ پوشهٔ [`iosApp`](./iosApp) در Xcode.
