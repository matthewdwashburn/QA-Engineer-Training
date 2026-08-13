# Git Working Directory, Staging Area, and Commits

## Learning Objectives
- Explain the three main areas in a Git workflow: Working Directory, Staging Area, and Repository.
- Describe the lifecycle of a file through these areas.
- Use fundamental Git commands (`git init`, `git status`, `git add`, `git commit`) with confidence.

---

## Why This Matters

> **Weekly Epic Connection:** Understanding the working directory → staging → commit pipeline is *the* foundational Git skill. Every other Git concept — branching, merging, pull requests — builds directly on your ability to create clean, intentional commits.

Think of Git's workflow like packing for a trip:

1. **Working Directory** = Your messy bedroom. Clothes are everywhere — some clean, some dirty, some you might want to bring.
2. **Staging Area** = Your open suitcase on the bed. You deliberately choose what goes in.
3. **Repository (Commit)** = The zipped, labeled suitcase. It's sealed, tagged with your name and date, and stored in the closet.

You wouldn't throw everything into the suitcase blindly. You *curate* what goes in. Git works the same way — the staging area lets you be intentional about what goes into each commit.

---

## The Concept

### The Three Areas of Git

Every Git project has three conceptual areas:

```
┌─────────────────────────────────────────────────────────────┐
│                        Your Computer                        │
│                                                             │
│  ┌──────────────┐    ┌──────────────┐    ┌──────────────┐  │
│  │   Working    │    │   Staging    │    │  Repository  │  │
│  │  Directory   │───►│    Area      │───►│   (.git)     │  │
│  │              │    │  (Index)     │    │              │  │
│  │  Your files  │    │  "Ready to   │    │  Committed   │  │
│  │  as you see  │    │   commit"    │    │  history     │  │
│  │  them        │    │              │    │              │  │
│  └──────────────┘    └──────────────┘    └──────────────┘  │
│       git add ──────────►      git commit ──────────►       │
└─────────────────────────────────────────────────────────────┘
```

#### 1. Working Directory

The **Working Directory** (also called the "working tree") is simply the folder on your computer where your project files live. When you edit a file in VS Code, Notepad, or any editor, you're editing files in the working directory.

Git constantly compares the working directory against the last commit to detect changes. Files in the working directory can be in one of several states:

| State | Meaning |
|-------|---------|
| **Untracked** | A new file Git has never seen before. |
| **Modified** | A tracked file that has been changed since the last commit. |
| **Unmodified** | A tracked file that hasn't changed — matches the last commit. |

#### 2. Staging Area (Index)

The **Staging Area** (also called the "index") is a holding area where you prepare the *next commit*. When you run `git add`, you're telling Git: "I want this specific change to be part of my next commit."

Why not just commit directly? Because the staging area gives you **surgical control**:

- You changed 5 files, but only 3 are related to the bug fix you're committing? Stage only those 3.
- You made 200 lines of changes in one file, but only the first 50 are ready? You can stage just those lines.

The staging area enforces a discipline of **clean, focused commits**.

#### 3. Repository (.git directory)

The **Repository** is the hidden `.git` folder at the root of your project. It contains:
- The complete history of every commit.
- All branches and tags.
- Configuration metadata.

You should **never** manually edit files inside `.git`. Git manages it for you.

### Initializing a Repository

Before Git can track anything, you need to create a repository:

```bash
# Navigate to your project folder
cd my-project

# Initialize a new Git repository
git init
```

This creates the `.git` folder. Your project is now a Git repo. Note: if you *clone* a repository from GitHub, `git init` has already been done for you.

### The File Lifecycle in Detail

```
Untracked ──► Staged ──► Committed
                ▲             │
                │             ▼
             Modified ◄── Unmodified
```

Let's walk through a complete lifecycle:

**Step 1: Create a new file (Untracked)**

```bash
echo "Hello, Git!" > hello.txt
git status
```

Output:
```
On branch main
Untracked files:
  (use "git add <file>..." to include in what will be committed)
        hello.txt
```

Git sees the file but doesn't track it. It's literally saying "I know this file exists, but it's not my responsibility yet."

**Step 2: Stage the file (`git add`)**

```bash
git add hello.txt
git status
```

Output:
```
On branch main
Changes to be committed:
  (use "git restore --staged <file>..." to unstage)
        new file:   hello.txt
```

The file is now in the staging area, ready to be committed.

**Step 3: Commit the file (`git commit`)**

```bash
git commit -m "Add hello.txt with greeting message"
git status
```

Output:
```
On branch main
nothing to commit, working tree clean
```

The file is now **committed** — permanently recorded in your project's history. The working directory and the repository are in sync.

**Step 4: Modify the file (Modified)**

```bash
echo "Welcome to version control." >> hello.txt
git status
```

Output:
```
On branch main
Changes not staged for commit:
  (use "git add <file>..." to update what will be committed)
        modified:   hello.txt
```

Git detects the change. The file is modified but *not yet staged*.

**Step 5: Stage and commit again**

```bash
git add hello.txt
git commit -m "Add welcome line to hello.txt"
```

The cycle repeats: modify → stage → commit.

### Essential Commands Reference

| Command | What It Does |
|---------|-------------|
| `git init` | Creates a new Git repository in the current directory. |
| `git status` | Shows the current state of the working directory and staging area. |
| `git add <file>` | Stages a specific file for the next commit. |
| `git add .` | Stages **all** changed and new files in the current directory (and subdirectories). |
| `git commit -m "message"` | Creates a commit with the staged changes and a descriptive message. |
| `git log` | Shows the commit history. |
| `git log --oneline` | Shows a condensed one-line-per-commit history. |
| `git diff` | Shows changes in the working directory that are NOT yet staged. |
| `git diff --staged` | Shows changes that ARE staged (ready to commit). |

### Anatomy of a Commit

Each commit stores:

```
commit 3a7f2b1e...   ← Unique SHA-1 hash (40-character identifier)
Author: Jane Smith <jane@example.com>
Date:   Mon Apr 5 10:30:00 2026 -0400

    Add user authentication module    ← Commit message

    - Implement login() function
    - Add password hashing with bcrypt
    - Create session management
```

Important properties of a commit:
- **Immutable** — Once created, a commit cannot be modified. (You can create a *new* commit that reverses it, but the original remains in history.)
- **Unique hash** — The SHA-1 hash is computed from the content, so identical content always produces the same hash.
- **Parent pointer** — Every commit (except the first) points to its parent, forming a chain of history.

### Writing Good Commit Messages

Your commit messages are communication to your future self and your team. Follow these conventions:

**Do:**
```
✅ Fix null pointer exception in login validation
✅ Add unit tests for payment processing module
✅ Refactor database connection pool for thread safety
```

**Don't:**
```
❌ fixed stuff
❌ WIP
❌ asdfasdf
❌ changes
```

**The Convention:**
1. **Subject line:** Imperative mood, ≤ 50 characters. ("Add feature" not "Added feature")
2. **Blank line** separating subject from body.
3. **Body (optional):** Explain *why*, not *what*. The diff shows what changed; your message should explain the reasoning.

### Unstaging and Discarding Changes

Made a mistake? Git has you covered:

```bash
# Unstage a file (remove from staging area, keep changes in working directory)
git restore --staged hello.txt

# Discard changes in working directory (revert to last committed version)
# ⚠️ WARNING: This permanently discards your changes!
git restore hello.txt
```

### The .gitignore File

Not every file should be tracked. Compiled binaries, secret keys, IDE settings, and `node_modules` folders should be **excluded**. Create a `.gitignore` file:

```bash
# .gitignore

# Python
__pycache__/
*.pyc
venv/

# IDE
.vscode/
.idea/

# OS
.DS_Store
Thumbs.db

# Secrets
.env
*.key
```

Place `.gitignore` in the root of your repository and commit it. Git will automatically ignore matching files.

---

## Summary

- **Working Directory** → Where you edit files. Git detects changes here.
- **Staging Area** → Where you prepare your next commit. Use `git add` to stage.
- **Repository** → Where commits are permanently stored. Use `git commit` to save.
- The lifecycle is: Untracked → Staged → Committed → (Modified → Staged → Committed...).
- Use `git status` constantly — it's your best friend for understanding what Git sees.
- Write clear, imperative commit messages.
- Use `.gitignore` to exclude files that shouldn't be tracked.

---

## Additional Resources
- [Git Docs — Recording Changes to the Repository](https://git-scm.com/book/en/v2/Git-Basics-Recording-Changes-to-the-Repository)
- [Atlassian — Saving Changes (add, commit, stash)](https://www.atlassian.com/git/tutorials/saving-changes)
- [Conventional Commits Specification](https://www.conventionalcommits.org/)
