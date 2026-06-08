from collections import deque
def tower_builder_advanced(n_floors, block_size):
    width = block_size[0]
    height = block_size[1]
    floor_size = (n_floors * 2) - 1
    half_floor_size = (floor_size - 1) / 2
    curr_floor_num = 0
    tower = []
    while curr_floor_num < n_floors:
        for _ in range(height):
            curr_floor_block_slice = deque(["*" * width])
            curr_stars_per_side = curr_floor_num 
            curr_spaces_per_side = half_floor_size - curr_stars_per_side
            # Append stars
            for _ in range(int(curr_stars_per_side)):
                curr_floor_block_slice.append("*" * width)
                curr_floor_block_slice.appendleft("*" * width)
            # Append spaces
            for _ in range(int(curr_spaces_per_side)):
                curr_floor_block_slice.append(" " * width)
                curr_floor_block_slice.appendleft(" " * width)
            # Append floor to tower
            tower.append(list(curr_floor_block_slice))
        # Increment to next floor
        curr_floor_num += 1

    # Turn tower from 2d list to list with strings
    for index, value in enumerate(tower):
        tower[index] = "".join(value)

    return tower

print(tower_builder_advanced(3, (2,3)))
