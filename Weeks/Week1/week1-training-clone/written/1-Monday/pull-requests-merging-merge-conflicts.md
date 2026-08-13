# Pull Requests, Merging, and Merge Conflicts

## Learning Objectives
- Explain what a pull request (PR) is and why it's central to team workflows.
- Describe the main merging strategies: merge commit, squash, and rebase.
- Identify, understand, and resolve merge conflicts.

---

## Why This Matters

> **Weekly Epic Connection:** Branching lets you work in isolation. Merging brings that work back together. Pull requests add a *quality gate* — code review — between the two. As quality engineers, you'll participate in code reviews daily, both reviewing others' test code and having your own reviewed.

In any professional team, code doesn't go directly from a developer's machine into production. There's a review step — a checkpoint where teammates examine the changes, suggest improvements, and catch bugs before they reach the main branch. Pull requests formalize this process.

---

## The Concept

### What Is a Pull Request?

A **Pull Request** (PR) — called a **Merge Request** (MR) in GitLab — is a formal proposal to merge changes from one branch into another. It's not a Git feature; it's a feature of hosting platforms like GitHub, GitLab, and Bitbucket.

A PR provides:

| Feature | Description |
|---------|-------------|
| **Code Review** | Teammates can read your changes, leave comments, suggest edits, and approve or request changes. |
| **Discussion** | A thread for discussing design decisions, edge cases, and implementation details. |
| **CI/CD Integration** | Automated tests can run against the PR before merging. |
| **Audit Trail** | A permanent record of what changed, who reviewed it, and what was discussed. |

### The Pull Request Workflow

```
1. Create a feature branch
2. Make commits on the branch
3. Push the branch to remote (GitHub)
4. Open a Pull Request
5. Team reviews, discusses, requests changes
6. Make additional commits if needed
7. PR is approved
8. Merge the PR into main
9. Delete the feature branch
```

### Opening a Pull Request on GitHub

After pushing your branch:

```bash
git push origin feature/add-login-tests
```

On GitHub:
1. Navigate to your repository.
2. You'll see a banner: "**feature/add-login-tests** had recent pushes — **Compare & pull request**."
3. Click it.
4. Fill in:
   - **Title:** A concise description (e.g., "Add Selenium login page tests").
   - **Description:** What changed, why, and any context reviewers need.
   - **Reviewers:** Assign teammates to review.
   - **Labels:** Categorize (e.g., `enhancement`, `bug`, `tests`).
5. Click **Create Pull Request**.

### What Reviewers Look For

As a QA engineer, you'll both write and review code. When reviewing a PR, look for:

- **Correctness** — Does the code do what it claims?
- **Test coverage** — Are there tests? Do they cover edge cases?
- **Readability** — Is the code clear? Are variable names descriptive?
- **Style** — Does it follow the team's coding conventions?
- **Security** — Are there any hardcoded secrets or vulnerabilities?

---

## Merging Strategies

Once a PR is approved, it's time to merge. GitHub offers three merge strategies:

### 1. Merge Commit (Default)

```bash
git checkout main
git merge feature-branch
```

Creates a new **merge commit** that ties the two branches together:

```
    C1 ── C2 ── C3 ────────── M (merge commit)
                  \          /
                   C4 ── C5
                   (feature-branch)
```

**Pros:** Preserves the complete history, including exactly when branches diverged and merged.
**Cons:** Can create a cluttered history with many merge commits.

### 2. Squash and Merge

Combines all commits from the feature branch into **one single commit** on `main`:

```
# Before (feature branch has 3 commits):
main:    C1 ── C2 ── C3
feature:            ── C4 ── C5 ── C6

# After squash merge:
main:    C1 ── C2 ── C3 ── S1 (squashed)
```

```bash
git checkout main
git merge --squash feature-branch
git commit -m "Add login tests (#42)"
```

**Pros:** Clean, linear history. One commit per feature/fix.
**Cons:** You lose the individual commit history from the feature branch.

### 3. Rebase and Merge

Replays the feature branch commits **on top of** main, as if they were made sequentially:

```
# Before:
main:    C1 ── C2 ── C3
feature:        ── C4 ── C5

# After rebase:
main:    C1 ── C2 ── C3 ── C4' ── C5'
```

Note: `C4'` and `C5'` are *new* commits (different hashes) because their parent changed, but the content is identical.

**Pros:** Perfectly clean, linear history.
**Cons:** Rewrites commit history (original hashes change). Can cause issues if others are working off the same branch.

### Which Strategy Should You Use?

| Strategy | Best For | History |
|----------|----------|---------|
| **Merge commit** | Open-source projects, complex features where history matters | Complete but verbose |
| **Squash merge** | Most team projects, simple features | Clean, one commit per feature |
| **Rebase** | Personal branches, when linear history is critical | Clean but rewrites history |

Most professional teams use **squash merge** for day-to-day work and **merge commits** for large features or release branches.

---

## Merge Conflicts

### What Is a Merge Conflict?

A **merge conflict** occurs when Git can't automatically combine changes from two branches because both branches modified the **same lines** in the **same file**.

**Example scenario:**
- On `main`, line 5 of `config.py` says: `timeout = 30`
- On `feature-branch`, you changed line 5 to: `timeout = 60`
- Meanwhile, a teammate changed line 5 on `main` to: `timeout = 45`

Git doesn't know which change wins — `60` or `45`? It asks you to decide.

### When Conflicts Happen

Conflicts occur during:
- `git merge`
- `git rebase`
- `git pull` (which is essentially fetch + merge)
- Merging a pull request on GitHub

### Identifying a Conflict

When Git encounters a conflict:

```bash
git merge feature-branch
# Output:
# Auto-merging config.py
# CONFLICT (content): Merge conflict in config.py
# Automatic merge failed; fix conflicts and then commit the result.
```

Running `git status` shows:

```bash
git status
# Unmerged paths:
#   both modified:   config.py
```

### Anatomy of a Conflict Marker

Open the conflicted file and you'll see conflict markers:

```python
# config.py

database = "production_db"
<<<<<<< HEAD
timeout = 45
=======
timeout = 60
>>>>>>> feature-branch
retries = 3
```

Breaking this down:

| Marker | Meaning |
|--------|---------|
| `<<<<<<< HEAD` | Start of the changes from your **current branch** (what you're merging *into*). |
| `=======` | Divider between the two conflicting versions. |
| `>>>>>>> feature-branch` | End of the changes from the **incoming branch** (what you're merging *from*). |

### Resolving a Conflict

To resolve, you **edit the file** to produce the final desired result, removing all conflict markers:

**Option A: Keep the current branch's version**
```python
timeout = 45
```

**Option B: Keep the incoming branch's version**
```python
timeout = 60
```

**Option C: Combine both changes (the most common resolution)**
```python
timeout = 60  # Increased from 45 based on load testing results
```

**Option D: Write something entirely new**
```python
timeout = int(os.environ.get("REQUEST_TIMEOUT", 30))  # Configurable via environment
```

After editing:

```bash
# 1. Stage the resolved file
git add config.py

# 2. Complete the merge with a commit
git commit -m "Merge feature-branch, resolve timeout conflict"
```

### Multi-File Conflicts

In practice, conflicts can span multiple files. The process is the same — resolve each file individually, stage them, then commit once:

```bash
# Check which files are conflicted
git status

# Edit and resolve each file...

# Stage all resolved files
git add config.py test_config.py settings.yaml

# Commit the resolution
git commit -m "Merge feature-branch, resolve config conflicts"
```

### Aborting a Merge

Not ready to resolve conflicts? You can abort the merge entirely:

```bash
# Cancel the merge and return to pre-merge state
git merge --abort
```

### Preventing Conflicts

While conflicts can't be entirely avoided, you can minimize them:

1. **Pull frequently** — Keep your branch up to date with `main`.
2. **Keep branches short-lived** — The longer a branch lives, the more it diverges.
3. **Communicate** — If you know a teammate is editing the same file, coordinate.
4. **Small commits** — Smaller, focused changes are easier to merge.

```bash
# Keep your branch updated with main
git switch feature-branch
git merge main
# Resolve any conflicts now, while they're small
```

---

## Summary

- **Pull Requests** are formal proposals to merge code, providing a platform for code review, discussion, and automated testing.
- **Merge strategies**: merge commit (preserves history), squash (clean single commit), rebase (linear history).
- **Merge conflicts** happen when two branches modify the same lines — Git can't decide which change wins.
- Conflict markers (`<<<<<<<`, `=======`, `>>>>>>>`) show you both versions — you edit the file to produce the final result.
- **Prevent conflicts** by pulling frequently, keeping branches short-lived, and communicating with your team.

---

## Additional Resources
- [GitHub Docs — About Pull Requests](https://docs.github.com/en/pull-requests/collaborating-with-pull-requests/proposing-changes-to-your-work-with-pull-requests/about-pull-requests)
- [Git Docs — Basic Branching and Merging](https://git-scm.com/book/en/v2/Git-Branching-Basic-Branching-and-Merging)
- [Atlassian — Resolve Merge Conflicts](https://www.atlassian.com/git/tutorials/using-branches/merge-conflicts)
