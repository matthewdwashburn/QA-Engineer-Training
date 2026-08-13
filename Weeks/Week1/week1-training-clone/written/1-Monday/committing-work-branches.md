# Committing Work and Branches

## Learning Objectives
- Explain what a Git branch is and why branching is essential for team collaboration.
- Create, switch between, and delete branches.
- Make commits on separate branches and understand how branches diverge.

---

## Why This Matters

> **Weekly Epic Connection:** Branching is what turns Git from a simple "save" system into a powerful collaboration platform. In quality engineering, you'll work on test suites, automation scripts, and framework code alongside teammates — branches let everyone work simultaneously without stepping on each other's toes.

Imagine a team of three QA engineers working on the same test framework:
- **Alex** is adding Selenium tests for the login page.
- **Sam** is refactoring the database helper utilities.
- **Jordan** is fixing a flaky test in the checkout flow.

Without branches, all three are editing the same codebase at the same time — a recipe for chaos. With branches, each person gets their own **isolated copy** of the code. When their work is ready, they merge it back into the main codebase.

---

## The Concept

### What Is a Branch?

A **branch** in Git is simply a lightweight, movable pointer to a specific commit. That's it — no file copying, no duplication. Git branches are incredibly fast and cheap to create because they're just pointers.

```
                        feature-branch
                              │
                              ▼
    C1 ◄── C2 ◄── C3 ◄── C4
                    ▲
                    │
                   main
```

In this diagram:
- `main` points to commit `C3`.
- `feature-branch` points to commit `C4`, which was created *on top of* `C3`.
- Commits `C1` through `C3` are shared history. Commit `C4` exists only on `feature-branch`.

### The `main` Branch

When you initialize a repository (`git init`) or clone one, you start on a default branch — typically called `main` (or `master` in older repos). This is just a regular branch with a conventional name. There's nothing technically special about it — it's special only because teams agree to treat it as the "official" version of the code.

### HEAD — Where Are You Right Now?

**HEAD** is a special pointer that tells Git which branch (and commit) you're currently working on. Think of HEAD as "You Are Here" on a map.

```bash
git log --oneline

# Output:
# 3a7f2b1 (HEAD -> main) Add user authentication
# 1c4d8e2 Initial commit
```

The `(HEAD -> main)` notation tells you: "You are on the `main` branch, which is at commit `3a7f2b1`."

### Creating a Branch

```bash
# Create a new branch (does NOT switch to it)
git branch feature-login-tests

# Verify branches — the asterisk (*) marks your current branch
git branch
# Output:
#   feature-login-tests
# * main
```

At this point, both `main` and `feature-login-tests` point to the **same commit**. They'll diverge once you make new commits on one of them.

### Switching Branches

```bash
# Switch to the new branch
git checkout feature-login-tests

# Or use the newer command (Git 2.23+):
git switch feature-login-tests
```

Now HEAD points to `feature-login-tests`. Any commits you make will advance this branch while `main` stays where it is.

### Create + Switch in One Command

This is the most common pattern — you typically want to create a branch and immediately switch to it:

```bash
# Shortcut: create and switch in one step
git checkout -b feature-login-tests

# Or with the newer syntax:
git switch -c feature-login-tests
```

### Committing on a Branch

Once you've switched to a branch, the workflow is identical to what you've already learned — edit, stage, commit:

```bash
# You're on feature-login-tests
echo "def test_login(): pass" > test_login.py
git add test_login.py
git commit -m "Add skeleton for login test"
```

Now the branches have diverged:

```
    C1 ◄── C2  ◄── C3 (main)
                     ◄── C4 (feature-login-tests, HEAD)
```

`main` is still at `C3`. `feature-login-tests` has moved forward to `C4`.

### Switching Back

```bash
# Switch back to main
git switch main

# Look at your files — test_login.py is GONE
ls
```

Wait, where did `test_login.py` go? It's safe — it exists on `feature-login-tests`. When you switch branches, **Git swaps out the files in your working directory** to match the branch you're switching to. Switch back to `feature-login-tests`, and the file reappears.

```bash
git switch feature-login-tests
ls
# test_login.py is back!
```

This is why Git branches are so powerful — they give you **completely isolated workspaces** that share the same folder on your filesystem.

### Viewing All Branches

```bash
# List local branches
git branch
# Output:
# * feature-login-tests
#   main

# List local and remote branches
git branch -a

# Show branches with their latest commit
git branch -v
```

### Deleting a Branch

Once a branch has been merged and is no longer needed:

```bash
# Delete a fully merged branch
git branch -d feature-login-tests

# Force-delete an unmerged branch (⚠️ use with caution)
git branch -D abandoned-experiment
```

The lowercase `-d` is the safe option — Git will refuse if the branch has unmerged work. The uppercase `-D` forces deletion regardless.

### Practical Example: Complete Branch Workflow

Here's a realistic workflow you'd follow as a QA engineer:

```bash
# 1. Start on main, make sure it's up to date
git switch main
git pull origin main

# 2. Create a feature branch for your work
git switch -c feature/add-checkout-tests

# 3. Do your work — create test files, edit configs, etc.
echo "def test_add_to_cart(): pass" > test_checkout.py
echo "def test_payment(): pass" >> test_checkout.py

# 4. Stage and commit
git add test_checkout.py
git commit -m "Add checkout test scaffolding"

# 5. Continue working...
echo "def test_shipping(): pass" >> test_checkout.py
git add test_checkout.py
git commit -m "Add shipping test skeleton"

# 6. Push the branch to the remote
git push origin feature/add-checkout-tests
```

At this point, your branch exists both locally and on GitHub. You're now ready to create a pull request — which we'll cover in the next reading.

### Branch Naming Conventions

Teams typically adopt a naming convention for branches. Common patterns:

| Pattern | Example | Use Case |
|---------|---------|----------|
| `feature/<description>` | `feature/add-login-tests` | New functionality |
| `bugfix/<description>` | `bugfix/fix-flaky-checkout-test` | Bug fixes |
| `hotfix/<description>` | `hotfix/critical-auth-bypass` | Urgent production fixes |
| `chore/<description>` | `chore/update-dependencies` | Maintenance tasks |
| `<initials>/<description>` | `js/refactor-helpers` | Personal feature work |

**Rules of thumb:**
- Use lowercase with hyphens (not spaces or underscores).
- Keep names descriptive but concise.
- Include a ticket number if your team uses them: `feature/JIRA-1234-add-login-tests`.

---

## Summary

- A **branch** is a lightweight pointer to a commit — creating one is instant and free.
- Use `git switch -c <name>` to create and switch to a new branch in one step.
- Commits made on a branch do **not** affect other branches.
- Switching branches swaps the files in your working directory to match the target branch.
- Use descriptive branch names that reflect the purpose of the work.
- Delete merged branches to keep your repository tidy.

---

## Additional Resources
- [Git Docs — Git Branching](https://git-scm.com/book/en/v2/Git-Branching-Branches-in-a-Nutshell)
- [Atlassian — Using Branches](https://www.atlassian.com/git/tutorials/using-branches)
- [GitHub Docs — About Branches](https://docs.github.com/en/pull-requests/collaborating-with-pull-requests/proposing-changes-to-your-work-with-pull-requests/about-branches)
