# Git Flow and Branching Strategies

## Learning Objectives
- Compare the three major branching strategies: Git Flow, GitHub Flow, and Trunk-Based Development.
- Identify when each strategy is most appropriate.
- Explain how branching strategies contribute to software quality.

---

## Why This Matters

> **Weekly Epic Connection:** You've learned *how* to create branches and merge. Now the question is: *when* do you create branches? What do you name them? How long should they live? Branching strategies answer these questions by giving your team a shared playbook.

Without an agreed-upon strategy, teams devolve into chaos — branches with random names, no clear process for releasing, and confusion about which branch represents "production." A branching strategy is like traffic rules: everyone follows the same conventions so the code flows smoothly.

---

## The Concept

### 1. Git Flow

**Created by:** Vincent Driessen (2010)
**Best for:** Projects with scheduled releases, longer development cycles, or multiple supported versions.

Git Flow uses **five types of branches**:

```
main (production)
  │
  ├── hotfix/fix-critical-bug ──► merge back to main AND develop
  │
develop (integration)
  │
  ├── feature/add-login-tests ──► merge into develop
  ├── feature/update-reporting ──► merge into develop
  │
  ├── release/v2.1 ──► final testing, then merge to main AND develop
```

| Branch Type | Purpose | Created From | Merges Into | Lifespan |
|-------------|---------|-------------|-------------|----------|
| `main` | Production code | — | — | Permanent |
| `develop` | Integration branch for next release | `main` | — | Permanent |
| `feature/*` | Individual features/tasks | `develop` | `develop` | Days to weeks |
| `release/*` | Prepare a release (final testing, version bumps) | `develop` | `main` + `develop` | Days |
| `hotfix/*` | Emergency production fixes | `main` | `main` + `develop` | Hours |

**Workflow example:**
1. Create `feature/add-login-tests` from `develop`.
2. Work on the feature, make commits.
3. Open a PR to merge `feature/add-login-tests` into `develop`.
4. When enough features are ready for release, create `release/v2.1` from `develop`.
5. Test the release branch, fix any last bugs directly on it.
6. Merge `release/v2.1` into `main` (tag it as `v2.1`).
7. Merge `release/v2.1` back into `develop` (so develop gets the bug fixes).

**Pros:**
- Clear separation between development, staging, and production.
- Supports multiple release versions simultaneously.
- Well-suited for teams with formal release schedules.

**Cons:**
- Overhead — lots of branches, lots of merges.
- Long-lived branches can lead to painful merge conflicts.
- Overkill for small teams or continuously-deployed applications.

---

### 2. GitHub Flow

**Created by:** GitHub (2011)
**Best for:** Web apps, SaaS products, teams that deploy continuously.

GitHub Flow is Git Flow's minimalist cousin. It uses only **two branch types**:

```
main (always deployable)
  │
  ├── feature/add-tests ──► PR ──► review ──► merge to main ──► deploy
  ├── fix/broken-api ──► PR ──► review ──► merge to main ──► deploy
```

**The rules are simple:**
1. `main` is **always deployable** — every commit on `main` could go to production.
2. Create a descriptive branch from `main` for every piece of work.
3. Open a Pull Request.
4. Get a review, discuss, and iterate.
5. Merge the PR into `main`.
6. Deploy immediately (or let CI/CD deploy automatically).

**Pros:**
- Simple — only one branch type to understand.
- Fast — short-lived branches mean less divergence and fewer conflicts.
- Natural fit for continuous deployment.

**Cons:**
- No staging environment by default — what's in `main` is production.
- Less structure for coordinating large releases.
- Requires robust automated testing (since you deploy every merge).

---

### 3. Trunk-Based Development

**Used by:** Google, Meta, Netflix, high-performing engineering organizations.
**Best for:** Teams with strong CI/CD, feature flags, and high deployment frequency.

Trunk-based development takes minimalism even further:

```
main (the "trunk" — only branch that matters)
  │
  ├── small-change ──► merge within hours
  ├── tiny-fix ──► merge within hours
```

**The rules:**
1. Everyone commits to `main` (or to very short-lived branches that merge within hours).
2. Branches, if used, last **no more than a day or two**.
3. Incomplete features are hidden behind **feature flags** (runtime toggles).
4. Automated tests run on every commit.

```python
# Feature flags in action
if feature_flags.is_enabled("new_checkout_flow"):
    run_new_checkout()
else:
    run_classic_checkout()
```

**Pros:**
- Virtually eliminates merge conflicts (branches are so short-lived).
- Fastest feedback loop — changes integrate continuously.
- Aligned with DevOps best practices (continuous integration/delivery).

**Cons:**
- Requires excellent CI/CD and automated test coverage.
- Requires feature flag infrastructure.
- Requires disciplined, small commits — big changes are broken into small increments.
- Harder for junior teams without strong testing practices.

---

### Side-by-Side Comparison

| Factor | Git Flow | GitHub Flow | Trunk-Based |
|--------|----------|-------------|-------------|
| **Complexity** | High | Low | Very Low |
| **Branch types** | 5 | 2 (main + feature) | 1–2 (main + tiny) |
| **Branch lifespan** | Days to weeks | Hours to days | Hours |
| **Release process** | Formal release branches | Deploy on merge | Deploy on merge |
| **Best team size** | Medium to large | Small to medium | Any (with CI/CD) |
| **Merge conflicts** | Frequent | Occasional | Rare |
| **CI/CD requirement** | Nice to have | Recommended | Essential |
| **Multiple versions** | Supported | Single version | Single version |

### How to Choose

Ask your team these questions:

```
Do you need to support multiple release versions simultaneously?
  → YES → Git Flow

Do you deploy continuously and have strong CI/CD?
  → YES → Trunk-Based Development

Are you somewhere in between?
  → GitHub Flow (the most common choice)
```

For **most teams starting out**, **GitHub Flow** is the best balance of simplicity and structure. As your testing and CI/CD practices mature, you may evolve toward trunk-based development.

### Real-World Context

| Company | Strategy | Why |
|---------|----------|-----|
| Google | Trunk-based | Monorepo, world-class CI, feature flags |
| Facebook/Meta | Trunk-based | Rapid iteration, feature flags |
| GitHub | GitHub Flow | Continuous deployment, simple model |
| Enterprise banking | Git Flow | Regulatory requirements, scheduled releases |
| Most startups | GitHub Flow | Small teams, moving fast |

---

## Summary

- **Git Flow** uses multiple long-lived branches and formal release processes — great for scheduled releases and enterprise environments.
- **GitHub Flow** is simple: branch from `main`, PR, review, merge, deploy — ideal for most teams.
- **Trunk-Based Development** keeps everyone on `main` with tiny, short-lived branches and feature flags — the strategy of high-performing engineering organizations.
- There is no universally "best" strategy — choose based on your team size, release cadence, and CI/CD maturity.

---

## Additional Resources
- [Vincent Driessen — "A Successful Git Branching Model" (Git Flow)](https://nvie.com/posts/a-successful-git-branching-model/)
- [GitHub Flow Guide](https://docs.github.com/en/get-started/using-github/github-flow)
- [Trunk-Based Development — trunkbaseddevelopment.com](https://trunkbaseddevelopment.com/)
