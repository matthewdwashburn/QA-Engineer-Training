def validate_password(password):
    special_chars = "!@#$%^&*"
    
    is_long_enough = len(password) >= 8
    has_upper = any(char.isupper() for char in password)
    has_lower = any(char.islower() for char in password)
    has_special = any(char in special_chars for char in password)
    has_digit = any(char.isdigit() for char in password)

    is_validated = all([is_long_enough, has_upper, has_lower, has_special, has_digit])

    return_dict = {}
    return_dict["valid"] = is_validated
    return_dict["errors"] = ""

    if not is_long_enough:
        return_dict["errors"] += "Too Short. "
    if not has_upper:
        return_dict["errors"] += "No Upper. "
    if not has_lower:
        return_dict["errors"] += "No Lower. "
    if not has_special:
        return_dict["errors"] += "No Special. "
    if not has_digit:
        return_dict["errors"] += "No Number. "

    return return_dict


print(validate_password("Abc123!x"))   # valid
print(validate_password("abc"))      # too short, no upper, no digit, no special
print(validate_password("ABCDEFGH"))    # no lower, no digit, no special
print(validate_password("ABCDefgh1!")) # valid

def fizz_buzz(n):
    for i in range(n + 1): # range n does not indlude n
        if i % 3 == 0 and i % 5 == 0 and i % 7 == 0:
            print("FizzBuzzBoom")
        elif i % 3 == 0 and i % 5 == 0:
            print("FizzBuzz")
        elif i % 3 == 0 and i % 7 == 0:
            print("FizzBoom")
        elif i % 5 == 0 and i % 7 == 0:
            print("BuzzBoom")
        elif i % 3 == 0:
            print("Fizz")
        elif i % 5 == 0:
            print("Buzz")
        elif i % 7 == 0:
            print("Boom")

fizz_buzz(105)

def guessing_game():
    import random
    random_num = random.randint(1,100)
    attempts = 7
    guessed = False
    while attempts > 0:
        try:
            guess = int(input(f"Guess the random number 1 to 100. You have {attempts} guesses remaining: "))
        except ValueError:
            print("Please input an integer.")
        if guess > random_num:
            print("Too high!")
            attempts -= 1
        elif guess < random_num:
            print("Too low!")
            attempts -= 1
        else:
            print(f"Congrats! You guessed the number in {8 - attempts} attempts.")
            guessed = True
            break
    
    if not guessed:
        print(f"Better luck next time! The number was {random_num}.")

# guessing_game()

# enumerate practice

def grade_processor(int_grades):
    for index, grade in enumerate(int_grades):
        if grade >= 93:
            print(f"Student:{index}, Grade A")
        elif grade < 93 and grade >= 90:
            print(f"Student:{index}, A-")
        elif grade < 90 and grade >= 87:
            print(f"Student:{index}, B+")
        elif grade < 87 and grade >= 83:
            print(f"Student:{index}, B")
        elif grade < 83 and grade >= 80:
            print(f"Student:{index}, B-")
        elif grade < 80 and grade >= 77:
            print(f"Student:{index}, C+")
        elif grade < 77 and grade >= 73:
            print(f"Student:{index}, C")
        elif grade < 73 and grade >= 70:
            print(f"Student:{index}, C-")
        elif grade < 70 and grade >= 67:
            print(f"Student:{index}, D+")
        elif grade < 67 and grade >= 63:
            print(f"Student:{index}, D")
        elif grade < 63 and grade >= 60:
            print(f"Student:{index}, D-")
        elif grade < 60 and grade > 0:
            print(f"Student:{index}, F")
        elif grade < 0 and grade != -999:
            print("Invalid grade. Skipped.")
        else:
            print("Sentinel Value. Processing Stopped.")
            break


grade_processor([88, 92, 75, -1, 63, 95, 81, 70, -5, 55, 100, 78, -999, 90, 85])