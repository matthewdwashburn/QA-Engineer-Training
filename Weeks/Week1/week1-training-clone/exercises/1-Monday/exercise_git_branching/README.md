# Exercise: Git Branching & Merge Conflict Resolution

**Mode:** Implementation (Code Lab)
**Duration:** 60–90 minutes
**Day:** Monday | **Week:** 1 — Git & Python Fundamentals

---

## Objective

By the end of this exercise, you will be able to:
- Initialize a Git repository and make commits.
- Create and switch between branches.
- Merge branches and resolve a merge conflict.
- Use `git log --graph` to visualize branch history.

---

## Prerequisites

| Concept | Source |
|---------|--------|
| What is Git / Version Control | `written/1-Monday/introduction-to-git.md` |
| Working Directory, Staging, Commits | `written/1-Monday/git-working-directory-commits.md` |
| Branching & Committing on Branches | `written/1-Monday/committing-work-branches.md` |
| PRs, Merging, Merge Conflicts | `written/1-Monday/pull-requests-merging-merge-conflicts.md` |
| Instructor Demo | `demos/1-Monday/INSTRUCTOR_GUIDE.md` (Demo 1) |

---

## The Scenario

You are a QA engineer joining a new project. Your first task is to set up the project repository, create feature branches, and practice the merge workflow your team uses daily.

---

## Core Tasks

### Task 1: Initialize the Repository (10 min)

1. Create a new directory called `qa-project` and initialize it as a Git repo.
2. Create a file called `README.md` with the following content:
   ```markdown
   # QA Project
   A quality assurance automation project.
   ```
3. Stage and commit with the message: `"Initial commit: add README"`.
4. Verify your commit with `git log --oneline`.

**✅ Checkpoint:** You should see one commit in your log.

---

### Task 2: Create a Feature Branch (15 min)

1. Create and switch to a new branch called `feature/add-test-plan`.
2. Create a file called `test-plan.md` with the following content:
   ```markdown
   # Test Plan

   ## Scope
   - Login functionality
   - User registration
   - Password reset

   ## Test Types
   - Unit tests
   - Integration tests
   - End-to-end tests
   ```
3. Stage and commit: `"Add test plan document"`.
4. Create another file called `test-cases.md`:
   ```markdown
   # Test Cases

   ## TC-001: Valid Login
   - **Input:** Valid username and password
   - **Expected:** Redirect to dashboard

   ## TC-002: Invalid Login
   - **Input:** Invalid password
   - **Expected:** Error message displayed
   ```
5. Stage and commit: `"Add initial test cases"`.
6. Run `git log --oneline` and note that you have 3 commits on the feature branch.

**✅ Checkpoint:** You should have 3 commits on `feature/add-test-plan`, but only 1 on `main`.

---

### Task 3: Switch Back and Verify Isolation (5 min)

1. Switch back to `main`.
2. List the files in the directory.
3. **Question:** Where did `test-plan.md` and `test-cases.md` go? Write your answer as a comment in your terminal.

**✅ Checkpoint:** The feature branch files should NOT appear on `main`.

---

### Task 4: Make a Change on Main (10 min)

1. While on `main`, edit `README.md` to add a "Team" section:
   ```markdown
   # QA Project
   A quality assurance automation project.

   ## Team
   - Lead: Your Name
   - Role: QA Engineer
   ```
2. Stage and commit: `"Add team section to README"`.

---

### Task 5: Merge the Feature Branch (10 min)

1. Merge `feature/add-test-plan` into `main`.
2. Verify that `test-plan.md` and `test-cases.md` now exist on `main`.
3. Run `git log --oneline --graph` to see the merge history.
4. Delete the merged branch: `git branch -d feature/add-test-plan`.

**✅ Checkpoint:** All files should be present, and the branch should be deleted.

---

### Task 6: Simulate and Resolve a Merge Conflict (20 min)

This is the most important task — merge conflicts are a daily reality.

1. Create a new branch: `feature/update-test-plan`.
2. On the **feature branch**, edit `test-plan.md` — change the "Scope" section:
   ```markdown
   ## Scope
   - Login functionality
   - User registration
   - Password reset
   - Shopping cart
   - Payment processing
   ```
3. Commit: `"Expand test plan scope on feature branch"`.
4. Switch back to `main`.
5. On **main**, edit the SAME file `test-plan.md` — change the "Scope" section differently:
   ```markdown
   ## Scope
   - Login functionality
   - User registration
   - Password reset
   - User profile management
   - Admin dashboard
   ```
6. Commit: `"Update test plan scope on main"`.
7. Attempt to merge `feature/update-test-plan` into `main`.
8. **You should see a CONFLICT.** Open `test-plan.md` and observe the conflict markers:
   ```
   <<<<<<< HEAD
   (main's version)
   =======
   (feature branch's version)
   >>>>>>>
   ```
9. **Resolve the conflict** by combining both versions:
   ```markdown
   ## Scope
   - Login functionality
   - User registration
   - Password reset
   - Shopping cart
   - Payment processing
   - User profile management
   - Admin dashboard
   ```
10. Stage the resolved file and commit: `"Resolve merge conflict: combine scope updates"`.
11. Run `git log --oneline --graph` to see the full history.

**✅ Checkpoint:** The merge conflict should be resolved, and your log should show the merge commit.

---

## Stretch Goals (Optional)

- [ ] Push your repository to GitHub and create a Pull Request.
- [ ] Set up a branch protection rule on `main` (require at least 1 reviewer).
- [ ] Try the same exercise using `git rebase` instead of `git merge` — what's different?

---

## Definition of Done

- [ ] Repository initialized with at least 6 commits.
- [ ] At least 2 branches created and merged.
- [ ] 1 merge conflict resolved manually.
- [ ] `git log --oneline --graph` shows branching and merging history.
- [ ] All feature branches deleted after merging.
