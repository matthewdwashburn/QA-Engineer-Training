def runner_up(n, arr):
    first = max(arr)

    while first in arr:
        arr.remove(first)

    if n == 0:
        return first
    else:
        second = max(arr)
        return second


assert runner_up(5, [2, 3, 6, 6, 5]) == 5       # Sample Input 0
assert runner_up(5, [1, 2, 3, 4, 5]) == 4        # all unique, runner-up is 4
assert runner_up(4, [3, 3, 5, 5]) == 3           # duplicates of both max and runner-up
assert runner_up(2, [1, 2]) == 1                 # two elements
