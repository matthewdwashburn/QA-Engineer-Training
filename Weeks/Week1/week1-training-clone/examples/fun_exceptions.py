#bad_math = 5/0 # this will give you a ZeroDivisionError, because you can't divide by zero

#placing the code inside a try/except block gives us a way to handle the exception
try:
    bad_math = 5/0
except:
    print("you can't divide by zero, first try")

# you can have multiple except blocks, which need to go from specific to general
try:
    5/"0"
except ZeroDivisionError: # this only triggers if the try code causes a Zero Division error
    print("you can't divide by zero")
except: # this will catch any other exception
    print("this will only show if a different kind of exception is caught")

# you can create your own custom exceptions
class MyException(Exception):
    """this is a custom exception I made"""
    def __init__(self, message): # you want the message parameter so you can include a custom messages when the exception is raised
        self.message = message
    
try:
    raise MyException("this is my custom message")
except MyException as e:
    print(e.message)
finally:
    print("done")




