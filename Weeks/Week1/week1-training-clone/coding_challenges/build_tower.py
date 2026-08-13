#https://www.codewars.com/kata/576757b1df89ecf5bd00073b
def tower_builder(n_floors):
    tower = []
    width = n_floors * 2 - 1  # total width of the base
    for i in range(1, n_floors + 1):
        stars = '*' * (2 * i - 1)       # number of stars for this floor
        floor = stars.center(width)     # center stars with spaces
        tower.append(floor)
    return tower
