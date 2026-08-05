# Task 15 — پایلوت خوشه‌ای، analytics، رشد و درآمد

**Status:** NOT STARTED  
**Planned at:** client `2a53bcf` / server `66c83ed`  
**Risk:** Medium/High  
**Dependencies:** Tasks 08, 09, 10 and 14

## Outcome

یک micro-cluster با عرضهٔ متراکم و outcome واقعی راه‌اندازی، economics/retention/freshness اندازه‌گیری و تصمیم گسترش یا توقف بر داده گرفته شود.

## North Star

`Weekly Verified Local Outcomes (WVLO)` = تعداد زوج یکتای consumer/shop در هفته با حداقل یک purchase/pickup/reservation/verified visit/accepted quote تأییدشده.

click، impression، call raw، install، تعداد shop ثبت‌شده و GMV به‌تنهایی North Star نیستند.

## Phase 0 — discovery قبل از rollout

1. score خوشه‌ها بر density، intent، همکاری، حضوری‌بودن، تنوع، ریسک و logistics.
2. انتخاب یک خوشه و حداکثر دو دسته.
3. مصاحبه حداقل 15 فروشنده، 15 مصرف‌کننده و 3 stakeholder خوشه؛ تعداد نهایی بر saturation کیفی.
4. journey observation حضوری و concierge دستی.
5. map universe فروشگاه‌های واجد شرایط و baseline.
6. legal/business model validation.

## Supply playbook

- قرارداد/هماهنگی مدیر پاساژ یا اتحادیه؛
- onboarding day و عکاسی/کاتالوگ اولیه؛
- agent assignment و claim QR/OTP؛
- CSV/photo/voice-assisted entry؛
- QR ویترین و share link؛
- referral فروشنده؛
- reward بر claim + activation + 30-day retention، نه signup خام.

## Demand playbook

- صفحات SEO واقعی خوشه/دسته/shop؛
- QR داخل بازار و signage؛
- content محلی کاربردی؛
- saved/follow و freshness alert؛
- local deal محدود و شفاف؛
- partnership با community/مدیر پاساژ؛
- performance campaign فقط پس از instrumentation و امکان attribution.

## Analytics contract

هر event schema version، actor pseudonymous، shop/cluster، timestamp، consent/purpose و idempotency دارد.

Funnelها:

- search → result → detail → high intent → verified outcome؛
- mapped → contacted → consented → submitted → approved → claimed → catalog active → 30-day active؛
- offer fresh → stale → reconfirmed؛
- reserve/order → complete/cancel/refund/dispute.

## Metrics

Supply:

- verified active shop density؛
- claim و time-to-activate؛
- catalog coverage/freshness؛
- merchant WAU و M2/M3 retention؛
- agent first-pass quality/duplicate/fraud.

Demand/liquidity:

- no-result، search-to-detail، high-intent rate؛
- D7/D30 retention؛
- response/acceptance/time-to-first-response؛
- WVLO per 100 active users و per cluster area.

Commerce guardrail:

- cancellation due out-of-stock؛
- refund/dispute/complaint؛
- on-time pickup؛
- reconciliation error؛
- contribution margin.

## Initial hypotheses, not promises

- ≥60% eligible shops verified-active in one micro-cluster؛
- ≥85% BUYABLE offers fresh within category SLA؛
- <15% no-result for primary intents؛
- ≥70% merchant month-2 retention؛
- <3% out-of-stock cancellation؛
- <1% serious order complaints.

این thresholdها پس از دو cohort بازتنظیم شوند.

## Monetization experiments

ترتیب:

1. profile/showcase رایگان؛
2. Pro subscription برای multi-user/import/analytics/automation؛
3. Local Deals/sponsored placement با label؛
4. quote/reservation lead fee در دسته مناسب؛
5. transaction commission فقط برای order پردازش‌شده؛
6. B2B cluster management subscription؛
7. CPC فقط با valid-click definition و dispute report.

برای یک outcome هم‌زمان CPC سنگین و commission نگیرید مگر value شفاف و آزمایش‌شده باشد.

## Experiments

هر experiment باید hypothesis، segment، metric اصلی، guardrail، sample/duration، stop rule و decision log داشته باشد. dark pattern، review incentive و hidden sponsored ranking ممنوع.

نمونه‌ها:

- assisted onboarding در برابر self-service؛
- one-tap freshness reminder cadence؛
- reservation CTA در برابر call؛
- QR visit verification؛
- Pro analytics willingness-to-pay؛
- local deal با exposure cap.

## Expansion gate

خوشهٔ بعدی فقط اگر:

- supply density و freshness پایدار؛
- merchant retention سالم؛
- WVLO cohort رشد/ثبات؛
- support SLA و fraud قابل‌کنترل؛
- contribution margin path credible؛
- playbook قابل‌تکرار با team/agent capacity.

## Verification

- event schema و metric dictionary در CI/analytics catalog validate شوند؛
- query یا notebook محاسبهٔ WVLO و cohortها versioned و روی snapshot ثابت قابل‌بازتولید باشد؛
- event count کلاینت/سرور با outcomeهای source-of-truth reconciliation شود؛
- interview، merchant activation و freshness evidence به pilot cluster/dataset دارای تاریخ و owner متصل باشد؛
- هر آزمایش hypothesis، allocation، exposure cap، stop rule و guardrail report داشته باشد؛
- تصمیم `Scale / Iterate / Stop` با دادهٔ ورودی، caveat و امضای owner ثبت شود.

## Done when

- [ ] pilot cluster/dataset و interview evidence ثبت است.
- [ ] instrumentation و metric definitions audit شده‌اند.
- [ ] دو cohort حداقل ارزیابی شده‌اند.
- [ ] thresholdها با دادهٔ واقعی بازتنظیم شده‌اند.
- [ ] monetization experiment بدون آسیب guardrail اجرا شده است.
- [ ] تصمیم مکتوب Scale / Iterate / Stop گرفته شده است.
- [ ] expansion بدون gate انجام نشده است.

## STOP conditions

- عرضه پراکنده خارج خوشه؛
- instrumentation ناقص یا WVLO قابل جعل؛
- freshness/complaint/payment guardrail خراب؛
- پرداخت به agent بر signup خام؛
- monetization قبل از measurable value؛
- گسترش شهر/کشور قبل از سلامت cohort.

## Executor prompt

```text
Task 15 را به‌عنوان pilot واقعی یک micro-cluster اجرا کن، نه launch سراسری. universe عرضه، concierge/claim playbook، event schema و WVLO را تعریف کن؛ دو cohort را با freshness، retention، no-result، complaint و contribution margin بسنج. reward agent بر activation/retention باشد. monetization را مرحله‌ای و شفاف آزمایش کن. فقط با decision log داده‌محور Scale/Iterate/Stop و رعایت expansion gate task را DONE کن.
```
