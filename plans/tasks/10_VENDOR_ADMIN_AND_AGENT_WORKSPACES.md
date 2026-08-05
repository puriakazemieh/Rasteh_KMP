# Task 10 — پنل فروشنده، ادمین و بازاریاب

**Status:** NOT STARTED  
**Planned at:** client `2a53bcf` / server `66c83ed`  
**Risk:** Medium  
**Dependencies:** Tasks 05, 06 and 07

## Outcome

سه workspace بر اساس اقدام روزانه ساخته شود، نه یک menu مشترک با boolean role. هر action capability و audit روشن داشته باشد.

## Current evidence

- `MoreScreen.kt` فقط CUSTOMER/VENDOR/ADMIN switch دارد؛
- dashboard فروشنده عملاً اولین فروشگاه را انتخاب می‌کند؛
- routeهای privileged بدون guard مرکزی mount شده‌اند؛
- admin پوشش جامع user/role/agent/audit ندارد؛
- field agent workspace کاملاً مفقود است؛
- screen/ViewModelهای بزرگ maintainability پایین دارند.

## Vendor workspace

Navigation:

- Today: stale offers، unanswered quotes، reservations/orders، verification issue؛
- Catalog: product/offer/variant/media، bulk update؛
- Leads & Orders؛
- Store: locations، hours، team/memberships؛
- Insights: search demand، outcome، response، seller health؛
- Settlement فقط برای capability تراکنشی.

چندفروشگاهی: shop selector پایدار و scope همه requestها؛ «اولین فروشگاه» ممنوع.

## Agent workspace

- assigned cluster/map و lead list؛
- visit plan؛
- offline draft capture؛
- consent/evidence؛
- claim handoff/status؛
- quality/activation metrics؛
- بدون wallet/order financial/settlement access.

## Admin workspace

صفحهٔ اصلی exception queue:

- shop verification/duplicate/appeal؛
- role/membership/agent quality؛
- reports/reviews/upload abuse؛
- stale/high-cancel seller health؛
- payment/reconciliation anomalies فقط برای permission مالی؛
- audit explorer.

## Steps

1. UX IA و capability matrix را از task 05 وارد کن.
2. route registrar per workspace و central auth redirect.
3. shared `WorkspaceContext(shopId/clusterId/capabilities)` با lifecycle امن.
4. server dashboard endpointهای projection/paginated؛ collection کامل برای count ممنوع.
5. task-oriented home و actionable empty states.
6. offline agent queue با conflict resolution و attachment lifecycle.
7. multi-shop switch و deep-link scoping.
8. audit timeline برای mutation؛ reason required برای admin action.
9. split screenهای >500 lines به coordinator/state/components؛ dependency inversion.
10. adaptive master-detail برای expanded layouts.

## Verification

- route capability matrix و unauthorized deep links؛
- multi-shop isolation؛
- shop switch inflight request cancellation؛
- agent offline sync idempotency/conflict؛
- admin action reason/audit؛
- dashboard query pagination/performance؛
- screenshot compact/expanded و fa/en؛
- screen reader/keyboard actions.

## Done when

- [ ] vendor چند فروشگاه را ایمن مدیریت می‌کند.
- [ ] agent workflow end-to-end و least privilege است.
- [ ] admin exception queue و audit دارد.
- [ ] privileged route مرکزی guard و server scope شده است.
- [ ] dashboardها projection/paginated هستند.
- [ ] فایل‌های god مرتبط شکسته و testable شده‌اند.

## STOP conditions

- اگر capability matrix task 05 کامل نیست؛
- اگر admin action بدون audit/reason پیشنهاد شود؛
- اگر offline agent data encryption/retention تعریف نشده؛
- اگر workspace financial data را به agent نشان دهد.

## Executor prompt

```text
Task 10 را اجرا کن. Vendor، Admin و Field Agent را به workspaceهای جدا و action-oriented تبدیل کن. capability server-side و route guard مرکزی، multi-shop isolation، agent offline/idempotent sync، admin audit/reason و paginated projections ضروری‌اند. dashboard فروشنده نباید اولین shop را hardcode کند. responsive master-detail و tests دسترسی/فروشگاه/آفلاین را کامل کن.
```
