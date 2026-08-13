
import day2

print(day2.name)
print("day 2 imported")

#This needs to be added to the example when you get to abstract classes and methods
from abc import ABC, abstractmethod

#basic class syntax is the class keyword, ClassName:, init dunder method( add instance vraiables here), then associated functions

class MyNewClass:
    #this sets up the constructor for the class. There can only be one
    #notice you can set default values for parameters by declarings then within the ()
    def __init__(self, age = 0, name = "default name"):
        self.name = name
        self.age = age

    def my_new_class_function(self): #first parameter is always a reference to self, doesn't have to be called self
        return "this is my class function"
    
    def __str__(self):
        return f"my name is {self.name} and my age is {self.age}"
    
    # the __repr__ method should give a string representation of the object
    def __repr__(self):
        return f"MyNewClass(self, {self.age},{self.name})"
    
my_class = MyNewClass(name="Will")
print(my_class.name)

print(my_class.my_new_class_function())

#below all return the same 
print(my_class.__str__())
print(MyNewClass.__str__(my_class))
print(str(my_class))

print(my_class.__repr__())
print(repr(my_class))

#Python support abstract class. You make a class abstract by adding ABC inside the parentheses
class MyAbstractClass(ABC):
    # this is a class variables, accessed by calling the class itself, not an instantiatd object
    class_count=0

    #class methods take in the class as an implicit first argument, can interact with and change the state of the class
    @classmethod
    def print_class_count(cls): # still need to add a parameter for class here
        return cls.class_count
    
    # abstract methods have no body: they need to be defined in their class
    @abstractmethod
    def to_be_determined(self):
        pass

    #static methods are similar to class methods in that they are called by the class itself, but they do not receive the
    #class or an instance of the class as an implicit argument. You can add it
    #but might as well use a class method at that point
    @staticmethod
    def static_method():
        return "This is my static method, notice it does not interact with the state of the class"

# MyAbstractClass.class_count=MyAbstractClass.class_count+1
# print(MyAbstractClass.class_count)

class MyInheritsTheAbstractClass(MyAbstractClass):
    def __init__(self):
        print("I inherited from the abstract class")
        MyAbstractClass.class_count=MyAbstractClass.class_count+1
    
    # you can now define what the abstract method does within the child class
    def to_be_determined(self):
        return "I have defined what this function actually does"
    
class AlsoInheritsTheAbstractClass(MyAbstractClass):
    def __init__(self):
        print("I also inherited from the abstract class")
        MyAbstractClass.class_count=MyAbstractClass.class_count+2

    def to_be_determined(self):
        return "I have also defined what this function actually does"

print(MyAbstractClass.class_count)
my_new_class = MyInheritsTheAbstractClass()
print(my_new_class.to_be_determined())
print(MyAbstractClass.class_count)

class InheritedConstructor:
    def __init__(self,name):
        self.name=name
        print("the parent constructor was called")

class Inheritsconstructor(InheritedConstructor):
    def __init__(self, age=0, name="default name"):
        super().__init__(name)
        self.age = age
        print("the child class constructor called")

ci1 = Inheritsconstructor()
ci = Inheritsconstructor(30, "Will")
print(ci.name)
print(ci.age)

print(dir())
    
    #abstract methods 

    