#!/bin/python3

#
# Complete the 'superDigit' function below.
#
# The function is expected to return an INTEGER.
# The function accepts following parameters:
#  1. STRING n
#  2. INTEGER k
#

def superDigit(n, k):
    # get the sum of just n
    inital_n_sum = 0
    for i in range(len(n)):
        inital_n_sum += int(n[i])

    # calculate total sum
    total_sum = inital_n_sum * k

    # convert to string
    string_sum = str(total_sum)
    
    # if it is longer than 1 digit, call again
    if len(string_sum) > 1:
        return superDigit(string_sum, 1)
    # otherwise return the sum in int form
    else:
        return total_sum

assert superDigit("148", 3) == 3          # Sample Input 0: (1+4+8)*3=39 → 3+9=12 → 1+2=3
assert superDigit("9", 1) == 9            # single digit, no recursion needed
assert superDigit("123", 2) == 3          # (1+2+3)*2=12 → 1+2=3
assert superDigit("9875", 4) == 8         # (9+8+7+5)*4=116 → 1+1+6=8
