#https://www.codewars.com/kata/562e6df5cf2d3908ad00019e


def sort_liquids(glass):
    # If the glass is empty or has no columns, return it as-is
    if not glass or not glass[0]:
        return glass

    # Define the density order from lowest to highest density
    # Lower density liquids float to the top
    # Oil (O) < Alcohol (A) < Water (W) < Honey (H)
    density_order = ['O', 'A', 'W', 'H']

    # Number of rows and columns in the glass
    rows = len(glass)
    cols = len(glass[0])

    # Step 1: Flatten the 2D glass into a 1D list
    # This makes it easier to sort all liquids by density
    liquids = []
    for row in glass:
        for cell in row:
            liquids.append(cell)

    # Step 2: Sort the liquids based on their density
    # We use the index in density_order as the sorting key
    liquids.sort(key=lambda x: density_order.index(x))

    # Step 3: Rebuild the glass row by row
    # Fill from top to bottom and left to right
    result = []
    index = 0
    for _ in range(rows):
        # Take exactly 'cols' elements for each row
        row = liquids[index:index + cols]
        result.append(row)
        index += cols

    # Return the newly sorted glass
    return result

sorted=sort_liquids([['A','A','O','H'],['A', 'H', 'W', 'O'],['W','W','A','W'],['H','H','O','O']])
print(sorted)