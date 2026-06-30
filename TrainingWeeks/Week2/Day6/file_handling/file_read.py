file = open("data.txt", "r")

# Read Entire File
# content = file.read()
# print(content)

# Read one line
# line = file.readline()
# print(line)

# Read all lines, put them in a list
lines = file.readlines()
print(lines)
file.close()