def tower_builder(n_floors):
    tower = []
    for i in range(n_floors):
        curr_str = ""
        # Left side of tower floor
        curr_str += " " * (n_floors - i - 1)
        # Center of tower floor
        curr_str += ("*" * 2 * i) + "*"
        # Right side of tower floor
        curr_str += " " * (n_floors - i - 1)
        tower.append(curr_str)
    return tower