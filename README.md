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
> طبقِ پلن به مدلِ راسته (vendor-scoped، دوحالته، چت، ۲۰ قابلیت) تطبیق داده می‌شوند.
> ناوبری در `core/navigation` مونولیتیک است؛ در فازهایِ بعد به تدریج صفحاتِ راسته جایگزین می‌شوند.

## فازِ ۱ — لایهٔ دادهٔ مارکت‌پلیس (مصرفِ APIهای سرور)

لایهٔ کاملِ network/domain/data برای APIهای فازِ ۱ سرور (راسته×محل + onboarding + تأییدِ ادمین) اضافه شد،
دقیقاً هم‌سبکِ بقیهٔ ماژول‌ها (Api/Impl → DataSource → Repository → AppResult) و ثبت‌شده در Koin:
- `core/network/marketplace`: `MarketplaceApi(+Impl)` + DTOها (Rasteh/Location/City/Shop، CreateShopRequest).
- `core/domain/marketplace`: مدل‌ها (`Rasteh`, `MarketplaceLocation`, `City`, `Shop`) + `MarketplaceRepository`.
- `core/data/marketplace`: `MarketplaceDataSource(+Impl)`، `MarketplaceRepositoryImpl`، مپرها.
- endpointها: `getRastehs`, `getLocationsByRasteh`, `getLocation`, `getCities`, `registerShop`, `getMyShops`, `getShop`, و ادمین `getAdminShops/approve/reject/suspend`.

## فازِ ۱b — رابطِ کاربریِ خانهٔ v2 (ماژولِ `feature/bazaar`)

ماژولِ تازهٔ `feature/bazaar` (پکیجِ `com.kazemieh.bazaar` — نامِ `bazaar` برای پرهیز از تداخلِ
namespace با `composeApp`) رابطِ کاربریِ کشفِ راسته را می‌سازد و لایهٔ دادهٔ فازِ ۱ را مصرف می‌کند:

- **`RastehHomeScreen`** — خانهٔ v2 «search-first»: نوارِ جستجو + گریدِ ۳ستونهٔ راسته‌ها (`getRastehs`).
  هدرِ سنگینِ قدیمی حذف شد؛ این صفحه جایگزینِ محتوایِ تبِ خانه در `MainGraphScreen` شد.
- **`LocationPickerSheet`** — باتم‌شیتِ انتخابِ محل با ضربه روی هر راسته (`getLocationsByRasteh`).
- **`RastehSearchScreen`** — مقصدِ انتخابِ محل؛ فعلاً وضعیتِ «به‌زودی» (endpointِ فهرستِ فروشگاه‌ها
  در فازِ ۲ سرور فعال می‌شود). مسیرِ `Screen.RastehSearch` به `AppNavigation` افزوده شد.
- **`RastehHomeViewModel`** (MVI) `MarketplaceRepository` را مستقیم مصرف می‌کند؛ در `bazaarModule`
  ثبت و در `App.kt` وصل شد. نگاشتِ `iconKey`→(اموجی/رنگ) در `util/RastehVisual`.

## فازِ ۱c — onboardingِ فروشنده + صفِ تأییدِ ادمین (حلقهٔ کاملِ فازِ ۱)

- **`BecomeVendorScreen`** (`becomeVendor`) — فرمِ ثبتِ فروشگاه: نام، انتخابِ راسته و محل
  (dropdownهایِ زنجیره‌ای؛ محل‌ها با انتخابِ راسته بارگذاری می‌شوند)، دسته، طبقه،
  نوع (خرید آنلاین/فقط حضوری)، تلفن، درباره، چت و پذیرشِ پیشنهاد. ثبت → `registerShop`
  → وضعیتِ PENDING و صفحهٔ موفقیت. مسیرِ `Screen.BecomeVendor`؛ ورودی از تبِ حساب برای
  کاربرِ عادیِ واردشده.
- **`AdminShopsScreen`** — صفِ تأییدِ فروشندگان برای ادمین: فیلترِ وضعیت
  (در انتظار/تأییدشده/معلق/ردشده) + اقداماتِ تأیید/رد/تعلیق روی هر فروشگاه
  (`getAdminShops` + `approve/reject/suspend`). تأیید → نقشِ VENDOR در سرور به مالک اعطا
  می‌شود. مسیرِ `Screen.AdminShops`؛ ورودی از تبِ حساب برای ادمین.
- ViewModelها در `bazaarModule` ثبت و از طریق `MainGraphScreen`/`AppNavigation` سیم‌کشی شدند.

با این فاز، حلقهٔ فازِ ۱ کامل است: کاربر درخواستِ فروشندگی می‌دهد → ادمین تأیید می‌کند →
حساب به فروشنده ارتقا می‌یابد.

## فازِ ۲ — کاتالوگ: فهرستِ فروشگاهِ محل + صفحهٔ فروشگاه

لایهٔ داده گسترش یافت (مدلِ `Product`، DTOها، `getShopsByLocation`/`getProductsByShop`/`getProduct`
و CRUDِ کالایِ ونـدور) و صفحاتِ زیر ساخته شدند:
- **`RastehSearchScreen`** حالا فهرستِ **واقعیِ** فروشگاه‌های محل را از `GET /api/locations/{id}/shops`
  می‌گیرد (کارتِ فروشگاه با امتیاز/نوع)؛ انتخابِ فروشگاه → `ShopDetail`.
- **`ShopDetailScreen`** (shopDetail v2): سربرگِ فروشگاه + دکمه‌هایِ **«پیام»/«تماس»** + فهرستِ کالاها
  (`GET /api/products?shopId=`). «تماس» شماره را نشان می‌دهد؛ چتِ «پیام» در فازِ ۳ فعال می‌شود.
- ViewModelهای `RastehSearchViewModel`/`ShopDetailViewModel` در `bazaarModule`؛ مسیرِ `Screen.ShopDetail`.

## فازِ ۳ — چت + پیشنهادِ قیمت + بوکمارک

لایهٔ دادهٔ `interaction` (network/domain/data + Koin) و صفحاتِ زیر:
- **`ChatThreadScreen`** — «پیامِ» `shopDetail` گفت‌وگو را می‌سازد/باز می‌کند؛ حباب‌های پیام
  (خودی/طرفِ مقابل بر اساسِ id پروفایل) + ارسال (polling با refresh).
- **`ChatListScreen`** — فهرستِ گفت‌وگوها (از تبِ حساب).
- **`ShopDetail`** حالا: آیکونِ **نشان‌کردن** (bookmark toggle)، دکمهٔ **«پیشنهادِ قیمت»** (اگر
  `acceptsOffers`) با دیالوگِ مبلغ/توضیح → `POST /api/offers`، و **«پیام»** → گفت‌وگو.
- **`BookmarksScreen`** — نشان‌شده‌ها (از تبِ حساب) با حذف.
- ViewModelها در `bazaarModule`؛ مسیرهای `ChatThread`/`ChatList`/`Bookmarks`؛ ورودی‌ها در تبِ حساب.

## فازِ ۴ — نظرات + جست‌وجو + سفارش

لایهٔ داده گسترش یافت (Review/Order + جست‌وجو) و UI:
- **`ShopDetail`**: بخشِ **نظرات** + دیالوگِ ثبتِ نظر (ستاره + متن) و دکمهٔ **«خرید»** روی کالاهایِ
  قابلِ خرید → سفارشِ تک‌ونـدوری (`POST /api/orders`).
- **`SearchScreen`** (تبِ جست‌وجو): جست‌وجوی فروشگاه از `GET /api/search/shops`.
- **`MyOrdersScreen`** (تبِ حساب): سفارش‌های من با وضعیت و اقلام.
- ViewModelها در `bazaarModule`؛ مسیرِ `MarketOrders`؛ تبِ جست‌وجو به `SearchScreen` تغییر کرد.

با این فاز، **MVP (فاز ۰–۴)** کامل است: کشفِ راسته×محل، onboarding+تأیید، کاتالوگِ دوحالته،
چت/پیشنهاد/بوکمارک، سفارشِ تک‌ونـدوری، جست‌وجو و نظرات.

## فازِ ۵ — قابلیت‌های تجاری (فلش/گروهی/وفاداری/هشدارِ قیمت)

لایهٔ دادهٔ `features` (network/domain/data + Koin) و صفحات:
- **`DealsScreen`** (پیشنهادها و جوایز): کارتِ امتیازِ وفاداری + فلش‌ها + خریدِ گروهی با «پیوستن».
- **`PriceAlertsScreen`**: هشدارهای قیمت با حذف.
- ViewModelها در `bazaarModule`؛ مسیرهای `Deals`/`PriceAlerts`؛ ورودی در تبِ حساب.

## فازِ ۶ — خدمات (کارتِ هدیه)

لایهٔ دادهٔ `services` + **`GiftCardsScreen`** (ساخت با مبالغِ آماده، دریافت با کد، فهرست/موجودی)؛
مسیرِ `GiftCards`؛ ورودی در تبِ حساب. (endpointهای رزروِ بازدید/مرجوعی نیز در لایهٔ داده آماده‌اند.)

## فازِ ۷+۸ — انجمن + اعلان‌ها (+ اشتراک/آنالیتیکس در لایهٔ داده)

لایهٔ دادهٔ `advanced` + صفحات:
- **`CommunityScreen`**: فهرست/ثبتِ پستِ انجمن.
- **`NotificationsScreen`**: اعلان‌ها با علامتِ خوانده‌شدن.
- endpointهای اشتراکِ پلاس و آنالیتیکسِ ونـدور نیز در لایهٔ داده آماده‌اند.
- مسیرهای `Community`/`Notifications`؛ ورودی در تبِ حساب.

**همهٔ فازهای پلن (۰–۸) پیاده شد.** قابلیت‌های نیازمندِ زیرساختِ سنگین
(live/escrow/concierge/visualsearch/parking) طبقِ پلن به‌عنوانِ «آینده» باقی می‌مانند.

## هم‌ترازسازی با پایهٔ جدیدِ فروشگاه (ریسپانسیو + وایت‌لیبل)

اسپکِ مارکت‌پلیس (`design_handoff_v2`) تغییری نکرد؛ این دو ارتقاء از پایهٔ به‌روزِ اپِ فروشگاه آمدند:
- **فازِ R — ریسپانسیو:** `core/designSystem/WindowSize.kt` (WindowSizeClass بدونِ وابستگی)،
  `ProvideWindowSizeClass` در ریشه، گریدِ خانه با `adaptiveGridColumns`، **`SideNavRail`** روی
  نمایشگرهای بزرگ به‌جای نوارِ پایین، و `responsiveMaxWidth` روی صفحاتِ لیست/جزئیات.
- **فازِ WL — وایت‌لیبل/برند:** `core/designSystem/brand/Brand.kt` (`BrandFeatures`/`BrandConfig`/
  `BrandRegistry` + `RastehBrand`)، `ProvideBrand` در ریشه، و گیت‌کردنِ ردیف‌های تبِ حساب پشتِ
  پرچم‌های برند — آماده برای وایت‌لیبلِ چندبرندی در آینده.

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
