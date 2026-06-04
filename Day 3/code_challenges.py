# Measure the little Pythons

class SnakeEye:
    def __init__(self):
        self.count = 0

    def __len__(self):
        return self.count

    def __gt__(self, input):
        self.count = 5
        return self

    def __sub__(self, input):
        self.count += 2
        return self


o = SnakeEye()
assert len((o > o) - 0 - 0 - 0 - 0) == 13

b = SnakeEye()
assert len((b > b) - 0 - 0) == 9


density_dict = {
    'H': 1.36,
    'W': 1.00,
    'A': 0.87,
    'O': 0.80
}

# Don't Drink the Water

def separate_liquids(glass):
    liquid_dict = {}
    for set in glass:
        for liquid in set:
            if liquid == 'H' or liquid == 'W' or liquid == 'A' or liquid == 'O':
                if liquid in liquid_dict:
                    liquid_dict[liquid] += 1
                else:
                    liquid_dict[liquid] = 1

    # Lambda function practice

    # def square(x):
    #     return x * x
    # square_lambda = lambda x: x * x

    # print(square(5))         # Output: 25
    # print(square_lambda(5))  # Output: 25

    sorted_liquid_dict = dict(sorted(liquid_dict.items(), key=lambda item: density_dict[item[0]]))

    for i in range(len(glass)):
        for j in range(len(glass[i])):
            for key in sorted_liquid_dict:
                if sorted_liquid_dict[key] > 0:
                    glass[i][j] = key
                    sorted_liquid_dict[key] -= 1
                    break
    return glass

# Don't drink the water - More efficient solution

def separate_liquids(glass):
    # if glass is empty, return empty, no liquids to separates
    if not glass:
        return []
    
    # define desity map
    density = {
        'H': 1.36,
        'W': 1.00,
        'A': 0.87,
        'O': 0.80
    }

    # create a flat array from the 2d array, list comprehension
    liquids_1d = [liquid for row in glass for liquid in row]

    # sort the list using the density map, ascending, starting with O, ending at H
    liquids_1d.sort(key=lambda liquid: density[liquid])

    # turn the 1D array back into a 2D array
    liquids_2d = []

    # get the width of each row
    width = len(glass[0])

    # loop through 1d array with range start, end, step
    for i in range(0, len(liquids_1d), width):  # jumps to the next row every time
        liquids_2d.append(liquids_1d[i:i + width])  # appends the full row

    return liquids_2d

assert separate_liquids([['H', 'H', 'W', 'O'], ['W', 'W', 'O', 'W'], ['H', 'H', 'O', 'O']]) == \
    [['O', 'O', 'O', 'O'], ['W', 'W', 'W', 'W'], ['H', 'H', 'H', 'H']]

assert separate_liquids([['A', 'A', 'O', 'H'], ['A', 'H', 'W', 'O'], ['W', 'W', 'A', 'W'], ['H', 'H', 'O', 'O']]) == \
    [['O', 'O', 'O', 'O'], ['A', 'A', 'A', 'A'], [
        'W', 'W', 'W', 'W'], ['H', 'H', 'H', 'H']]

assert separate_liquids([['A', 'H', 'W', 'O']]) == [['O', 'A', 'W', 'H']]

assert separate_liquids([['A'], ['H'], ['W'], ['O']]) == [['O'], ['A'], ['W'], ['H']]

assert separate_liquids([]) == []


