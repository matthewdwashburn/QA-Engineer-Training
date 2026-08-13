#this is global, it can actually be referenced in other modules if you import it
# and it is availbl inside methods within this module
#reusing the name is shadwoing, not generally recommended

name = "Will2"

def local_and_enclosed():
    name = "Sam" #this is local, is it available inside the function but not outside
    def enclosed(): # a function within a function is an enclosed namespace: same lifecycle as it parent function,
        #and access to its variables
        return name # will return Sam, since the enclosed function has access to the parent function's local block
    print(enclosed()) #prints Sam
    name="Luke"
    return name # this now return Luke, since it is the local assignment to the variables

print(name) #prints Will2
print(local_and_enclosed()) #prints Sam first because of the enclosed() method, then it prints Luke because of the local assignment
print(name)

print(dir())

class Num:
    def __init__(self, number):
        self.number=number
    def __add__(self, other):
        return self.number + other*100
    
num = Num(10)
print(num.number)
print(num + 2) #210




