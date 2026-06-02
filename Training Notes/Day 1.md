# Git & Python Review
### Bash
ls -a
ls -la
Ctrl+L to move to the top of bash
## Git

```bash
git status
git log # See what has been done in the working tree
git restore —staged <file> #Restore what was staged to append again
git diff # What changed
git switch -c <branch-name> # switch to and create a branch if it doesn’t already exist
git merge <file-name> # merge file name to working branch
git log --oneline —graph —all
git switch main
```

**Resolve conflict in the merge editor in VS Code**

## Fix: Git Push Rejected (non-fast-forward)

**When this happens:** Your push is rejected because the remote has commits your local branch doesn't have.

**Step-by-step fix:**

```bash
# 1. Stash your local changes so they don't block the pull
git stash

# 2. Pull the remote changes and replay your work on top
git pull --rebase origin main

# 3. Restore your stashed changes
git stash pop

# 4. Stage, commit, and push
git add .
git commit -m "your commit message"
git push
```

**Why this works:**

- `git stash` temporarily saves your uncommitted changes
- `git pull --rebase` fetches the remote commits and puts your changes _after_ them (instead of creating a messy merge commit)
- `git stash pop` brings your changes back
- Then you commit and push as normal

**If `git stash pop` causes conflicts**, Git will mark the conflicting files. Open them, look for the `<<<<<<<` markers, resolve manually, then `git add .` and continue.
## Python

### Virtual Environment
```bash
python3 -m venv ./venv # create virtual environment
source venv/bin/activate
python --version
pip install numpy
deactivate
```

**Review Day 1 Python Code** 