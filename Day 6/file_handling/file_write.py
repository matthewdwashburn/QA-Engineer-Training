# Writes or over writes the file depending if it already exists
file=open("notes.txt", "w")

# file.write("\nHello There\n")
# file.write("Python")
# file.write("Jasdhir\n")
# file.writelines("This is a new line")
file.writelines(['a','b','c'])
file.close()