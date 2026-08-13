n = int(input())
arr = map(int, input().split())
print(list(sorted(set(arr),reverse=True))[1])
