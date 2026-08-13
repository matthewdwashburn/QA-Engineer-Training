from functools import reduce

names = ["Alice","Bob","Charlie","John"]
scores = [85,92,78,91]
grades=["B","A","C","A"]
        
all_info=list(zip(names,scores,grades))
print(all_info)

score_dict = dict(zip(names,scores))
print(score_dict)

curved = list(map(lambda s: min(s+5,100),scores))
print(curved)

scores_only_above_90 = list(filter(lambda item: item[1] >= 90, all_info))
print(scores_only_above_90)

l1=[1,2,3,4,5]
# print(sum(l1))

cumulative_multiplier=reduce(lambda x,y: x*y, l1)
print(cumulative_multiplier)

