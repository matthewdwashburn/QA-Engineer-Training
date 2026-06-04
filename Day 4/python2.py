import matplotlib.pyplot as plt
import numpy as np
from typing import Callable

plt.plot(np.random.rand(50).cumsum())
plt.savefig("Day 4/test1.png")

# pass by object reference
l2 = []
l1 = [0,1,2]
print(l2)
print(l1)
l1.append(3)
print(l2)
print(l1)
print(id(l2))
print(id(l1))
l2=l1
l1.append(3)
print(l2)
print(l1)
print(id(l2))
print(id(l2))

l2 = [1, 2, 3, 4]  # a list named array
pivot = 2
less = []
greater = []

# Good convention
for num in l2:
    if num < pivot:
        less.append(num)
    else:
        greater.append(num)

# # Bad convention
# for i in range(len(l2)):
#     if l2[i] < pivot:
#         less.append(num)
#     else:
#         greater.append(num)

print(less)
print(greater)


s = r'this\has\a\bunch\of\raw\back\slashes'
print(s)

sequence = [1, 2, None, 4, None, 5]
total = 0
for value in sequence:
    if value is None:
        continue
    total += value

print(total)

sequence = [1, 2, 0, 4, 6, 5, 2, 1]
total_until_5 = 0
for value in sequence:
    if value == 5:
        break
    total_until_5 += value
print(total_until_5)

# ternary expressions
x = 5
result = 'Non-negative' if x>5 else 'Negative'

print(result)

# List, Set, and Dict Comprehensions

# List comprehension
list_strings = ['b', 'is', 'john', 'far', 'cat', 'python']
words:list = [word.upper() for word in list_strings]
print(words)

# Equivalent, but not as clean
words = []
for word in list_strings:
    words.append(word.lower())
print(words)

# Set comprehension
# What is the unique set of lengths in my list
string_lengths={len(word) for word in list_strings}
print(string_lengths)

# Dict comprehension
print(list(enumerate(list_strings)))
loc_mapping = {index:val for index,val in enumerate(list_strings)}
print(loc_mapping)

#lambdas or anonymous functions
def short_function(x):
    return x*2

equiv_anon: Callable[[int],int] = lambda x: x*2

print(short_function(6))
print(equiv_anon(6))

max_val = lambda x,y: x if x>y else y
print(max_val(10,7))

# Nested List Comprehension

all_data = [['John', 'Emily', 'Michael', 'Mary', 'Steven'],
            ['Maria', 'Juan', 'Javier', 'Natalia', 'Pilar', 'Rylee', 'Reese', 'Kaylee']]

# returns a lits with all the names that have at least 1 a
names_of_interest = []
for names in all_data:
    enough_es = [name for name in names if name.count('a') >= 1]
    names_of_interest.extend(enough_es)

# returns a lits with all the names that have at least 2 e's
print(names_of_interest)
names_of_interest_2 = [
    x for List in all_data for x in List if x.lower().count('e') >= 2]
print(names_of_interest_2)

some_tuples = [(1,2,3), (4,5,6), (7,8,9)]
# Turn list of tuples into flat list
flattend = [x for tup in some_tuples for x in tup]
print(flattend)

# Turn list of tuples into list of lists
list_of_lists_was_list_tuples = [[x for x in tup] for tup in some_tuples]
print(list_of_lists_was_list_tuples)