#data = [x for x in range(1000000000)]
# print(data)

def gen():
    for x in range(1000000000):
        yield x

g = gen()

print(next(g))
print(next(g))

#or you could generate it as generator expression

data = (x for x in range(1000000000))

print(next(data))
print(next(data))
print(next(data))
print(next(data))


