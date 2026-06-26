
def section(title):
    """Helper to print section headers."""
    print(f"\n{'─' * 50}")
    print(f"  {title}")
    print(f"{'─' * 50}")


def demo_integers():
    section("Integers (int)")

    age = 28
    population = 8_000_000_000  # Underscores for readability
    negative = -42

    print(f"  age          = {age}  (type: {type(age).__name__})")
    print(f"  population   = {population:,}")
    print(f"  negative     = {negative}")

    # Python handles big numbers natively — no overflow!
    big = 2 ** 100
    print(f"  2 ** 100     = {big}")
    print(f"  (That's {len(str(big))} digits!)")


def demo_floats():
    section("Floats (float)")

    price = 19.99
    pi = 3.14159
    scientific = 2.5e6  # 2,500,000.0

    print(f"  price        = {price}  (type: {type(price).__name__})")
    print(f"  pi           = {pi}")
    print(f"  scientific   = {scientific:,.0f}")

    # ⚠️ Floating-point precision gotcha
    result = 0.1 + 0.2
    print(f"\n  ⚠️ 0.1 + 0.2 = {result}")
    print(f"     Expected 0.3, got {result}")
    print(f"     This is normal! It's how CPUs represent decimals.")
    print(f"     Fix: abs(result - 0.3) < 1e-9 → {abs(result - 0.3) < 1e-9}")


def demo_strings():
    section("Strings (str)")

    name = "Quality Engineering"
    print(f"  name           = '{name}'")
    print(f"  name.upper()   = '{name.upper()}'")
    print(f"  name.lower()   = '{name.lower()}'")
    print(f"  name.title()   = '{name.title()}'")
    print(f"  len(name)      = {len(name)}")

    # Indexing and slicing
    word = "Python"
    print(f"\n  word           = '{word}'")
    print(f"  word[0]        = '{word[0]}'      (first character)")
    print(f"  word[-1]       = '{word[-1]}'      (last character)")
    print(f"  word[0:3]      = '{word[0:3]}'    (slice: first 3)")
    print(f"  word[::-1]     = '{word[::-1]}'  (reversed)")

    # Immutability
    print(f"\n  Strings are IMMUTABLE — you can't change a character in place.")
    print(f"  'B' + word[1:] = '{'B' + word[1:]}'  (creates a NEW string)")


def demo_booleans():
    section("Booleans (bool) and None")

    is_active = True
    has_errors = False

    print(
        f"  is_active      = {is_active}  (type: {type(is_active).__name__})")
    print(f"  has_errors     = {has_errors}")

    # Surprising: bool is a subclass of int!
    print(f"\n  🤯 True + True  = {True + True}")
    print(f"  isinstance(True, int) = {isinstance(True, int)}")

    # Truthiness / Falsiness
    print(f"\n  Falsy values:")
    for val in [0, 0.0, "", [], {}, None, False]:
        print(f"    bool({str(val):10s}) → {bool(val)}")

    # None
    result = None
    print(f"\n  result = None")
    print(f"  result is None  → {result is None}")
    print(f"  Always use 'is None', not '== None'")


def demo_operators():
    section("Operators")

    a, b = 17, 5
    print(f"  a = {a}, b = {b}")
    print(f"\n  Arithmetic:")
    print(f"    a + b   = {a + b:5}   (addition)")
    print(f"    a - b   = {a - b:5}   (subtraction)")
    print(f"    a * b   = {a * b:5}   (multiplication)")
    print(f"    a / b   = {a / b:5.1f}   (division → always float!)")
    print(f"    a // b  = {a // b:5}   (floor division → int)")
    print(f"    a % b   = {a % b:5}   (modulo → remainder)")
    print(f"    a ** b  = {a ** b:5}   (exponentiation)")

    print(f"\n  Comparison:")
    print(f"    5 == 5.0  → {5 == 5.0}   (value equality)")
    print(f"    5 is 5.0  → {5 is 5.0}  (identity — different objects!)")

    print(f"\n  Logical:")
    print(f"    True and False  → {True and False}")
    print(f"    True or False   → {True or False}")
    print(f"    not True        → {not True}")


def demo_fstrings():
    section("F-String Formatting")

    name = "Alice"
    score = 95.678
    count = 1234567

    print(f"  Basic:       f'Hello, {{name}}!'        → 'Hello, {name}!'")
    print(f"  Decimals:    f'{{score:.2f}}'            → '{score:.2f}'")
    print(f"  Percentage:  f'{{0.856:.1%}}'            → '{0.856:.1%}'")
    print(f"  Comma:       f'{{count:,}}'              → '{count:,}'")
    print(f"  Right-align: f'{{name:>20}}'             → '{name:>20}'")
    print(f"  Left-align:  f'{{name:<20}}'             → '{name:<20}'")
    print(f"  Center:      f'{{name:^20}}'             → '{name:^20}'")
    print(f"  Zero-pad:    f'{{42:05d}}'               → '{42:05d}'")


def demo_user_input():
    section("User Input (Interactive)")

    print("  Let's try input() — type your answers below.\n")

    name = input("  What is your name? ")
    age_str = input("  How old are you? ")

    try:
        age = int(age_str)
        future_age = age + 10
        print(f"\n  Hello, {name}! In 10 years you'll be {future_age}.")
        print(f"  Fun fact: You've been alive for roughly {age * 365:,} days!")
    except ValueError:
        print(f"\n  ⚠️ '{age_str}' is not a valid number!")
        print(f"  input() always returns a STRING — you must convert with int().")


def main():
    print("=" * 50)
    print("  Demo: Data Types, Operators & I/O")
    print("  Week 1 — Monday")
    print("=" * 50)

    demo_integers()
    demo_floats()
    demo_strings()
    demo_booleans()
    demo_operators()
    demo_fstrings()
    demo_user_input()

    print(f"\n{'=' * 50}")
    print("  ✅ Demo complete!")
    print("=" * 50)


if __name__ == "__main__":
    main()
