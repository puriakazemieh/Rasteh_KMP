# Task 08 — جستجو، کشف محلی و SEO

**Status:** NOT STARTED  
**Planned at:** client `2a53bcf` / server `66c83ed`  
**Risk:** Medium/High  
**Dependencies:** Tasks 06 and 07

## Outcome

کاربر با عبارت فارسی و context مکانی به فروشگاه/محصول تازه برسد، علت رتبه را بفهمد و صفحات عمومی از Web قابل index باشند.

## Current evidence

- server queryهای unbounded و `%query%` دارد؛
- marketplace APIها اغلب `List` بدون pagination هستند؛
- shop lat/lng و PostGIS کامل نیست؛
- Web development JS artifact است و indexability واقعی اثبات نشده؛
- search UI freshness/ranking explanation کامل ندارد.

## Search contract

ورودی:

- normalized query؛
- city/cluster/location یا lat/lng + radius؛
- entity type shop/product؛
- category، offer mode، open now، freshness، price range؛
- cursor/page size سقف‌دار.

خروجی هر result:

- relevance identifiers؛
- distance؛
- open state؛
- verification/freshness؛
- available modes؛
- `rankingReasons[]` قابل‌نمایش؛
- sponsored flag صریح.

## Persian normalization

- `ی/ي`, `ک/ك`؛
- اعداد فارسی/عربی/لاتین؛
- نیم‌فاصله و فاصله؛
- diacritics؛
- brand aliases و typo dictionary با audit؛
- حفظ original برای نمایش.

## Steps

1. search intent/query schema و zero-result taxonomy تعریف کن.
2. PostgreSQL FTS/`pg_trgm` + PostGIS index و ranking v1.
3. projection query paginated و query plan budget.
4. ranking signals: relevance, distance, open, verified, freshness, response/stock health و exploration.
5. sponsored نتیجه جدا، label و سقف سهم.
6. search analytics privacy-aware: query، result count، click/high-intent/outcome.
7. UI map/list، filter قابل‌فهم، reason chip و stale/no-result recovery.
8. Web SEO spike: بررسی کن Compose Web فعلی semantic/indexable هست یا خیر.
9. اگر نیست، ADR برای SSR/prerender/public catalog shell بساز؛ API canonical را reuse کن.
10. URL hierarchy: city/cluster/category/shop/product؛ canonical، sitemap، robots، noindex.
11. structured data مناسب و valid؛ thin page تولید نشود.
12. performance/load test با dataset pilot و رشد 10x.

## SEO acceptance

- page HTML بدون اجرای JavaScript باید title/description/heading/content اصلی و لینک canonical داشته باشد؛
- shop/product URL پایدار و shareable؛
- `LocalBusiness`, `Product`, `Offer`, `BreadcrumbList` فقط با داده واقعی؛
- unavailable/stale state صادقانه؛
- private/account/admin pages noindex؛
- filter combinations محدود و allowlisted.

## Verification

- normalization golden corpus فارسی؛
- relevance regression query set؛
- geo distance/cluster boundary؛
- pagination stability؛
- sponsored separation؛
- no-result fallback؛
- EXPLAIN/index budget؛
- rendered HTML/structured-data validation؛
- Web performance and accessibility smoke.

## Done when

- [ ] search paginated و indexed است.
- [ ] ranking reasons در API/UI قابل‌مشاهده‌اند.
- [ ] freshness و verified signals رتبه را اثر می‌دهند.
- [ ] zero-result و search analytics تعریف شده‌اند.
- [ ] public Web pages بدون JS indexable و canonical هستند.
- [ ] sponsored placement شفاف و جداست.

## STOP conditions

- اگر Compose Web فعلی indexability را برآورده نمی‌کند، SEO را با meta tag نمایشی DONE نکن؛ SSR/prerender decision لازم است.
- اگر location consent نیست، precise geo را اجبار نکن.
- اگر search quality dataset نیست، ML/recommendation اضافه نکن.

## Executor prompt

```text
Task 08 را اجرا کن. PostgreSQL FTS/pg_trgm/PostGIS، normalization فارسی، pagination و ranking قابل‌توضیح بساز. freshness/verification/distance را در result و UI نشان بده و sponsored را جدا برچسب بزن. indexability Web را با HTML بدون JavaScript اثبات کن؛ اگر Compose Web کافی نیست ADR و public SSR/prerender shell بساز، نه ترفند meta. relevance corpus، query plan و structured-data tests را اجرا کن.
```
