#https://www.codewars.com/kata/538835ae443aae6e03000547/train/python

#or with a lambsa

from typing import Callable

def add(n: int) -> Callable[[int], int]:
    return lambda x: x + n

f1 = add(6)
print(f1(3))