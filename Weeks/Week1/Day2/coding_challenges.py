from typing import Callable

# Curried addition: takes a number and returns a function that adds to it.
# Lets you call it in two steps: add(5)(3) instead of add(5, 3).
def add(n: int) -> Callable:
    def Callable(add):
        return n + add
    return Callable

assert add(5)(3) == 8
assert add(10)(0) == 10
assert add(-2)(4) == 2


# Inverts a dictionary, swapping keys and values.
# Since multiple keys can share the same value, each new value is a list of all original keys that mapped to it.
def switch_dict(dic):
    swapped_dict = {}
    for key, value in dic.items():
        valueExists = swapped_dict.get(value, False)
        if not valueExists:
            swapped_dict[value] = [key]
            continue
        swapped_dict[value].append(key)
    return swapped_dict

assert switch_dict({"a": 1, "b": 2, "c": 1}) == {1: ["a", "c"], 2: ["b"]}
assert switch_dict({"x": "hello"}) == {"hello": ["x"]}
assert switch_dict({}) == {}


GIFTS = {
    1: 'Toy Soldier',
    2: 'Wooden Train',
    4: 'Hoop',
    8: 'Chess Board',
    16: 'Horse',
    32: 'Teddy',
    64: 'Lego',
    128: 'Football',
    256: 'Doll',
    512: "Rubik's Cube"
}

# Given a budget number, returns the gifts whose prices (powers of 2) add up exactly to that number.
# Uses a greedy approach, picking the largest gift that still fits in the remaining budget.
def gifts(number):
    runningCount = 0
    toys = []
    for key, value in reversed(GIFTS.items()):
        if key + runningCount <= number:
            runningCount += key
            toys.append(value)
        if runningCount == number:
            break
    toys.sort()
    return toys

assert gifts(3) == ['Toy Soldier', 'Wooden Train']
assert gifts(7) == ['Hoop', 'Toy Soldier', 'Wooden Train']
assert gifts(512) == ["Rubik's Cube"]


