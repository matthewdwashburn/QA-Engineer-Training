def separate_liquids(glass):
    density_dict = {
        "H": 1.36,
        "W": 1.00,
        "A": 0.87,
        "O": 0.80
    }
    if not glass:
        return []
    layer_length = len(glass[0])
    layer_count = len(glass)
    # Review, make sure you know list comprehension
    flat_glass = [liquid for layer in glass for liquid in layer]
    # Review, make sure you know sorted lambda function
    sorted_flat_glass = sorted(flat_glass, key=lambda x: density_dict[x])
    sorted_glass = []
    for i in range(layer_count):
        local_layer = []
        offset = layer_length * i
        for j in range (layer_length):
            local_layer.append(sorted_flat_glass[offset + j])
        sorted_glass.append(local_layer)  
    return sorted_glass