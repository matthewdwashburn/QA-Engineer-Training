# data = [x for x in range(1000000000000)]
# print(data)

# lazy list also known as generator
def gen():
    for x in range(1000000000000):
        yield x

g = gen()
print(next(g))
print(next(g))

