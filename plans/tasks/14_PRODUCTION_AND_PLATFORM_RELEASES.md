# Task 14 — زیرساخت تولید، تطبیق و انتشار چهار پلتفرم

**Status:** NOT STARTED  
**Planned at:** client `2a53bcf` / server `66c83ed`  
**Risk:** Critical  
**Dependencies:** Tasks 02, 03, 11, 12 and 13

## Outcome

یک release candidate واقعی با backend production-ready و artifactهای امضاشدهٔ کانال‌های تصویب‌شده ساخته شود. نتیجه نهایی GO/CONDITIONAL GO/NO-GO با evidence است.

## Source of truth

`plans/RELEASE_READINESS.md` باید کامل اجرا شود. این task جای آن را خلاصه نمی‌کند.

## Server/infra deliverables

- dev/stage/prod isolation؛
- secrets manager و least privilege؛
- PostgreSQL backup/PITR/restore؛
- object storage/CDN؛
- non-root signed container + SBOM؛
- health/readiness/metrics/tracing/alerts؛
- canary/rollback؛
- DNS/TLS/CORS/security headers؛
- runbook/on-call/status communication؛
- reconciliation و DR rehearsal؛
- capacity/load test.

## Deferred provider selection from Task 00

پایلوت تهران/لاله‌زار با دستهٔ الکتریکی بدون provider مالی آغاز می‌شود. پیش از هر production release، این task باید برای هر مورد زیر provider اصلی، fallback، SLA، owner عملیاتی، sandbox/rehearsal و runbook failure را ثبت و آزمایش کند: map، SMS، push، object storage/CDN و analytics. fallback MVP به‌ترتیب آدرس/مسیریابی بدون نقشه، ثبت درخواست بدون ادعای ارسال SMS، اعلان درون‌برنامه‌ای، رد امن upload و عدم ارسال PII است. payment/PSP/refund/settlement تا قرارداد مجاز و sign-off حقوقی/مالی disabled است و انتخاب آن شرط جداگانهٔ release تراکنشی خواهد بود.

### Carried forward from Task 01 — ZarinPal release rehearsal

Before enabling `PAYMENT_ENABLED` in any staging or production release, run one completed ZarinPal sandbox payment against the persistent staging HTTPS callback. Preserve redacted evidence of: request-to-callback delivery; the provider verify response; one atomic order/payment/ledger transition; a replay of the same callback producing no second effect; and the feature-flag rollback back to `PAYMENT_ENABLED=false`. This is a payment-release gate, not satisfied by a forged callback reachability check, and it requires non-production credentials kept outside the repository.

## Android deliverables

- final applicationId/version؛
- AAB release signed؛
- R8/resource shrink و mapping؛
- release HTTPS/network config؛
- verified app links؛
- Data Safety/privacy/account deletion؛
- listing/assets/content rating؛
- internal/closed/staged rollout؛
- crash/ANR monitoring.

## iOS deliverables

- stable Bundle ID، signing/provisioning؛
- release archive/TestFlight؛
- ATS، Keychain، universal links؛
- privacy manifest/usage descriptions؛
- App Store privacy/metadata/review notes؛
- dSYM/crash؛
- eligibility/distribution legal verification؛
- platform feature parity یا disclosure.

## Web deliverables

- production minified/hashed build؛
- bundle budget و no OOM؛
- SSR/prerender indexable public pages؛
- CSP/HSTS/cookie/CSRF؛
- canonical/sitemap/structured data؛
- accessibility/Web Vitals؛
- PWA update/cache/rollback؛
- RUM/error monitoring.

## Desktop deliverables

- only if approved in task 00؛
- signed installer (Windows/macOS notarization as applicable)؛
- secure credential store؛
- signed auto-update + rollback؛
- high-DPI/resize/keyboard/proxy tests؛
- stable/beta channels.

## Legal/operations deliverables

- Terms, Privacy, Seller Agreement؛
- refund/cancel/dispute/prohibited-goods policy؛
- account deletion/export؛
- PSP/settlement/reconciliation sign-off؛
- licensing/Enamad/tax/invoice review توسط متخصص؛
- support SLA و escalation؛
- incident/privacy breach process.

## Release procedure

1. RC branch/tag و change freeze.
2. required CI و independent security/review.
3. migration + restore rehearsal.
4. signed artifacts/SBOM/checksum.
5. staging smoke با provider sandbox و synthetic monitoring.
6. T-7/T-1 checklist در RELEASE_READINESS.
7. canary rollout و real-time dashboard.
8. reconciliation sample و go/no-go checkpoint.
9. staged expansion؛ rollback اگر guardrail fail.
10. T+1/T+7 review.

## Verification

- command output + artifact hash؛
- signing/notarization/store validation؛
- migration/restore timestamps؛
- load/security/accessibility reports؛
- SLO/alert test؛
- legal/payment approvals؛
- the carried-forward ZarinPal sandbox callback/verify/replay rehearsal and feature-flag rollback evidence؛
- rollback demo؛
- known limitations/support matrix.

## Done when

- [ ] هیچ P0/P1 انتشار باز نیست.
- [ ] channelهای approved artifact signed و reproducible دارند.
- [ ] Web production—not development—منتشر می‌شود.
- [ ] stage/provider/restore/rollback rehearsals پاس‌اند.
- [ ] if payments are enabled, the carried-forward ZarinPal sandbox callback/verify/replay rehearsal and `PAYMENT_ENABLED=false` rollback have passed.
- [ ] privacy/account deletion و policies live هستند.
- [ ] alert/on-call/support آماده‌اند.
- [ ] independent audit نتیجه GO یا شروط دقیق Conditional GO داده است.

## STOP conditions

- هر failure مالی/امنیتی/migration؛
- artifact unsigned یا development؛
- نبود rollback/backup verified؛
- نبود legal/PSP sign-off برای transaction؛
- store eligibility نامشخص؛
- alert یا on-call آزمایش‌نشده.

## Executor prompt

```text
Task 14 را با اجرای کامل plans/RELEASE_READINESS.md انجام بده. release candidate را روی stage واقعی، migration/restore/rollback، load/security/privacy و provider sandbox بررسی کن. Android/iOS/Web/Desktop را فقط طبق scope مصوب و با artifact signed/reproducible تحویل بده؛ Web development bundle قابل قبول نیست. evidence hashes، store checks، SLO/alerts و approvals را ثبت کن و فقط با GO واقعی DONE کن.
```
