from collections import deque
d = deque([1, 2, 3])

# Provides a highly efficient way to add and remove elements from both the beginning and the end of a sequence
d.append(4)
d.appendleft(0)

print(d)

d.pop()
print(d)

d.popleft()
print(d)
print(type(d))