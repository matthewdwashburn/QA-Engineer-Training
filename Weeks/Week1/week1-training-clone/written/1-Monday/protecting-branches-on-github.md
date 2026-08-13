# Protecting Branches on GitHub

## Learning Objectives
- Explain why branch protection is critical in team environments.
- Configure branch protection rules in GitHub.
- Describe required reviews, status checks, and other protection mechanisms.

---

## Why This Matters

> **Weekly Epic Connection:** Git gives your team powerful tools — but power needs guardrails. Branch protection prevents accidental (or intentional) pushes directly to `main`, enforces code review, and ensures automated tests pass before changes are merged. As a quality engineer, these guardrails are part of your quality strategy.

Consider this nightmare scenario: A junior developer accidentally pushes a broken commit directly to `main` at 4:55 PM on a Friday. The CI/CD pipeline deploys it automatically. The production site goes down. Nobody noticed because the code was never reviewed.

Branch protection rules prevent this entirely. They ensure that the `main` branch — the branch that represents your production-ready code — can only be modified through a controlled, reviewable process.

---

## The Concept

### What Is Branch Protection?

**Branch protection** is a set of rules you configure on a GitHub repository that restrict how certain branches (typically `main`) can be modified. Once enabled, these rules apply to *everyone* — even repository admins (optionally).

### Available Protection Rules

GitHub offers several protection rules that can be combined:

#### 1. Require Pull Request Reviews

```
✅ Require a pull request before merging
   ✅ Require approvals: 1 (or more)
   ✅ Dismiss stale pull request approvals when new commits are pushed
   ✅ Require review from Code Owners
```

**What it does:**
- No one can push directly to the protected branch.
- All changes must go through a pull request.
- At least *N* teammates must approve the PR before it can be merged.

**Dismiss stale approvals** means: If a reviewer approves your PR, then you push more commits, the approval is invalidated and a fresh review is required. This prevents sneaking in changes after approval.

#### 2. Require Status Checks

```
✅ Require status checks to pass before merging
   ✅ Require branches to be up to date before merging
   Select checks: [unit-tests] [lint] [build]
```

**What it does:**
- Automated checks (CI/CD pipelines, linters, test suites) must pass before the PR can be merged.
- If the tests fail, the merge button is blocked.

This is one of the most important rules for quality engineering — it means **no code can reach `main` without passing your automated tests**.

#### 3. Require Signed Commits

```
✅ Require signed commits
```

**What it does:**
- All commits on the protected branch must be cryptographically signed with GPG or SSH keys.
- This verifies that the commit actually came from the person it claims to be from.

#### 4. Require Linear History

```
✅ Require linear history
```

**What it does:**
- Only squash merges or rebase merges are allowed — no merge commits.
- This ensures a clean, linear commit history on the protected branch.

#### 5. Restrict Who Can Push

```
✅ Restrict who can push to matching branches
   Allowed: [team-leads] [release-manager]
```

**What it does:**
- Only specified users, teams, or apps can push to (or merge into) the protected branch.

#### 6. Do Not Allow Bypassing

```
✅ Do not allow bypassing the above settings
```

**What it does:**
- Even repository administrators must follow the protection rules.
- Without this, admins can bypass all rules — which defeats the purpose.

### Setting Up Branch Protection (Step by Step)

1. Navigate to your repository on GitHub.
2. Go to **Settings** → **Branches**.
3. Under "Branch protection rules," click **Add rule**.
4. In "Branch name pattern," type `main`.
5. Configure the rules you want (see above).
6. Click **Create** or **Save changes**.

### Recommended Configuration for Teams

For a typical team project, here's a solid starting configuration:

| Rule | Setting | Why |
|------|---------|-----|
| Require PR before merging | ✅ Yes | Forces code review for all changes. |
| Required approvals | 1–2 | At least one teammate reviews every change. |
| Dismiss stale approvals | ✅ Yes | Prevents sneaking changes after approval. |
| Require status checks | ✅ Yes | Ensures tests pass before merge. |
| Required checks | Unit tests, linting | Catches bugs and style issues automatically. |
| Require up-to-date branches | ✅ Yes | Ensures the PR is tested against the latest `main`. |
| Include administrators | ✅ Yes | Nobody gets to skip the rules. |

### What Happens When Rules Are Active?

**For the developer:**

When you try to push directly to a protected branch:
```bash
git push origin main
# Error: push declined — protected branch
# Use a pull request to update this branch.
```

**On the pull request page:**

GitHub shows a checklist of requirements:

```
✅ Review required — 1 approving review required
   ✅ Approved by @teammate
✅ Status checks — All checks have passed
   ✅ unit-tests (3m 22s)
   ✅ lint (45s)
✅ Branch is up to date with main

[Merge pull request] ← Button is now green/active
```

If any requirement is not met, the merge button is grayed out and disabled.

### Code Owners (CODEOWNERS File)

GitHub supports a special file called `CODEOWNERS` that automatically assigns reviewers based on which files are changed:

```
# .github/CODEOWNERS

# The QA team must review all test files
tests/                 @myorg/qa-team

# The DevOps team must review CI/CD configs
.github/workflows/     @myorg/devops-team

# The security team must review auth code
src/auth/              @myorg/security-team

# Default: the tech lead reviews everything else
*                      @tech-lead
```

When a PR modifies files matching these patterns, the specified owners are automatically requested as reviewers. Combined with the "Require review from Code Owners" protection rule, this ensures the right experts review the right code.

### Rulesets (Newer GitHub Feature)

GitHub has introduced **Repository Rulesets** as a more powerful evolution of branch protection:

- Can target multiple branches and tags with patterns.
- Can be applied at the **organization level** (not just per-repo).
- Offer more granular controls.

Rulesets are configured under **Settings** → **Rules** → **Rulesets** and provide the same protections with additional flexibility.

---

## Summary

- **Branch protection** prevents direct pushes to critical branches, enforcing quality gates.
- **Required PR reviews** ensure every change is reviewed by at least one teammate.
- **Required status checks** block merging until automated tests and linters pass.
- **CODEOWNERS** automatically assigns the right reviewers based on which files change.
- These rules should apply to **everyone**, including administrators.
- Branch protection is a core part of any team's quality engineering strategy.

---

## Additional Resources
- [GitHub Docs — Managing Branch Protection Rules](https://docs.github.com/en/repositories/configuring-branches-and-merges-in-your-repository/managing-protected-branches/managing-a-branch-protection-rule)
- [GitHub Docs — About CODEOWNERS](https://docs.github.com/en/repositories/managing-your-repositorys-settings-and-features/customizing-your-repository/about-code-owners)
- [GitHub Docs — About Rulesets](https://docs.github.com/en/repositories/configuring-branches-and-merges-in-your-repository/managing-rulesets/about-rulesets)
