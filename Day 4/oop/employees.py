from abc import ABC, abstractmethod

# ABSTRACTION: ABC makes Employee an abstract base class — it can't be instantiated directly
class Employee(ABC):
    # ENCAPSULATION (class variable): shared state owned by the class, not any instance
    _id_counter = 1000

    def __init__(self, name, salary):
        # ENCAPSULATION (public): accessible from anywhere
        self.name = name
        # ENCAPSULATION (protected): _ signals "don't touch outside class/subclasses" — not enforced by Python
        self._salary = salary

        Employee._id_counter += 1
        # ENCAPSULATION (private): __ triggers name mangling, preventing accidental access from outside
        self.__employee_id = Employee._id_counter

    # ENCAPSULATION: controlled read access to a private attribute without exposing it directly
    def get_employee_id(self):
        return self.__employee_id

    # ABSTRACTION: forces every subclass to define its own calculate_pay — the "what" is promised, the "how" is deferred
    @abstractmethod
    def calculate_pay(self):
        pass

    def display_info(self):
        print(
            f"Name:{self.name}\n"
            f"Salary:{self._salary}\n"
            f"ID: {self.__employee_id}"
        )

# INHERITANCE: SalariedEmployee gets all of Employee's attributes and methods for free
class SalariedEmployee(Employee):
    # POLYMORPHISM: fulfills the abstract contract with its own implementation
    def calculate_pay(self):
        return self._salary

# INHERITANCE: HourlyEmployee also extends Employee but needs extra constructor args
class HourlyEmployee(Employee):
    def __init__(self, name, hourly_rate, hours_worked):
        # INHERITANCE: super() delegates shared setup (name, salary, ID) to the parent
        super().__init__(name, hours_worked * hourly_rate)
        self.hours_worked = hours_worked
        self.hourly_rate = hourly_rate

    # POLYMORPHISM: same method name as SalariedEmployee, different behavior
    def calculate_pay(self):
        self._salary = self.hourly_rate * self.hours_worked
        return self._salary

    # POLYMORPHISM (method overriding): replaces Employee.display_info with hourly-specific output
    def display_info(self):
        print(
            f"Name:{self.name}\n"
            f"Rate:{self.hourly_rate}\n"
            f"Hours Worked:{self.hours_worked}\n"
            f"Total Pay:{self.calculate_pay()}\n"
            f"ID: {self.get_employee_id()}"
        )

emp = SalariedEmployee("Matt", "65000")
hourly_emp = HourlyEmployee("Will", 10, 40)

print(emp.name)
print(emp._salary)        # accessible but _ signals: don't do this outside the class without good reason
# print(emp.__employee_id)  # NameError — private name mangling enforces this at runtime
print(emp.get_employee_id())

# NAME MANGLING: Python rewrites __employee_id as _Employee__employee_id internally — this bypasses private, but is intentional bad practice
print(emp._Employee__employee_id)
emp.display_info()
hourly_emp.calculate_pay()
hourly_emp.display_info()
