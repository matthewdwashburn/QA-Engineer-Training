# Testing Principles — Week 5 Recap

## Learning Objectives

By the end of this reading you will be able to:

- Recall all **seven ISTQB testing principles** from Tuesday's foundation lesson — fluently, without the list.
- Connect each principle to **specific techniques, activities, and examples** from this week's content.
- Apply the principles as a **decision-making checklist** during sprint work and release conversations.

---

## Why This Matters

Principles are not just exam bullet points. They are **decision guides** — hard-won wisdom from the industry compressed into seven sentences. The risk of learning them on Tuesday and never connecting them to practice is that they stay abstract; they inform an exam answer but don't change how you work.

This reading is an integration exercise. By tying each principle to what you actually did and discussed this week — techniques, tools, Agile practices, and team dynamics — you will remember them because they are anchored to experience, not memorization.

---

## The Seven Principles: Applied Across Week 5

### Principle 1: Testing Shows the Presence of Defects, Not Their Absence

**Core meaning:** A passing test suite does not prove the software is bug-free. It proves that no defects were found by those specific tests under those specific conditions. Unknown unknowns remain.

**This week's connections:**

- **Statement coverage (Tuesday/Wednesday):** 100% statement coverage does not mean no bugs — wrong logic can execute all lines and pass all assertions if the expected values are also wrong.
- **Exploratory testing (Wednesday):** The automation suite passed. The exploratory session found DEF-304 — a stack trace exposed on binary file upload. The automation had never tried that scenario.
- **Release readiness narratives (Wednesday/Thursday):** The test manager's report says "scope tested A, B, C; NOT TESTED: D." Acknowledging what was NOT tested is expressing Principle 1 honestly.
- **Monitoring in production:** Passing tests in staging do not guarantee production behavior. Canary releases, feature flags, and production monitoring are the team's response to Principle 1 at the operational level.

**Sprint-level application:**
When the PO says "all tests are green — we're ready to release," the tester's professional response is: "Green means we found no defects in the scenarios we tested. Here is what we did not test this sprint. Here is the residual risk. I recommend GO with those gaps acknowledged."

---

### Principle 2: Exhaustive Testing Is Impossible

**Core meaning:** It is impossible to test all possible input combinations, all paths, all environments, and all conditions. Testing must be risk-based and time-boxed.

**This week's connections:**

- **Equivalence Partitioning (Friday):** Instead of testing all 10,000 possible age values (0–9999), you identified 5 equivalence classes and selected one representative each. 5 tests, maximum information.
- **Boundary Value Analysis (Thursday):** Instead of testing all integers near a boundary, 3-value BVA selects 6 values that together catch the most common boundary defects.
- **Decision tables (Wednesday):** For N conditions, 2^N combinations. Decision table analysis and don't-care reduction brought that to a manageable set without losing coverage of the meaningful rules.
- **Risk-based test ordering:** Running P1 tests first when time is limited — explicitly acknowledging that not all tests will run, and prioritizing the ones that matter most.
- **Sprint testing capacity:** A two-week sprint does not have infinite testing time. The test plan explicitly states what will and will not be tested. Principle 2 is why this honesty is professional, not a failure.

**Sprint-level application:**
When asked "have you tested everything?", the professional answer is: "Exhaustive testing is impossible. We applied EP and BVA to cover the highest-risk input combinations. We ran the P1 regression suite. We conducted an exploratory session on the riskiest new feature. That is our best achievable coverage in this sprint's time."

---

### Principle 3: Early Testing Saves Time and Cost

**Core meaning:** Defects found early in development are dramatically cheaper to fix than defects found in production. The cost multiplier of late defect detection is real and significant.

**This week's connections:**

- **Shift-left philosophy (Monday):** Testers participating in Three Amigos before a story is coded. Questions asked in refinement ("what happens if the coupon code is applied to an empty cart?") prevent defects rather than finding them.
- **Requirements testability (Tuesday):** Writing acceptance criteria in Given-When-Then format during refinement makes ambiguities visible before code is written — when changing the spec costs nothing.
- **Verification vs validation (Tuesday):** Catching a wrong requirement (verification failure) before implementation saves the entire cost of building, testing, and reworking the wrong feature.
- **Testing lifecycle (Thursday):** Test analysis and design happen concurrently with development in Agile — not after. This is Principle 3 operationalized.

**Sprint-level application:**
Every Three Amigos question asked in refinement is Principle 3 in action. The 10-minute conversation that prevents a 2-day rework cycle — that is the compounding return of early testing.

---

### Principle 4: Defect Clustering

**Core meaning:** Defects are not evenly distributed across a codebase. They cluster in specific modules — often complex, recently changed, or poorly understood areas.

**This week's connections:**

- **Risk-based test ordering (Wednesday):** Running the most recently changed and historically defective modules first. If time runs out, you have covered the areas most likely to contain defects.
- **Exploratory testing charter selection (Wednesday):** Choosing "explore the checkout flow under network degradation and expired sessions" over "explore the About Us page" — because past defects and complexity point to checkout as the higher-risk area.
- **Defect lifecycle triage (Thursday):** Seeing 5 open bugs from the Authentication module and 0 from the Reporting module — this pattern is Principle 4 in action. Allocate more test attention to Authentication.
- **Test case organization by risk (Wednesday):** Grouping P1 tests around critical paths (authentication, payment, data integrity) reflects awareness that these areas cluster defects.

**Sprint-level application:**
Track where defects are found across sprints. If the same module consistently generates bugs, it deserves proportionally more testing attention — not a uniform distribution of effort across the codebase.

---

### Principle 5: The Pesticide Paradox

**Core meaning:** Running the same tests over and over will eventually stop finding new defects. The existing test set "immunizes" the software against those specific test cases. New tests must be designed to find new defects.

**This week's connections:**

- **Exploratory testing (Wednesday):** The automated regression suite has run 200 times. It finds regressions — but it never finds new behavior, UX issues, or emergent integration problems. Exploratory sessions with new charters are the response to the pesticide paradox.
- **Error guessing as a supplement (Friday):** After the systematic techniques (EP, BVA, decision tables) are applied, error guessing adds experience-based test ideas that the formal methods would not generate.
- **Regression suite evolution:** A regression suite that is never updated slowly becomes irrelevant as the product changes. Adding new test cases when new features are built, and removing obsolete ones when features are removed, is Principle 5 maintenance.
- **Test charter rotation (Wednesday):** Using different charters in each sprint's exploratory sessions — covering different areas, different oracles, different conditions — applies Principle 5 deliberately.

**Sprint-level application:**
Challenge yourself to write at least one test case this sprint that was not in any previous sprint's test set. Review the exploratory session notes and ask: "What charter would a different tester have used? What did we miss?"

---

### Principle 6: Testing Is Context Dependent

**Core meaning:** The appropriate testing approach, depth, tooling, and documentation formality depend on the context — the product type, the risk profile, the industry, the team, and the regulatory environment.

**This week's connections:**

- **Manual vs automated (Wednesday):** The correct answer to "should we automate this?" is "it depends" — on frequency, stability, team skills, and ROI. A single correct answer does not exist without context.
- **Test documentation formality (Thursday):** A one-page sprint test plan is appropriate for an Agile SaaS team. A versioned, sign-off-required, audit-retained test plan is appropriate for medical device software. Both are "test plans" — context determines which is right.
- **Jira workflow configuration (Friday):** Every organization's Jira looks different — different workflows, statuses, issue types, and field definitions. There is no universal "right" configuration. Context shapes the tool.
- **Coverage targets:** 80% statement coverage might be appropriate for a marketing website. 100% MC/DC is required for aviation safety-critical software (DO-178C Level A). Context determines the target.
- **Test team organization (Thursday):** Centralized, embedded, and hybrid QA models all have appropriate use cases. No single model is universally correct.

**Sprint-level application:**
Before asking "should we do X?", ask "in our context, does X serve our risk profile and constraints?" Blanket rules ("always have 85% coverage," "always automate regression") that ignore context produce misaligned testing investments.

---

### Principle 7: Absence-of-Errors Fallacy

**Core meaning:** Finding and fixing defects does not mean the software is useful to its users. A product can be technically correct (no defects against spec) and still fail to deliver value.

**This week's connections:**

- **Verification vs validation (Tuesday):** Verification asks "did we build it right?" (does it match the spec?). Validation asks "did we build the right thing?" (does it serve the user's actual need?). A system can pass all verification tests and still fail validation.
- **Sprint Review and stakeholder feedback (Thursday):** The Sprint Review is where stakeholders validate the increment. "It works as specified" is not enough — stakeholders must also say "this serves our actual need." Both are required.
- **Exploratory testing and usability (Wednesday):** Automated regression confirms specifications are met. Exploratory testing, particularly with usability oracles, catches the "technically works but is horribly confusing" failures that no specification ever captured.
- **Acceptance criteria quality (Tuesday):** If the acceptance criteria are poorly written (ambiguous, missing key behaviors), a 100% pass rate against those AC means nothing — you validated against the wrong target.

**Sprint-level application:**
After all tests pass, ask the Product Owner: "Does this do what you actually intended? Does it meet the user's real need?" This question is the tester's contribution to preventing the absence-of-errors fallacy before release.

---

## The Principles as a Sprint Decision Checklist

Use this checklist at key sprint moments:

**At release / go-no-go decision:**

| Principle | Question to ask |
|-----------|----------------|
| P1 | "What have we NOT tested? What unknowns remain?" |
| P2 | "Have we explicitly acknowledged the coverage limits for this sprint?" |
| P3 | "Were there defects this sprint that would have been cheaper to prevent at refinement?" |
| P4 | "Are we testing high-clustering areas with proportionally more attention?" |
| P5 | "Have we added new tests this sprint, or just re-run the same set?" |
| P6 | "Is our testing approach appropriate for this product's risk and context?" |
| P7 | "Did stakeholders validate that the increment serves their actual needs — not just that it matches spec?" |

If you can answer all seven honestly, you have applied the principles.

---

## Summary

- The seven principles are **professional decision guides**, not exam answers.
- **P1** (defects not proved absent) → honesty about coverage gaps at release.
- **P2** (exhaustive impossible) → EP, BVA, risk-based prioritization.
- **P3** (early testing) → Three Amigos, shift-left, requirements testability.
- **P4** (defect clustering) → risk-based test ordering, attention to complex/changed modules.
- **P5** (pesticide paradox) → exploratory sessions, error guessing, evolving the regression suite.
- **P6** (context dependent) → appropriate formality, tooling, and approach for the product and environment.
- **P7** (absence-of-errors fallacy) → stakeholder validation, V&V distinction, Sprint Review feedback.

---

## Additional Resources

- `2-tuesday/testing-principles.md` — Tuesday's deep-dive treatment of each principle with ISTQB definitions and individual examples.
- [ISTQB Foundation Syllabus — Section 1.3: Testing Principles](https://www.istqb.org/) — Official treatment with full definitions.
- [ISTQB Glossary](https://glossary.istqb.org/) — Canonical definitions for each principle's key terms.
- [Agile Testing (Crispin & Gregory)](https://lisacrispin.com/) — Principles applied throughout Agile delivery.
