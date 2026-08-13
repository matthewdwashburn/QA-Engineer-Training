# Instructor notes — shipping_eligibility

**US branch nuance:** `subtotal >= 35` short-circuits before `member` path; **member & >= 50** is **dead** for typical thresholds—actually wait: if subtotal is 40, first if false, second checks member and >=50 false. If subtotal 55 and not member, first true. If subtotal 55 member, first true. For member path >=50 to be **True** while first **False**, need subtotal in [35,50)? No—first checks >=35 so any >=35 returns True. So **`is_member and subtotal >= 50`** is **unreachable** when `subtotal >= 35` is False... Actually when subtotal is 40, first is True. When subtotal is 30, first False; member True and >=50 False. When subtotal 60 member True, first True. The **member** clause only matters when **35 <= subtotal < 50**? 40: first True. 36: first True. So indeed **member AND >=50** never triggers because any subtotal >=50 also has >=35. **This is an intentional defect seed** for the reflection paragraph.

**Class discussion:** Dead code / wrong business rule vs tests “passing.”

**Coverage:** EU true/false at 25 boundary; US at 35 and other; other region at 100.
