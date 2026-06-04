from abc import ABC, abstractmethod

class Employee(ABC):
    # class level counter (shared across all employees)
    _id_counter = 1000
    def __init__(self, name, salary):
        self.name = name # public
        self._salary = salary # protected, _ convention
        
        # auto-generate unique ID
        Employee._id_counter += 1
        
        # set id for this employee
        self.__employee_id = Employee._id_counter

    def get_employee_id(self):
        return self.__employee_id
    
    @abstractmethod
    def calculate_pay(self):
        pass

    def display_info(self):
        print(
            f"Name:{self.name}\n"
            f"Salary:{self._salary}\n"
            f"ID: {self.__employee_id}"
        )

class SalariedEmployee(Employee):
    # abstraction, have to define the abstract classes in child class
    def calculate_pay(self):
        return self._salary
    
class HourlyEmployee(Employee):
    def __init__(self, name, hourly_rate, hours_worked):
        super().__init__(name, hours_worked * hourly_rate) # cast it back up to the employee class
        self.hours_worked = hours_worked
        self.hourly_rate = hourly_rate

    def calculate_pay(self):
        self._salary = self.hourly_rate * self.hours_worked
        return self._salary
    
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
print(emp._salary) # Python doesn't enforce protected, but note to developers, don't touch this outside class, unless you have a good reason
# print(emp.__employee_id) # does not work and private is enforced
print(emp.get_employee_id()) 

print(emp._Employee__employee_id) # bypassed private, name mangling: python rewrites __employee_id as _Employee__employee_id behind the scenes
# In python the purpose of private access modifier is not security, but for programmers to prevent accidental access
emp.display_info()
hourly_emp.calculate_pay()
hourly_emp.display_info()
