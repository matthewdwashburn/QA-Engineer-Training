# PyPI Repository and pip

## Learning Objectives
- Explain what PyPI is and its role in the Python ecosystem.
- Use `pip` to install, uninstall, and manage third-party packages.
- Use `pip freeze` to capture installed package versions.

---

## Why This Matters

> **Weekly Epic Connection:** The Python standard library is powerful, but real-world projects rely heavily on third-party packages — `pytest` for testing, `requests` for HTTP calls, `selenium` for browser automation. `pip` is how you install and manage these tools, and PyPI is where they come from.

---

## The Concept

### What Is PyPI?

**PyPI** (Python Package Index) is the official repository of third-party Python packages. Think of it as an "app store" for Python libraries.

- **URL:** [pypi.org](https://pypi.org/)
- **Packages available:** 500,000+
- **Free and open:** Anyone can publish packages.

### What Is pip?

**pip** (Pip Installs Packages) is Python's package installer. It comes pre-installed with Python 3.4+.

```bash
pip --version
# pip 24.x from ... (python 3.12)
```

### Installing Packages

```bash
# Install a single package
pip install requests

# Install a specific version
pip install requests==2.31.0

# Install minimum version
pip install "requests>=2.28"

# Install multiple packages
pip install pytest selenium flask
```

### Listing Installed Packages

```bash
# List all installed packages
pip list

# Output:
# Package    Version
# ---------- -------
# pip        24.0
# requests   2.31.0
# setuptools 69.0.3
```

### Package Information

```bash
# Show details about a package
pip show requests

# Output:
# Name: requests
# Version: 2.31.0
# Summary: Python HTTP for Humans.
# Home-page: https://requests.readthedocs.io
# Requires: certifi, charset-normalizer, idna, urllib3
# Required-by: (packages that depend on this)
```

### Uninstalling Packages

```bash
# Remove a package
pip uninstall requests

# Remove without confirmation prompt
pip uninstall requests -y
```

### `pip freeze` — Capturing Dependencies

`pip freeze` outputs **all installed packages with exact versions** — perfect for reproducing your environment:

```bash
pip freeze

# Output:
# certifi==2024.2.2
# charset-normalizer==3.3.2
# idna==3.6
# requests==2.31.0
# urllib3==2.2.0
```

Save to a requirements file:

```bash
pip freeze > requirements.txt
```

Install from a requirements file (we'll cover this in more detail on Wednesday):

```bash
pip install -r requirements.txt
```

### Upgrading Packages

```bash
# Upgrade a package to the latest version
pip install --upgrade requests

# Upgrade pip itself
pip install --upgrade pip
```

### Searching for Packages

Search on [pypi.org](https://pypi.org/) directly — the browser search is more reliable than the CLI.

### Key Packages for QA Engineers

| Package | Purpose | Install |
|---------|---------|---------|
| `pytest` | Testing framework | `pip install pytest` |
| `requests` | HTTP/API testing | `pip install requests` |
| `selenium` | Browser automation | `pip install selenium` |
| `playwright` | Modern browser automation | `pip install playwright` |
| `pandas` | Data analysis | `pip install pandas` |
| `numpy` | Numerical computing | `pip install numpy` |
| `black` | Code formatter | `pip install black` |
| `flake8` | Linter | `pip install flake8` |

### Best Practices

1. **Always use a virtual environment** — never `pip install` into the global Python.
2. **Pin versions** in `requirements.txt` — `requests==2.31.0`, not just `requests`.
3. **Update dependencies regularly** — but test after updating.
4. **Read the PyPI page** before installing — check the project's health (last update, downloads, open issues).

---

## Summary

- **PyPI** is the official repository of 500,000+ Python packages.
- **pip** installs, upgrades, and removes packages from PyPI.
- `pip freeze > requirements.txt` captures your exact dependency versions.
- Always install packages inside a **virtual environment**.
- Pin exact versions in `requirements.txt` for reproducibility.

---

## Additional Resources
- [PyPI — Python Package Index](https://pypi.org/)
- [pip Documentation](https://pip.pypa.io/en/stable/)
- [Real Python — What Is Pip?](https://realpython.com/what-is-pip/)
