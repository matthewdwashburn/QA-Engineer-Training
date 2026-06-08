from functools import reduce

# more functional
names = ["Alice", "Bob", "Charlie", "John"]
scores = [85,92,78, 91]
grades = ["B", "A", "C"]

# Zip up tuples inside a list
all_info = list(zip(names,scores,grades))
print(all_info)

# Zip up values inside of a dict
score_dict = dict(zip(names, scores))
print(score_dict)

# Take the scores, map each value to have a max of 100
curved = list(map(lambda s: min(s,100), scores))
print(curved)

# Filter for values that are above 90 from flat list
scores_only_above_90 = list(filter(lambda x: x>=90, scores))
print(scores_only_above_90)

# Filter for values that are above 90 from tuples inside a list
scores_only_above_90 = list(filter(lambda item: item[1] >= 90, all_info))
print(scores_only_above_90)

# Print the sum
l1=[1,2,3,4,5]
print(sum(l1))

# Sum all the values together
summer=reduce(lambda x, y: x+y, l1)
print(summer)