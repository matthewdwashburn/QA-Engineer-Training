from os import getenv

from dotenv import load_dotenv
import pandas as pd
from sqlalchemy import create_engine, text


load_dotenv()
CS = getenv("CS")
engine = create_engine(CS)

query = "SELECT * FROM employees;"
df = pd.read_sql(query, engine)
print(df)

df.to_sql(
    name = "processed",
    con=engine,
    if_exists = 'replace',
    index=False
)

#Get input from the user
first_name = input ("First name: ")
last_name = input ("Last name: ")
email = input ("email: ")
hire_date = input ("Hire Date (YYYY-MM-DD): ")
salary = float(input("Salary: "))

with engine.begin() as conn:
    result = conn.execute(
        text("""
            INSERT INTO employees (
             first_name,
             last_name,
             email,
             hire_date,
             salary
             )
             VALUES (
             :first_name,
             :last_name,
             :email,
             :hire_date,
             :salary
             )
             RETURNING employee_id
             """),
             {
                 "first_name": first_name,
                 "last_name": last_name,
                 "email": email,
                 "hire_date": hire_date,
                 "salary": salary
             }
    )
    employee_id = result.scalar()
    print(f"Employee {employee_id} inserted successfully!")


    

