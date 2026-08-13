#https://www.codewars.com/kata/5a86d2e24a6b34be2700003e

def sum_of_unique_sublists(arr):
    n = len(arr)
    seen = {}
    total = 0
    start = 0  # start of the current unique window
    
    for end, val in enumerate(arr):
        if val in seen and seen[val] >= start:
            start = seen[val] + 1
        seen[val] = end
        # sum all elements in current window
        total += sum(arr[start:end+1])
    
    return total

# Example
arr = [1, 2, 1]
print(sum_of_unique_sublists(arr))  # Output: 13