import pickle

numbers=[1,2,3,4,5,6,7]

with open("num_pick.dat","wb") as file:
    pickle.dump(numbers, file)
    # no need to close because of with statement

with open("num_pick.dat", "rb") as file:
    data = pickle.load(file)
    print(data)
    # no need to close because of with statement
