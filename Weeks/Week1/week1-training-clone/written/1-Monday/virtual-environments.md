# Virtual Environments

## Learning Objectives
- Explain what a virtual environment is and why isolation matters.
- Create, activate, and deactivate virtual environments using `venv`.
- Understand the relationship between virtual environments and `pip`.

---

## Why This Matters

> **Weekly Epic Connection:** You'll work on multiple Python projects throughout this training — and eventually, multiple projects at work. Each project may need different versions of libraries. Virtual environments keep each project's dependencies isolated so they don't interfere with each other.

Here's the horror story that virtual environments prevent:

> Project A needs `requests==2.28.0`. Project B needs `requests==2.31.0`. Without isolation, installing one version breaks the other project. With virtual environments, each project has its own copy — no conflicts, no surprises.

---

## The Concept

### The Problem: Shared Dependencies

When you install a Python package with `pip install`, it goes into a **global** location by default — shared across all your projects. This creates problems:

```
Global Python Installation
├── requests 2.31.0    ← Project B updated this
├── pandas 2.0.0       ← Both projects use this
└── numpy 1.24.0       ← Project A needs 1.23.0 😱
```

If Project A needs `numpy 1.23.0` and Project B needs `numpy 1.24.0`, you can only have one installed globally. Upgrading for one project *breaks* the other.

### The Solution: Virtual Environments

A **virtual environment** is an **isolated Python installation** — a self-contained directory that has its own Python interpreter and its own set of installed packages.

```
Project A/
├── venv/                  ← Project A's virtual environment
│   ├── python 3.12
│   ├── requests 2.28.0
│   └── numpy 1.23.0
├── src/
└── tests/

Project B/
├── venv/                  ← Project B's virtual environment
│   ├── python 3.12
│   ├── requests 2.31.0
│   └── numpy 1.24.0
├── src/
└── tests/
```

Each project gets exactly the dependencies it needs. No conflicts.

### Creating a Virtual Environment

Python ships with the `venv` module built-in. No installation needed.

```bash
# Navigate to your project directory
cd my-project

# Create a virtual environment named "venv"
python -m venv venv
```

The `python -m venv venv` command means:
- `python -m venv` — Run the `venv` module.
- `venv` — Name the environment directory `venv`.

You can name it anything, but `venv` is the universal convention. Other common names: `.venv`, `env`.

### What Gets Created

```
my-project/
├── venv/
│   ├── Include/        ← C header files (for compiling packages)
│   ├── Lib/            ← Installed packages go here
│   │   └── site-packages/
│   ├── Scripts/        ← Windows: activate script, python.exe
│   │   ├── activate
│   │   ├── activate.bat
│   │   ├── python.exe
│   │   └── pip.exe
│   └── pyvenv.cfg      ← Configuration file
├── my_code.py
└── ...
```

The `venv` folder is a complete, isolated Python installation.

### Activating a Virtual Environment

Creating the environment doesn't automatically use it. You must **activate** it:

#### Windows (PowerShell)
```powershell
.\venv\Scripts\Activate
```

#### Windows (Command Prompt)
```cmd
venv\Scripts\activate.bat
```

#### macOS / Linux
```bash
source venv/bin/activate
```

Once activated, your terminal prompt changes to show the environment name:

```
(venv) PS C:\Users\you\my-project>
```

The `(venv)` prefix tells you the virtual environment is active.

### What Activation Does

When activated:
- `python` points to the **virtual environment's** Python, not the global one.
- `pip install` installs packages into the **virtual environment only**.
- Packages from the global Python are not visible.

You can verify:

```bash
# Check which Python you're using
(venv) > python --version
Python 3.12.x

# Check which pip
(venv) > pip --version
pip 24.x from c:\...\venv\lib\site-packages\pip (python 3.12)

# Where is python?
(venv) > where python    # Windows
(venv) > which python    # macOS/Linux
```

### Installing Packages in a Virtual Environment

With the environment activated, use `pip` as normal:

```bash
(venv) > pip install requests
# Installs requests ONLY into this virtual environment

(venv) > pip install pandas numpy
# Install multiple packages at once

(venv) > pip list
# Shows only packages installed in this environment
```

### Deactivating a Virtual Environment

When you're done working on the project:

```bash
(venv) > deactivate
PS C:\Users\you\my-project>    # Notice: (venv) prefix is gone
```

The `deactivate` command returns you to the global Python environment.

### The Golden Rule: Never Commit `venv/`

The `venv` directory is **local and disposable**. It should **never** be committed to Git. Add it to your `.gitignore`:

```
# .gitignore
venv/
.venv/
env/
```

Why? Because:
1. It's large (hundreds of megabytes for even small projects).
2. It's OS-specific (Windows venvs don't work on Linux).
3. It's reproducible — you can recreate it from a requirements file (we'll cover this on Wednesday).

### Virtual Environment Workflow (Daily Practice)

Here's the workflow you'll follow for every project:

```bash
# 1. Create the project directory
mkdir my-qa-project
cd my-qa-project

# 2. Initialize Git
git init

# 3. Create a virtual environment
python -m venv venv

# 4. Add venv to .gitignore
echo "venv/" > .gitignore

# 5. Activate the environment
.\venv\Scripts\Activate    # Windows
# source venv/bin/activate  # macOS/Linux

# 6. Install your dependencies
(venv) > pip install pytest requests

# 7. Do your work...
(venv) > python my_script.py
(venv) > pytest tests/

# 8. When done
(venv) > deactivate
```

### Common Issues and Solutions

| Issue | Solution |
|-------|---------|
| `python -m venv venv` fails | Ensure Python 3.3+ is installed. On Ubuntu, install `python3-venv`. |
| PowerShell won't activate | Run `Set-ExecutionPolicy -ExecutionPolicy RemoteSigned -Scope CurrentUser` first. |
| Wrong Python version in venv | Specify the Python version: `python3.12 -m venv venv` or `py -3.12 -m venv venv`. |
| Accidentally installed globally | Deactivate, check `pip list`, and clean up with `pip uninstall`. |
| venv got corrupted | Delete the entire `venv/` folder and recreate it. |

### Why Not `conda`?

You may hear about **Conda** (Anaconda/Miniconda) as an alternative. Here's the key difference:

| Feature | venv + pip | Conda |
|---------|-----------|-------|
| **Scope** | Python packages only | Python + non-Python (C libs, R, etc.) |
| **Built-in** | Yes (ships with Python) | Separate installation |
| **Speed** | Fast | Slower dependency resolution |
| **Best for** | Web dev, automation, QA | Data science, ML, scientific computing |

For this training, we use `venv` + `pip` — it's the standard for professional Python development and everything you need for quality engineering.

---

## Summary

- **Virtual environments** isolate project dependencies so they don't conflict with each other.
- Create one with `python -m venv venv`.
- **Activate** with `.\venv\Scripts\Activate` (Windows) or `source venv/bin/activate` (Mac/Linux).
- **Deactivate** with `deactivate`.
- **Never commit `venv/`** to Git — add it to `.gitignore`.
- Create a fresh virtual environment for **every project** — this is non-negotiable professional practice.

---

## Additional Resources
- [Python Docs — venv: Creation of Virtual Environments](https://docs.python.org/3/library/venv.html)
- [Real Python — Python Virtual Environments: A Primer](https://realpython.com/python-virtual-environments-a-primer/)
- [PEP 405 — Python Virtual Environments](https://peps.python.org/pep-0405/)
