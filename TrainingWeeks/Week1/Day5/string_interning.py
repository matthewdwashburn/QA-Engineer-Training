import sys

# String interning stores only one copy of each distinct immutable string value.
# CPython automatically interns strings that look like Python identifiers
# (only letters, digits, and underscores) regardless of length.

a = 'name'
b = 'name'
print(a is b)  # True - short identifier-like string, auto-interned

x = 'longer_variable_name'
y = 'longer_variable_name'
print(x is y)  # True - length doesn't matter, still identifier-like

# Strings with spaces or special characters are NOT auto-interned.
# Use a variable to prevent compile-time constant folding.
word = 'hello'
a = word + ' world'
b = word + ' world'
print(a is b)  # False - runtime-constructed strings are never interned

# Manual interning with sys.intern() forces reuse of a single copy
a = sys.intern(word + ' world')
b = sys.intern(word + ' world')
print(a is b)  # True - explicitly interned, same object

# Identifier-like strings returned from functions are also interned
def get_key():
    return 'config_key'

a = get_key()
b = 'config_key'
print(a is b)  # True - identifier-like strings are interned across code objects
