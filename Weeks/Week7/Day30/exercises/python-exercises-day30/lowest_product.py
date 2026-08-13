def lowest_product(num):
    if(len(num) < 4):
        return "Number is too small"
    lowest_prod = 999999999999
    for i in range(len(num)):
        if i + 3 > len(num) - 1:
            break
        curr_prod = int(num[i]) * int(num[i + 1]) * int(num[i + 2]) * int(num[i + 3])
        if(curr_prod < lowest_prod):
            lowest_prod = curr_prod
    return lowest_prod
        