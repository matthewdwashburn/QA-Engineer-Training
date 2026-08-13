def require_admin(func):
    def wrapper(user):
        if user != "admin":
            print("Access Denied!")
        
    return wrapper
    
@require_admin
def delete_database(user):
    print("Database deleted")


delete_database("Bob")
delete_database("Alice")

def my_decorator(func):
    def wrapper():
        print("Before function runs")
        func()
        print("After function runs")
    return wrapper

@my_decorator
def say_hello():
    print("Hello!")

say_hello()

def my_decorator_2(func):
    def wrapper(*args, **kwargs):
        print("Starting function...")
        result = func(*args, **kwargs)
        print("Function finished.")
        return result
    return wrapper

@my_decorator_2
def add(a,b):
    return a+b


@my_decorator_2
def multiply(a, b):
    return a*b

print(add(3,5))
print(multiply(3,5))