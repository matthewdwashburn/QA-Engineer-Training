# Business rules — checkout shipping discount (fictional)

**Inputs:**

- **PromoActive** (Y/N): global marketing toggle.
- **CartSubtotal** (USD): use **≥ 75** as a threshold where referenced below.
- **CustomerTier:** **Gold** or **Standard** (no other tiers in this exercise).

**DiscountPercent** applies to **shipping fee** only (not the whole cart).

## Rules

1. If **PromoActive = N**, **DiscountPercent = 0** for everyone.
2. If **PromoActive = Y** and **CartSubtotal < 75**, **DiscountPercent = 0**.
3. If **PromoActive = Y** and **CartSubtotal ≥ 75** and **Tier = Standard**, **DiscountPercent = 10**.
4. If **PromoActive = Y** and **CartSubtotal ≥ 75** and **Tier = Gold**, **DiscountPercent = 15**.

There are **no** stacking rules beyond the above.
