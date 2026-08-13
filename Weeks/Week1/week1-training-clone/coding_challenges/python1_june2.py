#https://www.codewars.com/kata/538835ae443aae6e03000547/train/python

from typing import Callable

def add(n: int) -> Callable:
    def inner(x: int) -> int:
        return x + n
    return inner

f1 = add(7)
print(f1(3))

