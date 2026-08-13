#https://www.codewars.com/kata/5665d30b3ea3d84a2c000025

GIFTS = {
  1: 'Toy Soldier',
  2: 'Wooden Train',
  4: 'Hoop',
  8: 'Chess Board',
  16: 'Horse',
  32: 'Teddy',
  64: 'Lego',
  128: 'Football',
  256: 'Doll',
  512: "Rubik's Cube"
}
def decode_gifts(value):
    result = []
    
    # Process gifts from highest value to lowest
    for gift_value in sorted(GIFTS.keys(), reverse=True):
        if value >= gift_value:
            result.append(GIFTS[gift_value])
            value -= gift_value

    # Alphabetical order as requested
    return sorted(result)

print(decode_gifts(160))