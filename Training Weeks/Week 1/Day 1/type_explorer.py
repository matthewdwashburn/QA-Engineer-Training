def type_explorer():
    age = 28
    price = 19.99
    name = "Alice"
    is_active = True
    result = None

    print("Variable Exploration:\n")
    print(f"age = 28 (type: {type(age).__name__})")
    print(f"price = 19.99 (type: {type(price).__name__})")
    print(f"name = Alice (type: {type(name).__name__})")
    print(f"is_active = True (type: {type(is_active).__name__})")
    print(f"result = None (type: {type(result).__name__})")

    print("Operators Demo:\n")
    print(f"17 // 5 = {17 // 5} (floor division)")
    print(f"17 / 5 = {(17 / 5):.1f} (true division)")
    print(f"QA * 3 = {'QA ' * 3}")
    print(f"True + True = {True + True}")

    print("Precision Gotcha:\n")
    print(f"0.1 + 0.2 = {(0.1 + 0.2):.25f} (not exactly 0.3)")



def main():
    type_explorer()

if __name__ == "__main__":
    main()