#https://www.codewars.com/kata/57675f3dedc6f728ee000256

def tower_builder(floors, block_size):
    block_width, block_height = block_size
    tower = []
    total_width = floors * block_width * 2 - block_width  # width of the base floor

    for i in range(1, floors + 1):
        stars = '*' * (block_width * (2 * i - 1))  # stars for this floor
        line = stars.center(total_width)
        for _ in range(block_height):  # repeat each floor for block_height
            tower.append(line)

    return tower
