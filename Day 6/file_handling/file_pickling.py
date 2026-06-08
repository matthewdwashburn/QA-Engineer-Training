import pickle
file=open("num_pickles.dat", "wb")

numbers=[10,20,30,40,50]

# Dump numbers into a file
pickle.dump(numbers, file)

file.close()

file=open("num_pickles.dat", "rb")
# Load the file
data=pickle.load(file)
print(data)
file.close