from abc import ABC, abstractmethod
class animal(ABC):
    @abstractmethod
    def makeSound(self):
        pass

    def breath(self):
        print("breath")
    
class Bear(animal):
    def makeSound(self):
        print("Grrrrr")


my_bear = Bear()

my_bear.makeSound()

my_bear.breath()
