### Bash
ls -a
ls -la
Ctrl+L to move to the top of bash
## Git

```
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
## Python

### Virtual Environment
```
<<<<<<< Updated upstream
python --version
pip install numpy
python3 -m venv ./venv # create virtual environment
source venv/bin/activate
=======
python3 --version
pip install numpy
python3 -m venv ./venv # create virtual environment
source venv/bin/activate
deactivate

>>>>>>> Stashed changes
```

**Review Day 1 Python Code** 