# The Waterfall Model

## Learning Objectives

By the end of this reading you will be able to:

- Name and describe the **classic phases** of the Waterfall software lifecycle and explain what happens in each.
- Identify the **strengths** of Waterfall for predictable, regulated, or stable-requirement work.
- Explain the **limitations** that make Waterfall risky for fast-changing products or markets.
- Describe how a tester's role and timing differ in a Waterfall context compared to what you will study in Agile.

---

## Why This Matters

Week 5's epic is **Agile delivery and testing foundations**. Before you adopt or advocate for iterative methods, you need to understand **Waterfall** thoroughly — not as a straw man to dismiss, but as a legitimate model with real strengths in specific contexts.

Many organizations you will work in still use Waterfall for parts of their delivery (especially contractual, regulatory, or hardware-integrated work), even if their software development uses Agile. Testers who can articulate *why* Waterfall succeeds or fails in a given context add real value in planning and risk conversations. You also need to understand Waterfall's pitfalls so you can make the strongest case for **shift-left testing** — a core principle of Agile test philosophy.

---

## The Concept

### What Is Waterfall?

**Waterfall** is a **linear, sequential** software development lifecycle model: each phase largely completes before the next one begins, like water cascading down steps. The name itself is a metaphor — water flows *downward*, not back up. In a strict Waterfall model, you do not return to requirements once design begins, and you do not rework design once implementation starts.

This is fundamentally different from Agile's iterative approach, where you revisit planning, design, and testing in every short cycle.

### The Classic Phases

Waterfall is typically described with five to six phases. Names vary slightly by company and standard (e.g., the V-Model is a testing-centric variant of Waterfall), but the core sequence is:

---

#### Phase 1: Requirements

**What happens here:**
The project team (business analysts, stakeholders, architects) captures *everything* the system must do — functional requirements (features, behaviors) and non-functional requirements (performance, security, compliance). The output is typically a **Software Requirements Specification (SRS)** document.

Requirements are then **baselined**: formally approved and version-controlled. Changing a baselined requirement requires a formal **Change Request (CR)** process, which adds time and cost.

**Tester role in Waterfall requirements phase:**
- **Review** the SRS for testability: "Can we actually verify this requirement?" (e.g., "The system shall respond quickly" is untestable; "The system shall respond within 2 seconds under 1,000 concurrent users" is testable.)
- **Begin planning** the test approach and identifying what environments and data will be needed.
- In strict Waterfall, testers may have limited involvement here — a pattern Agile corrects.

---

#### Phase 2: Design

**What happens here:**
Architects and senior developers translate requirements into a **technical blueprint**. This typically produces:
- **High-Level Design (HLD):** Overall system architecture, technology choices, integration points, database schemas.
- **Low-Level Design (LLD):** Detailed module specifications, class/API definitions, algorithm choices.

Designs are reviewed and approved before coding begins.

**Tester role in Waterfall design phase:**
- Review designs for **testability** (observability, controllability, isolation).
- Begin producing a **test plan** aligned to the agreed design.
- Identify **risks**: complex modules, external dependencies, security-sensitive components.

---

#### Phase 3: Implementation (Coding)

**What happens here:**
Developers write code according to the design specifications. **Unit testing** at the individual component level may happen here (though in strict Waterfall, this varies). The output is **built and unit-tested components**.

**Tester role:**
- Develop and refine **test cases** based on requirements and design.
- Prepare **test environments** and **test data**.
- May not yet execute formal tests — that waits for the Testing phase.

---

#### Phase 4: Testing (Verification & Validation)

**What happens here:**
The completed, integrated system is formally tested against requirements. Typical testing activities include:
- **Integration testing** — Do components work together?
- **System testing** — Does the end-to-end system meet the SRS?
- **User Acceptance Testing (UAT)** — Do real users or business representatives confirm the system meets their needs?
- **Performance and security testing** where required.

Defects found are logged, fixed, and re-tested. If defects require design changes, the cost rises dramatically — because design was completed months earlier.

**Tester role:**
This is when testers are busiest in Waterfall. However, this is also where the model's biggest weakness bites: if requirements were ambiguous, if design had errors, or if implementation drifted from design, the tester is the first person to discover it — late in the project, under schedule pressure.

---

#### Phase 5: Deployment & Maintenance

**What happens here:**
The verified system is deployed to production. Ongoing work includes:
- **Bug fixes** for defects found in production.
- **Enhancements** — new features added via new mini-Waterfall cycles or change requests.
- **Performance monitoring** and operational support.

---

### Phase Gates: How Waterfall Controls Progress

Each phase in Waterfall ends with a **phase exit review** (also called a **phase gate** or **milestone**). Typical exit criteria might include:

| Phase | Exit Criteria Example |
|-------|-----------------------|
| Requirements | SRS reviewed, approved, and signed by sponsor and QA lead |
| Design | HLD and LLD peer-reviewed, updated per comments, and baseline |
| Implementation | Code complete, unit tests passing, code review sign-off |
| Testing | Exit criteria met (e.g., zero open critical defects, 90% test coverage pass) |
| Deployment | Go/no-go checklist complete, rollback plan approved |

These gates create **formal checkpoints** — useful for compliance and audit trails, but potentially slow when conditions change.

---

### Strengths of Waterfall

**Clarity and Predictability**
When requirements are stable and well-understood, Waterfall gives everyone a clear map: what will be built, when each phase ends, and what evidence shows completion. This is valuable for sponsors, auditors, and customers who need certainty.

**Strong Documentation Trail**
Every phase produces documents (SRS, HLD, LLD, Test Plan, Test Summary Report). In regulated industries (medical devices, defense, finance), this documentation is not optional — it is required for certification, regulatory approval, or contractual obligations.

**Simple to Schedule (in Theory)**
Phase-based planning maps naturally to project management tools and fixed-price contracts. "Phase 2 delivery by month 3" is easy to put on a Gantt chart.

**Suited for Stable, Well-Understood Problems**
Building a bridge, developing firmware for a pacemaker, or implementing a legally mandated reporting system — these have known, stable requirements. Waterfall's structure fits well.

---

### Limitations of Waterfall

**Late Feedback**
Problems discovered during the Testing phase were typically introduced in the Requirements or Design phase — months earlier. The cost to fix a defect at Testing is **5-10x** the cost to fix it at Requirements. By the time a tester finds a fundamental architectural flaw, it may require a complete redesign.

*Analogy:* Imagine building a house, painting every room, laying the carpet — and then discovering the floor plan had the bathroom in the wrong place. Waterfall's testing phase often finds "the bathroom is in the wrong place."

**Poor Fit for Changing Requirements**
Modern software markets move fast. User needs evolve. Competitors launch features. Regulatory guidance updates. In Waterfall, any significant change after requirements baseline triggers a formal Change Request, which is slow and expensive. Teams often choose between shipping outdated software or absorbing cost overruns.

**Risk of Big-Bang Integration**
Long gaps between specification and working software mean that integration risk accumulates silently. Two teams build components independently for six months, then integrate — and discover the interfaces were misunderstood. This "big bang" integration problem is one of the most common sources of Waterfall project failures.

**Testing Squeezed at the End**
When earlier phases slip (and they almost always do), testing is the buffer that gets compressed. A six-month testing window shrinks to six weeks. Coverage suffers. Testers are pressured to sign off on software they know is not fully validated. This pattern is so common it has a name: **schedule chicken** — everyone hoping someone else announces a delay first.

**No Working Software Until Very Late**
Stakeholders see nothing tangible until the Testing phase or beyond. By then, it may be too late to course-correct if the product misses the mark on user needs.

---

## Worked Example: Waterfall Phase Gate Language

Consider a fictional banking system project using Waterfall. Their phase exit policy might look like this:

```
REQUIREMENTS PHASE EXIT:
✅ SRS v1.0 approved by Business Owner
✅ Traceability matrix baseline complete
✅ Test planning begun (preliminary test plan v0.1 submitted)
✅ Compliance review complete (GDPR, PCI-DSS checklist)

DESIGN PHASE EXIT:
✅ HLD reviewed by Architecture Board
✅ LLD peer-reviewed by senior developers
✅ Security design reviewed by InfoSec

TESTING PHASE EXIT:
✅ Test execution complete (>= 95% test cases run)
✅ Zero open Critical (P1) defects
✅ <= 3 open High (P2) defects, all with accepted mitigation
✅ Performance test within agreed thresholds
✅ UAT sign-off from Product Owner
✅ Test Summary Report published
```

This is **policy**, not code — but it shapes when testers get involved, how much time they have, and what evidence they must produce. As a tester, you must understand and negotiate these gate criteria.

---

## Summary

- Waterfall moves linearly: **Requirements → Design → Implementation → Testing → Deployment & Maintenance**.
- **Phase gates** formalize hand-offs with reviews, approvals, and exit criteria — creating valuable audit trails.
- Waterfall **excels** when requirements are stable, regulation demands documentation, and the problem is well understood.
- Waterfall's **core weakness** is late discovery: requirements problems, design errors, and integration surprises all surface at the Testing phase — expensive to fix, and often under schedule pressure.
- Understanding Waterfall's limits is the **best motivation** for the shift-left, iterative, feedback-rich practices of Agile — the subject of the rest of Monday's reading.

---

## Additional Resources

- [ISO/IEC/IEEE 12207:2017 — Systems and Software Engineering — Life Cycle Processes](https://www.iso.org/standard/63712.html) — Formal process standard that many Waterfall-derived methodologies map to.
- [SWEBOK Guide v3](https://www.computer.org/education/bodies-of-knowledge/software-engineering/) — Software engineering body of knowledge, including lifecycle models chapter.
- [Chaos Report (Standish Group)](https://www.standishgroup.com/sample_research_files/chaos_report_1994.pdf) — Landmark 1994 research on software project failure rates, much attributed to Waterfall's late-feedback problem.
- [Agile Manifesto](https://agilemanifesto.org/) — Contrast: the four values and twelve principles that emerged as a direct response to Waterfall's limitations.
