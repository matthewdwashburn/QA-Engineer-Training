print("Hello, World!")

#numeric datatypes are integer and float
my_integer = 1 
my_float = 1.1 

print(type(my_integer))
print(type(my_float))

# string 
my_string = "this is a string"

# bool
my_true_boolean = True
my_false_boolean = False

# none can be assingned, useful for avoiding errors
my_none = None

my_string_literal = "the string literal is the content of the string"

name = "Wil"
greeting = "Hello " + name

# f string is short for formatted string
formatted_string = f"Hello {name}"

print(greeting)
print(formatted_string)

# regex
import re

text = "Order ID: 12345"

match = re.search(r'\d+', text)

if match:
    print(f"Found ID:{match.group()}")

# .format() is worse f string
formatted_by_method_string = "Hello {}".format("Will")

print(formatted_by_method_string)

my_string = "Hello Wil"
just_hello = my_string[0:5]
print(just_hello)
just_wil = my_string[6:]
print(just_wil)

#use a negative number to work backwards in a string (-1 is the last element of the string)
using_negative_index = my_string[0:-2] # this will be Hello W
print(using_negative_index)

# use the third position to designate the increment steps
every_other_letter = my_string[0::2] # starts with first character and appends every other letter after it
print(every_other_letter)
reversed_letters = my_string[::-1]
print(reversed_letters)

# string indices are still 0 based
print(len(my_string)) # 9
print(my_string[8]) # l, last characters

a_number:float = 6 # casting does nothing here, its an int
print(type(a_number))
string_6 = (str(a_number))
print(type(string_6))

