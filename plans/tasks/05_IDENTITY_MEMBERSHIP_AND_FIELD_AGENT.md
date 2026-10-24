# Task 05 — هویت، عضویت فروشگاه و نقش بازاریاب

**Status:** DONE — 2026-09-27  
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

## Execution baseline — 2026-09-27

- client `6cf6740e52e722ac8fd7c1d08ac5e230b31e127d`
- server `a7120e7c2bb615b0996ced0d5ba2870669b76652`
- existing local-only server artifacts (`.artifacts/`, `.gradle-user-home/`) are preserved and excluded from this task.

## Blocking decision — 2026-09-27

Task 05 must implement ownership transfer with strong re-auth, audit and separation of duties, but no approved policy defines who may initiate/approve a Shop ownership transfer or how financial/settlement access behaves during and after transfer. This cannot be inferred safely from the legacy single `shops.owner_user_id` column.

### Decision required from product/operations owner

1. **Maker-checker transfer (recommended):** current OWNER initiates after re-auth; a platform ADMIN/SUPERADMIN who is neither old nor new owner approves; transfer has a configurable cooling period; all old shop memberships are revoked except explicitly recreated ones. Settlement/wallet permissions remain unavailable to shop memberships.
2. **Platform-admin transfer only:** only ADMIN/SUPERADMIN initiates and approves after evidence review; slower but simplest audit path. Owner cannot self-service a transfer.
3. **Disable transfers for the current release:** ownership is immutable; support creates a new Shop only through a future reviewed migration process. This avoids transfer risk but blocks legitimate ownership changes.

Until one option is approved, no ownership-transfer endpoint, migration default, or financial-access inference will be implemented. The task is BLOCKED by its declared STOP condition.

### Resolution — 2026-09-27

مالک محصول گزینهٔ ۳ را تأیید کرد: در release فعلی انتقال مالکیت ممنوع است و `Shop.owner_user_id` تغییرناپذیر می‌ماند. هیچ endpoint یا migration برای transfer ساخته نمی‌شود. تغییر آینده فقط با policy و migration جداگانه ممکن است؛ shop membership هرگز مالکیت یا دسترسی settlement/wallet ایجاد نمی‌کند.

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

- [x] هیچ تصمیم امنیتی به `isVendor/isAdmin` client محدود نیست؛ server capability resolver در هر درخواست تصمیم می‌گیرد. booleanهای کلاینت فقط compatibility UX هستند.
- [x] vendor global role منبع حقیقت نیست؛ membership فعالِ shop منبع capability است.
- [x] authorization matrix test required CI است؛ تست‌ها در Gradle `test/check` اجرا می‌شوند.
- [x] field agent workflow و audit کامل است: assignment، create/edit/consent/submit/review و maker-checker.
- [x] separation of duties و least privilege اثبات شده است.
- [x] role migration و rollback/compatibility مستند است.

## Completion evidence — 2026-09-27

- Server: `AuthorizationCatalog`, `AuthorizationService`, membership APIs, agent workflow APIs and Flyway V6–V8.
- Client: capability contract, deep-link gate and capability-gated authorization-admin surface.
- Ownership transfer remains deliberately disabled under the product decision above; OWNER membership is neither invitable nor revocable.
- Verified with server authorization tests and Kotlin compilation of the client authorization surfaces.

## STOP conditions

- اگر policy مالکیت/انتقال فروشگاه تصویب نشده؛
- اگر agent به داده مالی نیاز ادعایی دارد ولی justification/approval نیست؛
- اگر migration role فعلی membership دقیق نمی‌سازد؛
- اگر UI guard به‌جای server authorization پیشنهاد شود.

## Executor prompt

```text
Task 05 را اجرا کن. یک permission catalog و authorization matrix بساز، global vendor role را به shop membership تبدیل و FIELD_AGENT/AGENT_SUPERVISOR را با assignment و maker-checker اضافه کن. server authority نهایی است؛ client فقط capability-aware UX می‌دهد. migration و audit را کامل و matrix tests را required کن. هیچ دسترسی مالی برای agent بدون تصمیم مکتوب اضافه نکن.
```
