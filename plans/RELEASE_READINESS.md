# برنامهٔ جامع آمادگی انتشار

## حکم فعلی

**NO-GO برای انتشار عمومی و دریافت پول واقعی.**

کلاینت JVM/JS کامپایل و تست‌های موجود سرور سبز هستند، اما این شواهد برای production کافی نیستند. P0های امنیت، پرداخت، wallet، ownership، migration و session می‌توانند به افشای داده، دستکاری سفارش یا خطای مالی منجر شوند.

## گیت‌های اجباری

| Gate | وضعیت فعلی | شرط GO |
|---|---|---|
| G1 Security stop-ship | رد | TLS امن، seed/secrets، IDOR/admin policy، token/log hardening |
| G2 Financial correctness | رد | Money دقیق، idempotency، amount صحیح، ledger/refund/reconciliation |
| G3 Data migration | رد | Flyway baseline، `ddl-auto=validate`، backup/restore rehearsal |
| G4 Test evidence | رد | integration/security/concurrency/E2E و CI required |
| G5 Product coherence | رد | یک commerce domain و سه listing mode |
| G6 Privacy/legal | رد | policy، consent، account deletion، seller/return/payment sign-off |
| G7 Operations | رد | health/SLO/alerts/on-call/runbook/DR |
| G8 Platform artifacts | رد | release signing و store/build validation برای هر target |
| G9 Pilot readiness | رد | micro-cluster، support team، analytics و rollback |

هیچ تصمیم مدیریتی نباید G1 تا G4 را waive کند. G5 تا G9 فقط با risk acceptance مکتوب و دامنهٔ pilot بسته قابل تعدیل‌اند.

## 1. اقدامات stop-ship

### سرور

- حذف trust-all TLS و hostname verifier در `RastehApplication.kt`؛
- production fail-fast برای JWT/payment/SMS/database secrets؛
- seed فقط در `dev/test` و rotate هر credential در معرض ریسک؛
- deny-by-default برای admin و owner/shop scoped access برای order tracking/shipping؛
- جلوگیری از amount منفی و ساخت ledger/lock برای wallet؛
- permit دقیق callback درگاه، verify server-side و idempotency/unique constraint؛
- charge فقط `gatewayPaidAmount`، نه total پس از استفاده از wallet؛
- validation quantity، inventory lock/reservation و order transition table؛
- توقف/feature flag endpointهای مالی نمایشی مثل gift card/escrow/subscription تا تکمیل.

### کلاینت

- payment completion فقط از وضعیت server و orderId نتیجه بگیرد؛
- cart فقط پس از `PAID/CONFIRMED` پاک شود؛
- checkout in-flight guard + idempotency key؛
- logging body/header حساس در release خاموش و redacted شود؛
- secure token/session storage؛
- URL و flavorهای dev/stage/prod یکپارچه و HTTPS-only؛
- `CancellationException` دوباره throw شود؛
- route guard capability-aware؛ امنیت نهایی در server.

## 2. بک‌اند و زیرساخت تولید

### محیط‌ها

حداقل سه محیط مستقل:

| محیط | داده | درگاه/SMS | دسترسی | هدف |
|---|---|---|---|---|
| dev | synthetic | sandbox/fake | تیم توسعه | توسعه سریع |
| staging | anonymized/synthetic | sandbox واقعی | VPN/allowlist | rehearsal و E2E |
| production | واقعی | provider production | least privilege | سرویس عمومی |

config schema مستند و validation startup داشته باشد. هیچ fallback حساس در production قابل‌قبول نیست. CORS allowlist دقیق دامنه‌های Web/ops، credentials policy سازگار و CSRF متناسب با نوع session لازم است.

### دیتابیس

- Flyway baseline از schema موجود؛
- migrationهای forward-only با checksum؛
- `spring.jpa.hibernate.ddl-auto=validate` در stage/prod؛
- FK/CHECK/UNIQUE برای amount، quantity، status، idempotency و authority؛
- PostgreSQL production با encryption، PITR و backup؛
- restore drill و زمان واقعی RPO/RTO؛
- migration rehearsal روی clone staging؛
- connection pool، slow query log، index و query budget.

### media

- object storage و CDN به‌جای filesystem container؛
- random object key، original name فقط metadata؛
- size/count limit؛
- magic-byte/MIME allowlist؛
- image re-encode و metadata stripping؛
- AV scan برای فایل‌های مجاز؛
- signed upload/download در دادهٔ خصوصی؛
- lifecycle و orphan cleanup.

### runtime/container

- image چندمرحله‌ای، base image pinned و non-root؛
- health/readiness؛
- graceful shutdown؛
- CPU/memory limit و JVM sizing؛
- SBOM، dependency/vulnerability scan و image signing؛
- rollout canary/blue-green یا rollback اثبات‌شده؛
- scheduled job با distributed lock.

## 3. امنیت و privacy

### session

- Android: Keystore-backed encrypted storage؛
- iOS: Keychain با accessibility مناسب؛
- Desktop: OS credential vault؛
- Web: ترجیحاً HttpOnly + Secure + SameSite cookie با BFF؛ access token حافظه‌ای اگر BFF ممکن نیست؛
- refresh token rotation، reuse detection و revocation؛
- logout تمام cache/profile user-specific را پاک کند؛
- reset password همه sessionها را revoke کند.

### API/security controls

- rate limit برای OTP/login/reset/search/report/upload/payment؛
- CSPRNG و hashed OTP با attempt/cooldown؛
- uniform account recovery responses؛
- security headers، CSP برای Web، HSTS و secure cookies؛
- authorization matrix integration test؛
- dependency/SAST/secret scan؛
- threat model برای auth، payment، upload، agent fraud و review abuse؛
- incident response و key rotation drill.

### privacy lifecycle

- data inventory: چه داده‌ای، چرا، کجا، توسط چه نقشی و تا چه زمان؛
- consent ledger و نسخهٔ policy؛
- data access/export/correction/delete؛
- retention matrix برای account، order، chat، location، document و logs؛
- deletion با legal hold تفکیک‌شده؛
- encryption برای PII/KYB documents؛
- location تقریبی default و precise فقط هنگام نیاز/رضایت؛
- public badge به‌جای نمایش مدرک خام؛
- vendor/agent access logs و immutable admin audit.

## 4. آزمون و CI/CD

### حداقل هرم تست سرور

- unit: Money، pricing، discount، transition و policy؛
- repository integration با PostgreSQL/Testcontainers؛
- security matrix برای anonymous/user/member/admin/superadmin/agent؛
- concurrency: inventory reservation، wallet، payment callback؛
- idempotency replay؛
- migration from baseline + rollback/roll-forward rehearsal؛
- provider contract برای payment/SMS/object storage؛
- API contract/OpenAPI compatibility؛
- smoke E2E برای browse → reserve و buy single-shop.

### حداقل تست KMP

- domain/data mapper و Persian normalization؛
- session refresh/logout/cancellation؛
- checkout/payment state machine؛
- ViewModel intent/effect tests؛
- Ktor MockEngine integration؛
- screenshot/golden compact/expanded، fa/en و light/dark؛
- platform adapter tests؛
- smoke روی Android، Web، Desktop و iOS simulator.

### required checks

```powershell
# Client
.\gradlew.bat --no-daemon check
.\gradlew.bat --no-daemon :composeApp:lintRelease
.\gradlew.bat --no-daemon :composeApp:bundleRelease
.\gradlew.bat --no-daemon :composeApp:jsBrowserProductionExecutableDistribution
.\gradlew.bat --no-daemon :composeApp:packageDistributionForCurrentOS

# Server
.\gradlew.bat --no-daemon --stacktrace test
.\gradlew.bat --no-daemon --stacktrace bootJar
```

iOS روی macOS باید framework release و `xcodebuild archive` واقعی داشته باشد. exact taskها پس از تنظیم signing در CI تثبیت شوند.

CI روی pull request و branch اصلی اجرا شود؛ `continue-on-error` و artifact development برای release ممنوع است. coverage gate برای فایل‌های حساس، نه فقط میانگین کل repository، تعریف شود.

## 5. Android

### فنی

- applicationId نهایی، versionCode/versionName اتوماتیک؛
- release signing در secret store؛
- AAB release، R8/minification/resource shrinking و mapping upload؛
- HTTPS-only production و network security فقط debug؛
- baseline profile/startup performance؛
- permission minimization، خصوصاً location/camera/notification؛
- deep link/app link verified و payment recovery؛
- crash/ANR monitoring و staged rollout؛
- 64-bit و device compatibility testing.

پروژه اکنون targetSdk 36 دارد. سیاست جاری Google Play می‌گوید app جدید/update باید حداکثر یک سال از API اصلی جدید عقب باشد و برای deadline دقیق باید منبع رسمی چک شود؛ target 36 با الزام اعلام‌شدهٔ Android 16 در 2026 هم‌راستا است، ولی Play Console در روز انتشار مرجع نهایی است. منابع: [Target API policy](https://support.google.com/googleplay/android-developer/answer/16561298?hl=en)، [timeline رسمی](https://support.google.com/googleplay/android-developer/answer/11926878?hl=en).

### Play/App stores

- privacy policy عمومی و داخل app؛
- Data Safety دقیق برای داده‌های خود app و SDKها؛
- account deletion داخل app و web link؛
- content rating، target audience و ads declaration؛
- store listing فارسی/انگلیسی، screenshot/tablet assets؛
- closed testing و staged production؛
- test account بدون credential ثابت commit‌شده؛
- verification کانال‌های Cafe Bazaar/Myket نیز جدا و با SDK/policy روز انتشار انجام شود.

Google Play برای همه appهای منتشرشده Data Safety می‌خواهد و حتی app بدون جمع‌آوری داده privacy policy لازم دارد. app دارای account creation باید مسیر حذف account ارائه دهد. منابع: [Data Safety](https://support.google.com/googleplay/android-developer/answer/10787469?hl=en)، [Account deletion](https://support.google.com/googleplay/android-developer/answer/13327111?hl=en-EN).

## 6. iOS

### فنی

- base URL production HTTPS و ATS بدون exception گسترده؛
- Keychain session؛
- universal links و callback قابل‌بازیابی پس از kill؛
- privacy manifest و Required Reason APIs؛
- permission usage descriptions فارسی/انگلیسی؛
- release framework و archive روی macOS؛
- Bundle ID، signing certificate، provisioning و entitlements؛
- App Store Connect/TestFlight metadata؛
- crash reporting/dSYM upload؛
- iPhone/iPad support matrix؛
- media player و platform adapterهای واقعاً پیاده‌شده.

### review

- app نباید placeholder، broken link یا feature demo گمراه‌کننده داشته باشد؛
- review notes، test account و راه تست payment/reservation؛
- privacy details و account deletion؛
- physical-goods payment flow با guidance روز Apple بررسی شود؛
- eligibility حساب developer و توزیع برای کاربران هدف ایران پیش از سرمایه‌گذاری انتشار قطعی شود.

مرجع نهایی: [Apple App Review Guidelines](https://developer.apple.com/app-store/review/guidelines/). وضعیت کشور، قرارداد developer و مسیرهای توزیع می‌تواند تغییر کند و باید در روز تصمیم حقوقی/انتشار دوباره بررسی شود.

## 7. Web

کامپایل JS کافی نیست. artifact فعلی development و به‌دلیل OOM production build کنار گذاشته شده است.

الزامات:

- production minified/hashed bundle و bundle budget؛
- tree shaking/code splitting/lazy feature loading؛
- SSR یا prerender برای صفحات city/cluster/shop/product؛
- canonical URL، sitemap، robots، structured data و noindex policy؛
- CSP، HSTS، secure cookie، CSRF در صورت cookie auth؛
- XSS review، به‌خصوص rich content/blog/chat/upload؛
- responsive compact/medium/expanded؛
- keyboard/focus/screen reader؛
- PWA manifest/service worker با cache policy و update UX؛
- payment redirect recovery و refresh-safe navigation؛
- Web Vitals و RUM؛
- privacy/cookie disclosure متناسب با analytics واقعی؛
- CDN/cache invalidation و rollback.

هدف اولیه performance بعد از اندازه‌گیری: LCP زیر 2.5s در p75 mobile، CLS زیر 0.1 و INP زیر 200ms؛ این‌ها باید با RUM واقعی ارزیابی شوند.

## 8. Desktop

- package identity/versioning؛
- Windows code signing و installer reputation؛
- macOS signing/notarization اگر DMG ارائه می‌شود؛
- Linux package metadata؛
- secure credential store؛
- auto-update با signed manifest و rollback؛
- proxy/certificate/file-system behavior؛
- keyboard shortcuts، menu، window resizing و high-DPI؛
- crash dump privacy و upload consent؛
- release channel stable/beta.

اگر ارزش مستقل desktop در pilot اثبات نشده، انتشار عمومی آن را عقب بیندازید؛ maintain کردن installer/update/security برای target بدون کاربر هزینه دائمی است.

## 9. حقوقی، مالی و پشتیبانی

این بخش چک‌لیست مهندسی است و جای مشاورهٔ حقوقی/مالیاتی نیست.

پیش از transaction:

- تعیین نقش حقوقی: معرفی‌کننده، reservation intermediary یا marketplace؛
- Terms of Use مصرف‌کننده؛
- Seller Agreement و acceptable use؛
- privacy policy و consent version؛
- سیاست لغو، مرجوعی، refund، dispute و prohibited goods؛
- نمایش هویت فروشنده، مشخصات کالا، هزینه کامل و زمان تحویل؛
- durable receipt و snapshot شرایط معامله؛
- قرارداد PSP/پرداخت‌یار و reconciliation/settlement؛
- بررسی اینماد، مجوزها، مالیات/صورتحساب و مسئولیت هر طرف؛
- moderation، appeal و law-enforcement request policy؛
- SLA پشتیبانی و escalation مالی.

منابع برای بازبینی تخصصی: [قانون تجارت الکترونیکی ایران](https://www.wipo.int/wipolex/en/legislation/details/7711)، [دستورالعمل حریم خصوصی کاربران](https://nezamat.ir/post-44947/)، [آیین‌نامه مقابله با پولشویی](https://nezamat.ir/post-41584/)، [قانون پایانه‌های فروشگاهی و سامانه مؤدیان](https://nezamat.ir/post-41585/).

## 10. observability و SRE

### telemetry

- structured logs بدون PII/secret؛
- request/correlation id؛
- traces برای checkout/payment/provider؛
- metrics: latency/error/saturation، auth failure، inventory conflict، payment state، outbox lag، notification failure؛
- product events با schema version و consent؛
- reconciliation dashboard جدا از business analytics.

### alertهای بلاک‌کننده

- callback 4xx/5xx یا verify mismatch؛
- duplicate payment/idempotency conflict غیرعادی؛
- balance/ledger invariant violation؛
- oversell یا negative inventory؛
- DB migration/replication/backup failure؛
- auth brute-force spike؛
- upload malware/type violation؛
- error budget burn و queue lag.

### runbookها

- payment provider outage؛
- SMS outage؛
- DB failover/restore؛
- secret compromise؛
- bad release rollback؛
- stuck order/refund؛
- fraudulent shop/agent؛
- privacy deletion/export؛
- major abuse/content incident.

## 11. release sequence

### R0 — internal development

P0 fixها، domain decision، migrations و test baseline. هیچ پول واقعی.

### R1 — staff alpha

داده synthetic، یک خوشه، Android/Web، بدون transaction یا با sandbox. هدف: flow و data quality.

### R2 — closed merchant pilot

Concierge + claim، storefront/search/reservation، حداکثر تعداد فروشگاه کنترل‌شده، support دستی. معیار: freshness و WVLO.

### R3 — paid pilot

single-shop pickup-first، سقف transaction، reconciliation روزانه، manual approval و rollback فوری.

### R4 — public cluster launch

پس از دو cohort سالم، store artifacts signed، legal/ops sign-off، on-call و staged rollout.

### R5 — adjacent cluster

فقط اگر contribution margin، merchant retention، no-result، cancellation و complaint guardrail سالم‌اند.

## 12. checklist روز انتشار

### T-7 روز

- code freeze و release candidate؛
- migration dry-run و restore drill؛
- security/privacy review؛
- provider capacity و contact escalation؛
- support scripts و incident roles؛
- store metadata و review accounts؛
- synthetic probes و alert test.

### T-1 روز

- backup verified؛
- hashes/signatures/SBOM؛
- smoke stage؛
- feature flags default؛
- rollback artifact؛
- legal/payment sign-off ثبت‌شده؛
- status page/communication template.

### T0

- migration با observer؛
- canary/staged rollout؛
- monitor payment/order/error/latency؛
- sample reconciliation؛
- merchant support channel فعال؛
- go/no-go checkpoint.

### T+1 و T+7

- financial reconciliation؛
- incident/complaint review؛
- crash/ANR/Web Vitals؛
- freshness/no-result/WVLO؛
- rollback یا expansion decision.

## 13. تعریف GO

انتشار فقط وقتی GO است که:

- هیچ P0 باز نیست؛
- تمام financial invariant tests و concurrency/idempotency pass؛
- migration و restore rehearsal موفق؛
- account deletion/privacy disclosure آماده؛
- artifact هر channel امضاشده و قابل rollback؛
- alert و on-call آزمایش‌شده؛
- یک مالک مشخص برای product، security، payment، support و release وجود دارد؛
- خوشهٔ pilot، عرضه و تیم عملیات واقعی دارد؛
- تصمیم حقوقی نقش پلتفرم و پرداخت مکتوب است.

