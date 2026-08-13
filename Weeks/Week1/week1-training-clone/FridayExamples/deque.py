from collections import deque
d = deque([1,2,3])

d.append(4)
d.appendleft(0)

print(d)

d.pop()
print(d)
d.popleft()
print(d)

print(type(d))