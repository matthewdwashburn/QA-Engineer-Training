# https://www.codewars.com/kata/5a21e090f28b824def00013c

dict_1={'Ice': 'Cream', 'Age': '21', 'Light': 'Cream', 'Double': 'Cream'}
def switch_dict(dic):
    result = {}
    for key, value in dic.items():
        result.setdefault(value, []).append(key)
    return result

switch_dict(dict_1)
print(switch_dict(dict_1))