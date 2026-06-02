import sys
def metrics_calculator():
    test_cases = int(input(" Test Cases: "))
    passed_tests = int(input(" Passed Tests: "))
    execution_time = float(input(" Total Execution Time in Seconds: "))
    failed_tests = test_cases - passed_tests
    pass_rate = passed_tests/test_cases
    fail_rate = failed_tests/test_cases
    average_time_per_test = execution_time / test_cases
    print(f"Total Tests: {test_cases}")
    print(f"Passed Tests: {passed_tests}")
    print(f"Failed Tests: {failed_tests}")
    print(f"Pass Rate: {pass_rate:.1%}")
    print(f"Fail Rate: {fail_rate:.1%}")
    print(f"Avg Time/Test: {average_time_per_test:.2f}s")
    print(f"Total Time: {execution_time:.2f}s")
    if(pass_rate >= 0.95):
        print(f"✅ RELEASE APPROVED")
        return
    elif (pass_rate >= 0.80 and pass_rate < 0.95):
        print(f"⚠️ CONDITIONAL RELEASE — review failures")
        return
    elif (pass_rate < 0.80):
        print(f"❌ RELEASE BLOCKED — too many failures")
        return

    
    


def main():
    metrics_calculator()

if __name__ == "__main__":
    main()