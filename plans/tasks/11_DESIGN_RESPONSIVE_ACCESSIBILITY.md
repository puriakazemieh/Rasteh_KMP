# Task 11 — بازطراحی، RTL، responsive و accessibility

**Status:** NOT STARTED  
**Planned at:** client `2a53bcf`  
**Risk:** Medium  
**Dependencies:** Task 00  
**Coordination:** پس از Tasks 07–10 یک دور regression با دادهٔ واقعی اجرا شود؛ این هماهنگی مانع شروع Task 11 نیست.

## Outcome

یک design system با هویت راسته و UX متناسب compact/medium/expanded ساخته شود؛ journeyهای اصلی بر feature grid غلبه کنند و معیارهای accessibility/i18n قابل‌آزمون شوند.

## Source of truth

- `plans/DESIGN_AUDIT.md`
- `core/designSystem/**`
- `feature/bazaar/FeaturesLauncherScreen.kt`
- `feature/main/MainGraphScreen.kt`
- screenهای checkout/payment/shop/product/vendor/admin/profile.

## In scope

- token cleanup و rebrand Carmilla → Rasteh؛
- IA اصلی consumer/vendor/admin/agent؛
- responsive container/grid/master-detail؛
- iconography/state/number formatting؛
- accessibility semantics/touch/contrast/focus؛
- localization fa/en و RTL/LTR؛
- screenshot/golden matrix؛
- حذف production navigation به placeholder.

## Out of scope

- تغییر business rule بدون task مرتبط؛
- طراحی visual search/AI نمایشی؛
- بازنویسی همه screenها در یک PR.

## Steps

1. design inventory و token usage report تولید کن.
2. primitive/semantic/component/state token hierarchy؛ raw color/dp lint برای featureهای migrate‌شده.
3. rename/migrate Carmilla components و strings با compatibility alias کوتاه‌مدت.
4. IA جدید و route priority؛ ۲۰ tile هم‌وزن حذف/گروه‌بندی.
5. breakpoints و layout contract:
   - compact single pane؛
   - medium rail/adaptive grid؛
   - expanded master-detail/table.
6. componentهای button/field/card/badge/nav/price/status با semantics داخلی.
7. 48dp touch target، content description/action label، heading، stateDescription و focus order.
8. Web/Desktop keyboard/focus/hover؛ no layout-shifting border.
9. تمام copyهای screenهای MVP به Compose Resources؛ plural/date/currency locale-aware.
10. screenshot matrix: fa/en × light/dark × compact/medium/expanded.
11. accessibility scanner/TalkBack/VoiceOver/keyboard smoke.
12. runtime UX test با کاربران pilot و ثبت issue severity.

## Screen migration order

1. payment completion و checkout؛
2. home/search/result؛
3. shop/product detail؛
4. reservation/activity؛
5. vendor/agent/admin workspaces؛
6. profile/settings/account deletion؛
7. secondary screens.

## Verification

- golden/screenshot diff threshold؛
- semantics unit tests؛
- automated contrast/token lint؛
- keyboard tab order Web/Desktop؛
- RTL mirror snapshots؛
- large font/text scale؛
- reduced motion در صورت animation؛
- window resize state preservation.

## Done when

- [ ] production primary nav بر journey است، نه feature tile showroom.
- [ ] MVP screenها compact/medium/expanded واقعی دارند.
- [ ] هیچ interactive target زیر 48dp در migrated scope نیست.
- [ ] icon-only action label و focus visible دارد.
- [ ] fa/en و RTL/LTR واقعی‌اند؛ literal copy در migrated scope صفر است.
- [ ] token drift/Carmilla leftover در public API حذف شده است.
- [ ] screenshot/a11y matrix در CI pass می‌شود.
- [ ] user test findings P0/P1 بسته شده‌اند.

## STOP conditions

- اگر داده/CTA screen با سه listing mode سازگار نیست، mock visual نهایی نساز؛ contract task 07 را حل کن.
- اگر brand identity هنوز تصویب نشده، semantic system را بساز ولی logo/visual signature را final اعلام نکن.
- accessibility failure را با suppression پنهان نکن.

## Executor prompt

```text
Task 11 را با plans/DESIGN_AUDIT.md اجرا کن. Hallmark tells را رفع کن: feature tile grid، mobile canvas کشیده‌شده، token/rebrand drift، icon/state inconsistency و placeholder routes. design system semantic، IA journey-based، responsive واقعی، RTL/LTR، localization و accessibility 48dp/semantics/focus بساز. مهاجرت را screen-by-screen انجام بده و screenshot/a11y matrix را required کن؛ redesign نمایشی بدون contract داده نساز.
```
