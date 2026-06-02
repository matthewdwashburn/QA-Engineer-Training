def first_function():
    print("First Function! Yay!")
    return "This is the first function"

ff = first_function()
print(ff)

def increment_function(parameter):
    return parameter + 1

print(increment_function(6))

def add_type_annotation(param1: str, param2: int) -> str:
    return "return string, expected"

print(add_type_annotation("brunch", 7))

def annotations_dont_matter(param1: int, param2: int) -> str: # python does not care
    return param1 + param2

print(annotations_dont_matter(1, 2))

# you can add a variable to the end of the parameters called a variable argument, which takes an unspecified amount of information
# in and place them inside a tuple
def variable_arguments (*args):
    for element in args:
        print(element)

variable_arguments(1,2,3,4,5,6,7)

variable_arguments(1, 2, "hi", 3, 4, 5)

# kwargs = keyword arguments
def key_word_function(**kwargs): # this adds key value pairs
    print(kwargs["username"])
    print(kwargs["password"])

key_word_function(password = "My password", username = "My username")

def more_kwargs(**kwargs):
    for key, value in kwargs.items():
        print(f"{key}:{value}")

more_kwargs(first_key="first value", second_key="second value", third_key="third value")

def called_function():
    return "this is called the outer function"

def calls_a_function(function):
    return function() + ", and it was called here"

print(calls_a_function(called_function))

def create_user(**kwargs):
    print("\nUser information:")

    for key, value in kwargs.items():
        print(f"{key}:{value}")

# collect user input 
name = input("enter name: ")
age = input("enter age: ")
city = input("enter city: ")

create_user(name = name, age = age, city = age)