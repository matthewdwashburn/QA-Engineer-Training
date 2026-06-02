import sys
def hello_qa():
    name = input(" What is your name? ")
    role = input(" What is your role? ")
    print(f"Welcome, {name}! Your role is {role}.")
    print(f"Python version: {sys.version}")


def main():
    hello_qa()

if __name__ == "__main__":
    main()