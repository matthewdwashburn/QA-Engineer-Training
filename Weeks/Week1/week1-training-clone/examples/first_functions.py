# to create a function you use the def keyword, name the function, give it ():, and then write the code with a single indent
#Use the return keyword if you want to return something specific
def basic_function():
    return "this is the basic function"

bf = basic_function
print(type(bf))

print(basic_function())

def basic_function_2(parameters):
    return parameters + 1
def add_type_annotation(param1: str, param2: int) -> str:
    return "you are indicating you want a string and in param entere into this function and you expect a string back"

print(add_type_annotation("hi", 1))

def annotations_dont_matter(num: int, num2: int) -> int:
    return num + num2

print(annotations_dont_matter(1, 2))
print(annotations_dont_matter("hi ", "everybody"))

# you can add a variable to the end of the parameters called a variable argument, which takes an unspecified amount of information
# in and place them inside a Tuple
def variable_arguments (*args): # use this vararg when you don't know how much information the function will work with
    for element in args:
        print(element)
variable_arguments(1,2,3,4,5,6)

variable_arguments(1,"two",3,4,5,6) # can mix and match data types

# this allows you to enter information in key/value pairs. The kwargs is a dictionary
def key_word_function(**kwargs): # this adds key:value arguments into your function
    print(kwargs["password"])
    print(kwargs["username"])

key_word_function(password = "my password", username= "my username")

def more_kwargs(**kwargs):
    for key, value in kwargs.items():
        print(f"{key}:{value}")

more_kwargs(first_key="first value",second_key="second value",third_key="third_value")

# you can even add functions as arguments in your functions

def called_function():
    return "this is called the outter function "

def calls_a_function(function):
    return function() + ", and it was called here"

print(calls_a_function(called_function))

def create_user(**kwargs):
    print("\nUser information:")

    for key, value in kwargs.items():
        print(f"{key}:{value}")

    
#collect user input

name = input("enter name:")
age = input("enter age:")
city = input("enter city: ")

create_user(name = name, age = age, city=city)









