def solution(s):
    odd = False
    length = len(s)
    if(length % 2 != 0):
        odd = True
    split_list = []
    str_index = 0
    list_index = 0
    while str_index < int(length - 1):
        split_list.append(s[str_index] + s[str_index + 1])
        str_index += 2
        list_index += 1
    if(odd):
        split_list.append(s[length - 1] + "_")
    return split_list

# Much better solution using for i in range(0, len(s), 2) stepping 2 every time
def solution(s):
    result = []
    if len(s) % 2:
        s += '_'
    for i in range(0, len(s), 2):
        result.append(s[i:i+2])
    return result