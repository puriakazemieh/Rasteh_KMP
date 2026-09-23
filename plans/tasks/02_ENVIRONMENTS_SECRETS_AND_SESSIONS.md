# Task 02 — محیط‌ها، secrets، session و logging امن

**Status:** DONE
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

## Execution evidence

- **Started at:** 2026-08-08 (Asia/Tehran).
- **Baseline:** client `d65955adc3fc0376b0aa66ec0fb98de46b137c38`; server `8c860b422d1996599db1be4598755171882bdfc4`.
- **Working tree:** client was clean. Server already contained untracked local runtime artifacts, a Gradle user-home cache, and `local.properties`; they are preserved and will not be committed or altered by this task.
- **Decision approval:** 2026-08-08. The product owner approved the Web BFF design with HttpOnly, Secure, SameSite cookies. Development and tests use local endpoints only; no staging/production domain, certificate, cookie domain, or secret value will be committed. Public domain/TLS/cookie-domain validation is a release gate in Task 14.
- **Blocked at:** 2026-08-08 (Asia/Tehran). The current JS `SettingsFactory` uses `StorageSettings`, and `TokenManager` writes access and refresh tokens to that shared settings store; this is persistent browser storage. Task 00 approves Android + Web as launch channels but does not select or provision a BFF/cookie session architecture. Per this task's STOP condition, no localStorage or plaintext fallback may be retained or introduced while that infrastructure decision is open.
- **No runtime change:** only the synchronized task/roadmap status and this evidence were changed. Existing server-local untracked files remain untouched; no secret value, credential, merchant ID, or personal data was inspected or emitted.

## Decision required — Web session architecture

**Owner:** product/release owner with the web/platform security owner.

1. **Approve a BFF with HttpOnly, Secure, SameSite cookies (recommended for production Web).** Requires staging/production BFF deployment, cookie domain and CSRF/CSP/CORS policy, and enables the target Web session design without browser JavaScript access to refresh tokens.
2. **Approve a memory-only direct-API Web interim.** Tokens are never persisted and all users reauthenticate after reload; this can be used only with an explicit XSS/CSP threat-model acceptance and does not satisfy a persistent Web session experience.
3. **Keep Web authenticated flows disabled until the BFF exists.** Guest Web remains possible, while Android can proceed separately; it delays Web login/profile/order flows but avoids putting bearer credentials in browser storage.

**Impact:** until one option is selected, Task 02 cannot truthfully complete its Web session, logout/session rotation, and real TLS stage-smoke criteria. Payment remains disabled independently.

## Resumption evidence

- **Resumed at:** 2026-08-08 (Asia/Tehran) after approval of option 1 (Web BFF/cookie architecture).
- **Local-only boundary:** implementation and automated tests must use localhost or test doubles. Persistent staging/production URLs, public certificates, DNS, real cookie domains, external secret stores, and public HTTPS smoke tests are deferred to Task 14 and must not be represented as complete in this task.
- **Decision resolved:** on 2026-08-08 the product owner approved option 1, the Web BFF with HttpOnly, Secure, SameSite cookies. Local execution uses localhost; the actual cookie domain, public TLS and staging values remain Task 14 gates.
- **Implemented locally:** production/tunnel URLs were removed from KMP target configs; Android cleartext is limited to the debug manifest for the emulator loopback; JS settings are page-memory only; HTTP body/error logging is removed; logout revokes the current refresh token and purges identity cache; BFF login/refresh/logout endpoints issue HttpOnly session cookies and return no bearer tokens in their login body; refresh-token replay revokes all active sessions for the owner.
- **Secret hygiene:** server configuration now references environment variable names only and `.env.example` contains names without values. Existing server-local runtime artifacts remain untouched.
- **Focused verification:** `:core:data:jvmTest --tests AuthRepositorySignOutTest` PASS; `:core:data:compileKotlinJs :core:network:compileKotlinJs :composeApp:compileKotlinJs` PASS; focused server tests for `RefreshTokenService`, `WebSessionCookieService`, and `WebSessionController` PASS. The first red executions exposed the intended missing cache purge, JS settings type mismatch, cookie/BFF implementation, and replay-revocation behavior; none were suppressed.
- **Adapter limitation:** an Android EncryptedSharedPreferences adapter was attempted and safely reverted because its new transitive dependency could not be resolved from the configured Google Maven repository (`androidx.annotation:annotation:1.1.0`). No plaintext fallback was added. Android, iOS, and Desktop use memory-only credentials in this local-only phase; persistent secure-store adapters are a release blocker carried to Task 14.
- **Local runtime evidence (2026-08-08):** local server smoke check `GET /api/swagger-ui.html` returned `200`; `./gradlew.bat --no-daemon --stacktrace test` completed successfully. The user supplied local database migration/config adjustments are preserved; production release remains governed by the base validation/migration policy and Task 14 gates.
- **Session hardening update (2026-08-08):** legacy bearer tokens are removed from general Settings at startup and are memory-only until native secure-storage adapters are available. Server Web sessions authenticate from the HttpOnly access-cookie; unsafe requests carrying a Web session cookie require the framework CSRF token, while bearer-token clients are unaffected. Focused client token and server Web-session tests pass.
- **Verification status:** the full client `./gradlew.bat --no-daemon check` was started on 2026-08-08 but exceeded the execution-tool time limit before completion; it is not recorded as passing and must be rerun to completion. No test assertion was weakened or removed.

## Completion evidence

- **Completed at:** 2026-09-23 (Asia/Tehran).
- **Code commits:** client `df665bc`, `d40c5f9`; server `988627f`, `48ebbc5`.
- **Changed client areas:** debug-only Android cleartext policy; local target URLs; no body/error logging; memory-only token and JS settings; logout cache purge; browser BFF cookie/CSRF transport and session rehydration.
- **Changed server areas:** environment-name-only configuration and local profile; BFF login/register/OTP/refresh/logout/session endpoints; HttpOnly cookie service; CSRF matcher; cookie JWT authentication; refresh-token replay revokes all active sessions; focused tests.
- **Migration/contract:** no Task 02 schema migration. The pre-existing address migration files were preserved and excluded from these commits. The additive `/web/auth/*` BFF is separate from the existing bearer-token `/api/auth/*` contract, so native clients remain compatible.
- **Secret review:** `.env.example` contains names only; safe named-secret assignment scan reported zero files. No secret, merchant identifier, credential or personal data was committed.
- **Security/privacy review:** Web bearer credentials are not returned in BFF login/session JSON and are not stored in browser storage; cookies are HttpOnly and SameSite, CSRF applies to unsafe requests carrying a web-session cookie, and response-body logging is disabled. Cookie `Secure`, public domain, TLS, CORS and CSP validation require real domains and are release gates in Task 14.
- **Verification:** `./gradlew.bat --no-daemon check` PASS (final run, 14m59s); `./gradlew.bat --no-daemon :composeApp:assembleRelease` PASS (local build, 5m09s); `./gradlew.bat --no-daemon :composeApp:compileKotlinJvm :composeApp:compileKotlinJs` PASS; `./gradlew.bat --no-daemon --stacktrace test` PASS; `./gradlew.bat --no-daemon --stacktrace bootJar` PASS. Focused token, cookie, BFF-controller and refresh-replay tests also PASS.
- **Release boundary:** the locally assembled Android package is not a signed production artifact and is not release evidence. Production config values, secure native credential stores, signing, real HTTPS/domain/cookie checks and rollback rehearsal remain explicitly owned by Task 14.

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

- [x] config محلی و server fail-fast برای متغیرهای required وجود دارد؛ config واقعی production در Task 14 تزریق و بررسی می‌شود.
- [x] cleartext Android فقط در debug emulator policy است و URLهای local جایگزین tunnel/production hardcode شده‌اند؛ HTTPS/domain واقعی در Task 14.
- [x] logging پاسخ/بدنه و token redacted/disabled است.
- [x] support limitation برای storage native/desktop مکتوب است؛ credential در این فاز memory-only است و fallback plaintext وجود ندارد.
- [x] logout، refresh rotation/replay revocation و Web BFF session/CSRF با تست پوشش داده شده‌اند.
- [x] demo credential در source/workflow جدید وجود ندارد و `.env.example` value-free است.
- [x] smoke TLS واقعی به‌صراحت به Task 14 منتقل شده است؛ local-only scope این task تکمیل شده است.

## STOP conditions

- اگر Web BFF/cookie نیازمند تصمیم زیرساختی است، token را در localStorage نگه ندار؛ task را روی تصمیم BLOCKED کن.
- اگر credential موجود احتمالاً واقعی است، آن را inspect/print نکن؛ owner را برای rotation مطلع کن.
- اگر platform credential API پشتیبانی نمی‌شود، plaintext fallback تولیدی نگذار.

## Executor prompt

```text
Task 02 را اجرا کن: environment matrix، HTTPS-only release، log redaction، secure session storage، refresh/logout hardening و credential hygiene. مقادیر secret موجود را هرگز چاپ نکن. برای هر target test یا support limitation صریح بده. release build باید بدون config معتبر fail شود. همه Verificationها را اجرا و فقط با evidence وضعیت DONE را ثبت کن.
```
