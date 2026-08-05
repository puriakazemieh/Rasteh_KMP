# Task 05 — هویت، عضویت فروشگاه و نقش بازاریاب

**Status:** NOT STARTED  
**Planned at:** client `2a53bcf` / server `66c83ed`  
**Risk:** High  
**Dependencies:** Tasks 01 and 04

## Outcome

single-role فعلی به platform roles + shop memberships تبدیل و نقش بازاریاب با least privilege، assignment و maker-checker اضافه شود.

## Current evidence

- server `UserRole.kt` فقط CUSTOMER/VENDOR/ADMIN/SUPERADMIN دارد؛ principal یک authority می‌سازد.
- SUPERADMIN با controllerهای `hasRole('ADMIN')` سازگار نیست.
- client `Roles.kt` فقط CUSTOMER/VENDOR/ADMIN و `MainViewModel.kt:63-64` string comparison دارد.
- MARKETER/field agent و چندنقشی بودن وجود ندارد.
- approval shop با تغییر global role کاربر مدل شده است.

## Target model

```text
User
├── PlatformRoleGrant(USER|FIELD_AGENT|AGENT_SUPERVISOR|ADMIN|SUPERADMIN)
├── Session
└── ShopMembership(shopId, OWNER|MANAGER|CATALOG_EDITOR|ORDER_OPERATOR|SUPPORT)
```

permissionها explicit و resource-scoped باشند. «فروشنده» نتیجهٔ membership فعال در shop approved است.

## Agent model

- assignment به cluster/lead؛
- draft capture و consent evidence؛
- offline draft queue با sync idempotent؛
- ممنوعیت approve own draft؛
- ممنوعیت مشاهده/تغییر settlement، wallet، order financial data؛
- audit هر create/edit/submit؛
- supervisor review و quality metric؛
- incentive event فقط پس از claim/activation/retention.

## Steps

1. permission catalog و authorization matrix بنویس.
2. tables/entityهای role grant و shop membership با temporal status/audit اضافه کن.
3. migration roleهای فعلی به grant/membership؛ exception report.
4. principal را multi-authority و permission resolver را resource-aware کن.
5. method security default-deny برای admin/agent/shop mutation.
6. membership invitation/accept/revoke/transfer ownership با strong re-auth.
7. agent assignment، draft، submission، review و audit endpoints.
8. client profile/capability contract؛ boolean `isVendor/isAdmin` حذف تدریجی.
9. navigation graph بر اساس capability؛ unauthorized deep link redirect + clear state.
10. admin UI برای role/membership/agent queue با separation of duties.

## Files/areas

Server: `identity/**`, `shared/security/**`, `marketplace/**/Shop*`, admin controllers، Flyway.  
Client: `core/common/Roles.kt`, profile contracts، `MainViewModel.kt`, `MoreScreen.kt`, `AppNavigation.kt`, vendor/admin screens و network DTOها.

## Verification

- authorization matrix برای همه roleها؛
- SUPERADMIN capability بدون string mismatch؛
- user هم‌زمان customer و owner/manager؛
- membership revoke بلافاصله access را قطع کند؛
- agent خارج assignment دسترسی نداشته باشد؛
- agent own draft را approve نکند؛
- ownership/settlement change audit + re-auth؛
- unauthorized deep link در client data leak نکند.

## Done when

- [ ] هیچ تصمیم امنیتی به `isVendor/isAdmin` client محدود نیست.
- [ ] vendor global role منبع حقیقت نیست.
- [ ] authorization matrix test required CI است.
- [ ] field agent workflow و audit کامل است.
- [ ] separation of duties و least privilege اثبات شده است.
- [ ] role migration و rollback/compatibility مستند است.

## STOP conditions

- اگر policy مالکیت/انتقال فروشگاه تصویب نشده؛
- اگر agent به داده مالی نیاز ادعایی دارد ولی justification/approval نیست؛
- اگر migration role فعلی membership دقیق نمی‌سازد؛
- اگر UI guard به‌جای server authorization پیشنهاد شود.

## Executor prompt

```text
Task 05 را اجرا کن. یک permission catalog و authorization matrix بساز، global vendor role را به shop membership تبدیل و FIELD_AGENT/AGENT_SUPERVISOR را با assignment و maker-checker اضافه کن. server authority نهایی است؛ client فقط capability-aware UX می‌دهد. migration و audit را کامل و matrix tests را required کن. هیچ دسترسی مالی برای agent بدون تصمیم مکتوب اضافه نکن.
```
