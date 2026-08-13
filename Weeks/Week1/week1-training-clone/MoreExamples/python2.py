from typing import Callable

import matplotlib.pyplot as plt
import numpy as np
plt.plot(np.random.randn(50).cumsum())
plt.savefig("test1.png")

#pass by object reference
l2=[]
l1=[0,1,2]
print(l2)
print(l1)
print(id(l2))
print(id(l1))
l2=l1
l1.append(3)
print(l2)
print(l1)
print(id(l2))
print(id(l1))

##############################################
l2=[1,2,3,4] # a list named array
pivot=2
less=[]
greater=[]

for num in l2:
    if num < pivot:
        less.append(num)
    else:
        greater.append(num)

print(less)
print(greater)
#################################################

l2=[1,2,3,4] # a list named array
pivot=2
less=[]
greater=[]

for i in range(len(l2)):
    if l2[i] < pivot:
        less.append(l2[i])
    else:
        greater.append(l2[i])

print(less)
print(greater)

##################################################
s = r'this\has\no\special\characters'
print(s)
s2='this\\has\\no\\special\\characters'
print(s2)

#################################################
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

######################################
x=5
# if x>=0:
#     result = 'Non-negative'
# else:
#     result = 'Negative'

# print(result)

# or as ternary expression
result = 'Non-negative' if x>=0 else 'Negative'

print(result)

#list comprehenstions

list_strings = ['b', 'is', 'cat', 'far', 'love', 'python']
words:list = [word.upper() for word in list_strings]
print(words)

#is equivalent to
words=[]
for word in list_strings:
    words.append(word.upper())
print(words)

#set comprehension
#what is the unique set of word lengths in my list
string_lengths={len(word) for word in list_strings}
print(string_lengths)

#dict comprehension
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

#############################################################
#nested list_comprehensions

all_data = [['John', 'Emily', 'Michael', 'Mary', 'Steven','Even'],
    ['Maria', 'Juan', 'Javier', 'Natalia', 'Pilar', 'Tomee']]

# need to return single list with all names that have two or more e's in them
names_of_interest = []
for names in all_data:
    enough_es = [name for name in names if name.lower().count('e')>=2]
    names_of_interest.extend(enough_es)

print(names_of_interest)

names_of_interest_2 = [x for List in all_data for x in List if x.lower().count('e')>=2]
print(names_of_interest_2)

#a more functional way and even shorter, that is also good, both are good, this is just different
list_of_names_2=list(filter(lambda x: x.lower().count('e')>=2, sum(all_data,[])))
print(list_of_names_2)

#the sum can actually flatten a list of lists to a list here
print("functional")
print(sum(all_data,[]))



##########################################
some_tuples = [(1, 2, 3), (4, 5, 6), (7, 8, 9)]
flattened = [x for tup in some_tuples for x in tup]
print(flattened)

list_of_lists_was_list_tuples = [[x for x in tup] for tup in some_tuples] 
print(list_of_lists_was_list_tuples)


###############################################
states = [' Alabama ', 'Georgia!', 'Georgia', 'georgia', 'FlOrIda','south carolina##', 'West virginia?']

import re

def clean_strings(strings):
    result = []
    for value in strings:
        value=value.strip()
        value=re.sub('[!#?]','', value)
        value=value.title()
        result.append(value)
    return result

print(clean_strings(states))

#or instad

def remove_punctuation(value):
    return re.sub('[!#?]','', value, flags=re.I)

clean_ops = [str.strip, remove_punctuation, str.title]

def clean_strings2(strings, ops):
    result=[]
    for value in strings:
        for function in ops:
            value=function(value)
        result.append(value)
    return result

#print(states)
print(clean_strings2(states,clean_ops))

y=[x for x in map(remove_punctuation, states)]


