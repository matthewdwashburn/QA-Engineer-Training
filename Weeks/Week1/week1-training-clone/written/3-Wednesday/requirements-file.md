# Requirements File

## Learning Objectives
- Understand the purpose and format of `requirements.txt`.
- Pin dependency versions for reproducible environments.
- Install packages from a requirements file.

---

## Why This Matters

> **Weekly Epic Connection:** Yesterday you learned to install packages with `pip` and capture them with `pip freeze`. The requirements file completes the picture — it's the recipe that lets anyone on your team recreate your exact environment. Without it, "works on my machine" becomes a daily frustration.

---

## The Concept

### What Is `requirements.txt`?

A **requirements file** is a plain text file listing the Python packages your project depends on, with their versions. By convention it's named `requirements.txt`.

```
# requirements.txt
requests==2.31.0
pytest==8.0.2
pandas==2.2.0
numpy==1.26.4
selenium==4.17.2
```

### Creating a Requirements File

**Option 1: `pip freeze` (captures everything installed)**

```bash
pip freeze > requirements.txt
```

This captures *all* packages in your virtual environment, including sub-dependencies. It's thorough but can be verbose.

**Option 2: Manual creation (captures only your direct dependencies)**

```
# requirements.txt — written by hand
requests==2.31.0
pytest==8.0.2
pandas==2.2.0
```

This is cleaner but requires you to manually track versions.

### Version Specifiers

```
# Exact version (most reproducible)
requests==2.31.0

# Minimum version
requests>=2.28.0

# Compatible release (2.31.x but not 3.x)
requests~=2.31.0

# Range
requests>=2.28.0,<3.0.0

# Any version (⚠️ risky — may break on updates)
requests
```

**Best practice:** Use `==` (exact pinning) for applications. Use `>=` for libraries.

### Installing from Requirements

```bash
# Install all packages listed in requirements.txt
pip install -r requirements.txt

# Upgrade packages to match requirements
pip install -r requirements.txt --upgrade
```

### Standard Team Workflow

```bash
# Developer A sets up the project
python -m venv venv
.\venv\Scripts\Activate
pip install requests pytest pandas
pip freeze > requirements.txt
git add requirements.txt
git commit -m "Add project dependencies"
git push

# Developer B clones and reproduces the environment
git clone <repo-url>
cd project
python -m venv venv
.\venv\Scripts\Activate
pip install -r requirements.txt
# Now Developer B has the EXACT same packages
```

### Multiple Requirements Files

Larger projects often split requirements:

```
requirements/
├── base.txt          # Shared dependencies
├── dev.txt           # Development tools (pytest, black, flake8)
└── prod.txt          # Production-only dependencies
```

```
# dev.txt
-r base.txt           # Include base requirements
pytest==8.0.2
black==24.1.1
flake8==7.0.0
```

```bash
pip install -r requirements/dev.txt
```

### Comments in Requirements Files

```
# Core dependencies
requests==2.31.0
pandas==2.2.0

# Testing
pytest==8.0.2
pytest-cov==4.1.0     # Coverage reporting

# Linting
# flake8==7.0.0       # Commented out — not yet adopted
```

---

## Summary

- `requirements.txt` lists your project's dependencies with versions — the "recipe" for your environment.
- Use `pip freeze > requirements.txt` to auto-generate from your current environment.
- Use `pip install -r requirements.txt` to install from the file.
- **Pin exact versions** (`==`) for applications to ensure reproducibility.
- Always commit `requirements.txt` to Git; never commit `venv/`.

---

## Additional Resources
- [pip Docs — Requirements Files](https://pip.pypa.io/en/stable/reference/requirements-file-format/)
- [Real Python — Using Requirements Files](https://realpython.com/what-is-pip/#using-requirements-files)
- [PEP 440 — Version Specifiers](https://peps.python.org/pep-0440/)
