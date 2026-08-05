# ممیزی طراحی، تجربه کاربری و Hallmark

## دامنه و محدودیت

این ممیزی بر کد Compose، design system و handoffهای داخل مخزن انجام شده است. اپ به‌صورت runtime روی چهار target و اندازه‌های واقعی screen مشاهده نشد؛ بنابراین یافته‌های visual regression، contrast واقعی font rendering و interaction latency باید در task 11 با screenshot matrix و device testing تأیید شوند.

## نتیجهٔ کلی

پروژه یک design system مرکزی، palette معنایی، dark theme، RTL، typography فارسی و adaptive navigation rail دارد؛ این‌ها پایه‌های ارزشمندند. مشکل اصلی نبود رنگ و component نیست، بلکه information architecture شلوغ، مصرف کم responsive utilities، leftoverهای برند قبلی، accessibility ناقص و الگوهای هم‌وزن کارت/کاشی است.

### ارزیابی سریع

| محور | وضعیت | ریسک |
|---|---|---|
| هویت بصری | زرد | purple/gradient به‌صورت generic و leftover برند Carmilla |
| IA | قرمز | ۲۰ قابلیت هم‌وزن و journey اصلی نامشخص |
| responsive | قرمز | ابزار موجود، مصرف بسیار محدود |
| RTL فارسی | زرد | direction پایه هست، copy و locale واقعی ناقص |
| accessibility | قرمز | null description و touch target زیر 48dp |
| consistency | زرد/قرمز | icon family، hardcoded color/dp و state borders |
| desktop/web | قرمز | mobile canvas کشیده‌شده و bundle/dev artifact |
| feedback/error/recovery | زرد/قرمز | payment state غلط و placeholderها |

## یافته‌های Hallmark

### H1 — داشبورد کاشی‌های هم‌وزن

**Tell:** `FeaturesLauncherScreen.kt:58-79` بیست قابلیت را در یک آرایهٔ تخت نگه می‌دارد و `:102-151` آن‌ها را در grid دو ستونه با وزن بصری برابر نمایش می‌دهد؛ حتی featureهای آزمایشی/به‌زودی کنار مسیرهای حیاتی‌اند.

**Severity:** زیاد.

**اثر:** کاربر نمی‌فهمد اقدام اصلی چیست؛ browse shop/product، خرید و پیگیری سفارش با parking، live، escrow و loyalty رقابت بصری دارند. این همان الگوی «feature showroom» است، نه navigation بر اساس intent.

**Fix:** IA بر اساس journey:

- مشتری: جستجو → مقایسه/ذخیره → مسیر/رزرو/خرید → پیگیری؛
- فروشنده: وضعیت فروشگاه → freshness کاتالوگ → سفارش/لید → performance؛
- ادمین: queue و exception، نه launcher فیچر؛
- موارد experimental در Labs/Coming Soon و خارج از primary nav.

### H2 — mobile canvas روی desktop/web

**Tell:** `core/designSystem/.../WindowSize.kt` helperهای adaptive مفید دارد، ولی خارج از آن تقریباً فقط `MainGraphScreen.kt:133-159` مصرف جدی دیده شد. `FeaturesLauncherScreen.kt:102-107` حتی در viewport بزرگ `GridCells.Fixed(2)` است.

**Severity:** زیاد.

**اثر:** فضای دسکتاپ تلف می‌شود، فرم‌ها بیش از حد کشیده یا باریک‌اند، admin workflow چندمرحله‌ای می‌ماند و navigation/product detail از master-detail بهره نمی‌برد.

**Fix:** الگوی responsive رسمی:

| Window | Navigation | Content |
|---|---|---|
| Compact | bottom bar/single pane | full-width با padding 16 |
| Medium | rail/دو pane اختیاری | max width و grid adaptive |
| Expanded | rail + list/detail | master-detail، side panel و dense tables |

### H3 — token drift و rebrand ناتمام

**Tell:** `Dimens.kt`, `Shape.kt`, `Typography.kt`, componentهایی مثل `CarmillaFilterChip.kt`, `CarmillaBadge.kt`, `StoryRing.kt` و stringهای `Carmilla` هنوز نام برند قبلی را دارند. `Colors.kt` gradient purple را به‌عنوان الگوی اصلی توضیح می‌دهد و چند feature نیز color/dp hardcoded دارند.

**Severity:** متوسط.

**اثر:** تیم دو منبع هویت دارد؛ توسعه‌دهنده نمی‌داند کدام token canonical است و خروجی به template عمومی purple-marketplace نزدیک می‌شود.

**Fix:** namespace و tokenها به Rasteh تغییر کنند، semantic roleها (`brand`, `commerce`, `location`, `warning`) از decorative color جدا شوند و استفاده مستقیم از `Color(...)` در featureها lint شود. بنفش می‌تواند accent بماند، اما gradient نباید جای hierarchy و محتوا را بگیرد.

### H4 — iconography و state polish نامنسجم

**Tell:** `ShopDetailScreen.kt:269-311` resource icon و Material icon را در یک action block ترکیب می‌کند. `CheckoutScreen.kt:338-341,365-368` ضخامت border انتخاب را از 1 به 1.5dp تغییر می‌دهد و layout shift می‌سازد. price/countها tabular figures/alignment ثابت ندارند.

**Severity:** متوسط.

**Fix:** یک icon family و grid سایزبندی، border ثابت با تغییر color/background، alignment عددی و format locale-aware.

### H5 — placeholderها به‌عنوان محصول واقعی

`VisualSearchScreen.kt`, Concierge، Wayfinding و media playerهای چند target وجود UI دارند ولی outcome واقعی کامل ندارند. حضور CTAهای فعال برای این قابلیت‌ها اعتماد را کم می‌کند.

**Severity:** زیاد برای release.

**Fix:** feature flag و status label صادقانه؛ قابلیت ناقص از production navigation حذف شود. «به‌زودی» یک مقصد navigation دائمی نباشد.

## accessibility

اسکن ایستا ۱۱۶ مورد `contentDescription = null` نشان داد. برخی تزئینی‌اند، اما کنترل‌های تعاملی نیز audit نشده‌اند.

نمونه‌های قطعی:

- `AdminBlogListScreen.kt:289-305`: edit/delete حدود 30dp، بدون label/role؛
- `QuantityCounter.kt:43-71`: حالت Small نزدیک 24dp و `Box.clickable` بدون semantics استاندارد button؛
- `CheckoutScreen.kt:331-375`: Row و RadioButton هر دو clickable و semantics تکراری؛
- icon buttonهای متعدد در featureها description ندارند.

معیار هدف:

- touch target حداقل 48dp در Compose؛
- label و role برای actionهای icon-only؛
- stateDescription برای stock, selected, favorite و order status؛
- heading semantics و ترتیب focus درست؛
- contrast طبق WCAG AA برای متن معمولی و stateهای focus/error؛
- keyboard navigation و focus ring در Web/Desktop؛
- screen-reader smoke test Android TalkBack و iOS VoiceOver.

## localization و RTL

۱۳۹ فایل Kotlin literal فارسی دارند. language switch نمی‌تواند UI را واقعاً انگلیسی کند؛ پیاده‌سازی iOS نیز locale resource را کامل تغییر نمی‌دهد.

هدف:

- تمام copy در Compose Resources؛
- plural، currency، date/time و number locale-aware؛
- تست screenshot فارسی RTL و انگلیسی LTR؛
- عدم استفاده از arrow/icon جهت‌دار بدون mirroring؛
- پشتیبانی search از `ی/ي`, `ک/ك`, نیم‌فاصله و رقم فارسی/لاتین.

## معماری اطلاعات پیشنهادی

### مشتری

1. Home: انتخاب شهر/خوشه، search اصلی، دسته‌های اصلی، «نزدیک و موجود»؛
2. Search Results: toggle فروشگاه/کالا، map/list، freshness و explainability؛
3. Shop: هویت، تأییدها، ساعات، مسیر، ویترین/محصول، review، CTA متناسب با mode؛
4. Product: variant، offerهای فروشگاه، distance/freshness، reserve/buy؛
5. Saved: فروشگاه‌ها، محصولات، alerts؛
6. Activity: reservation/order/quote/message timeline؛
7. Profile: privacy، addresses، notification و account deletion.

### فروشنده

1. Today: موارد نیازمند اقدام؛
2. Catalog: freshness queue، bulk edit، media؛
3. Leads & Orders: quote/reservation/order با SLA؛
4. Store: profile، hours، location، team؛
5. Insights: search demand، outcomes، seller health؛
6. Billing/Settlement فقط برای mode تراکنشی.

### بازاریاب

1. assigned cluster/map؛
2. leads و visit plan؛
3. draft shop capture با offline queue؛
4. evidence/photo/consent؛
5. claim handoff و status؛
6. quality/activation metrics، نه دسترسی مالی.

### ادمین

صفحهٔ اصلی exception queue باشد: shop verification، duplicate، report، payment/reconciliation anomaly، stale inventory و account appeal. dashboard متریک بدون action نباید مرکز IA باشد.

## screen-level redesign priorities

| اولویت | صفحه | تغییر |
|---|---|---|
| P0 | Payment completion | نمایش state واقعی server، retry/recovery و عدم پاک‌کردن cart روی failure |
| P0 | Checkout | CTA idempotent، breakdown پایدار، policy/refund و loading guard |
| P1 | Home/Search | search-first، cluster context، freshness و no-result recovery |
| P1 | Shop/Product | سه mode واضح، verified badges، distance و lastConfirmedAt |
| P1 | Vendor | action queue و bulk freshness به‌جای tabهای صرفاً نمایشی |
| P1 | Agent/Admin | maker-checker، audit timeline و exception workflow |
| P2 | Desktop/Web | master-detail، keyboard، URL/SEO و density مناسب |
| P2 | Profile/Settings | privacy controls، export/delete و language واقعی |

## design system target

### token hierarchy

- primitive: color, type, spacing, radius, elevation؛
- semantic: surface, content, border, brand, success, warning, destructive, location؛
- component: button, field, card, badge, navigation؛
- mode/state: default, hover, focus, pressed, disabled, selected, error.

### component contract

هر component باید:

- semantics و minimum target داخلی داشته باشد؛
- RTL/LTR و dark/light را پشتیبانی کند؛
- حالت loading/error/disabled را تعریف کند؛
- hardcoded copy نداشته باشد؛
- screenshot/golden test در compact و expanded داشته باشد.

## معیار اتمام طراحی

- هیچ production route به placeholder ختم نشود؛
- critical journeys در compact/medium/expanded تست شوند؛
- تمام actionهای icon-only label داشته باشند؛
- touch targets و contrast pass؛
- keyboard traversal و focus visible در Web/Desktop؛
- فارسی و انگلیسی screenshot baseline؛
- design token lint و zero raw color برای featureهای مهاجرت‌داده‌شده؛
- UX review حضوری با حداقل ۵ مشتری، ۵ فروشنده و ۲ بازاریاب در pilot cluster.

