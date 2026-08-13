# Week 3 — Tuesday exercises

**Epic tie-in:** OOP pillars, hierarchies, `equals`/`hashCode` contract (`written/2-tuesday/`, `demos/2-tuesday/`).

**Time:** ~2.5–3.5 hours.

---

## Lab 1 — Vehicle hierarchy (`exercise_oop_hierarchy`)

**Mode:** Hybrid (sketch on paper/whiteboard, then code).

### Part A — Design (15–20 min)

On paper, draw a small hierarchy:

- One **`abstract`** class **`Vehicle`** with at least: `make`, `modelYear` (encapsulated), and an **abstract** method `double fuelCostPer100Km()` (or `operatingCost()` if you prefer electric).
- At least **two** concrete subclasses (e.g. `GasCar`, `ElectricCar`) with **different** cost logic.
- One **`interface`** (e.g. `AutonomousCapable` with `boolean supportsSelfDrive()` or `Refuelable`) implemented by **only one** branch (not all vehicles).
- Show **one** polymorphic call: `List<Vehicle>` and a loop printing each vehicle’s cost.

### Part B — Implement

Complete the classes under `starter_code/vehicle/`. Provide **`VehicleDemo.main`** that:

1. Adds mixed concrete types to `List<Vehicle>`.
2. Uses **runtime polymorphism** to print `fuelCostPer100Km()` (or your abstract method) for each.
3. Uses **`instanceof`** (or pattern matching) to call **one** interface-only method where applicable.

### Definition of done

- No `public` fields; use accessors where needed.
- `@Override` on concrete methods.
- Compiles and runs: `javac vehicle/*.java && java vehicle.VehicleDemo` (from `starter_code`, adjust package if you change it).

---

## Lab 2 — `equals` & `hashCode` (`exercise_equals_hashcode`)

**Mode:** Implementation.

Complete `starter_code/money/Money.java` (or rename to your domain):

- Fields: `currency` (String, non-null), `amountMinor` (long, cents/smallest unit).
- **`equals`/`hashCode`** consistent: same currency + same minor amount ⇒ equal.
- **Reject** `null` currency in constructor (`IllegalArgumentException`).

Write `MoneyDemo.main` with:

1. `HashSet<Money>` containing duplicates by value — set size reflects **dedup**.
2. Print result of `money1.equals(money2)` vs `money1 == money2`.

### Definition of done

- If two objects are equal, `hashCode()` **must** match (verify mentally or with a tiny assert-style print).

### References

- `written/2-tuesday/equality-hashcode-equals.md`, `abstract-classes.md`, `interfaces.md`, `oop-polymorphism.md`
