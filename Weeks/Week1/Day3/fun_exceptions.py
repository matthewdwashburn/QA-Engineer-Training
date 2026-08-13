try:
    bad_math = 5/0
except:
    print("You can't divide by zero.")


# you can handle multiple except blocks, which need to go from specific to general

try:
    5/"0"
except ZeroDivisionError:
    print("You can't divide by zero.")
except: # this will catch any other exception
    print("This will only show if a different kind of exception is caught")


class MyException(Exception):
    """this is a custom exception I made"""
    def __init__(self, message): # you want the message param so you can include custom message
        self.message = message

try:
    raise MyException("Custom exception") # only exception because we raised it
except MyException as e:
    print(e.message)
finally:
    print("done")