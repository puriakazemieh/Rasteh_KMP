# Task 02 — محیط‌ها، secrets، session و logging امن

**Status:** NOT STARTED  
**Planned at:** client `2a53bcf` / server `66c83ed`  
**Risk:** High  
**Dependencies:** Task 00

## Outcome

artifactهای dev/stage/prod reproducible، HTTPS-only و بدون credential hardcoded شوند و session هر target در storage مناسب نگهداری شود.

## Current evidence

- `PlatformConfig.android.kt` tunnel موقت، JS دامنهٔ دیگر و iOS/JVM localhost/HTTP دارند.
- `AndroidManifest.xml` cleartext را global فعال می‌کند.
- `HttpClientFactory.kt` از `LogLevel.ALL` استفاده می‌کند.
- `TokenManager.kt` token را در Settings عمومی ذخیره می‌کند.
- logout فقط token را پاک می‌کند و profile/cache user-specific باقی می‌ماند.
- workflow screenshot نوع credentialهای دمو را در فایل commit‌شده دارد؛ مقدارها نباید در گزارش/commit جدید بازتولید شوند.
- برای اجرای توسعهٔ سرور، `JWT_SECRET_KEY` باید فقط از environment یا secret store خوانده شود. کلید موقتیِ اجرای تست هرگز نباید در source، `application.properties`، گزارش یا commit ثبت شود. پیش از stage/release، مالک انتشار باید یک کلید تصادفیِ مستقل و حداقل ۳۲ بایتی را در secret store محیط تنظیم کند.

## Target design

| Target | Session |
|---|---|
| Android | Keystore-backed encrypted storage |
| iOS | Keychain |
| Desktop | Windows Credential Manager/macOS Keychain/Secret Service adapter |
| Web | HttpOnly Secure SameSite cookie با BFF؛ fallback access token memory-only |

Config با build property/CI secret تزریق شود و app هنگام نبود production URL fail build کند.

## Files likely affected

Client:

- `core/network/src/*Main/.../PlatformConfig.*.kt`
- `core/network/.../HttpClientFactory.kt`
- `core/network/.../ResultHandler.kt`
- `core/data/.../TokenManager.kt`
- `core/data/src/*Main/.../SettingsFactory.*.kt`
- `composeApp/src/androidMain/AndroidManifest.xml`
- `composeApp/src/androidMain/res/xml/network_security_config.xml`
- `.github/workflows/*.yml`, `.github/scripts/bake-base-url.sh`
- logout/profile cache repositories.

Server:

- `src/main/resources/application*.properties|yml`
- `SecurityConfig.kt`
- JWT/session/auth serviceها؛
- Docker/CI env documentation.

## Steps

1. config matrix dev/stage/prod با schema و required values تعریف کن.
2. URL hardcoded production را حذف؛ debug default فقط localhost/tunnel صریح داشته باشد.
3. cleartext فقط debug network config؛ release HTTPS-only.
4. logging level بر اساس build type؛ header/body denylist/allowlist و correlation id.
5. platform secure storage interface و migration: token legacy را یک‌بار بخوان، به secure store منتقل و legacy را پاک کن.
6. Web session architecture را طبق ADR اجرا؛ اگر BFF خارج scope است token persist نشود و XSS/CSP threat model ثبت شود.
7. refresh rotation/revocation/reuse detection و logout-all-session endpoint.
8. logout همه cacheهای identity-bound را پاک کند.
9. demo accounts را ephemeral seed/test fixture کن؛ committed credential را حذف و در صورت احتمال reuse rotate کن.
10. `.env.example` فقط key name و توضیح داشته باشد، نه مقدار واقعی.

## Verification

- release build بدون production URL fail شود؛
- release manifest cleartext نداشته باشد؛
- log snapshot token/body/OTP/IBAN نداشته باشد؛
- token migration و logout cache purge؛
- refresh rotation و reuse revocation؛
- Web XSS/session test متناسب با architecture؛
- هر target adapter compile/smoke.

## Verification

```powershell
.\gradlew.bat --no-daemon check
.\gradlew.bat --no-daemon :composeApp:assembleRelease
.\gradlew.bat --no-daemon :composeApp:compileKotlinJvm :composeApp:compileKotlinJs
```

Server:

```powershell
.\gradlew.bat --no-daemon --stacktrace test
.\gradlew.bat --no-daemon --stacktrace bootJar
```

اسکن secret نیز با ابزار مصوب repository اجرا شود؛ output نباید secret value را چاپ کند.

## Done when

- [ ] config matrix و fail-fast production وجود دارد.
- [ ] هیچ target production URL توسعه‌ای یا cleartext ندارد.
- [ ] log release redacted است.
- [ ] secure storage همه targetها یا support limitation مکتوب دارد.
- [ ] logout/session rotation تست شده است.
- [ ] demo credential در source/workflow نیست و rotation ثبت شده است.
- [ ] stage smoke با TLS واقعی پاس است.

## STOP conditions

- اگر Web BFF/cookie نیازمند تصمیم زیرساختی است، token را در localStorage نگه ندار؛ task را روی تصمیم BLOCKED کن.
- اگر credential موجود احتمالاً واقعی است، آن را inspect/print نکن؛ owner را برای rotation مطلع کن.
- اگر platform credential API پشتیبانی نمی‌شود، plaintext fallback تولیدی نگذار.

## Executor prompt

```text
Task 02 را اجرا کن: environment matrix، HTTPS-only release، log redaction، secure session storage، refresh/logout hardening و credential hygiene. مقادیر secret موجود را هرگز چاپ نکن. برای هر target test یا support limitation صریح بده. release build باید بدون config معتبر fail شود. همه Verificationها را اجرا و فقط با evidence وضعیت DONE را ثبت کن.
```
