from collections import deque
def tower_builder(n_floors):
    floor_size = (n_floors * 2) - 1
    half_floor_size = (floor_size - 1) / 2
    curr_floor_num = 0
    tower = []
    while curr_floor_num < n_floors:
        curr_floor = deque(["*"])
        curr_stars_per_side = curr_floor_num
        curr_spaces_per_side = half_floor_size - curr_stars_per_side
        # Append stars
        for _ in range(int(curr_stars_per_side)):
            curr_floor.append("*")
            curr_floor.appendleft("*")
        # Append spaces
        for _ in range(int(curr_spaces_per_side)):
            curr_floor.append(" ")
            curr_floor.appendleft(" ")
        # Append floor to tower
        tower.append(list(curr_floor))

        # Increment to next floor
        curr_floor_num += 1
    
    # Turn tower from 2d list to list with strings
    for index, value in enumerate(tower):
        tower[index] = "".join(value)

    return tower


assert tower_builder(1) == ['*']
assert tower_builder(2) == [' * ', '***']
assert tower_builder(3) == ['  *  ', ' *** ', '*****']
assert tower_builder(6) == [
    "     *     ",
    "    ***    ",
    "   *****   ",
    "  *******  ",
    " ********* ",
    "***********"
]
