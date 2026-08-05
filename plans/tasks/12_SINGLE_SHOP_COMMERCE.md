# Task 12 — خرید تک‌فروشگاهی، پرداخت، refund و تسویه

**Status:** NOT STARTED  
**Planned at:** client `2a53bcf` / server `66c83ed`  
**Risk:** Critical  
**Dependencies:** Tasks 01, 04, 07 and 09

## Outcome

فقط Offerهای `BUYABLE` یک فروشگاه وارد cart شوند و checkout pickup-first با قیمت snapshot، رزرو موجودی، پرداخت idempotent، ledger، refund و reconciliation قابل‌اثبات کار کند.

## Non-goals

- cart چندفروشندگی؛
- wallet/escrow داخلی بدون مجوز؛
- BNPL؛
- ارسال سراسری چندمرسوله؛
- loyalty/giftcard/group-buy.

## Prerequisite decisions

- Money unit/currency؛
- PSP/payment partner و callback contract؛
- نقش حقوقی پلتفرم؛
- refund/cancel/settlement policy؛
- invoice/tax owner؛
- pickup verification method.

## Domain invariants

- cart فقط یک `shopId`؛
- order line snapshot immutable؛
- `walletPaid + gatewayPaid = payableTotal` در صورت فعال‌بودن wallet مجاز؛
- هیچ amount منفی/Double؛
- inventory reserve پیش از payment و release روی expiry/failure؛
- create/payment/refund با idempotency key؛
- callback replay فقط یک effect؛
- ledger append-only و balance projection؛
- status transition table تنها مسیر mutation.

## Steps

1. pricing service واحد: base price، discount، fee/tax، final total و versioned snapshot.
2. cart canonical single-shop و validation offer freshness.
3. checkout attempt با key تولیدشده client و server unique.
4. transaction ایجاد Order + line snapshot + inventory reservation.
5. payment attempt state machine و provider adapter timeout/retry محدود.
6. callback public endpoint با verify provider، authority lookup و CAS/row lock.
7. client فقط order state server را poll/refresh کند؛ deep link hint.
8. pickup code one-time و completion flow.
9. cancel/refund rules؛ inventory release و ledger reversal/compensation.
10. reconciliation job/report: provider ↔ payment attempt ↔ ledger ↔ order.
11. settlement instruction فقط طبق قرارداد partner؛ maker-checker برای adjustment.
12. durable receipt/terms snapshot و consumer timeline.
13. outage/retry/recovery UX و support tooling.

## State machine

`PLACED → PAYMENT_PENDING → PAID → CONFIRMED → READY_FOR_PICKUP → COMPLETED`

شاخه‌ها:

- `PAYMENT_PENDING → FAILED|EXPIRED`؛
- eligible states → `CANCEL_REQUESTED → CANCELLED → REFUND_PENDING → REFUNDED`؛
- anomaly → `MANUAL_REVIEW` با audit.

## Required tests

- pricing golden و exact money serialization؛
- duplicate click/request همان order/payment response؛
- concurrent last-item checkout فقط یک موفق؛
- callback replay/out-of-order/invalid authority؛
- partial wallet gateway remainder؛
- payment success + client kill/restart recovery؛
- failure/expiry inventory release؛
- cancel/refund ledger balance و idempotency؛
- pickup code replay؛
- owner/customer/admin authorization؛
- provider sandbox contract و reconciliation mismatch alert؛
- load/failure injection DB/provider/outbox.

## Verification

- تمام tests server/client؛
- staging end-to-end با sandbox provider؛
- reconciliation zero unexplained mismatch؛
- migration/restore rehearsal؛
- security review مستقل؛
- runbook tabletop payment outage/refund.

## Done when

- [ ] تمام invariantها در DB/service/test enforce‌اند.
- [ ] duplicate/replay/concurrency تست‌شده‌اند.
- [ ] client payment success را از server می‌گیرد.
- [ ] refund و inventory compensation کامل است.
- [ ] ledger/reconciliation auditپذیر است.
- [ ] policy/PSP/legal sign-off ثبت شده است.
- [ ] sandbox E2E و failure recovery پاس است.

## STOP conditions

- PSP/settlement/legal model تصویب نشده؛
- استفاده از Float/Double؛
- callback صرفاً به query param اعتماد کند؛
- ledger قابل‌ویرایش یا بدون business reference؛
- cart چندفروشندگی به scope اضافه شود؛
- mismatch reconciliation توضیح‌نداده باقی بماند.

## Executor prompt

```text
Task 12 را فقط برای single-shop pickup-first اجرا کن. pricing snapshot، Money exact، inventory reservation، order/payment/refund state machine، idempotency، provider verify، append-only ledger و reconciliation را end-to-end بساز. client موفقیت را فقط از server state می‌گیرد. تست concurrency/replay/restart/refund و sandbox E2E اجباری است. بدون sign-off حقوقی/PSP یا با mismatch مالی task را DONE نکن؛ wallet/escrow/multi-shop را گسترش نده.
```

