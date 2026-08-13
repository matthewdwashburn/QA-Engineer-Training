"""
Functional Pipeline — Test Data Processing
Week 1, Friday | Individual Exercise

Complete each task using the appropriate functional tool.
Run this file after each task to check your output:
    python functional_pipeline.py

References:
    written/5-Friday/map.md
    written/5-Friday/filter.md
    written/5-Friday/reduce.md
    written/5-Friday/lambda-anonymous-functions.md
    written/5-Friday/zip.md
"""

from functools import reduce


# ── Shared Dataset ────────────────────────────────────────────────────────────
# All tasks work with this same list of test result records.

test_results = [
    {"name": "test_login",        "module": "auth",     "duration_ms": 1200, "status": "pass"},
    {"name": "test_register",     "module": "auth",     "duration_ms": 2100, "status": "pass"},
    {"name": "test_logout",       "module": "auth",     "duration_ms": 300,  "status": "pass"},
    {"name": "test_search",       "module": "search",   "duration_ms": 850,  "status": "fail"},
    {"name": "test_filter",       "module": "search",   "duration_ms": 1800, "status": "fail"},
    {"name": "test_sort",         "module": "search",   "duration_ms": 670,  "status": "pass"},
    {"name": "test_add_cart",     "module": "checkout", "duration_ms": 2300, "status": "fail"},
    {"name": "test_payment",      "module": "checkout", "duration_ms": 3100, "status": "pass"},
    {"name": "test_confirm",      "module": "checkout", "duration_ms": 1900, "status": "pass"},
    {"name": "test_view_profile", "module": "profile",  "duration_ms": 380,  "status": "pass"},
    {"name": "test_edit_profile", "module": "profile",  "duration_ms": 540,  "status": "pass"},
    {"name": "test_settings",     "module": "profile",  "duration_ms": 420,  "status": "fail"},
]


def section(title: str) -> None:
    print(f"\n{'─' * 55}")
    print(f"  {title}")
    print(f"{'─' * 55}")


# ── Task 1: Lambda & Sorting ──────────────────────────────────────────────────
section("Task 1: Lambda & Sorting")

# TODO 1a: Sort test_results by duration_ms ascending.
# Use sorted() with a lambda key.
# Expected: test_logout (300ms) first, test_payment (3100ms) last.
by_duration = None  # TODO

# TODO 1b: Sort by module name, then by duration_ms within each module.
by_module_duration = None  # TODO

# TODO 1c: Sort so "fail" tests appear before "pass" tests, then alphabetically by name.
fail_first = None  # TODO

# Uncomment to print:
# for r in by_duration:
#     print(f"  {r['name']}: {r['duration_ms']}ms")


# ── Task 2: map() ─────────────────────────────────────────────────────────────
section("Task 2: map()")
# ref: written/5-Friday/map.md

# TODO 2a: Extract just the test names into a list.
# Expected: ['test_login', 'test_register', 'test_logout', ...]
names = None  # TODO — use map()

# TODO 2b: Transform each record into a status badge string.
# Format: "✅ test_login (1200ms)"  or  "❌ test_search (850ms)"
badges = None  # TODO — use map() with a lambda

# TODO 2c: Extract the set of unique module names.
# Expected: {'auth', 'search', 'checkout', 'profile'}
modules = None  # TODO — use set() + map()

# Uncomment to print:
# print(f"  Names:   {names}")
# for badge in badges: print(f"  {badge}")
# print(f"  Modules: {modules}")


# ── Task 3: filter() ──────────────────────────────────────────────────────────
section("Task 3: filter()")
# ref: written/5-Friday/filter.md

# TODO 3a: Get all failed tests.
failures = None  # TODO — use filter()

# TODO 3b: Get all tests slower than 1500ms.
slow_tests = None  # TODO — use filter()

# TODO 3c: Get all tests that BOTH failed AND are slower than 1500ms.
# Hint: combine conditions in the lambda, OR chain two filter() calls.
critical = None  # TODO

# Uncomment to print:
# print(f"  Failures:  {[f['name'] for f in failures]}")
# print(f"  Slow:      {[s['name'] for s in slow_tests]}")
# print(f"  Critical:  {[c['name'] for c in critical]}")


# ── Task 4: reduce() ──────────────────────────────────────────────────────────
section("Task 4: reduce()")
# ref: written/5-Friday/reduce.md

# TODO 4a: Total duration of ALL tests (in ms).
# Expected: 15560
total_duration = None  # TODO — use reduce() with initializer=0

# TODO 4b: Total duration of FAILED tests only.
# Hint: chain filter() before reduce().
total_fail_duration = None  # TODO

# TODO 4c: Find the name of the test with the LONGEST name (by character count).
longest_name = None  # TODO — reduce() over names

# TODO 4d: Build a module summary dict counting tests per module.
# Expected: {'auth': 3, 'search': 3, 'checkout': 3, 'profile': 3}
module_counts = None  # TODO — reduce() with a dict accumulator and initializer={}

# Uncomment to print:
# print(f"  Total duration:       {total_duration}ms")
# print(f"  Total fail duration:  {total_fail_duration}ms")
# print(f"  Longest name:         {longest_name}")
# print(f"  Module counts:        {module_counts}")


# ── Task 5: zip() ─────────────────────────────────────────────────────────────
section("Task 5: zip()")

endpoints      = ["/login", "/search", "/checkout", "/profile"]
expected_codes = [200, 200, 201, 200]
actual_codes   = [200, 500, 201, 403]

# TODO 5a: Compare expected vs actual codes. Print ✅ or ❌ for each endpoint.
# Format: "✅ /login: expected=200, actual=200"

# TODO 5b: Unzip test_results into 4 parallel tuples:
#   (names, modules, durations, statuses)
# Hint: zip(*[[r['name'], r['module'], ...] for r in test_results])

# TODO 5c: Build a dict mapping test names to duration_ms using zip().
# Expected: {'test_login': 1200, 'test_register': 2100, ...}
name_to_duration = None  # TODO


# ── Stretch: Full Pipeline ────────────────────────────────────────────────────
section("Stretch: Full Pipeline")

# TODO: Using ONE expression (no intermediate variables), compute:
#   The average duration (ms) of failed tests.
# Steps: filter failures → extract durations → sum with reduce → divide by count
# Hint: you may need len(list(filter(...))) for the count (watch out — iterators are consumed!)

# avg_fail_duration = ...
# print(f"  Avg failure duration: {avg_fail_duration:.1f}ms")


if __name__ == "__main__":
    print(f"\n{'=' * 55}")
    print("  ✅ Run complete — uncomment print statements to check results.")
    print("=" * 55)
