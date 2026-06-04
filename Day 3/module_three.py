from package_one import ClassOneFun # don't need to specify the file in the package because of our __init__ file
from package_two import module_two # fully qualified names are better to work with: help keep track of what belongs where
import module_four

class_one = ClassOneFun()
class_two = module_two.ClassTwo()
class_four = module_four.ClassFour() # Fully qualified name