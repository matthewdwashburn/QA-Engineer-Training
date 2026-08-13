# Professionalism in Quality Engineering

## Learning Objectives
- Apply professional communication practices in technical settings.
- Conduct and participate in effective code reviews.
- Manage time and prioritize tasks in an engineering environment.

---

## Why This Matters

> **Weekly Epic Connection:** Technical skills get you the job. Professional skills keep you the job — and advance your career. Quality engineering is inherently a collaborative, communicative role: you review code, report defects, coordinate with developers, and represent quality standards for the team.

---

## The Concept

### Professional Communication

#### Written Communication

As a QA engineer, your written communication matters enormously. Bug reports, test plans, code reviews, and Slack messages are read by teammates, managers, and stakeholders.

**Bug Reports — The Professional Standard:**

```markdown
## Bug Report: Checkout fails for international addresses

**Environment:** Staging (v2.4.1), Chrome 122, Windows 11
**Severity:** High
**Steps to Reproduce:**
1. Add any item to cart
2. Proceed to checkout
3. Enter an international address (e.g., UK format)
4. Click "Submit Order"

**Expected:** Order is submitted successfully
**Actual:** Error 500 — "Invalid address format"

**Additional Context:**
- Domestic addresses work correctly
- Issue may be related to address validation regex (see `/src/validators/address.py:47`)
- Affects ~15% of our user base (international customers)
```

**Slack/Teams Communication:**
```
❌ "checkout is broken"
✅ "The checkout flow returns a 500 error for international addresses on staging (v2.4.1). I've filed BUG-1234 with repro steps. @dev-team — does this block the Thursday release?"
```

#### Verbal Communication

- **Stand-ups:** Be concise. "Yesterday I completed API test coverage for the auth module. Today I'm investigating the flaky checkout test. No blockers."
- **Meetings:** Come prepared. If presenting test results, have the data ready.
- **Asking for help:** State what you tried first. "I'm seeing a timeout on the payment API test. I've verified the endpoint is up, checked the test timeout settings, and confirmed the test data exists. Any ideas?"

### Code Reviews

#### As a Reviewer

| Do | Don't |
|---|---|
| Focus on logic, correctness, and patterns | Nitpick formatting (that's what linters are for) |
| Ask questions: "What happens if X is null?" | Make demands: "Change this now" |
| Suggest alternatives with reasoning | Just say "This is wrong" |
| Acknowledge good work: "Nice approach here" | Only point out negatives |
| Review promptly (within 4 hours ideally) | Let PRs sit for days |

**Review comment examples:**

```
❌ "This is bad code."
✅ "Consider extracting this into a helper function — it would make the test more readable and we could reuse it in test_checkout.py too."

❌ "Wrong."
✅ "This assertion checks the status code but not the response body. Could we add a check for the expected user ID in the response?"
```

#### As the Author

- Write clear PR descriptions — the "why" matters more than the "what."
- Respond to all comments, even if it's "Good point, fixed in commit abc123."
- Don't take feedback personally — reviews improve code quality.
- Keep PRs small — 200-400 lines is ideal.

### Time Management

#### The Eisenhower Matrix for QA

| | **Urgent** | **Not Urgent** |
|---|---|---|
| **Important** | Blocking bugs, release-day testing, critical test failures | Test automation, code refactoring, documentation |
| **Not Important** | Flaky test investigations, ad-hoc requests | Cosmetic test improvements, exploring new tools |

**Prioritize:** Important/Urgent → Important/Not Urgent → Urgent/Not Important → Neither.

#### Practical Tips

1. **Timeboxing:** Set time limits for investigation. "I'll spend 30 minutes on this flaky test. If unresolved, I'll log it and move on."
2. **Task estimation:** For every task, estimate the time needed. Track actuals to improve future estimates.
3. **Communication:** If you're going to miss a deadline, communicate early — not at the last moment.
4. **Documentation:** Document as you go, not after. Future-you will thank present-you.

### Professional Ethics in QA

- **Honest reporting:** Never hide bugs to meet a deadline. Your job is to report quality accurately.
- **Reproducibility:** Always verify you can reproduce a bug before filing it.
- **Constructive feedback:** Critique code, not people.
- **Continuous learning:** The tech landscape evolves — dedicate time to learning new tools and practices.
- **Ownership:** When you find a problem, own it — track it to resolution even if the fix is someone else's.

---

## Summary

- **Communication** is a core QA skill — bug reports, code reviews, and status updates should be clear, professional, and actionable.
- **Code reviews** should be constructive, timely, and focused on logic and patterns.
- **Time management:** Use timeboxing, estimate tasks, and communicate proactively about deadlines.
- **Ethics:** Report quality honestly, reproduce bugs before filing, and own problems to resolution.

---

## Additional Resources
- [Google Engineering Practices — Code Review Guide](https://google.github.io/eng-practices/review/)
- [Atlassian — Why Code Reviews Matter](https://www.atlassian.com/agile/software-development/code-reviews)
- [Ministry of Testing — Resources for Testers](https://www.ministryoftesting.com/)
