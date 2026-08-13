import math
import os
import random
import re
import sys
from collections import Counter


if __name__ == '__main__':
    s = input()
    a = Counter(s)
    b = [(i, j) for i, j in a.items()]
    c = sorted(b, key=lambda x: (-x[1], x[0]))
    #print(c)
    for i in c[:3]:
        print(f"{i[0]} {i[1]}")


