"""
Legacy Test Runner — Uses print() everywhere.
YOUR TASK: Replace ALL print() calls with appropriate logging calls.

Import and use setup_logging() from logging_config.py.
"""

import time
import random


def run_test(test_name):
    """Run a single test (simulated)."""
    print(f"Running test: {test_name}")
    duration = random.uniform(0.1, 2.0)
    time.sleep(0.01)

    if random.random() < 0.2:
        print(f"ERROR: {test_name} failed!")
        print(f"  Duration: {duration:.2f}s")
        return False

    print(f"  {test_name} passed ({duration:.2f}s)")
    return True


def run_suite(suite_name, test_names):
    """Run a suite of tests."""
    print(f"\n{'='*50}")
    print(f"Starting suite: {suite_name}")
    print(f"Tests to run: {len(test_names)}")
    print(f"{'='*50}\n")

    results = {"passed": 0, "failed": 0}

    for i, test in enumerate(test_names, 1):
        print(f"[{i}/{len(test_names)}]", end=" ")
        if run_test(test):
            results["passed"] += 1
        else:
            results["failed"] += 1

    total = results["passed"] + results["failed"]
    rate = results["passed"] / total * 100

    print(f"\n{'='*50}")
    print(f"Results: {results['passed']}/{total} passed ({rate:.1f}%)")

    if rate < 80:
        print(f"WARNING: Pass rate below 80%!")
    if rate < 50:
        print(f"CRITICAL: More than half the tests failed!")

    return results


def main():
    print("QA Test Framework v1.0")
    print("Initializing...")

    random.seed(42)

    suites = {
        "Smoke Tests": ["test_login", "test_homepage", "test_search"],
        "Regression": ["test_checkout", "test_payment", "test_profile",
                       "test_settings", "test_logout"],
        "Performance": ["test_load_page", "test_api_response"],
    }

    all_results = {"passed": 0, "failed": 0}

    for suite_name, tests in suites.items():
        try:
            result = run_suite(suite_name, tests)
            all_results["passed"] += result["passed"]
            all_results["failed"] += result["failed"]
        except Exception as e:
            print(f"Suite {suite_name} crashed: {e}")

    total = all_results["passed"] + all_results["failed"]
    print(f"\nFinal: {all_results['passed']}/{total} overall")


if __name__ == "__main__":
    main()
