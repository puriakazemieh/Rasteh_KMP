# Task 13 — رسانه، اعلان، پشتیبانی و moderation

**Status:** NOT STARTED  
**Planned at:** client `2a53bcf` / server `66c83ed`  
**Risk:** High  
**Dependencies:** Tasks 05, 07 and 09

## Outcome

آپلود امن و پایدار، notification قابل‌اعتماد و workflow پشتیبانی/moderation با audit و privacy ساخته شود.

## Current evidence

- file original name وارد path local می‌شود؛
- content type عمدتاً از client پذیرفته و file عمومی serve می‌شود؛
- object storage/lifecycle/AV scan وجود ندارد؛
- notification producer/delivery/retry کامل نیست؛
- chat/report/review moderation و appeal ناقص است؛
- featureهای live/video روی targetها parity ندارند.

## Media pipeline

1. create upload intent با role/target/size/type؛
2. random object key و presigned upload یا server streaming محدود؛
3. quarantine؛
4. magic-byte validation، image decode/re-encode، metadata strip و AV scan؛
5. derivatives/thumbnail؛
6. publish metadata atomically؛
7. signed/private URL یا public CDN طبق classification؛
8. lifecycle/orphan/delete/retention.

original filename فقط metadata sanitized است. HTML/SVG/active content بدون policy صریح ممنوع.

## Notification architecture

- transactional outbox در همان transaction دامنه؛
- consumer با inbox/dedupe؛
- channel adapters SMS/push/email/in-app؛
- preference و mandatory transactional distinction؛
- template version/localization؛
- retry/backoff/dead-letter؛
- provider fallback بدون duplicate؛
- delivery status و privacy-redacted log.

## Moderation/support

- queueهای shop/product/review/report/chat/media؛
- reason taxonomy و severity؛
- assignment/SLA/escalation؛
- evidence access least privilege؛
- action/audit/appeal؛
- emergency suspension و reversible action؛
- support macros بدون افشای PII؛
- abuse/rate limit/device/account signals؛
- transparency status برای reporter در حد مجاز.

## Steps

1. data classification و media policy per target/category.
2. object storage abstraction و migration فایل‌های local با checksum.
3. upload validation/quarantine/scan pipeline.
4. outbox/inbox schema و notification events از reservation/order/freshness/moderation.
5. provider adapters با contract test و timeout.
6. client permission/preferences/deep links و duplicate-safe handling.
7. moderation entities/queues/audit/appeal.
8. admin support workspace integration.
9. retention/delete و legal hold.
10. disable live/video featureهایی که adapter target ندارند یا support matrix اعلام کن.

## Verification

- path traversal/filename collision؛
- MIME spoof/polyglot/oversize/quarantine؛
- unauthorized evidence/media access؛
- orphan/lifecycle deletion؛
- outbox crash/replay/dedupe/order؛
- notification preference/mandatory rules؛
- provider timeout/fallback؛
- moderation ownership/appeal/audit؛
- deep link opens correct scoped resource؛
- privacy log snapshot.

## Done when

- [ ] production file روی local container disk منبع حقیقت نیست.
- [ ] فایل قبل از publish validate/scan می‌شود.
- [ ] notification at-least-once ولی user-visible deduplicated است.
- [ ] preference/template/delivery status موجود است.
- [ ] moderation queue، SLA، audit و appeal دارد.
- [ ] retention/delete و access policy تست شده است.
- [ ] unsupported media/live routes production-disabled هستند.

## STOP conditions

- provider/storage قرارداد data residency/availability نامشخص؛
- فایل active content بدون sandbox؛
- moderation action irreversible بدون appeal/audit؛
- notification مالی از event غیرتراکنشی تولید شود؛
- PII در provider payload/log بیش از حداقل لازم.

## Executor prompt

```text
Task 13 را اجرا کن. local public upload را به object-storage quarantine/validate/re-encode/scan/lifecycle تبدیل کن؛ original filename path نشود. transactional outbox/inbox، notification preferences/templates/retry/dedupe و moderation/support queue با SLA/audit/appeal بساز. privacy و least privilege را test کن. featureهای media/live بدون adapter واقعی همه targetها را production-disabled نگه دار.
```
