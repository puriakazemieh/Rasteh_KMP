# محدودهٔ مصوب MVP راسته

- Status: Accepted
- Date: 2026-08-05
- Decision owner: مالک محصول
- Source of truth: `plans/README.md` و ADRهای `Rasteh_Kotlin_Spring_Boot/docs/adr/0001` تا `0005`

## تصمیم محصول

MVP راسته یک «فهرست محلی + ویترین + رزرو/استعلام» است. پلتفرم معرف و تسهیل‌گر ارتباط/رزرو است و نه فروشنده، نه نگه‌دارندهٔ وجه و نه تسویه‌کننده. مدل canonical شامل `MerchantOrganization`، `ShopLocation`، `Product`، `Variant`، `ShopOffer`، `InventoryPosition` و در آینده `Order` است. Offer فقط `SHOWCASE` یا `RESERVABLE` را در MVP فعال می‌کند؛ `BUYABLE` قراردادی است ولی disabled-by-default باقی می‌ماند.

## قراردادهای غیرقابل مذاکره

- پول: `Money(minorUnits: Long, currency: "IRR")`؛ تومان فقط نمایش است.
- هر endpoint حساس، authorization و scope مالکیت/عضویت server-side دارد.
- هر create/order/payment/refund/settlement آینده idempotency key دارد.
- client فقط پس از خواندن state سرور نتیجهٔ order/payment را نشان می‌دهد؛ deep link منبع موفقیت نیست.
- Android و Web launch target هستند؛ iOS و Desktop experimental هستند.

## ماتریس وضعیت feature

| Feature | Production | Pilot | Experimental | Disabled | Remove |
|---|---|---|---|---|---|
| discovery محلی، shop profile، showcase |  | ✓ |  |  |  |
| quote/reservation با lifecycle و audit |  | ✓ |  |  |  |
| onboarding/claim فروشگاه |  | ✓ پس از maker-checker |  |  |  |
| catalog، variant، shop offer، freshness |  | ✓ |  |  |  |
| search فارسی/geo و SEO وب |  | ✓ پس از validation |  |  |  |
| save/bookmark و report |  | ✓ |  |  |  |
| Android و Web |  | ✓ پس از release gates |  |  |  |
| iOS و Desktop |  |  | ✓ |  |  |
| transactional checkout، PSP، refund، settlement |  |  |  | ✓ |  |
| wallet، gift card، escrow، subscription پولی |  |  |  | ✓ |  |
| flash sale، group buy، loyalty، price alert |  |  | ✓ فقط بدون تعهد مالی | ✓ در صورت تعهد مالی |  |
| live commerce، visual search، concierge، parking |  |  | ✓ تحقیقاتی |  |  |
| cart چندفروشگاهی، BNPL، marketplace delivery سراسری |  |  |  | ✓ |  |
| global `VENDOR` role و دو commerce stack |  |  |  |  | ✓ پس از migration |

هیچ ردیف Experimental یا Disabled به‌عنوان قابلیت تکمیل‌شدهٔ MVP معرفی نمی‌شود. status `Production` تنها بعد از تکمیل Task 14 و evidence انتشار به‌روز می‌شود.

## محدودهٔ پایلوت و عملیات

پایلوت مصوب در **تهران، محدودهٔ تجاری لاله‌زار** اجرا می‌شود. دستهٔ نخست **الکتریکی** است و دستهٔ دوم برای این چرخه تعریف نشده است؛ افزودن آن نیازمند تصمیم جدید مالک محصول است. محدودهٔ عملیات، کل محدودهٔ تجاری لاله‌زار است و مسئولیت‌های محصول، عملیات، انتشار Android/Web و حقوقی/مالی به نقش‌های مالک تعیین‌شدهٔ پروژه واگذار شده‌اند؛ نام اشخاص در VCS نگهداری نمی‌شود.

providerهای map، SMS، push، storage و analytics از adapter استفاده می‌کنند. در MVP، fallbackها به‌ترتیب نمایش آدرس/مسیریابی بدون نقشه، صف/ثبت درخواست بدون ادعای ارسال SMS، اعلان درون‌برنامه‌ای، رد امن upload جدید، و عدم ارسال PII به analytics هستند. انتخاب provider واقعی، SLA، credential، sandbox و owner عملیاتی در Task 14 انجام می‌شود. پرداخت همچنان disabled است.

## تغییر محدوده

هر تغییر در نقش حقوقی، پرداخت، پول، role، provider یا انتشار نیازمند ADR جدید/اصلاح ADR و مرور Taskهای وابسته است. فعال‌سازی `BUYABLE` نیازمند شریک پرداخت مجاز، policy refund/dispute، تأیید حقوقی و تکمیل کنترل‌های امنیتی/مالی است.
