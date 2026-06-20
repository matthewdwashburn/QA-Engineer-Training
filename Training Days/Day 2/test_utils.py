def format_test_name(name):
    return "test_" + "_".join(name.lower().split())


import re

pattern = r'^\w+$'


def is_valid_test_name(name):
    if len(name) < 6:
        return False
    if name[0:5] != "test_":
        return False
    if re.fullmatch(pattern, name):
        return True
    else:
        return False

assert format_test_name("Valid Login") == "test_valid_login"
assert format_test_name("  Search Results  ") == "test_search_results"
assert is_valid_test_name("test_login") == True
assert is_valid_test_name("login_test") == False
assert is_valid_test_name("test_") == False
    

def create_test_result(name, status="pass", duration_ms=0, error=None):
    dic = {
        "name": name,
        "status": status,
        "duration_ms": duration_ms,
        "error": error
    }
    return dic

def format_duration(ms, unit="ms"):
    if unit == "ms":
        duration_string = f"{ms:,}{unit}"
    elif unit == "s":
        seconds = ms/1000
        duration_string = f"{seconds:.2f}{unit}"
    elif unit == "min":
        minutes = ms/60000
        duration_string = f"{minutes:.2f}{unit}"
    return duration_string


r1 = create_test_result("test_login")
assert r1 == {"name": "test_login", "status": "pass",
              "duration_ms": 0, "error": None}

r2 = create_test_result("test_checkout", status="fail",
                        duration_ms=2300, error="Timeout")
assert r2["status"] == "fail"
assert r2["error"] == "Timeout"

assert format_duration(1200) == "1,200ms"
assert format_duration(1200, "s") == "1.20s"


def calculate_stats(*scores):
    if scores == None:
        return ValueError
    
    count = len(scores)
    total = 0
    for score in scores:
        total += score
    average = total/count
    score_min = min(scores)
    score_max = max(scores)
    # create dict
    dic = {
        "count": count,
        "total": total,
        "average": average,
        "min": score_min,
        "max": score_max,
    }
    return dic

def build_test_config(**settings):
    default = {
        "browser": "chrome",
        "headless": False,
        "timeout": 30,
        "retries": 0,
        "base_url": "http://localhost:3000"
    }

    for key, value in settings.items():
        if key in default:
            default[key] = value
    
    return default


stats = calculate_stats(85, 92, 78, 95, 88)
assert stats["count"] == 5
assert stats["average"] == 87.6
assert stats["min"] == 78
assert stats["max"] == 95

config = build_test_config(headless=True, timeout=60)
assert config["browser"] == "chrome"  # default
assert config["headless"] == True     # overridden
assert config["timeout"] == 60       # overridden


def analyze_results(*results):
    passed_count = 0
    failed_count = 0
    duration_sum = 0
    results_count = len(results)
    for dict in results:
        if dict["status"] == "pass":
            passed_count += 1
        else:
            failed_count += 1
        
        duration_sum += dict["duration_ms"]
    
    pass_rate = passed_count/results_count
    avg_duration = duration_sum/results_count
    return_tuple = (passed_count, failed_count, pass_rate, avg_duration)
    return return_tuple

results = [
    create_test_result("test_login", "pass", 1200),
    create_test_result("test_search", "pass", 850),
    create_test_result("test_checkout", "fail", 2300, "Timeout"),
    create_test_result("test_profile", "pass", 450),
]

passed, failed, rate, avg = analyze_results(*results)
assert passed == 3
assert failed == 1
assert rate == 0.75
assert avg == 1200