# Day 24 - Git Merging & GitHub CLI

---

## GitHub CLI — Checking Out a PR Locally

### `gh pr checkout <PR-Number>` — fetch a PR and switch to its branch

```bash
gh pr checkout 2
```
Fetches `refs/pull/2/head` from the remote and creates a local branch (named after the PR's source branch, e.g. `manager-app`) tracking it. This is the normal way to get a teammate's PR onto your machine to merge or test locally — no need to undo it before merging.

---

## Git Restore vs Reset — Unstaging Files

### `git restore --staged <path>` — unstage files (modern syntax)

```bash
git restore --staged .   # unstage everything
git restore --staged employee/server.py   # unstage one file
```
`--staged` expects a **file path**, not a branch name — `git restore --staged <branch>` fails with a pathspec error. This is the modern equivalent of `git reset` for unstaging.

---

## Merging a Branch

### Basic merge workflow

```bash
git checkout main              # switch to the target branch (merge INTO this one)
git pull origin main            # make sure it's up to date
git merge manager-app           # merge the other branch into it
```

### Checking for conflicts

- If the merge is clean, Git either fast-forwards automatically or opens an editor for a merge commit message.
- If there are conflicts, Git lists the conflicted files and pauses the merge — check with:
```bash
git status   # conflicted files show under "Unmerged paths"
```
- Conflicted files contain markers to resolve manually:
```
<<<<<<< HEAD
=======
>>>>>>> manager-app
```
- After resolving: `git add <file>` on each fixed file, then `git commit` to finish.
- To back out entirely: `git merge --abort`

### Dry-run a merge without committing

```bash
git merge --no-commit --no-ff manager-app
git status   # inspect what would happen
git merge --abort   # bail out if not ready
```

### Previewing a merge before running it

```bash
git diff main manager-app   # see what would change
```

### If the commit editor fails to open

```bash
error: there was a problem with the editor 'vi'
Not committing merge; use 'git commit' to complete the merge.
```
The merge itself (e.g. auto-merging `database.py`) already succeeded — only the commit message editor failed. Skip the editor and accept the default merge message:
```bash
git commit --no-edit
```

### Finishing manually in vim (if the editor does open)

1. Press `Esc` to ensure you're not in insert mode
2. Type `:wq` and press `Enter` to save and quit, completing the merge commit

### After merging

```bash
git status
git log --oneline -5
git push origin main   # push the merge to the shared branch
```

---

```bash
gh pr checkout <PR-Number>
```



`pip install jwt` instead of `pip install PyJWT`

logging:
current_app.logger.exception(e)

blockers from project:
- Displaying tables properly, solved with pandas
- parsing json and passing it through properly between flask endpoints, solved with logging to trace data through endpoints
- Make sure I adhere to REST request convention, not implementation, passing jwt tokens in request headers and not body, no json body in get requests

Python object fields are accessed with a . , while python dicts are accessed with []

df = pd.DataFrame(expenses.json())

pd.set_option("display.float_format", "{:,.2f}".format)

df = df[["id", "amount", "description", "date"]]

