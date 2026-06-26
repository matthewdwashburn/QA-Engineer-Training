# How to clean a list of strings to make them alphanumeric using built in isalnum()
```
mixed_list = ["user_123!", "hello#world", "python3.14"]

# Keep only alphanumeric characters for each element
clean_list = ["".join(char for char in item if char.isalnum()) for item in mixed_list]

print(clean_list)
# Output: ['user123', 'helloworld', 'python314']
```

# Clean the raw string with list comprehension
```
text = "Hello, World! 123 @Python$"

# Keep only alphanumeric characters
cleaned_text = "".join([char for char in text if char.isalnum()])

print(cleaned_text)
# Output: HelloWorld123Python
```

# Using import re
```
import re

text = "User_Name! 2026 #Data$"
# [^a-zA-Z0-9] matches anything that is NOT a letter or a number
cleaned = re.sub(r"[^a-zA-Z0-9]", "", text)

print(cleaned)  # Output: UserName2026Data
```