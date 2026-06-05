class Car:
    def __init__(self, speed = 0):
        self.speed = speed
        self.odometer = 0
        self.time = 0
        print(__name__)
        print(dir())
    def say_state(self):
        print(f"I'm going {self.speed} mph!")
    def accelerate(self):
        self.speed += 5
    def brake(self):
        if self.speed<5:
            self.speed = 0
        else:
            self.speed -= 5
    def step(self):
        self.odometer += self.speed
        self.time += 1
if __name__ == "__main__":
    print(dir())
    my_car = Car()
    print("I'm a Car")
    while True:
        action = input("What do you want to do? (a)ccelerate (b)rake (o)domoter: ")
        if action not in "abo":
            print("Please choose a valid option.")
            continue
        match action:
            case "a":
                my_car.accelerate()
            case "b":
                my_car.brake()
            case "o":
                print(f"I've driven {my_car.odometer} miles")
        my_car.step()
        my_car.say_state()