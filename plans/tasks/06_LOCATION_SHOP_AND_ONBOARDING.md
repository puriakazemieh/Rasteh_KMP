# Task 06 — مکان، فروشگاه، claim و onboarding

**Status:** NOT STARTED  
**Planned at:** client `2a53bcf` / server `66c83ed`  
**Risk:** Medium/High  
**Dependencies:** Task 05

## Outcome

گراف واقعی بازار/پاساژ/راسته و lifecycle قابل‌اعتماد فروشگاه ساخته شود؛ seller self-service و agent-assisted onboarding به یک claim/verification pipeline واحد برسند.

## Current evidence

- Location/rasteh/shop پایه وجود دارد.
- ثبت فروشگاه lat/lng دقیق فروشگاه نمی‌گیرد و بیشتر `rastehId/locationId/floor` دارد.
- ارتباط rasteh/location و shop status در همه mutationها enforce نمی‌شود.
- owner واحد است؛ membership و claim/KYB/audit کامل نیست.
- Wayfinding فقط فهرست طبقات است.

## Target entities

- City, Neighborhood؛
- Cluster(type: STREET/MARKET/MALL)؛
- StreetSegment یا Building؛
- Entrance, Floor, Corridor, Unit؛
- MerchantOrganization؛
- ShopLocation با lat/lng و indoor coordinates اختیاری؛
- ShopApplication, VerificationCheck, Claim, Evidence, AuditEvent.

## Lifecycle

`DRAFT → CONSENTED → SUBMITTED → UNDER_REVIEW → APPROVED → CLAIM_PENDING → ACTIVE` با مسیرهای `NEEDS_CHANGES`, `REJECTED`, `SUSPENDED`, `CLOSED`.

## Steps

1. geo schema و parent/child invariantها را با Flyway اضافه کن.
2. PostGIS point/index و distance query؛ location precision/privacy policy.
3. duplicate detection بر اساس normalized name/phone/unit/location.
4. shop application را از active shop جدا کن.
5. evidence metadata/private object storage؛ document raw عمومی نشود.
6. seller self-service و agent draft هر دو application واحد بسازند.
7. consent capture، owner OTP/claim و checker review.
8. status transition guard و reason/appeal/audit.
9. mutation catalog/order فقط برای shop ACTIVE و membership مناسب.
10. client wizard کوتاه با autosave/offline agent support و error recovery.
11. admin queue با duplicate merge، map correction و SLA.
12. basic indoor wayfinding data؛ graphical routing خارج scope مگر داده کافی باشد.

## Verification

- invalid cluster/location/unit relation رد؛
- duplicate application detected/merged؛
- agent/seller claim race idempotent؛
- suspended shop mutation blocked و public status صحیح؛
- private evidence access role-scoped؛
- geo query accuracy و index plan؛
- precise location consent/revocation؛
- wizard recovery after app restart/offline sync.

## Done when

- [ ] shop و location/merchant جدا و canonical هستند.
- [ ] lat/lng و indoor hierarchy invariant دارند.
- [ ] claim/KYB/status/audit end-to-end کار می‌کند.
- [ ] seller و agent یک pipeline مشترک دارند.
- [ ] evidence خصوصی و retention-aware است.
- [ ] active/suspended boundaries در تمام mutationها enforce می‌شود.

## STOP conditions

- اگر مدرک/مجوز لازم هر دسته توسط متخصص تعیین نشده، فقط generic framework بساز و category rule را BLOCKED نگه دار.
- اگر provider نقشه یا geocoding شرایط استفاده نامناسب برای ایران دارد، dependency قطعی نکن.
- duplicate shop را خودکار حذف/merge نکن؛ reviewer لازم است.

## Executor prompt

```text
Task 06 را اجرا کن. مدل MerchantOrganization و ShopLocation و گراف Cluster/Building/Floor/Unit را جدا کن، PostGIS و geo query بساز و onboarding self-service/agent را به application/claim/verification واحد وصل کن. maker-checker، duplicate resolution، private evidence و status guard اجباری‌اند. نقشه گرافیکی پیچیده را بدون داده واقعی نساز. تست geo، claim race، suspension و privacy را اجرا کن.
```
