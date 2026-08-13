# Instructor notes — pair cart exercise

**States (example, not canonical):** `EmptyCart`, `Shopping`, `CheckoutAuth`, `CheckoutPayment`, `PaymentProcessing`, `OrderComplete`, `ErrorCheckout` — accept variations if **guarded**.

**Invalid transitions:** `pay` from `Shopping` without checkout; `addItem` when line count already **20**; quantity **100**.

**Coupon CSV:** Expect references to **minimum subtotal** if trainees add business rules—if unspecified, they must **document assumptions** in test plan.

**Grading:** Weight **clear oracles** and **traceability** over diagram beauty.

**Timebox:** If running short, require **8** state tests instead of **10**; keep **invalid** transitions mandatory.
