def report_generator():
    names = ["Alice", "Bob", "Trey", "Ethan", "Tanner"]
    durations = [1200, 850, 2300, 450, 180]
    statuses = ["PASS", "PASS", "FAIL", "PASS", "PASS"]


    top = "┌" + "─" * 18 + "┬" + "─" * 12 + "┬" + "─" * 10 + "┐"
    middle = "├" + "─" * 18 + "┼" + "─" * 12 + "┼" + "─" * 10 + "┤"
    bottom = "└" + "─" * 18 + "┴" + "─" * 12 + "┴" + "─" * 10 + "┘"

    print(top)
    print(f"| {'Test Name':<16} | {'Duration':<10} | {'Status':<8} |")
    print(middle)

    for x in range(5):
        dur = f"{durations[x]:,} ms" # add commas for thousands seperators
        emoji = "✅" if statuses[x] == "PASS" else "❌"
        print(f"| {names[x]:<16} | {dur:>10} | {emoji} {statuses[x]}  |")
    
    print(middle)
    total_duration = sum(durations)
    passed = statuses.count("PASS")
    dur = f"{total_duration:,} ms"
    print(f"| {'TOTAL':<16} | {dur:<10} | {passed}/5 Pass |")
    print(bottom)




def main():
    report_generator()


if __name__ == "__main__":
    main()
