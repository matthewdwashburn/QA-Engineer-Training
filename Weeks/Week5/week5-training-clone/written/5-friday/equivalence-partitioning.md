# Equivalence Partitioning

## Learning Objectives

By the end of this reading you will be able to:

- Divide an input domain into **equivalence classes** (valid and invalid) using requirement rules.
- Select **representative values** from each class with justification.
- Identify **multiple input variables** and apply single-factor EP analysis to each.
- Combine equivalence partitioning with **BVA** at class boundaries for a complete test set.

---

## Why This Matters

Testing every possible input is impossible (Testing Principle 2, Tuesday). Equivalence Partitioning is the systematic answer to: "Given that we cannot test everything, how do we choose a small set of tests that provides high confidence across the full input domain?"

By identifying groups of inputs that the system should handle identically — and testing one representative from each group — EP gives maximum coverage for minimum test count. It is one of the most important test design techniques, core to ISTQB Foundation, and essential for designing efficient automated test suites.

---

## The Concept

### What Is an Equivalence Class?

An **equivalence class** (or equivalence partition) is a set of input values that are expected to be **processed identically** by the system under test — producing the same class of behavior (not necessarily the exact same output, but the same response type: accepted, rejected, error message X, etc.).

The rationale: if the system really treats all values in a class identically, then testing one representative from that class provides the same information as testing all of them. Choosing wisely — one representative — and moving on is more efficient than testing redundant values.

**Implication:** If two values in the same equivalence class produce different behaviors, your class definitions were wrong — the partition boundary is in the wrong place.

---

### Valid vs Invalid Classes

For any input with defined rules, there are at least two types of classes:

**Valid equivalence classes:**
Input values that meet the requirements and should produce **successful/expected behavior**.

**Invalid equivalence classes:**
Input values that violate the requirements and should produce **error handling behavior** — rejection, error message, or system protection.

**Important:** Each distinct type of violation typically forms a **separate invalid class** — because the system may handle them differently (e.g., "value too low" vs "value too high" vs "wrong type" may produce different error messages, and those distinct behaviors each need a representative test).

---

### Step-by-Step: Building Equivalence Classes

**Step 1: Identify all input variables.**
List every input that the function or feature processes — including optional fields, headers, path parameters, and data types.

**Step 2: For each variable, identify the rules from requirements.**
Find the constraints: ranges, allowed values, patterns, data types, lengths.

**Step 3: Derive valid and invalid classes from each rule.**

| Rule | Valid Class(es) | Invalid Class(es) |
|------|-----------------|-------------------|
| Age must be 18–65 (integer) | 18 to 65 | < 18 (too young), > 65 (too old), non-integer (e.g., 25.5), non-numeric (e.g., "twenty") |
| Email must match RFC format | Valid email format (e.g., user@example.com) | Missing @ sign, missing domain, spaces in email, empty string |
| Region must be "UK", "EU", or "US" | "UK", "EU", "US" (note: each may be its own class if they behave differently) | Any other string ("AU", "DE"), empty, null |
| Password: 8–128 chars with complexity | Meets all rules | Too short (< 8 chars), too long (> 128 chars), missing uppercase, missing digit, missing special char |

**Step 4: Select one representative per class.**
Choose a value that clearly represents the class — not a boundary value (those are handled by BVA), but a value safely in the middle of the class.

**Step 5: Add boundary values at class edges (BVA).**
For ordered domains, supplement EP representatives with BVA values at each partition boundary.

---

### Representative Value Selection

The representative must be:
- **Clearly within the class** (not near a boundary — that is BVA territory).
- **Valid for the class purpose** (a value you would plausibly use in testing).
- **Documented with justification** ("This represents the valid class for age: a user aged 35 is clearly within the 18–65 range and does not stress any boundary.").

**Examples of good representative selection:**

| Class | Good Representative | Why |
|-------|---------------------|-----|
| Age 18–65 (valid) | 35 | Clear midpoint; no boundary ambiguity |
| Age < 18 (invalid — too young) | 10 | Clearly under 18; not near the boundary (17 is for BVA) |
| Age > 65 (invalid — too old) | 80 | Clearly over 65; not near the boundary |
| Non-integer age | 25.5 | Decimal — represents the wrong-type class |
| Non-numeric age | "twenty" | String — represents the non-parseable class |

---

### Multiple Input Variables

Most features have more than one input. EP is applied **per variable independently** (using the **single-fault assumption** introduced in BVA — vary one variable at a time while holding others at nominal/valid values).

**Example: Loan Application Form**

Variables:
- Applicant age (must be 18–65)
- Annual income (must be ≥ £25,000)
- Employment status (must be "Employed" or "Self-Employed")
- Credit score (must be 600–850)

**EP class table:**

| Variable | Valid class(es) | Invalid class(es) |
|---------|-----------------|-------------------|
| Age | 18–65 | < 18, > 65, non-integer |
| Income | ≥ £25,000 | < £25,000 |
| Employment | "Employed", "Self-Employed" | Any other value |
| Credit score | 600–850 | < 600, > 850 |

**Minimum test set (single-fault EP):**

| Test | Age | Income | Employment | Credit Score | Expected |
|------|-----|--------|-----------|-------------|---------|
| T1 | 35 (valid) | £40,000 (valid) | "Employed" (valid) | 700 (valid) | ACCEPTED |
| T2 | 16 (invalid — too young) | £40,000 | "Employed" | 700 | REJECTED — "Minimum age is 18." |
| T3 | 70 (invalid — too old) | £40,000 | "Employed" | 700 | REJECTED — "Maximum age is 65." |
| T4 | "twenty" (invalid — wrong type) | £40,000 | "Employed" | 700 | REJECTED — "Please enter a numeric age." |
| T5 | 35 | £20,000 (invalid — too low) | "Employed" | 700 | REJECTED — "Minimum income requirement." |
| T6 | 35 | £40,000 | "Unemployed" (invalid) | 700 | REJECTED — "Employment type not eligible." |
| T7 | 35 | £40,000 | "Employed" | 550 (invalid — too low) | REJECTED — "Minimum credit score is 600." |

7 tests cover 4 variables with multiple classes each — much more efficient than a combinatorial explosion of all possible combinations.

**Add BVA to this set:**
- Age: 17, 18, 65, 66.
- Income: £24,999, £25,000.
- Credit score: 599, 600, 850, 851.

Total with BVA: approximately 15 tests — a complete, efficient test set for a four-variable form.

---

### EP for Output Partitioning

EP can also be applied to **outputs** — not just inputs. If a function produces different types of results depending on conditions, you can partition those output behaviors and design tests to trigger each:

**Example: Tax Calculation Service**

Outputs:
- 0% tax (tax-exempt transactions)
- 10% tax (standard rate)
- 20% tax (luxury rate)
- Error: "Invalid transaction type"

Each output is a class. Design tests that trigger each one — ensuring all tax rate paths are exercised.

---

### EP for Complex Input Types

**Ordered sets (dropdown/enum):**
If a field accepts specific values ("UK", "EU", "US", "AU"), each value may or may not be its own equivalence class — depending on whether they are handled identically:
- If "UK" and "EU" both apply the same VAT rules → same class (pick one representative).
- If "UK" applies one tax regime and "EU" another → separate classes (test both).
- Invalid: any value not in the allowed set → one invalid class representative (e.g., "DE").

**Regular expression patterns:**
For an email field accepting `^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$`:
- Valid class: any email matching the pattern.
- Invalid classes: missing `@`, missing domain extension, spaces, special chars not in charset, empty.

---

## Complete Example: Voucher Code Validation

**Requirement:** A voucher code must be exactly 8 characters, consisting only of uppercase letters and digits (no spaces or special characters). The code must exist in the system's database and must not have been previously used or expired.

**Equivalence Classes:**

| Input Aspect | Valid Classes | Invalid Classes |
|-------------|--------------|-----------------|
| Length | Exactly 8 characters | < 8 characters, > 8 characters, empty |
| Character set | Uppercase A-Z and 0-9 only | Contains lowercase letters, Contains spaces, Contains special characters (!@#$) |
| Database existence | Code exists in database | Code does not exist |
| Usage status | Code not previously used | Code already used |
| Expiry status | Code not expired | Code expired |

**Test cases (one per class):**

| Test | Voucher Code | Expected | Class Tested |
|------|-------------|---------|-------------|
| T1 | "SAVE2024" | ACCEPTED | Valid (all conditions met) |
| T2 | "SAVE20" | REJECTED — "Code must be 8 characters" | Invalid — too short (6 chars) |
| T3 | "SAVE202401" | REJECTED — "Code must be 8 characters" | Invalid — too long (10 chars) |
| T4 | "" | REJECTED — "Code is required" | Invalid — empty |
| T5 | "save2024" | REJECTED — "Invalid code format" | Invalid — lowercase |
| T6 | "SAVE 024" | REJECTED — "Invalid code format" | Invalid — space |
| T7 | "SAVE@024" | REJECTED — "Invalid code format" | Invalid — special char |
| T8 | "XXXX9999" | REJECTED — "Code not found" | Invalid — does not exist |
| T9 | "USED2024" | REJECTED — "Code already redeemed" | Invalid — already used |
| T10 | "EXPR2024" | REJECTED — "Code has expired" | Invalid — expired |

**BVA additions:** 7-character code, 9-character code (adjacent length boundaries).

**Total: 12 well-justified test cases** — complete coverage of all specified behaviors.

---

## Summary

- EP divides the input domain into classes expected to behave identically — one representative per class provides maximum coverage for minimum tests.
- Apply EP per variable: identify all rules → derive valid and invalid classes from each rule → select a clear representative per class.
- Apply the **single-fault assumption** for multiple variables: vary one variable's class at a time while holding others valid/nominal.
- EP applies to **outputs** as well as inputs — design tests that trigger each distinct output class.
- Always combine EP with **BVA at class boundaries** — EP handles the class interior; BVA handles the edges where defects concentrate.

---

## Additional Resources

- [ISTQB Foundation Syllabus — Equivalence Partitioning](https://www.istqb.org/) — Official technique treatment with definitions.
- [ISTQB Glossary — Equivalence Partition](https://glossary.istqb.org/) — Canonical vocabulary.
- `boundary-value-analysis.md` (Thursday) — The complementary technique for class boundary testing.
- Demo: `demos/5-friday/demo_equivalence_partitioning/` — Instructor walkthrough applying EP to a registration form.
